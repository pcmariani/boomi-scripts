/* @data
@file('../../pfizer_columnWidthsXML_data.xml')
*/

import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;

logger = ExecutionUtil.getBaseLogger();

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def root = new XmlParser().parse(is)

    root.TableGroup.Table.each { Table ->
        Table.children().each { HeaderAndDataRows ->
            HeaderAndDataRows.children().each { row ->
                
                def rawWidthsArr = []

                row.children().each { item ->
                    rawWidthsArr.push(Integer.valueOf(item.@ColumnWidth))
                }
                logger.warning("rawWidthsArr: " + rawWidthsArr.toString())

                def widthsArr = ToPercents(rawWidthsArr)

                row.children().eachWithIndex { item, ind ->
                    item.@ColumnWidth = widthsArr[ind]
                }
                logger.warning("   widthsArr: " + widthsArr.toString() + " Total: " + widthsArr.sum())
            }
        }
    }

    def outData = groovy.xml.XmlUtil.serialize(root).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim()
// @nothing
    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}


private def ToPercents(def rawWidthsArr) {

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
