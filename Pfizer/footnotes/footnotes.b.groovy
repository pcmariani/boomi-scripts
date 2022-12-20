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

  println JsonOutput.prettyPrint(JsonOutput.toJson(root.footnotes))

    def footnotes = []
    def footnotesVals
    def footnotesRefs

    if (root.footnotes) {
        footnotes = new JsonSlurper().parseText(root.footnotes)
        footnotesVals = footnotes[0]
        footnotesRefs = footnotes[1]
    }

    def html = root.query_html
    def htmlroot = new XmlSlurper().parseText(html)

    def tableGroupsArr = []

    htmlroot.body.tablegroup.each { tableGroup ->
        def tableGroupMap = [:]

        tableGroupMap['query_html'] = "<html>\n<body>\n" + XmlUtil.serialize(tableGroup).replaceFirst("<\\?xml version=\"1.0\".*\\?>", "") + "</body>\n</html>"

        if (footnotes) {
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

        tableGroupMap['tableName'] = tableGroup.h3 as String

        tableGroupsArr << tableGroupMap
    }
    // tableGroupsArr.collect{
      // println it.footnotes + "\n"
    // }

    def builder = new JsonBuilder()
    def newroot = builder {
        MD5 (root.MD5)
        Table_and_Footnotes tableGroupsArr.collect{
            if (it.footnotes) {
                [
                  'query_html': it.query_html,
                  'footnotes': it.footnotes,
                  'tablename': it.tableName
                ]
            }
            else {
                [
                  'query_html': it.query_html,
                  'tablename': it.tableName
                ]
            }
        }
    }

    def outData = builder.toPrettyString()
    // outData = ""

    is = new ByteArrayInputStream(outData.getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}
