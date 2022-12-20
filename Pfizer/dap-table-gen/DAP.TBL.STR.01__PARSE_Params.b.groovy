//                                                                                               //
//                         	- -~- --~<>~---~=<< ♰ >>=~---~<>~-- -~- -                            //
//                                                                                               //
//                              ___   LASCIATE OGNI SPERANZE   ___                               //
//                                       VOI CHE ENTRATE                                         //
//                                                                                               //
//                         	- -~- --~<>~---~=<< ♰ >>=~---~<>~-- -~- -                            //
//                                                                                               //

import java.util.Properties;
import java.io.InputStream;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import com.boomi.execution.ExecutionUtil;

logger = ExecutionUtil.getBaseLogger();

def NEWLINE = System.lineSeparator()
def IFS = /\|\^\|/  // Input Field Separator
def OFS = "|^|"     // Output Field Separator

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i)
    Properties props = dataContext.getProperties(i)

    def designerConfig = props.getProperty("document.dynamic.userdefined.ddp_designerConfig")
    def designerConfigRoot = new JsonSlurper().parseText(designerConfig)
    def authorConfig = props.getProperty("document.dynamic.userdefined.ddp_authorConfig")
    def authorConfigRoot = new JsonSlurper().parseText(authorConfig)

    def tableStructureType = designerConfigRoot.tableStructureType
    props.setProperty("document.dynamic.userdefined.ddp_tableStructureType", tableStructureType)

    respectColumnWidths = authorConfigRoot.respectColumnWidths
    if (respectColumnWidths == null) respectColumnWidths = true
    props.setProperty("document.dynamic.userdefined.ddp_respectColumnWidths", respectColumnWidths as String)

    def numHeaderRows = designerConfigRoot.rowSets.findAll{it.location == "header"}.size()
    props.setProperty("document.dynamic.userdefined.ddp_numHeaderRows", numHeaderRows as String)

    if (tableStructureType.startsWith("pivot")) {
        def numGroupByCols = designerConfigRoot.rowSets.groupBy.collect{it?.size()}.max() ?: 1
        props.setProperty("document.dynamic.userdefined.ddp_numGroupByCols", numGroupByCols as String)
        def numPivotKeyRows = designerConfigRoot.rowSets.findAll{it.rowType == "pivot-column-names"}.size()
        props.setProperty("document.dynamic.userdefined.ddp_numPivotKeyRows", numPivotKeyRows as String)

        def pivotKeysCombinedLabel = designerConfigRoot.rowSets.findAll{it.rowType == "pivot-column-names" && it.source.type == "list"}.source.field.join("___")
        props.setProperty("document.dynamic.userdefined.ddp_pivotKeysCombinedLabel", pivotKeysCombinedLabel)

        def pivotGroupByColumnWidthsArr = []
        designerConfigRoot.rowSets.findAll{it.rowType == "pivot-column-names"}.groupBy?.columnWidth.each{
            it.eachWithIndex { item, c ->
                if (!pivotGroupByColumnWidthsArr[c] || pivotGroupByColumnWidthsArr[c] == "null") {
                    pivotGroupByColumnWidthsArr[c] = item
                }
            }
        }
        props.setProperty("document.dynamic.userdefined.ddp_pivotGroupByColumnWidths", pivotGroupByColumnWidthsArr.join(OFS))

        if (authorConfigRoot.sources.find{it.id == "filteredPivotColumnNames"}) {
          def pivotKeysColumnsArr = authorConfigRoot.sources.find{it.id == "filteredPivotColumnNames"}.items

          def pivotKeysColumns = pivotKeysColumnsArr.keys.name.collect{it.join("___").toUpperCase()}.join(OFS)
          props.setProperty("document.dynamic.userdefined.ddp_pivotKeysColumns", pivotKeysColumns)
          def numPivotKeyColumns = pivotKeysColumnsArr.keys.size()
          props.setProperty("document.dynamic.userdefined.ddp_numPivotKeyColumns", numPivotKeyColumns as String)
          def numColumns = numGroupByCols + numPivotKeyColumns
          props.setProperty("document.dynamic.userdefined.ddp_numColumns", numColumns as String)

          def pivotKeysColumnsWidthsArr = pivotKeysColumnsArr.columnWidth
          props.setProperty("document.dynamic.userdefined.ddp_pivotKeysColumnsWidths", pivotKeysColumnsWidthsArr.join(OFS))

          def pivotKeysSuppressIfNoDataArr = pivotKeysColumnsArr.suppressIfNoData
          props.setProperty("document.dynamic.userdefined.ddp_pivotKeysSuppressIfNoData", pivotKeysSuppressIfNoDataArr.join(OFS))

          def pivotKeysColumnsTableIndicesArr = []
          if (pivotKeysColumnsArr.subTableIndex.unique().join() == "null") {
              pivotKeysColumnsTableIndicesArr = [1] * numPivotKeyColumns
          }
          else {
              pivotKeysColumnsTableIndicesArr = pivotKeysColumnsArr.subTableIndex
          }
          props.setProperty("document.dynamic.userdefined.ddp_pivotKeysColumnsTableIndices", pivotKeysColumnsTableIndicesArr.join(OFS))
          props.setProperty("document.dynamic.userdefined.ddp_pivotKeysColumnsTableMaxIndex", pivotKeysColumnsTableIndicesArr.max() as String)
        }
    }

    dataContext.storeStream(is, props)
}

