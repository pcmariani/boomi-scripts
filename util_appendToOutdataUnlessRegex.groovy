/* @data
"Country","CM","Cameroon"
"Country","","Canada"
"Country","CANNOT_BE_BLANK","CANNOT_BE_BLANK"
"Country","KY","Cayman Islands"
"Country","","Central African Republic"
"Country","TD","Chad"
"Country","CL","Chile"
"Country","","China"
"Country","CX","Christmas Island"
"Country","DELETEFIELD","Cocos (Keeling) Islands"
"Country","DELETEARRAY","Colombia"
"Country","KM",""
*/

import java.util.Properties;
import java.io.InputStream;

def LINE_SEPARATOR = System.getProperty("line.separator");

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    reader = new BufferedReader(new InputStreamReader(is));
    outData = new StringBuffer();

    while ((line = reader.readLine()) != null ) {
    
        if (!(line =~ /(,"",|DELETE|CANNOT_?BE_?BLANK)/)) outData.append(line + LINE_SEPARATOR);

    }
    
    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}
