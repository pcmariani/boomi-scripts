def startDate = "20200102 000000"
def endDate = "20200105 000000"


// import java.util.Calendar;
// import java.text.SimpleDateFormat

// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

// Calendar cStart = Calendar.getInstance();
// Calendar cEnd = Calendar.getInstance();

if (startDate.compareTo(endDate) > 0) {
    startDate_OUT = "ERROR: startDate $startDate is after endDate $endDate";
    endDate_OUT = "ERROR: startDate $startDate is after endDate $endDate";
}
else {
    startDate_OUT = startDate;
    endDate_OUT = endDate;
}

println "startDate_OUT " + startDate_OUT
println "endDate_OUT " + endDate_OUT
