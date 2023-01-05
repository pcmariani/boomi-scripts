import java.util.Properties;
import java.io.InputStream;
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder;
import groovy.json.JsonOutput;
import groovy.xml.XmlUtil
import com.boomi.execution.ExecutionUtil;

logger = ExecutionUtil.getBaseLogger()

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def root = new JsonSlurper().parse(is)
    // println JsonOutput.prettyPrint(JsonOutput.toJson(root.footnotes))

    def footnotes = []
    def footnotesVals
    def footnotesRefs
    def hasFooterRow = true
    def footnotesPerTableGroupArr = []

    if (root.footnotes) {
        footnotes = new JsonSlurper().parseText("[" + root.footnotes + "]")
        // println footnotes[0][0].keySet()[0]
        if (footnotes[0][0].keySet()[0] != "footer-row") {
            hasFooterRow = false
            footnotes = new JsonSlurper().parseText(root.footnotes)
            footnotesVals = footnotes[0]
            footnotesRefs = footnotes[1]
        }
    }
    // println "hasFooterRow: " + hasFooterRow
    // println footnotes

    if (hasFooterRow) {
        footnotesPerTableGroupArr = getFootnotesPerTableGroupArr(footnotes)
        // println JsonOutput.prettyPrint(JsonOutput.toJson(footnotesPerTableGroupArr))
    }
    

    def html = root.query_html
    def htmlroot = new XmlSlurper().parseText(html)
    // println htmlroot.body.tablegroup.size()
    // println footnotesPerTableGroupArr.size()

    def tableGroupsArr = []

    htmlroot.body.tablegroup.eachWithIndex { tableGroup, tgnum ->
        def tableGroupMap = [:]

        tableGroupMap['query_html'] = "<html>\n<body>\n" + XmlUtil.serialize(tableGroup).replaceFirst("<\\?xml version=\"1.0\".*\\?>", "") + "</body>\n</html>"

        if (!hasFooterRow && footnotes) {
            def newFootnotesValsMap = [:]
            def newFootnotesRefsMap = [:]
            footnotesRefs.each { k, v ->
                tableGroup.table.tr.children().@id.each{
                    // println it == k
                    if (it == k) {
                        newFootnotesRefsMap[k] = v
                        v.each{
                            newFootnotesValsMap[it] = footnotesVals[it]
                        }
                    }
                }
            }
            // println newFootnotesValsMap
            // println newFootnotesRefsMap
            if (newFootnotesValsMap || newFootnotesRefsMap) {
                tableGroupMap['footnotes'] = JsonOutput.toJson([newFootnotesValsMap, newFootnotesRefsMap])
            }
        }
        else if (footnotes) {
            tableGroupMap['footnotes'] = JsonOutput.toJson(footnotesPerTableGroupArr[tgnum]['footnotes'])
            tableGroupMap['footer-row'] = footnotesPerTableGroupArr[tgnum]['footer-row']
        }

        tableGroupMap['tableName'] = tableGroup.h3 as String

        tableGroupsArr << tableGroupMap
    }

    // tableGroupsArr.collect{
    //   println "footer-row:\n" + it['footer-row'] + "\n"
    //   println "footnotes:\n" + it['footnotes'] + "\n"
    // }

    def builder = new JsonBuilder()
    def newroot = builder {
        MD5 (root.MD5)
        Table_and_Footnotes tableGroupsArr.collect{
            def outMap = [:]
            for (key in ["query_html","footnotes","footer-row","tableName"]) {
                if (it[key] && it[key] != "null") outMap[key] = it[key]
            }
            outMap
        }
    }

    def outData = builder.toPrettyString()
    // outData = ""

    is = new ByteArrayInputStream(outData.getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}


def getFootnotesPerTableGroupArr(footnotes) {

    def footnotesPerTableGroupArr = []
    def footnotesPerTableGroupCounter = 0
    def footnotesPerTableGroupCounterPrev = -1
    def footnotesPerTableGroupMap = [:]

    def footnotesValsMap = [:]
    def footnotesRefsMap = [:]

    footnotes.eachWithIndex { item, j ->
      item.each {
        if (item[0].keySet()[0].contains("footer-row")) {
          footnotesPerTableGroupMap = [:]
          footnotesPerTableGroupMap.'footer-row' = it.'footer-row'
          footnotesPerTableGroupCounter++
        }
        else {
          footnotesPerTableGroupMap.'footnotes' = item
          footnotesPerTableGroupCounterPrev = footnotesPerTableGroupCounter
        } 
      }
      if (footnotesPerTableGroupCounter != footnotesPerTableGroupCounterPrev) {
        footnotesPerTableGroupArr[footnotesPerTableGroupCounter-1] = footnotesPerTableGroupMap
      }
    }
    
    return footnotesPerTableGroupArr
}
