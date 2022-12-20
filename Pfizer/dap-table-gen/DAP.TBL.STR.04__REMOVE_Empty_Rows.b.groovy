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
    def numGroupByCols = (props.getProperty("document.dynamic.userdefined.ddp_numGroupByCols") ?: "0") as int
    def numHeaderRows = (props.getProperty("document.dynamic.userdefined.ddp_numHeaderRows") ?: "1") as int
    def pivotKeysSuppressIfNoData = props.getProperty("document.dynamic.userdefined.ddp_pivotKeysSuppressIfNoData")

    def pivotKeysSuppressIfNoDataArr = []
    if (pivotKeysSuppressIfNoData) {
        pivotKeysSuppressIfNoDataArr = pivotKeysSuppressIfNoData.split(/\s*$IFS\s*/).collect{it.toBoolean()}
    }

    def tableGroup = new XmlSlurper().parse(is)


    if (tableStructureType == "pivot-transposed") {
        tableGroup.table.tr[numHeaderRows..-1].eachWithIndex{ tr, r ->
            // println pivotKeysSuppressIfNoDataArr[r].toString() +" "+ (tr.td[numGroupByCols..-1].unique().join() == "NR") +" "+ (tr.td[numGroupByCols..-1])
            if (pivotKeysSuppressIfNoDataArr[r] && tr.td[numGroupByCols..-1].unique().join() == "NR") {
                tr.replaceNode{}
            }
        }
    }

    else if (tableStructureType == "pivot") {
        // Here col, column and set of THs and TDs are synonomous
        // Traverse the columns of the first row. Only the index colIndex is used.
        // If the column is a groupBy column, don't evaluate it.
        // If it's not a groupBy column (if it's a data column), remove the column
        // if the config value of "suppressIfNoData" is true AND there is no data
        // which means that all values for that column are "NR"
        def colOffset = 0
        tableGroup.table.eachWithIndex { table, tableIndex ->
            // println "--------------------- table: " + tableIndex + ", colOffset: " + colOffset
            (0..table.tr[0].th.size()).each { colIndex ->  // size is the size of the set of THs (number of Cols)
                def doSuppress = false
                if (colIndex >= numGroupByCols) {
                    doSuppress = pivotKeysSuppressIfNoDataArr[colIndex+colOffset-tableIndex-1]
                }
                // println colIndex +" "+ (colIndex+colOffset-tableIndex-1) +" "+ doSuppress +" "+ (table.tr.collect{it.td[colIndex]}.unique().join() == "NR")
                // if all the TDs in the column = "NR"
                if (doSuppress && table.tr.collect{it.td[colIndex]}.unique().join() == "NR") {
                    // remove the column (children includes THs and TDs)
                    table.tr.collect{it.children()[colIndex].replaceNode{}}
                    // println table.children().collect{it.children()[colIndex]}.toString() + " --- REMOVED"
                }
            }
            colOffset += table.tr[0].children().size()
        }
    }

    def outData = XmlUtil.serialize(tableGroup)
    // def outData = ""

    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"));
    dataContext.storeStream(is, props);

}
