/* --- @data
Header
line1
line2
line3
line4
line5
Footer
*/

/*
 * Remove First and Last line
 */
import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;

def LINE_SEPARATOR = System.getProperty("line.separator");

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    reader = new BufferedReader(new InputStreamReader(is));
    outData = new StringBuffer();

    // Create arrayList to hold lines of buffer so we can use the indexes 
    ArrayList<String> dataArr = new ArrayList<String>();  

    // Add lines of buffer into the arrayList
    while((line=reader.readLine())!=null) {
        dataArr.add(line);
    }

    // variable to hold size (number of lines) of array
    int arrLength = dataArr.size();

    // if the last line is empty subtract 1 from index of last line.
    if (!dataArr[arrLength]) arrLength -= 1;

    // Append contents of each arr element to the outData buffer starting with the second element (j = 1)
    int j = 1;
    while (j < arrLength) {
        outData.append(dataArr[j] + LINE_SEPARATOR);
        j++;
    }

    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"))
    dataContext.storeStream(is, props);
}
