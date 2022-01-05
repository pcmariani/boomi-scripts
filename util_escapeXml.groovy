/* @data
<event op="UPDATE" grid="ffa329e1-05a0-4392-9c80-eb59a9f7a890" ts="04-30-2020T23:58:15.000+0000">
    <id>10011261</id>
    <event_id>10011261</event_id>
</event>
*/

/*
 * Escapes XML
 */
import java.util.Properties;
import java.io.InputStream;
import org.apache.commons.text.StringEscapeUtils;

def LINE_SEPARATOR = System.getProperty("line.separator");

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    reader = new BufferedReader(new InputStreamReader(is));
    outData = new StringBuffer();

    while ((line = reader.readLine()) != null ) outData.append(line + LINE_SEPARATOR);
    
    is = new ByteArrayInputStream(StringEscapeUtils.escapeXml10(outData.toString()).getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}
