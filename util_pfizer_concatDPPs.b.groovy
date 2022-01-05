/* @props
DPP_INCLUDE_BATCH_LIST=111,222
DPP_INCLUDE_BATCH_LIST2=333,444,555
DPP_INCLUDE_BATCH_LIST3=Ignore this Group
DPP_INCLUDE_BATCH_LIST4=Ignore this Group
DPP_INCLUDE_BATCH_LIST5=Ignore this Group
*/

import java.util.Properties

def batchListArr = []
def tableIndexMap = ''

(1..5).each { j ->
    def batchList = ExecutionUtil.getDynamicProcessProperty("DPP_INCLUDE_BATCH_LIST${j == 1 ? '' : j}")
    if (batchList != 'Ignore this Group') {
        batchListArr.push(batchList)

        batchList.split(',').each { batchNum ->
            tableIndexMap += "${batchNum}:${j}\n"
        }
    }
}
ExecutionUtil.
ExecutionUtil.setDynamicProcessProperty('DPP_BATCH_LIST_COMBINED', batchListArr.join(','), false)
ExecutionUtil.setDynamicProcessProperty('DPP_TABLE_INDEX_MAP', (tableIndexMap - ~ /\n$/), false)

for ( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i)
    Properties props = dataContext.getProperties(i)

    dataContext.storeStream(is, props)
}
