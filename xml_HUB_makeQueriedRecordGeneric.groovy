/* @data
<RecordQueryResponse resultCount="1" totalCount="1">
  <Record recordId="49c51cde-e0ad-40a0-a6b0-3774caef19a0" createdDate="2019-11-12T15:01:42Z" updatedDate="2019-11-12T20:56:11Z" recordTitle="V_372">
    <Fields>
      <event>
        <event_id>V_372</event_id>
        <cmm_event_id>V_VHTESTVET1</cmm_event_id>
        <cmm_event_number>V_372</cmm_event_number>
        <accounting_cost_center>5060</accounting_cost_center>
        <event_details>
          <name>VH TEST VET 1</name>
          <managing_system>HUB.CMM_CMM</managing_system>
        </event_details>
        <addresses>
          <address>
            <city>Winterthur</city>
            <country>HUB.CH</country>
            <type>HUB.HOSTING</type>
          </address>
        </addresses>
        <languages>
          <language>
            <language>HUB.ENG</language>
            <type>HUB.PRIMARY</type>
          </language>
        </languages>
      </event>
    </Fields>
  </Record>
</RecordQueryResponse>
*/
/* @props
MODEL_NAME=event
QUERY_ID_FIELD=event_id
*/
import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;

String MODEL_NAME = ExecutionUtil.getDynamicProcessProperty("MODEL_NAME")
String ID_NAME = ExecutionUtil.getDynamicProcessProperty("QUERY_ID_FIELD").toLowerCase()


for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    String outData = is.text.replaceAll(/(<\/?)${ID_NAME}/,"\$1id")
                            .replaceAll(/(<\/?)${MODEL_NAME}/,"\$1object")

    is = new ByteArrayInputStream(outData.getBytes("UTF-8"))
    dataContext.storeStream(is, props);
}


