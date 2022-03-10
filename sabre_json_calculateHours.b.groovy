/* @data
@file('/mnt/c/Users/peter_mariani/Documents/Sabre/kronos_sched_testdata_full.json')
*/
/* @props
    #DPP_Key=val
    #document.dynamic.userdefined.ddp_Key=val
*/
import java.util.Properties;
import java.io.InputStream;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput;
import groovy.json.JsonBuilder;
import com.boomi.execution.ExecutionUtil;
logger = ExecutionUtil.getBaseLogger()

def LINE_SEPARATOR = System.getProperty("line.separator")

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def root = new JsonSlurper().parseText(is.getText())
    def schedules = root.result.agentSchedules

    def outData = new StringBuffer()

    schedules.each { schedule ->
      def userId = schedule.user.id
      schedule.shifts.each { shift ->
        def startDate = shift.startDate
        outData.append(userId)
        int minutes = 0
        shift.activities.each { activity ->
          if (activity.paid != false) {
            minutes += activity.lengthMinutes
          }
          else {
            outData.append(",$startDate,$minutes$LINE_SEPARATOR$userId")
            minutes = 0
          }
        }
        outData.append(",$startDate,$minutes$LINE_SEPARATOR")
      }
    }
    println outData.toString()

    //@nothing
    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}

