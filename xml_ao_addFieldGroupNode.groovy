/* @data
<event>
   <id>T_4956</id>
   <event_id>T_4956</event_id>
   <event_details>
      <managing_system>HUB.CMM_CMM</managing_system>
   </event_details>
   <agendas>
      <agenda_items>
         <agendaitem_id>1_0</agendaitem_id>
         <agenda_name>Test1</agenda_name>
         <agenda_type>HUB.SESSION</agenda_type>
         <location/>
         <attribute/>
         <duration>00:10</duration>
         <start_time>2021-09-01T08:00:00</start_time>
         <end_time>2021-09-01T08:10:00</end_time>
      </agenda_items>
      <agenda_items>
         <agendaitem_id>1_1</agendaitem_id>
         <agenda_name>Test Luksys</agenda_name>
         <agenda_type>HUB.ACTIVITY</agenda_type>
         <location/>
         <attribute>HUB.LECTURE</attribute>
         <duration>00:10</duration>
         <start_time>2021-09-01T08:00:00</start_time>
         <end_time>2021-09-01T08:10:00</end_time>
      </agenda_items>
   </agendas>
   </event>
*/

import java.util.Properties;
import java.io.InputStream;

for( int i = 0; i < dataContext.getDataCount(); i++) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def root = new XmlParser().parseText(is.text);

    if (!root.contributor_agenda_items) {
        def newNode = new Node(root, "contributor_agenda_items") 
    }
    
    def result = groovy.xml.XmlUtil.serialize(root).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim()
    
    is = new ByteArrayInputStream(result.getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}
