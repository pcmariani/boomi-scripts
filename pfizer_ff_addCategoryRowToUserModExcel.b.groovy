/* @data
TaskId,Hierarchy Path,Project Name,Code,Activity Description,Is Milestone,Duration,Dependency Type,Predecessor 1 TaskId,Predecessor 1 Type ,Predecessor 1 Lag,Predecessor 2 TaskId,Predecessor 2 Type ,Predecessor 2 Lag,Predecessor 3 TaskId,Predecessor 3 Type ,Predecessor 3 Lag,Predecessor 4 TaskId,Predecessor 4 Type,Predecessor 4 Lag,Predecessor 5 TaskId,Predecessor 5 Type,Predecessor 5 Lag,Predecessor 6 TaskId,Predecessor 6 Type,Predecessor 6 Lag
1000,/1000,,BTx110,Discovery Collaboration,,225,,,,,,,,,,,,,,,,,,,
2000,/1000/2000,,BTx110,Engage Clinical on acceleration strategy for candidate,,0,,3000,SS,,,,,,,,,,,,,,,,
3000,/1000/3000,,BTx110,[BTx date] Lead Development,,0,,,,,,,,,,,,,,,,,,,
4000,/1000/4000,,BTx110,Stage III Molecular Assessment,,225,,,,,,,,,,,,,,,,,,,
5000,/1000/4000/5000,,BTx110,Make SSI constructs (2x SSI 2.0)- upto 4 constructs,,20,,3000,SS,,,,,,,,,,,,,,,,
6000,/1000/4000/6000,,BTx110,MAT Review of Full read-out of the data from Stage-II,,0,,5000,FS,10,,,,,,,,,,,,,,,
7000,/1000/4000/7000,,BTx110,Bioassay Transfer,,10,,6000,FS,-10,,,,,,,,,,,,,,,
8000,/1000/4000/8000,,BTx110,Transfer bioassay critical reagents to PS,,5,,7000,FS,,,,,,,,,,,,,,,,
9000,/1000/4000/9000,,BTx110,Transfer 5 vials to cell line development,,1,,6000,FS,4,20000,FS,,,,,,,,,,,,,
10000,/1000/4000/10000,,BTx110,TS0 Scale up expression (20L),,15,,9000,FS,5,,,,,,,,,,,,,,,
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

    def firstLine = true

    while ((line = reader.readLine()) != null ) {
        lineArr = line.split(",")

        if (firstLine == true) {
            for (j=0;j<lineArr.size();j++) {
                if (lineArr[j].startsWith("Pred")) outData.append("Predecessors,")
                else outData.append(",")
            }
            outData.append(LINE_SEPARATOR)
            outData.append(line + LINE_SEPARATOR)
            firstLine = false
        }
        outData.append(line + LINE_SEPARATOR)
    }

    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"))
    dataContext.storeStream(is, props)
}


