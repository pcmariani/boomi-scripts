import java.util.Properties;
import java.io.InputStream;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import com.boomi.execution.ExecutionUtil;
import java.text.DecimalFormat;

logger = ExecutionUtil.getBaseLogger();

def NEWLINE = System.lineSeparator()
def IFS = /\|\^\|/  // Input Field Separator
def OFS = "|^|"     // Output Field Separator

InputStream is
Properties props
def designerConfigRoot
def authorConfigRoot
int tableIndex
int numPivotKeyColumns
int numHeaderRows
int numColumns
int numPivotKeyRows
int numGroupByCols
def pivotColsArr = []
def numPivotCols = 0
ArrayList pivotKeysCombinedLabelArr
ArrayList pivotKeysColumnsArr
def columnsListArrMap = [:]
def tableMap = [:]

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    is = dataContext.getStream(i)
    props = dataContext.getProperties(i)

    def designerConfig = props.getProperty("document.dynamic.userdefined.ddp_designerConfig")
    designerConfigRoot = designerConfigRoot ?: new JsonSlurper().parseText(designerConfig)
    def authorConfig = props.getProperty("document.dynamic.userdefined.ddp_authorConfig")
    authorConfigRoot = authorConfigRoot ?: new JsonSlurper().parseText(authorConfig)

    def sqlStatementId = props.getProperty("document.dynamic.userdefined.ddp_sqlStatementId")

    def sqlColumnsList = props.getProperty("document.dynamic.userdefined.ddp_sqlColumnsList")
    columnsListArrMap[sqlStatementId] = sqlColumnsList.split(/\s*$IFS\s*/) as ArrayList

    tableIndex = (props.getProperty("document.dynamic.userdefined.ddp_tableIndex") ?: "0") as int
    numColumns = (props.getProperty("document.dynamic.userdefined.ddp_numColumns") ?: "0") as int
    numHeaderRows = (props.getProperty("document.dynamic.userdefined.ddp_numHeaderRows") ?: "0") as int
    numPivotKeyColumns = (props.getProperty("document.dynamic.userdefined.ddp_numPivotKeyColumns") ?: "0") as int
    numPivotKeyRows = (props.getProperty("document.dynamic.userdefined.ddp_numPivotKeyRows") ?: "0") as int
    numGroupByCols = (props.getProperty("document.dynamic.userdefined.ddp_numGroupByCols") ?: "0") as int

    def pivotKeysCombinedLabel = props.getProperty("document.dynamic.userdefined.ddp_pivotKeysCombinedLabel") ?: ""
    if (pivotKeysCombinedLabel) pivotKeysCombinedLabelArr = pivotKeysCombinedLabel.split("___")
    def pivotKeysColumns = props.getProperty("document.dynamic.userdefined.ddp_pivotKeysColumns") ?: ""
    if (pivotKeysColumns) pivotKeysColumnsArr = pivotKeysColumns.split(/\s*$IFS\s*/)

    def reader = new BufferedReader(new InputStreamReader(is))

    def tableLineArr = []
    while ((line = reader.readLine()) != null ) {
        tableLineArr << line.split(/\s*$IFS\s*/)
    }
    tableMap[sqlStatementId] = tableLineArr
}

