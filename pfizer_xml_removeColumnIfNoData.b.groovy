/* @data
@file('../../pfizer_xml_removeColumnIfNoData_data.xml')
*/
/* @props
    DPP_SuppressIfNoDataCsv=a|^|b|^|2|^|7\nd|^|e|^|2|^|8
    #document.dynamic.userdefined.ddp_Key=val
*/
import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;

logger = ExecutionUtil.getBaseLogger();

def suppressIfNoDataCsv = ExecutionUtil.getDynamicProcessProperty("DPP_SuppressIfNoDataCsv") ?: ""
def fieldsToSuppressArr = suppressIfNoDataCsv.split(/\s*\n\s*/)

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def root = new XmlParser().parse(is)

    if (fieldsToSuppressArr[0]) fieldsToSuppressArr.each {

        def suppressColumn = true
        fieldArr = it.split(/\s*\|\^\|\s*/)

        def tableIndex = fieldArr[2]
        def rowIndex = fieldArr[3]

        root.TableGroup.Table."**".findAll { it.@TableIndex == '2' }.collect { table ->

            table.DataRows.DataRow.FieldValue."**".findAll { 
                if (it.getClass() == groovy.util.Node) it.@ColumnIndex == rowIndex
            }.each { if (it.value()[0] != "NT") suppressColumn = false }

            println suppressColumn
            if (suppressColumn) {
                table.DataRows.DataRow.FieldValue."**".findAll { 
                    if (it.getClass() == groovy.util.Node) it.@ColumnIndex == rowIndex
                }.each { it.replaceNode {} }
            
                table.HeaderRows.HeaderRow.HeaderItem."**".findAll { 
                    if (it.getClass() == groovy.util.Node) it.@ColumnIndex == rowIndex
                }.each { it.replaceNode {} }
            }

        }
    }

    def outData = groovy.xml.XmlUtil.serialize(root).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim()

    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}


