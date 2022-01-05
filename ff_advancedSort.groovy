/* @data
Po_number,line_item_number,Po_date,required_ship_date
15507,11,2019-08-13,2019-09-06
15507,12,2019-08-13,2019-09-06
15507,14,2019-08-13,2019-09-06
1234ABC,1,2019-07-12,2019-08-10
15507,8,2019-08-13,2019-09-06
15302,2,2019-09-02,2019-10-03
14561,1,2019-09-02,2019-10-03
ABC5678,1,2019-07-12,2019-08-10
16301,1,2019-07-12,2019-08-03
*/

/* @props
document.dynamic.userdefined.DDP_SORT_DATA_HEADER=TRUE
*/
import java.util.Properties;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.time.LocalDate;

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);
    
    // Name of Dynamic Document Property that contains 'TRUE' or 'FALSE' to represent header row in the data
    String sortDataHeader = "DDP_SORT_DATA_HEADER";
    
    // To Store the header Row if its present
    String headerData = "";
    
    // Element Delimiter -> Change this if comma is not your delimiter
    String elementDelimiter = ",";
    
    // File Delimiter -> Change this if newline is not your delimiter
    String fileDelimiter = "\n";
    
    // Read all the lines from Document
    List<String> lines = IOUtils.readLines(is,Charset.defaultCharset());
    
    // Init temp collections
    List<List<String>> csvLines = new ArrayList<List<String>>();
    
    // Access the value set in Dynamic Document Property
    String dataHeader = props.getProperty("document.dynamic.userdefined." + sortDataHeader);
    
    // if sortDataHeader value is not set then its assumed to contain header row and removes the first line
    if(dataHeader == null || (!dataHeader.trim().isEmpty() && dataHeader.trim().toUpperCase().equals("TRUE"))){
        //Save the header row data
        headerData = lines.get(0);
        
        // Skip the first line and Loop through rest of the line. Parse each line with elementDelimiter and add it to new collection
        lines.stream().skip(1).forEach{line -> csvLines.add(Arrays.asList(line.split(elementDelimiter)))};
    }else{
        
        // Loop through and Parse each line with elementDelimiter and add it to new collection csvLines
        lines.stream().forEach{line -> csvLines.add(Arrays.asList(line.split(elementDelimiter)))}; 
    }
    
    // Build Comparator as per requirement
    
    // 1. Sort 3rd column Po_date
    Comparator<List<String>> poDateCol = Comparator.comparing{line -> LocalDate.parse(line.get(2))};  
    
    // 2. Sort 1st column Po_number
    Comparator<List<String>> poNumCol = Comparator.comparing{line -> line.get(0)};
    
    // 3. Sort 2nd column line_item_number
    Comparator<List<String>> poItemCol = Comparator.comparing{line -> Integer.parseInt(line.get(1))};

    // Finally Chain all the Comparators together in the order you want to perform the multiple sort
    // As per our requirment; Sort the rows in CSV by Po_date then Po_number and then by line_item_number
    Comparator<List<String>> finalCompare = poDateCol.thenComparing(poNumCol).thenComparing(poItemCol);
    
    // Sort the Data in the csvLines collection
    Collections.sort(csvLines,finalCompare);
    
    // Init String Builder
    StringBuilder builder = new StringBuilder();
    
    // If Header Row Data exist then add it back to output CSV file
    if(!headerData.isEmpty()){
        builder.append(headerData);
        builder.append(fileDelimiter);
    }
    
    // Build the sorted data back to csv
    for (List<String> line: csvLines){
                for(String colData: line){
                    builder.append(colData);
                    builder.append(elementDelimiter);
                }
                builder.deleteCharAt(builder.lastIndexOf(elementDelimiter));
                builder.append(fileDelimiter);
    }
    
    InputStream newis = IOUtils.toInputStream(builder.toString(),Charset.defaultCharset());

    dataContext.storeStream(newis,props);
}
