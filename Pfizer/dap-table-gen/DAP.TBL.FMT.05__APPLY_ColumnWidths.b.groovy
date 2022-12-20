import java.util.Properties;
import java.io.InputStream;
import groovy.xml.XmlUtil
import com.boomi.execution.ExecutionUtil;
 
def NEWLINE = System.lineSeparator()
def IFS = /\|\^\|/  // Input Field Separator
def OFS = "|^|"     // Output Field Separator

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def tableStructureType = props.getProperty("document.dynamic.userdefined.ddp_tableStructureType") ?: "normal"
    def respectColumnWidths = (props.getProperty("document.dynamic.userdefined.ddp_respectColumnWidths") ?: "true").toBoolean()
  
    if (tableStructureType == "pivot") {
        def tableGroup = new XmlSlurper().parse(is)

        tableGroup.table.each { table ->
            def widthsArr = table.tr.th.findAll{it.@columnWidth != ""}.collect{
                if (it.@columnWidth == "null") 10
                else it.@columnWidth.toInteger()
            }
            def widthsPercentsArr = toPercents(widthsArr.collect{it as int})

            table.tr.th.findAll{it.@columnWidth != ""}.eachWithIndex{ th,c ->
                th.attributes().remove('columnWidth')
                if (respectColumnWidths) th.@style = "width:" + widthsPercentsArr[c].toString() + "%"
            }
            table.tr.td.findAll{it.@columnWidth != ""}.eachWithIndex{ td,c ->
                td.attributes().remove('columnWidth')
                if (respectColumnWidths) td.@style = "width:" + widthsPercentsArr[c].toString() + "%"
            }
        }

        def outData = XmlUtil.serialize(tableGroup)
        is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"));
    } 
    dataContext.storeStream(is, props);
}

def toPercents(rawWidthsArr) {
    // calculate percentage multiplier
    def multiplier = 100 / rawWidthsArr.sum()
    // create array with widths as percentages
    // can add up to more or less than 100 because of rounding
    def widthsArr = rawWidthsArr.collect { (it.multiply(multiplier) as double).round() }
    // logger.warning("widthsArr: " + widthsArr.toString())
    // calculate the difference between 100 and the sum of widths
    def difference = 100 - widthsArr.sum()
    // logger.warning("difference: " + difference)
    /* Distribute the difference among the highest/lowest widths */
    // create a sorted version of the widthsArr
    def widthsArrSorted = widthsArr.clone().sort()
    if (difference < 0) widthsArrSorted.reverse()
    // loops as many times as the difference between 100 and the sum of widths
    for (j=0;j<Math.abs(difference);j++) {
        // find the value of sorted array in the original arrray
        def width = widthsArr.indexOf(widthsArrSorted[j])
        // add one or subtract one from it
        if (difference > 0) widthsArr[width] = widthsArr[width] + 1
        else  widthsArr[width] = widthsArr[width] - 1
    }
    return widthsArr
}
