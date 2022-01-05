
/* @data
@file('../../pfizer_rowcolspan_data.htm')
*/

/* data
<html>
<body>
    <tablegroup>
        <h3 tableheader="yes"> Stability Data for Drug Product 20L161N010(EL7116) Stored at 25°C / 60 % Relative Humidity</h3>
        <table border="1" parent_tablegroup="1" seq="1" width="100%">
            <tr bgcolor="#85C1E9">
                <th>Time (Months)</th>
                <th>Appearance</th>
                <th>Appearance</th>
                <th/>
                <th>UV Spectroscopy</th>
                <th>iCE</th>
                <th>iCE</th>
                <th>iCE</th>
            </tr>
            <tr bgcolor="#85C1E9">
                <th/>
                <th>Clarity</th>
                <th>Color</th>
                <th>pH</th>
                <th>Concentration</th>
                <th>Acidic Species</th>
                <th>Main Peak</th>
                <th>Basic Species</th>
            </tr>
            <tr bgcolor="#85C1E9">
                <th>Clinical Acceptance Criteria</th>
                <th>Report Results</th>
                <th>&amp;lt;=B9</th>
                <th/>
                <th/>
                <th>Report Results</th>
                <th>Report Results</th>
                <th>Report Results</th>
            </tr>
            <tr>
                <td>0.0</td>
                <td>9</td>
                <td>&amp;lt;=B9</td>
                <td>5.6</td>
                <td>41.2</td>
                <td>49.2</td>
                <td>40.5</td>
                <td>10.3</td>
            </tr>
            <tr>
                <td>1.0</td>
                <td>9</td>
                <td>&amp;lt;=B8</td>
                <td>5.7</td>
                <td>44.2</td>
                <td>47.9</td>
                <td>41.5</td>
                <td>10.5</td>
            </tr>
        </table>
        <br/>
        <table border="1" parent_tablegroup="1" seq="2" width="100%">
            <tr bgcolor="#85C1E9">
                <th/>
                <th>SE-HPLC</th>
                <th>SE-HPLC</th>
                <th>CGE (reducing)</th>
                <th>CGE (non-reducing)</th>
                <th>CGE (non-reducing)</th>
                <th>Cell Based Assay</th>
            </tr>
            <tr bgcolor="#85C1E9">
                <th>Time (Months)</th>
                <th>Monomer</th>
                <th>HMMS</th>
                <th>HC + LC</th>
                <th>Fragments</th>
                <th>Intact IgG</th>
                <th>Relative Potency</th>
            </tr>
            <tr bgcolor="#85C1E9">
                <th>Clinical Acceptance Criteria</th>
                <th>Result &amp;gt;= 92.0 %</th>
                <th>Result &amp;lt;= 4.0 %</th>
                <th>Result &amp;gt;= 90.0 %</th>
                <th>Result &amp;lt;= 5.0 %</th>
                <th>Result &amp;gt;= 92.0 %</th>
                <th>50.0  to  150.0 %</th>
            </tr>
            <tr>
                <td>0.0</td>
                <td>99.9</td>
                <td>NMT 0.2</td>
                <td>99.0</td>
                <td>0.5</td>
                <td>99.5</td>
                <td>103</td>
            </tr>
            <tr>
                <td>1.0</td>
                <td>99.8</td>
                <td>NMT 0.2</td>
                <td>98.6</td>
                <td>0.5</td>
                <td>99.5</td>
                <td>99999</td>
            </tr>
        </table>
        <br/>
    </tablegroup>
</body>
</html>
*/

import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;

logger = ExecutionUtil.getBaseLogger();

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def root = new XmlSlurper().parse(is)

    root.'**'.findAll() { node -> node.name() == "table" }.each() { table ->
    
        /*
        * --- RowSpan ---
        */

        def rowSpanMap = [:]
    
        /* 1st pass - 1st (assay) row & 2nd (parameter) row */
        table.tr[0..1].eachWithIndex() { tr, r ->
            tr.children().eachWithIndex() { th, h ->
                if (!rowSpanMap."$h") {
                    if (th.text() == "") rowSpanMap."$h" = ["ASSAY_OR_PARAM_IS_EMPTY",th.text()]
                    else rowSpanMap."$h" = ["IGNORE",th.text()]
                }
                else {
                    if (rowSpanMap."$h"[0] == "ASSAY_OR_PARAM_IS_EMPTY") rowSpanMap."$h"[1] = th.text()
                    if (th.text() == "") rowSpanMap."$h" = ["ASSAY_OR_PARAM_IS_EMPTY",rowSpanMap."$h"[1]]
                }
            }
        }
        // logger.warning(rowSpanMap)

        /* 2nd pass - 1st row */
        table.tr[0].eachWithIndex() { tr, r ->
            tr.children().eachWithIndex() { th, h ->
                // logger.warning(h + " " + th + " " + rowSpanMap."$h")
                if (rowSpanMap."$h"[0] == "ASSAY_OR_PARAM_IS_EMPTY") { 
                    // logger.warning(h + " " + rowSpanMap."$h")
                    if (th.text()) th.value = rowSpanMap."$h"[1]
                    else th << rowSpanMap."$h"[1]
                    th.@rowSpan = "2"
                }
            }
        }
        // logger.warning(rowSpanMap)

        /* 3rd pass - 2nd row */
        table.tr[1].eachWithIndex() { tr, r ->
            tr.children().eachWithIndex() { th, h ->
                if (rowSpanMap."$h"[0] == "ASSAY_OR_PARAM_IS_EMPTY") th.replaceNode {}
            }
        }
        // logger.warning(rowSpanMap)



        /*
        * --- ColSpan ---
        */
       
        def colSpanMap = [:]
        def prevAssay = ""
        def prevWidth = 0
        def assayIndex = 1

        /* 1st pass - 1st (assay) row */
        table.tr[0].children().eachWithIndex() { assay, j ->
            def width = 0
            if (assay.@style.text()) width = assay.@style.text().replaceAll(/[^\d]/,"") as int
            // logger.warning(width)

            if (assay.text() == prevAssay) {
                assayIndex++
                width = prevWidth + width
            }
            else {
                assayIndex = 1
                widthPrev = 0
            }

            if (assay.text()) colSpanMap."${assay}" = [assayIndex,width]
            // logger.warning(j + " " + assay + " " + prevAssay + " " + assayIndex + " " + width)

            prevAssay = assay.text()
            prevWidth = width
        }
        // logger.warning(colSpanMap)

        prevAssay = ""

        /* 2st pass - 1st (assay) row */
        table.tr[0].children().eachWithIndex() { assay, j ->
            def colSpanVal = colSpanMap."${assay}"[0]
            if (colSpanVal > 1) {
                if (assay.text() != prevAssay) {
                    def colWidth = colSpanMap."${assay}"[1]
                    assay.@colspan = colSpanVal.toString()
                    assay.@style = "width:"+colWidth.toString()+"%"
                }
                else assay.replaceNode{}
                // logger.warning(j + " " + assay + " " + colSpanMap."${assay}")
            }
            prevAssay = assay.text()
        }
    }

    def outData = groovy.xml.XmlUtil.serialize(root).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim()
    
    // File fileOut = new File("../../pfizer_rowcolspan_output.htm")
    // fileOut.write outData

    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"));
    dataContext.storeStream(is, props);

}
