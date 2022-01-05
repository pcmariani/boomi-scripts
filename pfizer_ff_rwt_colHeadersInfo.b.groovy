/* @data
"TaskId","Hierarchy Path","Project Name","Code","Activity Description","Is Milestone","Duration","Dependency Type","Predecessor 1 TaskId","Predecessor 1 Type ","Predecessor 1 Lag","Predecessor 2 TaskId","Predecessor 2 Type ","Predecessor 2 Lag","Predecessor 3 TaskId","Predecessor 3 Type ","Predecessor 3 Lag","Predecessor 4 TaskId","Predecessor 4 Type","Predecessor 4 Lag","Predecessor 5 TaskId","Predecessor 5 Type","Predecessor 5 Lag","Predecessor 6 TaskId","Predecessor 6 Type","Predecessor 6 Lag","BTx-ARD Portfolio Project Rep","BTx-BIT","BTx-MSBC","BTx-PPL1","BTx-PPL2","BTx-PPL3","BTx-QCSM","BTx-VATT","BTx-BRD Portfolio Project Rep","BTx-CLD","BTx-CPD","BTx-CPPD","BTx-DS GMP Mfg","BTx-DS non-GMP Mfg","BTx-GTx PD","BTx-PPD","BTx-Outsourcing Specialist","BTx-Project Manager","BTx-PSTL","PTx-Tech Dev Personnel","BTx-Cell Based Development","BTx-Analytical Development","BTx-DP DCOE","BTx-DP Dev Mfg","GCS-Supply Chain Coordinator","GCS-Workchain Coordinator","LPEH-ARD Bioanalytical Development","LPEH-DS Dev & Tech Transfer"
"1000","/1000","","BTx110","Discovery Collaboration","","225","","","","","","","","","","","","","","","","","","","","0.2","","","","","","","","","","","","","","","","","","","","","","","","","","",""
"2000","/1000/2000","","BTx110","Engage Clinical on acceleration strategy for candidate","","0","","3000","SS","","","","","","","","","","","","","","","","","0.1","","","","","","","","","","","","","","","","","","","","","","","","","","",""
"3000","/1000/3000","","BTx110","[BTx date] Lead Development","","0","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""
"4000","/1000/4000","","BTx110","Stage III Molecular Assessment","","225","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""
"5000","/1000/4000/5000","","BTx110","Make SSI constructs (2x SSI 2.0)- upto 4 constructs","","20","","3000","SS","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""
"6000","/1000/4000/6000","","BTx110","MAT Review of Full read-out of the data from Stage-II","","0","","5000","FS","10","","","","","","","","","","","","","","","","0.2","0.2","","","0.15","","","","","","","","","","","","","","","","","","","","","","",""
"7000","/1000/4000/7000","","BTx110","Bioassay Transfer","","10","","6000","FS","-10","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""
"8000","/1000/4000/8000","","BTx110","Transfer bioassay critical reagents to PS","","5","","7000","FS","","","","","","","","","","","","","","","","","0.2","","","","0.03","","","","","","","","","","","","","","","","","","","","","","",""
"9000","/1000/4000/9000","","BTx110","Transfer 5 vials to cell headers development","","1","","6000","FS","4","20000","FS","","","","","","","","","","","","","","","0.1","","","0.5","","","","","","","","","","","","","","","","","","","","","","",""
"10000","/1000/4000/10000","","BTx110","TS0 Scale up expression (20L)","","15","","9000","FS","5","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""
*/

import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;

logger = ExecutionUtil.getBaseLogger()

def LINE_SEPARATOR = System.getProperty("line.separator")

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i)
    Properties props = dataContext.getProperties(i)

    reader = new BufferedReader(new InputStreamReader(is))
    outData = new StringBuffer()
    
    // get header row
    def headers = reader.readLine()

    // Set DPP_Headers
    ExecutionUtil.setDynamicProcessProperty("DPP_Headers", headers, false);
    logger.warning("DPP_Headers: " + headers)

    // split '","' delimited flat file
    headers = headers - ~/^"/ - ~/"$/   // remove initial and final "
    def headersArr = headers.split(/\"\s*,\s*\"/)   // split on ","
    logger.warning("headersArr.size(): " + headersArr.size().toString())

    // Set DPP_HierarchyCol
    hierarchyCol = headersArr.findIndexOf { it ==~ /Hierarchy.*/ }
    ExecutionUtil.setDynamicProcessProperty("DPP_HierarchyCol", hierarchyCol.toString(), false);
    logger.warning("DPP_HierarchyCol: " + hierarchyCol.toString())

    // loop through columns to determine start and end of regions
    def itemPrev = ""
    headersArr.eachWithIndex { item, j ->
        /* logger.warning(j + " " + item) */
        if (!itemPrev.contains("Pred") && item.contains("Pred")) {
            predecessorStartCol = j
        }
        if (itemPrev.contains("Pred") && !item.contains("Pred")) {
            predecessorEndCol = j-1
                rwtStartCol = j
        }
        itemPrev = item
    }

    // Set DPPs 
    ExecutionUtil.setDynamicProcessProperty("DPP_PredecessorStartCol", predecessorStartCol.toString(), false);
    logger.warning("DPP_PredecessorStartCol: " + predecessorStartCol.toString())
    ExecutionUtil.setDynamicProcessProperty("DPP_PredecessorEndCol", predecessorEndCol.toString(), false);
    logger.warning("DPP_PredecessorEndCol: " + predecessorEndCol.toString())
    ExecutionUtil.setDynamicProcessProperty("DPP_RwtStartCol", rwtStartCol.toString(), false);
    logger.warning("DPP_RwtStartCol: " + rwtStartCol.toString())

    // Write rest of lines to output
    while ((headers = reader.readLine()) != null ) {
        outData.append(headers + LINE_SEPARATOR) 
    }

    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"))
    dataContext.storeStream(is, props)
}


