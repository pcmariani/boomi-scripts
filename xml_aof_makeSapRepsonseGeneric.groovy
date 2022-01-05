/*@data
<env:Envelope xmlns:env="http://www.w3.org/2003/05/soap-envelope">
    <env:Header/>
    <env:Body>
        <n0:SupplierInvoiceBundleMaintainConfirmation_sync xmlns:n0="http://sap.com/xi/SAPGlobal20/Global"
            xmlns:prx="urn:sap.com:proxy:LF4:/1SAI/TAE23BCC1DE5DB542ABE5EE:804">
            <SupplierInvoice>
                <ChangeStateID>                 20201111180145.0000000</ChangeStateID>
                <BusinessTransactionDocumentID>439</BusinessTransactionDocumentID>
                <UUID>00163eaf-e610-1eeb-8988-fe5eec7da965</UUID>
            </SupplierInvoice>
            <Log>
                <MaximumLogItemSeverityCode>2</MaximumLogItemSeverityCode>
                <Item>
                    <TypeID>015(//SRMAP/LSIV_SIV/)</TypeID>
                    <CategoryCode>ENV.CBM</CategoryCode>
                    <SeverityCode>2</SeverityCode>
                    <Note>Invoice may be a duplicate of invoice 394</Note>
                </Item>
                <Item>
                    <TypeID>009(//ISCB/CB_CHECK/)</TypeID>
                    <CategoryCode>INC.BOI</CategoryCode>
                    <SeverityCode>2</SeverityCode>
                    <Note>G/L account Expenses Non Employees (Travel) does not exist or is not permitted</Note>
                </Item>
            </Log>
        </n0:SupplierInvoiceBundleMaintainConfirmation_sync>
    </env:Body>
</env:Envelope>
*/

import java.util.Properties;
import java.io.InputStream;
import groovy.xml.*

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def root = new XmlSlurper().parse(is)
    def sapObjectConfirmation = root.Body.children()[0]
    // def sapObject = sapObjectConfirmation.children()[0]
    // def sapLog = sapObjectConfirmation.Log

    props.setProperty("document.dynamic.userdefined.DDP_SAP_ObjectConfirmation", sapObjectConfirmation.name());
    // props.setProperty("document.dynamic.userdefined.DDP_SapObjectName", sapObject.name());

    String newRoot = new StreamingMarkupBuilder().bind {
        Envelope {
            Body {
                GenericConfirmation {
                    mkp.yield sapObjectConfirmation.children()
                    // GenericObject {
                    //     mkp.yield sapObject.children()
                    // }
                    // Log {
                        // mkp.yield sapLog.children()
                    // }
                }
            }
        }
    }

    def outData = groovy.xml.XmlUtil.serialize(newRoot).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim()
    is = new ByteArrayInputStream(outData.getBytes("UTF-8"));


    dataContext.storeStream(is, props);
}
