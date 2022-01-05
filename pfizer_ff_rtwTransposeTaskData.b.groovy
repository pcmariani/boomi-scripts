/* @data
6000,1,BTx-ARD Portfolio Project Rep,0.20,
6000,2,BTx-BIT,0.20,
6000,5,BTx-PPL2,0.15,
*/
/* @props
    DPP_numRwtCols=20
    #document.dynamic.userdefined.ddp_Key=val
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

    def rwtArr = ["\"\""] * numRwtCols
    def taskId = ""

    def firstLine = true
    while ((line = reader.readLine()) != null ) {
        def lineArr = line.split(",")
        if (firstLine) {
            taskId = lineArr[0]
            firstLine = false
        }
        int rwtColIndex = lineArr[rwtSeqCol] as int
        rwtArr[rwtColIndex] = "\"" + lineArr[fteCol] + "\""
    }
    outData.append(taskId + "|" + rwtArr.join(",").replaceAll("null",""))

    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"))
    dataContext.storeStream(is, props)
}


