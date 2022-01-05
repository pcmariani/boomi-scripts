/*@data
<RecordHistoryResponse resultCount="2" offsetToken="2" grid="b8571cba-ff65-4083-bacc-878e1ab55b9e" totalCount="326">
   <contact enddate="05-08-2020T13:22:43.000+0000" grid="b8571cba-ff65-4083-bacc-878e1ab55b9e" source="FON" enddatesource="FON" startdate="04-16-2020T09:17:18.000+0000" version="227775" transactionId="c8865e1c-4403-45d6-8862-1f31fc29b3a7">
    <id>b8571cba-ff65-4083-bacc-878e1ab55b9e</id>
    <ao_id>100265205</ao_id>
    <about_me>Claude Krajcik</about_me>
    <academic_title_pre>Doct</academic_title_pre>
    <academic_title_post>FABN</academic_title_post>
    <user_id>100265205</user_id>
  </contact>
</RecordHistoryResponse>
*/

import java.util.Properties;
import java.io.InputStream;

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def record = new XmlSlurper().parseText(is.text)
    def newRecords = new Node(null, "records")
    
    record.children().each() { nodes -> 
        
        def newRecord = new Node(newRecords,"record")

        nodes.children().collect { node -> 

            newRecord.appendNode(node.name(),node.text())
        }
    }

    def outData = groovy.xml.XmlUtil.serialize(newRecords).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim()
    
    is = new ByteArrayInputStream(outData.getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}