designerConfigRoot.rowSets.eachWithIndex { rowSet, rowSetIndex ->
    def outData = new StringBuilder()
    switch(rowSet.rowType) {

        case "column-names":
            def sourceId = rowSet.source.id
            def columnsListArr = columnsListArrMap[sourceId]
            def newColumnsListArr = []
            columnsListArr.each {newColumnsListArr << it}
            def vCols = authorConfigRoot.tables[0].sources.find{it.id==sourceId}?.virtualColumns
            if (vCols) {
                vCols.sort{ a, b ->  // reverse sort the vCols by insertAfterColumnName
                    columnsListArr.indexOf(b.insertAfterColumnName) <=> columnsListArr.indexOf(a.insertAfterColumnName)
                }.each {
                    def vColRowKeyColumnName = it.rowKeyColumnName
                    def vColRowKeyColIndex = columnsListArr.indexOf(vColRowKeyColumnName)
                    // println vColRowKeyColumnName + " " + vColRowKeyColIndex
                    int vColInsertAfterColIndex = columnsListArr.indexOf(it.insertAfterColumnName)
                    // println it.insertAfterColumnName + " " + vColInsertAfterColIndex
                    newColumnsListArr.add(vColInsertAfterColIndex + 1, it.name)
                }
            }
            outData.append(newColumnsListArr.join(OFS))
            break;

        case "data":
            def sourceId = rowSet.source.id
            def columnsListArr = columnsListArrMap[sourceId]
            def vCols = authorConfigRoot.tables[0].sources.find{it.id==sourceId}?.virtualColumns
            tableMap[sourceId].each { lineArr ->
                def newLineArr = []
                lineArr.eachWithIndex { item, c ->
                    def modifications = rowSet.modifications.findAll{it.field == columnsListArr[c]}
                    if (modifications) {
                        rowSet.modifications.findAll{it.field == columnsListArr[c]}.each {
                            // println c + " " + columnsListArr[c] + " " + item + " " + getValueWithModifications(it.stringReplacements, it.decimalFormat, item)
                            newLineArr[c] = getValueWithModifications(it.stringReplacements, it.decimalFormat, item)
                        }
                    }
                    else newLineArr[c] = item
                }
                if (vCols) {
                    vCols.sort{ a, b ->  // reverse sort the vCols by insertAfterColumnName
                        columnsListArr.indexOf(b.insertAfterColumnName) <=> columnsListArr.indexOf(a.insertAfterColumnName)
                    }.each {
                        def vColRowKeyColumnName = it.rowKeyColumnName
                        def vColRowKeyColIndex = columnsListArr.indexOf(vColRowKeyColumnName)
                        // println vColRowKeyColumnName + " " + vColRowKeyColIndex
                        int vColInsertAfterColIndex = columnsListArr.indexOf(it.insertAfterColumnName)
                        // println it.insertAfterColumnName + " " + vColInsertAfterColIndex
                        def vColDataKey = lineArr[vColRowKeyColIndex]
                        def vColDataValue = it.data.find{it.rowKey==vColDataKey}?.value ?: "---"
                        // println vColDataKey + " " + vColDataValue
                        // println newLineArr.join(",") + " - " + (vColDataValue)
                        newLineArr.add(vColInsertAfterColIndex + 1, vColDataValue)
                    }
                }
                outData.append(newLineArr.join(OFS) + NEWLINE)
            }
            break;

        case "separator":
            if (!numColumns){
                numColumns = 0
                if (rowSet.numColumns.type == "text") numColumns = rowSet.numColumns.value
                else if (rowSet.numColumns.type == "sql") numColumns = columnsListArrMap[rowSet.numColumns.id].size()
                int numVCols = authorConfigRoot.tables[0].sources.find{it.id==sourceId}?.virtualColumns.size() ?: 0
                numColumns += numVCols
            }
            outData = ([rowSet.value] * numColumns).join(OFS)
            break;

        case "pivot-column-names":
            def lineOutArr = []
            def sourceType = rowSet.source.type
            def sourceId = rowSet.source.id
            def sourceIndex = rowSet.source.index
            def pivotCombinedGroupByKeyArr = []

            // Only possible sourceTypes are list or pivot-result <-- that's old info, there is no pivot-result
            // Below is only applicable for a filteredPivotColumnNames list
            if (sourceType == "list" && sourceId == "filteredPivotColumnNames") {

                if (rowSet.groupByColumnLabel && rowSet.groupByColumnLabel != "") {
                    pivotCombinedGroupByKeyArr = [rowSet.groupByColumnLabel] * numGroupByCols
                }
                else if (rowSet.groupBy) {
                    pivotCombinedGroupByKeyArr = rowSet.groupBy.collect{ it.value }
                }

                def pivotColumnNamesItemKeys = authorConfigRoot.sources.find{it.type == sourceType && it.id == sourceId}.items.keys
                pivotColumnNamesItemKeys.each {
                    def mapKey = it.name.join("___").toUpperCase()
                    def mapVal = it[sourceIndex].label

                    if (rowSet.appendToValue) {
                        def appendVal = ""
                        def doAppend = true
                        rowSet.appendToValue.each {
                            def appendValItem = ""

                            if (it.type == "text") appendValItem = it.value
                            else if (it.type == "sql") {
                                def table = tableMap[it.id]
                                def columnsListArr = columnsListArrMap[it.id]
                                for (int n = 0; n < table.size(); n++) {
                                    def lineArr = table[n]
                                    def pivotCombinedKey = pivotKeysCombinedLabelArr.collect{lineArr[columnsListArr.indexOf(it)]}.join("___").toUpperCase()
                                    if (pivotCombinedKey == mapKey) {
                                        appendValItem = lineArr[columnsListArr.indexOf(it.field)]
                                        break
                                    }
                                }
                            }
                            if (!appendValItem || appendValItem == "null" || appendValItem in rowSet.appendToValueNullChars) {
                                doAppend = false
                            }
                            else appendVal += appendValItem
                        }
                        if (appendVal && doAppend) mapVal += " " + appendVal
                    }
                    lineOutArr << mapVal
                }
            }

            outData.append((pivotCombinedGroupByKeyArr + lineOutArr).join(OFS))
            break;

        case "pivot-data":
            def sourceId = rowSet.source.id
            def tableLineDataArr = tableMap[sourceId]
            def columnsListArr = columnsListArrMap[sourceId]
            
            tableMap[sourceId].each { lineArr ->
                def pivotCombinedKey = pivotKeysCombinedLabelArr.collect{lineArr[columnsListArr.indexOf(it)]}.join("___").toUpperCase()

                def pivotCombinedGroupByKey = ""
                def pivotCombinedGroupByKeyArr = []

                if (rowSet.groupByColumnLabel && rowSet.groupByColumnLabel != "") {
                    pivotCombinedGroupByKeyArr = [rowSet.groupByColumnLabel] * numGroupByCols
                }
                else if (rowSet.groupBy) {
                    pivotCombinedGroupByKeyArr = rowSet.groupBy.collect{ groupBy ->
                        def groupByValue = ""
                        if (groupBy.type == "text") groupByValue = groupBy.value
                        else if (groupBy.type == "sql") groupByValue = lineArr[columnsListArr.indexOf(groupBy.field)]
                        else if (groupBy.type == "list") groupByValue = lineArr[columnsListArr.indexOf(groupBy.field)].toUpperCase()

                        groupByValue = getValueWithModifications(groupBy.stringReplacements, groupBy.decimalFormat, groupByValue)
                        getValueWithAppendToValue(lineArr, columnsListArr, groupBy, groupByValue)
                    }
                }

                if (pivotCombinedGroupByKeyArr.size() < numGroupByCols) {
                    pivotCombinedGroupByKeyArr = [pivotCombinedGroupByKeyArr[0]] * numGroupByCols
                }
                pivotCombinedGroupByKey = pivotCombinedGroupByKeyArr.join("___")

                def pivotValue = ""
                if (rowSet.source.type == "text") {
                    pivotValue = rowSet.source.value
                }
                else if (rowSet.source.type == "sql") {
                    pivotValue = lineArr[columnsListArr.indexOf(rowSet.source.field)]
                    if (pivotValue == "null") pivotValue == "---"
                }

                pivotValue = getValueWithAppendToValue(lineArr, columnsListArr, rowSet, pivotValue)
                outData.append([pivotCombinedGroupByKey, pivotCombinedKey, pivotValue].join(OFS) + NEWLINE)
            }
            break;
    }
    // println outData.toString()

    props.setProperty("document.dynamic.userdefined.ddp_rowType", rowSet.rowType)
    props.setProperty("document.dynamic.userdefined.ddp_rowSetIndex", rowSetIndex.toString())
    props.getProperty("document.dynamic.userdefined.ddp_sqlStatementId", "*CLEARED*")
    props.getProperty("document.dynamic.userdefined.ddp_sqlColumnsList", "*CLEARED*")
    
    designerConfigRoot.globalStringReplacements.each {
        def searchFor = it.searchFor
        if (searchFor == "\\^") searchFor = /(?<!\|)\^(?!\|)/
        else if (searchFor == "\\|") searchFor = /(?<!\^)\|(?!\^)/
        outData = outData.replaceAll(searchFor, it.replaceWith)
    }

    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}


def getValueWithAppendToValue(lineArr, columnsListArr, configObject, toBeAppended) {
    def appendVal = ""
    def doAppend = true
    if (configObject.appendToValue) {
        appendVal = configObject.appendToValue.collect{
            if (it.type == "text") it.value
            else {
                def appendValItem = lineArr[columnsListArr.indexOf(it.field)]
                if (!appendValItem || appendValItem == "null" || appendValItem in configObject.appendToValueNullChars) {
                    doAppend = false
                }
                else appendValItem
            }
        }.join("")
    }
    if (appendVal && doAppend) toBeAppended += " " + appendVal
    return toBeAppended
}

def getValueWithModifications(stringReplacements, decimalFormat, value) {
    if (decimalFormat) {
        try {
            def DF = new DecimalFormat(decimalFormat)
            value = DF.format(value as BigDecimal)
        }
        catch(e) { true }
    }
    stringReplacements.each {
        value = value.replaceAll(it.searchFor, it.replaceWith)
    }
    return value

}
