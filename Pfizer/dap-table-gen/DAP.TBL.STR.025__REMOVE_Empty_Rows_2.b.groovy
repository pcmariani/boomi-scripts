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

    def outData = new StringBuilder()
    def reader = new BufferedReader(new InputStreamReader(is))

    def numGroupByCols = (props.getProperty("document.dynamic.userdefined.ddp_numGroupByCols") ?: "0") as int
    def numHeaderRows = (props.getProperty("document.dynamic.userdefined.ddp_numHeaderRows") ?: "1") as int
    def pivotKeysSuppressIfNoData = props.getProperty("document.dynamic.userdefined.ddp_pivotKeysSuppressIfNoData")

    if (pivotKeysSuppressIfNoData) {
        def pivotKeysSuppressIfNoDataArr = pivotKeysSuppressIfNoData.split(/\s*$IFS\s*/).collect{it.toBoolean()}

        /* --- Put data into array --- */

        def dataArr = []
        while ((line = reader.readLine()) != null ) {
            ArrayList lineArr = line.split(/\s*$IFS\s*/)
            dataArr << lineArr
        }

        /* --- Romove Columns if all values are "NR" --- */
        def dataWidth = dataArr[0].size() - 1
        def removeColArr = [false] * dataWidth
        (0..dataWidth).each { w ->
            if (dataArr.collect{it[w]}[numHeaderRows..-1].unique().join() == "NR") {
                removeColArr[w] = true
            }
        }
        // println removeColArr

        def numColumns = 0
        def newDataArr = []
        dataArr.eachWithIndex { row, r ->
            def newLineArr = []
            row.eachWithIndex { col, c ->
                def suppressIfNoData = pivotKeysSuppressIfNoDataArr[c-numGroupByCols]
                if (   c < numGroupByCols  // if a groupBy col
                    || !suppressIfNoData  // if config says false
                    || (suppressIfNoData && removeColArr[c] != true)) {  // if config says true and all NR
                    newLineArr << col
                    if (r == 0) numColumns++
                }
            }
            newDataArr << newLineArr
        }
        // println newDataArr

        /* --- Romove rows if all values are "NR" --- */

        newDataArr.eachWithIndex { row, r ->
            def rowCopy = []
            row.each{rowCopy << it}  // need to copy row because the unique function mutates the row
            def suppressIfNoData = pivotKeysSuppressIfNoDataArr[r-numHeaderRows]
            if (   r < numHeaderRows  // if a groupBy col    s
                || !suppressIfNoData  // if config says falsea
                || (suppressIfNoData && row[numGroupByCols..-1].unique().join() != "NR")) {  // if config says true and all NR
                outData.append(rowCopy.join(OFS) + NEWLINE)
            }
        }

        // outData = ""

        is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"));

        def numPivotKeyColumns = numColumns - numGroupByCols
        props.setProperty("document.dynamic.userdefined.ddp_numPivotKeyColumns", numPivotKeyColumns as String)
        props.setProperty("document.dynamic.userdefined.ddp_numColumns", numColumns as String)
    }

    dataContext.storeStream(is, props);
}
