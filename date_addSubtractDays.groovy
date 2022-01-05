def startDate = "2020-01-02T00:00:00"
def endDate = "2020-01-02T00:00:20"
def quarantineMessage = ""
def startDate_OUT = ""
def endDate_OUT = ""

// import java.util.Calendar;
import java.text.SimpleDateFormat

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

Calendar cStart = Calendar.getInstance();
Calendar cEnd = Calendar.getInstance();

if (startDate.compareTo(endDate) > 0) {
    startDate_OUT = "startDate $startDate is after endDate $endDate";
    endDate_OUT = "startDate $startDate is after endDate $endDate";
}
else {
    startDate_OUT = startDate;
    endDate_OUT = endDate;
}

println "startDate $startDate_OUT"
println "endDate $endDate_OUT"
// cStart.setTime(sdf.parse(startDate));
// cEnd.setTime(sdf.parse(endDate));

