/* @data
@file('/mnt/c/Users/peter_mariani/Documents/Sabre/kronos_sched_testdata.json')
*/
/* @props
    #DPP_Key=val
    #document.dynamic.userdefined.ddp_Key=val
*/

/*
 * This script breaks up the shifts into separate rows. It loops through the
 * activities and if there is an item where paid=false, if so it starts a new row.
 * 
 * Input: JSON response to query on Genesys Agent Schedules
 *
 * Output: 3 column csv -> userId,startDate,lengthMinutes
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
      int minutes = 0

      shift.activities.each { activity ->
        // get the startdate of the first item to be aggrigated on the new row
        if (minutes == 0) {
          startDate = activity.startDate
        }
        // add up the minutes until there is an activity where paid=false
        if (activity.paid != false) {
          minutes += activity.lengthMinutes
        }
        // if paid=false, create the row, unless there no minutes
        else if(minutes > 0) {
          outData.append("$userId,$startDate,$minutes" + LINE_SEPARATOR)
          minutes = 0
        }
      }
      // create the last row for that set of activities
      if (minutes > 0) {
        outData.append("$userId,$startDate,$minutes" + LINE_SEPARATOR)
      }
    }
  }
  println outData.toString()

  //@nothing
  is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"));
  dataContext.storeStream(is, props);
}

