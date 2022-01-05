/* @data
   "9000","/1000/4000/9000","","BTx110","Transfer 5 vials to cell line development","","1","","6000","FS","4","20000","FS","","","","","","","","","","","","","","","0.1","","","0.5","","","","","","","","","","","","","","","","","","","","","","",""
 */
/*
   "1000","/1000","","BTx110","Discovery Collaboration","","225","","","","","","","","","","","","","","","","","","","","0.2","","","","","","","","","","","","","","","","","","","","","","","","","","",""
   "2000","/1000/2000","","BTx110","Engage Clinical on acceleration strategy for candidate","","0","","3000","SS","","","","","","","","","","","","","","","","","0.1","","","","","","","","","","","","","","","","","","","","","","","","","","",""
   "3000","/1000/3000","","BTx110","[BTx date] Lead Development","","0","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""
   "4000","/1000/4000","","BTx110","Stage III Molecular Assessment","","225","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""
   "5000","/1000/4000/5000","","BTx110","Make SSI constructs (2x SSI 2.0)- upto 4 constructs","","20","","3000","SS","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""
   "6000","/1000/4000/6000","","BTx110","MAT Review of Full read-out of the data from Stage-II","","0","","5000","FS","10","","","","","","","","","","","","","","","","0.2","0.2","","","0.15","","","","","","","","","","","","","","","","","","","","","","",""
   "7000","/1000/4000/7000","","BTx110","Bioassay Transfer","","10","","6000","FS","-10","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""
   "8000","/1000/4000/8000","","BTx110","Transfer bioassay critical reagents to PS","","5","","7000","FS","","","","","","","","","","","","","","","","","0.2","","","","0.03","","","","","","","","","","","","","","","","","","","","","","",""
   "10000","/1000/4000/10000","","BTx110","TS0 Scale up expression (20L)","","15","","9000","FS","5","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""
*/

/* @props
DPP_HierarchyCol=1
*/

import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;

logger = ExecutionUtil.getBaseLogger()

def LINE_SEPARATOR = System.getProperty("line.separator")

int hierarchyCol = ExecutionUtil.getDynamicProcessProperty("DPP_HierarchyCol") as int

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i)
    Properties props = dataContext.getProperties(i)

    reader = new BufferedReader(new InputStreamReader(is))
    outData = new StringBuffer()

    while ((line = reader.readLine()) != null ) {
        outData.append(line + LINE_SEPARATOR)

        // split '","' delimited flat file ensuring all fields are included in split
        line = line.replaceAll(/,""$/,",\"null\"")
        line = line - ~/^"/ - ~/"$/   // remove initial and final "
        def lineArr = line.split(/\"\s*,\s*\"/)   // split on ","
        /* logger.warning(lineArr.size() + " " + lineArr) */

        // ** construct hierarchy level records [taskId, relatedTaskId, level]
        // before splitting, remove the first char if not a digit
        // split on any non-digit char
        def hierarchyArrSize = lineArr[hierarchyCol].replaceAll(/^\D/,"").split(/\D/).size()
        // outline level = hierarchyArrSize
        props.setProperty("document.dynamic.userdefined.ddp_HierarchyLevel", hierarchyArrSize.toString());
        /* outData.append(hierarchyArrSize + LINE_SEPARATOR) */
/*         hierArr.eachWithIndex { item, j -> */
/*             outData.append(lineArr[taskIdCol] + "," + item + "," + (j+1) + LINE_SEPARATOR) */
/*         } */
    }
    
    outData = outData - ~/\n$/
    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"))
    dataContext.storeStream(is, props)
}


