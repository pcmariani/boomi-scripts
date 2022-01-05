/* @data
<event>
  <id>10012391</id>
  <event_id>10012391</event_id>
  <accounting_cost_center>3430</accounting_cost_center>
  <event_details>
    <url>https://aocmf2.int2.aofoundation.org/eventdetails.aspx?id=1286&amp;from=PG_COURSEDIRECTORY</url>
    <type>HUB.EDUCATIONAL_EVENT</type>
    <managing_system>HUB.ERP_CMM</managing_system>
    <level>sdf</level>
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
        (!root.event_details.level.size() || root.event_details.level == "")
        ) { 

        root.event_details.appendNode{ 

            level("LEVEL_REQUIRED_WHEN_TYPE_NOT_MEETING")
        }
    }
    
    def outData = groovy.xml.XmlUtil.serialize(root).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim()
    is = new ByteArrayInputStream(outData.getBytes("UTF-8"));


    dataContext.storeStream(is, props);
}
