import java.time.ZonedDateTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;

def inputDate = "2022-02-19"
def inputShiftMinutes = 480
def inputShiftStartTime = "09:00"

def dateFormat = "yyyy-MM-dd'T'HH:mm"
def targetDateFormat = "yyyyMMdd"
def targetTimeFormat = "HHmm"

if (inputDate) {
  inputDate += "T" + inputShiftStartTime;

  def formatter = DateTimeFormatter.ofPattern(dateFormat)
  def formatterD = DateTimeFormatter.ofPattern(targetDateFormat)
  def formatterT = DateTimeFormatter.ofPattern(targetTimeFormat)

  def startDateUTC = LocalDateTime.parse(inputDate, formatter).atZone(ZoneOffset.UTC) 
  def endShiftTime = startDateUTC.plusMinutes(inputShiftMinutes)

  def shiftStartTime = formatterT.format(startDateUTC)
  def shiftEndTime = formatterT.format(endShiftTime)

  out_startDate = formatterD.format(startDateUTC)
  out_shiftStartEndTime = shiftStartTime + shiftEndTime

  println "out_startDate: " + out_startDate 
  println "out_shiftStartEndTime: " + out_shiftStartEndTime 
}
