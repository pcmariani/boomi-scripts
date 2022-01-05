/*@data
<event>
  <id>10010161</id>
  <event_id>10010161</event_id>
  <event_details>
    <type>HUB.ADFDAFDS</type>
  </event_details>
</event>
*/

import java.util.Properties;
import java.io.InputStream;

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def root = new XmlSlurper().parse(is)

    if (root.event_details.type.text() != "HUB.MEETING" && 
        !root.event_details.level.size()) { 

        root.event_details.appendNode{ 

            level("LEVEL_REQUIRED_WHEN_TYPE_NOT_MEETING")
        }
    }
    
    def outData = groovy.xml.XmlUtil.serialize(root).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim()
    is = new ByteArrayInputStream(outData.getBytes("UTF-8"));


    dataContext.storeStream(is, props);
}
