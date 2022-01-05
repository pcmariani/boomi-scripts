/* @data
<RecordQueryResponse resultCount="1" totalCount="1">
  <Record recordId="7ea346ce-bbcc-4377-a65d-7a2b3fd874f5" createdDate="2019-10-22T18:54:51Z" updatedDate="2019-10-22T18:54:51Z" recordTitle="100091798">
    <Fields>
      <contact>
        <ao_id>100091798</ao_id>
        <email_main>AOCMTEST+amarkwalder_aospine.org@gmail.com</email_main>
      </contact>
    </Fields>
  </Record>
</RecordQueryResponse>
*/
import java.util.Properties;
import java.io.InputStream;

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def root = new XmlSlurper().parse(is)

    root.Record.Fields.contact.email_main.find() { it.replaceBody(it.text().toLowerCase()) }

    def outData = groovy.xml.XmlUtil.serialize(root).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim()
    is = new ByteArrayInputStream(outData.getBytes("UTF-8"));


    dataContext.storeStream(is, props);
}
