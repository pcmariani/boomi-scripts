/* @data
2,,111
2,333,bbb,444
2,555,ccc,666
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

    def predArr = []
    while ((line = reader.readLine()) != null ) {
      def lineArr = line.split(/\s*,\s*/)
      if (firstLine == true) {
        taskId = lineArr[0]
      }
      firstLine = false

      int maxNumCommas = 3
      int numCommasInLine = line.replaceAll(/[^,]/,"").size()

      if (numCommasInLine <= maxNumCommas) {
        while (maxNumCommas - numCommasInLine > 0) {
          line += ","
          numCommasInLine++
        }
        predArr.push(line - ~/^\d+?,/)
      }
    }

    for (g=0;g<predArr.size();g++){
      println predArr[3]
    }
    outData.append(taskId + "," + predArr.reverse().join(","))

    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"))
    dataContext.storeStream(is, props)
}

