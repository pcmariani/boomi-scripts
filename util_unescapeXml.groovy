/*
 * Unescapes XML
 */
import java.util.Properties;
import java.io.InputStream;
import org.apache.commons.lang.StringEscapeUtils;

def LINE_SEPARATOR = System.getProperty("line.separator");

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    reader = new BufferedReader(new InputStreamReader(is));
    outData = new StringBuffer();

    while ((line = reader.readLine()) != null ) outData.append(line + LINE_SEPARATOR);

    is = new ByteArrayInputStream(StringEscapeUtils.unescapeXml(outData.toString()).getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}