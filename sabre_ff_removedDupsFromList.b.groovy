/* @data
aaa,bbb,ccc,ccc,ddd,bbb,aaa,bbb,bbb,ccc
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
def DELIMITER = ","

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
  InputStream is = dataContext.getStream(i)
  Properties props = dataContext.getProperties(i)

  reader = new BufferedReader(new InputStreamReader(is))
  outData = new StringBuffer()

  while ((line = reader.readLine()) != null ) {
    ArrayList lineArr = line.split(/\s*${DELIMITER}\s*/)
    outData.append(lineArr.unique().join(DELIMITER) + LINE_SEPARATOR)
  }

  is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"))
  dataContext.storeStream(is, props)
}

