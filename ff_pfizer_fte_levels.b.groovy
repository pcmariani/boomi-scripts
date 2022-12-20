/* @data
1000:::hello:::3
*/
import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;
logger = ExecutionUtil.getBaseLogger();

def levelsArr = []
def levelPrev

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);
 
    def lineArr = is.text.split(/\s*:::\s*/)
    /* logger.warning("lineArr: " + lineArr) */
   
    def id = lineArr[0]
    def name = lineArr[1]
    def level = lineArr[2].toInteger()
    /* logger.warning("id: " + id + ", name: " + name + ", level: " + level) */
   
    if (level == 1) {
        levelsArr = []
    } else if (level < levelPrev) {
        levelsArr = levelsArr.take(level)
    }
 
    levelsArr[level-1] = "$id,$level,$name"
 
    def result = levelsArr.collect{ "$id,$it" }.join('\n')
 
    is = new ByteArrayInputStream(result.getBytes("UTF-8"));
    dataContext.storeStream(is, props);
   
    levelPrev = level
}
