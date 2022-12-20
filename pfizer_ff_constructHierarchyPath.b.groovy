/* @data
12,10,1,blah
12,11,2,blah
12,12,3,blah
*/
/* @props
    #DPP_Key=val
    #document.dynamic.userdefined.ddp_Key=val
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

    def taskId = ""
    def firstLine = true
    def hierarchyArr = []
    while ((line = reader.readLine()) != null ) {
      def lineArr = line.split(/\s*,\s*/)
      if (firstLine == true) {
        taskId = lineArr[0]
      }
      hierarchyArr.push(lineArr[1] + "000")
      firstLine = false
    }
    outData.append(taskId + ",/" + hierarchyArr.reverse().join("/"))

    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"))
    dataContext.storeStream(is, props)
}


