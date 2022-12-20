/* @data
"1000","/1000","","BTx110","Discovery Collaboration","","225","","","","","","","","","","","","","","","","","","","","0.2","","","","","","","","","","","","","","","","","","","","","","","","","","",""
"2000","/1000/2000","","BTx110","Engage Clinical on acceleration strategy for candidate","","0","","3000","SS","","","","","","","","","","","","","","","","","0.1","","","","","","","","","","","","","","","","","","","","","","","","","","",""
"3000","/1000/3000","","BTx110","[BTx date] Lead Development","","0","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""
"4000","/1000/4000","","BTx110","Stage III Molecular Assessment","","225","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""
"5000","/5000","","BTx110","Make SSI constructs (2x SSI 2.0)- upto 4 constructs","","20","","3000","SS","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""
"6000","/5000/4000/6000","","BTx110","MAT Review of Full read-out of the data from Stage-II","","0","","5000","FS","10","","","","","","","","","","","","","","","","0.2","0.2","","","0.15","","","","","","","","","","","","","","","","","","","","","","",""
"7000","/5000/4000/7000","","BTx110","Bioassay Transfer","","10","","6000","FS","-10","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""
"8000","/5000/4000/8000","","BTx110","Transfer bioassay critical reagents to PS","","5","","7000","FS","","","","","","","","","","","","","","","","","0.2","","","","0.03","","","","","","","","","","","","","","","","","","","","","","",""
"9000","/5000/4000/9000","","BTx110","Transfer 5 vials to cell line development","","1","","6000","FS","4","20000","FS","","","","","","","","","","","","","","","0.1","","","0.5","","","","","","","","","","","","","","","","","","","","","","",""
"10000","/1000/4000/10000","","BTx110","TS0 Scale up expression (20L)","","15","","9000","FS","5","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""
 */
/*
*/

/* @props
DPP_HierarchyCol=1
*/

import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;

logger = ExecutionUtil.getBaseLogger()

  StringBuffer outData = new StringBuffer()
  def LINE_SEPARATOR = System.getProperty("line.separator")

  /* int hierarchyCol = ExecutionUtil.getDynamicProcessProperty("DPP_HierarchyCol") as int */

  def name = ""

  for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i)
    Properties props = dataContext.getProperties(i)

    reader = new BufferedReader(new InputStreamReader(is))

    while ((line = reader.readLine()) != null ) {

        line = line.replaceAll(/,""$/,",\"null\"") - ~/^"/ - ~/"$/
        def lineArr = line.split(/\"\s*,\s*\"/)
        /* logger.warning(lineArr.size() + " " + lineArr) */

        def id = lineArr[0]
        def path = lineArr[1]
        /* name = lineArr[4] */

        def hierArr = path.split("/")
  
        if (hierArr.size() == 2) name = lineArr[4]
        for (int j = 1; j < hierArr.size(); j++) {
          outData.append(id + "," + hierArr[j] + "," + j + "," + name + LINE_SEPARATOR)
        }
        outData.append(LINE_SEPARATOR)
    }
    
    String outDataStr = outData.toString() - ~/\n$/
    is = new ByteArrayInputStream(outDataStr.getBytes("UTF-8"))
    dataContext.storeStream(is, props)
}




