import java.util.Properties;
import java.io.InputStream;
import groovy.json.JsonSlurper
import groovy.xml.MarkupBuilder;
import com.boomi.execution.ExecutionUtil;

logger = ExecutionUtil.getBaseLogger()

def NEWLINE = System.lineSeparator()
def IFS = /\|\^\|/  // Input Field Separator
def OFS = "|^|"     // Output Field Separator

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def designerConfig = props.getProperty("document.dynamic.userdefined.ddp_designerConfig")
    def designerConfigRoot = new JsonSlurper().parseText(designerConfig)
    def authorConfig = props.getProperty("document.dynamic.userdefined.ddp_authorConfig")
    def authorConfigRoot = new JsonSlurper().parseText(authorConfig)

    def outData = new StringBuilder()
    def reader = new BufferedReader(new InputStreamReader(is))

    def styles = props.getProperty("document.dynamic.userdefined.ddp_table-css")

    def tableTitle = props.getProperty("document.dynamic.userdefined.ddp_tableTitle") ?: "No Table Title"

    def tableStructureType = props.getProperty("document.dynamic.userdefined.ddp_tableStructureType") ?: "normal"
    def sqlParams = props.getProperty("document.dynamic.userdefined.ddp_sqlParams")
    def numHeaderRows = (props.getProperty("document.dynamic.userdefined.ddp_numHeaderRows") ?: "1") as int
    def numPivotKeyRows = (props.getProperty("document.dynamic.userdefined.ddp_numPivotKeyRows") ?: "0") as int
    def numGroupByCols = (props.getProperty("document.dynamic.userdefined.ddp_numGroupByCols") ?: "0") as int

    def ArrayList pivotKeysColumnsArr
    def ArrayList pivotKeysColumnsWidthsArr
    def ArrayList pivotGroupByColumnWidthsArr
    def ArrayList pivotKeysColumnsTableIndicesArr
    int pivotKeysColumnsTableMaxIndex = 1

    if (tableStructureType.startsWith("pivot")) {
        if (tableStructureType == "pivot-transposed") {
            def tmp = numHeaderRows
            numHeaderRows = numGroupByCols
            numGroupByCols = tmp
        }

        def pivotKeysColumns = props.getProperty("document.dynamic.userdefined.ddp_pivotKeysColumns") ?: ""
        pivotKeysColumnsArr = pivotKeysColumns.split(IFS)

        def pivotKeysColumnsWidths = props.getProperty("document.dynamic.userdefined.ddp_pivotKeysColumnsWidths") ?: ""
        pivotKeysColumnsWidthsArr = pivotKeysColumnsWidths.split(IFS)
println pivotKeysColumnsWidthsArr

        pivotGroupByColumnWidths = props.getProperty("document.dynamic.userdefined.ddp_pivotGroupByColumnWidths") ?: ""
println pivotGroupByColumnWidths
        pivotGroupByColumnWidthsArr = pivotGroupByColumnWidths.split(IFS)
println pivotGroupByColumnWidthsArr

        def pivotKeysColumnsTableIndices = props.getProperty("document.dynamic.userdefined.ddp_pivotKeysColumnsTableIndices") ?: ""
        pivotKeysColumnsTableIndicesArr = pivotKeysColumnsTableIndices.split(IFS)
        pivotKeysColumnsTableMaxIndex = (props.getProperty("document.dynamic.userdefined.ddp_pivotKeysColumnsTableMaxIndex") ?: "1") as int
    }

    def nonPivotGroupByColumn = 2

    // --- Put InputStream into array ---

    def dataArr = []
    def dataLineArr = []
    while ((line = reader.readLine()) != null ) {
        dataArr << line
        dataLineArr << line.split(/\s*$IFS\s*/)
    }

    // --- create idHeadersKeyArr as x coordinate in ids

    def idHeadersKeyArr = dataLineArr[0..numHeaderRows-1].transpose().collect{ it.join("") }
    // println idHeadersKeyArr

    // --- Build HTML ---

    def xmlWriter = new StringWriter()
    def xmlMarkup = new MarkupBuilder(xmlWriter)

    xmlMarkup.'html'() {
        // 'head'() { 'style'(styles) }
        'body'() {
            // 'form'() {
            //     'button'(type:"submit", "Refresh Table")
            // }
            // 'form'(action:"http://boomi-dev.pfizer.com:9090/ws/rest/dap/testing/package-deploy-dev", method:"get", target:"dummyframe", onsubmit:"this.reset();") {
            //     'button'(type:"submit", "Repackage & Deploy to DEV")
            // }
            // 'iframe'(name:"dummyframe", id:"dummyframe", scrolling:"no") { 'p'(){"hello"} }
            int colCounter = 0
            (1..pivotKeysColumnsTableMaxIndex).each { tableIndex ->
                'tableGroup'(tableIndex:tableIndex) {
                    'h3'(tableHeader:"yes", tableTitle)
                    'table'(border:1) {
                        int r = 0
                        def idsArr = []
                        dataArr.each { line ->
                            'tr'() {
                                def lineArr = line.split(/\s*$IFS\s*/)
                                int c = 0
                                lineArr.each{
                                    // def idString = getIdStringMD5(r, c, sqlParams, idHeadersKeyArr[c], numHeaderRows, lineArr)

                                    // if normal, add the html tags
                                    // if pivot, if the column is a groupBy column, add the html tags
                                    //           else only add the html tags if the tableIndex matches
                                    // if (r == 0 && pivotKeysColumnsTableIndicesArr[c-numGroupByCols] as int == tableIndex) {
                                    //     colCounter++
                                    // }
                                    if (tableStructureType == "normal"
                                        || c < numGroupByCols
                                        || pivotKeysColumnsTableIndicesArr[c-numGroupByCols] as int == tableIndex) {
                                        if (tableStructureType == "pivot" && r == 0) {
                                            def columnWidth
                                            if (c < numGroupByCols) {
                                                columnWidth = pivotGroupByColumnWidthsArr[c]
                                                // columnWidth = "9"
                                            }
                                            else {
                                                columnWidth = pivotKeysColumnsWidthsArr[colCounter]
                                                colCounter++
                                                println tableIndex + " " + colCounter + " " + it + " " + columnWidth
                                            }
                                            'th'(ColumnWidth:columnWidth, it)
                                            // 'th'(it)
                                        }
                                        else if (r < numHeaderRows) {
                                            // 'th'(id:idString, it)
                                            // println tableIndex + " " + colCounter + " " + it
                                            'th'(it)
                                        }
                                        else {
                                            // println tableIndex + " " + it
                                            // 'td'(id:idString, it)
                                            'td'(it)
                                        }
                                    }
                                    c++
                                }
                            }
                            r++
                            println "-----------------------------"
                        }
                        checkIdsArrHasDups(idsArr, props)
                    }
                }
            println "=========================="
            }
            'br'()
        }
    }

    is = new ByteArrayInputStream(xmlWriter.toString().getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}


def getIdStringMD5(r, c, sqlParams, headerKey, numHeaderRows, lineArr) {
    def idString = sqlParams + headerKey
    if (r < numHeaderRows) idString += lineArr[c]
    else idString += lineArr.join("")
    return "id" + idString.replaceAll(/\s/,"")//.md5()
}


def checkIdsArrHasDups(idsArr, props) {
    def idsArrCopy = []
    idsArr.each { idsArrCopy << it }
    if (idsArrCopy.size() == idsArr.unique().size()) {
        // println "NO DUPS"
        props.setProperty("document.dynamic.userdefined.ddp_htmlIdsHasDups", "false")
    }
    else {
        // println "**** DUPS :( --> " + idsArrCopy.size() + " ids, " + idsArr.size() + " unique ids"
        props.setProperty("document.dynamic.userdefined.ddp_htmlIdsHasDups", "true")
    }

  }
