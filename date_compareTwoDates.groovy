// def inputDate = "20200430 124000.000"
// def outputDate = ""
def startDate = "2020-01-01T00:00:00"
def endDate = "2020-01-02T00:00:00"

/*
 * to be used in map
 * Add or subtract days
 */

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat

SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss.SSS");
// the initial date format is "yyyyMMdd HHmmss.SSS" despite the original data being in the format "yyyy-MM-dd" 
// because of the special internal date format when using a map in boomi
SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
// is the date format you want the output to be in
Calendar c = Calendar.getInstance();
c.setTime(sdf.parse(inputDate));
c.add(Calendar.DATE, 3);
// number of days to add, 30 days in this case
outputDate= sdf2.format(c.getTime());
// println outputDate
 
