/* @data
   "Email","job_title","first_name","last_name","organization","department","unit","street_lines","state","postal_code","City","country","topic","is_subscribed","interest_area","graduation_year"
   "aoctmail+jan.brune_swisscom.com@gmail.com","","Jan","Brune","Swisscom","","","","","","Embrach","CH","Joint_preservation_and_osteotomies,Joint_replacement, Knee_surgery, Hip_and_pelvis_surgery,","0","AORECON","N/A"
   "aoctmail+hornglii_hotmail.com@gmail.com","Dr","Horng Lii","Oh","St Vincents Hospital","","","","","","Sydney","AU","Orthopedic_trauma, Joint_preservation_and_osteotomies,Joint_replacement, Hip_and_Hip_and_pelvis_surgery_surgery, Knee_surgery, Hip_and_pelvis_surgery, Surgical_sports_medicine,","0","AORECON","N/A"
   "aoctmail+janew66_hotmail.com@gmail.com","","Jane","Webber","Milton Keynes University Hospital","","","","","","Eaglestone","GB","","0","AORECON","N/A"
   */

import java.util.Properties;
import java.io.InputStream;

def delimiter = "\",";
def splitColDelimiter = ",";
def colToSplit = 12
def LINE_SEPARATOR = System.getProperty("line.separator");

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    reader = new BufferedReader(new InputStreamReader(is));
    outData = new StringBuffer();

    while ((line = reader.readLine()) != null ) {

        def lineArr = line.split(delimiter);
        def splitColArr = lineArr[colToSplit].split( splitColDelimiter );

        for (int m = 0; m < splitColArr.size(); m++) {

            def outDataArr = [];
            for ( int j = 0; j < lineArr.size(); j++ ) {

                if (j == colToSplit) outDataArr.push("\""+splitColArr[m].trim());
                else outDataArr.push(lineArr[j].trim())
            }
            outData.append(outDataArr.join(delimiter) + LINE_SEPARATOR);
        }
    }

    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}
