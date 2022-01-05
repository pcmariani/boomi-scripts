/* @data
<Contact>
  <OtherCountry>United States</OtherCountry>
  <OtherCountryCode>US</OtherCountryCode>
  <MailingCountry />
  <MailingCountryCode />
  <AO_ID__c>100200907</AO_ID__c>
</Contact>
*/
import java.util.Properties;
import java.io.InputStream;
import org.json.JSONObject;
import org.json.XML;
import com.boomi.execution.ExecutionUtil;
logger = ExecutionUtil.getBaseLogger();


for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);
    
    def reader = new BufferedReader(new InputStreamReader(is));
    def buffer = new StringBuffer();
    def firstLine = reader.readLine()
    def closeTagOfRootNode

    if (!firstLine.startsWith("<?xml")){
        closeTagOfRootNode = firstLine.replace("<","</")
    }

    int j = 0
    while ((line = reader.readLine()) != null) {
        if (i == 0 && !closeTagOfRootNode) closeTagOfRootNode = line.replace("<","</")
        else buffer.append(line);
        j++
    }
    
    JSONObject xmlJsonObj = XML.toJSONObject(buffer.toString().replace(closeTagOfRootNode,""));
    String jsonString = xmlJsonObj.toString();
    
    is = new ByteArrayInputStream(jsonString.getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}