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

    ArrayList pivotKeysColumnsArr
    ArrayList pivotKeysColumnsWidthsArr
    ArrayList pivotGroupByColumnWidthsArr
    ArrayList pivotKeysColumnsTableIndicesArr
    int pivotKeysColumnsTableMaxIndex = 1

    if (tableStructureType.startsWith("pivot")) {
        // if (tableStructureType == "pivot-transposed") {
        //     def tmp = numHeaderRows
        //     numHeaderRows = numGroupByCols
        //     numGroupByCols = tmp
        // }

        def pivotKeysColumns = props.getProperty("document.dynamic.userdefined.ddp_pivotKeysColumns") ?: ""
        pivotKeysColumnsArr = pivotKeysColumns.split(IFS)

        def pivotKeysColumnsWidths = props.getProperty("document.dynamic.userdefined.ddp_pivotKeysColumnsWidths") ?: ""
        pivotKeysColumnsWidthsArr = pivotKeysColumnsWidths.split(IFS)
        // println pivotKeysColumnsWidthsArr
        def pivotGroupByColumnWidths = props.getProperty("document.dynamic.userdefined.ddp_pivotGroupByColumnWidths") ?: ""
        pivotGroupByColumnWidthsArr = pivotGroupByColumnWidths.split(IFS)
        // println pivotGroupByColumnWidthsArr
        def pivotKeysColumnsTableIndices = props.getProperty("document.dynamic.userdefined.ddp_pivotKeysColumnsTableIndices") ?: ""
        pivotKeysColumnsTableIndicesArr = pivotKeysColumnsTableIndices.split(IFS)
        pivotKeysColumnsTableMaxIndex = (props.getProperty("document.dynamic.userdefined.ddp_pivotKeysColumnsTableMaxIndex") ?: "1") as int
    }

    // --- Put InputStream into array ---

    def dataArr = []
    while ((line = reader.readLine()) != null ) {
        dataArr << line.split(/\s*$IFS\s*/)
    }

    // --- create idHeadersKeyArr as x coordinate in ids

    def idHeadersKeyArr = dataArr[0..numHeaderRows-1].transpose().collect{ it.join("") }
    // println idHeadersKeyArr

    // --- Build HTML ---

    def xmlWriter = new StringWriter()
    def xmlMarkup = new MarkupBuilder(xmlWriter)

    xmlMarkup.'tableGroup'() {
        'h3'(tableHeader:"yes", tableTitle)
        (1..pivotKeysColumnsTableMaxIndex).each { tableIndex ->
            'table'(tableIndex:tableIndex, border:1) {
                int r = 0
                def idsArr = []
                dataArr.each { lineArr ->
                    'tr'() {
                        int colCounter = 0
                        lineArr.eachWithIndex { el, c ->
                            def idString = getIdStringMD5(r, c, sqlParams, idHeadersKeyArr[c], numHeaderRows, lineArr)
                            def columnWidth
                            if (tableStructureType == "pivot" && (r == 0 || r == numHeaderRows)) {
                                if (c < numGroupByCols) {
                                    columnWidth = pivotGroupByColumnWidthsArr[c]
                                }
                                else {
                                    columnWidth = pivotKeysColumnsWidthsArr[colCounter]
                                    colCounter++
                                }
                                // println tableIndex + " " + colCounter + " " + el + " " + columnWidth
                            }
                            // if normal, add the html tags
                            // if pivot, if the column is a groupBy column, add the html tags
                            //           else only add the html tags if the tableIndex matches
                            if (tableStructureType == "normal"
                                || c < numGroupByCols
                                || pivotKeysColumnsTableIndicesArr[c-numGroupByCols] as int == tableIndex) {
                                if (tableStructureType == "pivot" && r == 0) {
                                    // 'th'('columnWidth':columnWidth, id:idString, el)
                                    'th'('columnWidth':columnWidth, el)
                                }
                                else if (r < numHeaderRows) {
                                    // 'th'(id:idString, el)
                                    'th'(el)
                                }
                                else if (tableStructureType == "pivot" && r == numHeaderRows) {
                                    // 'td'('columnWidth':columnWidth, id:idString, el)
                                    'td'('columnWidth':columnWidth, el)
                                }
                                else {
                                    // 'td'(id:idString, el)
                                    'td'(el)
                                }
                            }
                        }
                        // println "-----------------------------"
                    }
                    r++
                }
                checkIdsArrHasDups(idsArr, props)
            }
            'br'()
        }
        // println "=========================="
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

