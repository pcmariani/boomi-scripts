/* @data
111,01,aaaaaaa,0.03
222,02,bbbbbbb,0.06
333,03,ccccccc,0.00
*/
/* @props
DPP_numRwtCols=28
*/
import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;
 
logger = ExecutionUtil.getBaseLogger()
 
int rwtSeqCol = 1
int fteCol = 3
int numRwtCols = ExecutionUtil.getDynamicProcessProperty("DPP_numRwtCols") as int
 
for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i)
    Properties props = dataContext.getProperties(i)
 
    reader = new BufferedReader(new InputStreamReader(is))
    outData = new StringBuffer()
 
    def rwtArr = [""] * numRwtCols
    def taskId = ""
 
    def firstLine = true
    while ((line = reader.readLine()) != null ) {
        def lineArr = line.split(",")
        if (firstLine) {
            taskId = lineArr[0]
            firstLine = false
        }
        int rwtColIndex = (lineArr[rwtSeqCol] as int)-1
        println lineArr[fteCol].replaceAll(/^0*?\.0*$/,"")
        rwtArr[rwtColIndex] = lineArr[fteCol].replaceAll(/^0*?\.0*$/,"") // replace 0.00 values with blank
    }
    outData.append(taskId + "|" + rwtArr.join(/","/).replaceAll("null",""))
 
    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"))
    dataContext.storeStream(is, props)
}

