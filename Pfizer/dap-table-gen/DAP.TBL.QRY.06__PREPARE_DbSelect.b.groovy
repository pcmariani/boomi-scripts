import java.util.Properties;
import java.io.InputStream;
import groovy.json.JsonSlurper
import com.boomi.execution.ExecutionUtil;
 
logger = ExecutionUtil.getBaseLogger()
 
for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);
   
    def tableIndex = props.getProperty("document.dynamic.userdefined.ddp_tableIndex") as int
   
    def designerConfig = props.getProperty("document.dynamic.userdefined.ddp_designerConfig")
    def designerConfigRoot = new JsonSlurper().parseText(designerConfig)
    def authorConfig = props.getProperty("document.dynamic.userdefined.ddp_authorConfig")
    def authorConfigRoot = new JsonSlurper().parseText(authorConfig)

    println authorConfigRoot.params.type

    designerConfigRoot.sources.findAll{it.type == "sql"}.each { source ->
        def sqlStatementId = source.id
        def sqlStatement = source.sqlStatement

        def sqlParamsArr = []
        // regex: remove last occurrance of ? and everything after it
        sqlStatement.split("WHERE")[1].replaceFirst(/\?(?:[^?]*)$/,"") .split("\\?").each { whereClause ->
            // regexes: remove inital quote and everything before; remove final quote and everything after
            def paramName = whereClause.replaceFirst(/.*?"/,"").replaceFirst(/\s*".*/,"")
            def paramValue = authorConfigRoot.params.tables[tableIndex][paramName].trim()
            sqlParamsArr << paramValue
        }
        def sqlParams = sqlParamsArr.join(";") 

        // println sqlStatementId + "\n------"
        // println sqlStatement + "\n------"
        // println sqlParams + "\n\n"

        props.setProperty("document.dynamic.userdefined.ddp_@@@_sqlStatementId", sqlStatementId)
        props.setProperty("document.dynamic.userdefined.ddp_@@@_sqlStatement", sqlStatement)
        props.setProperty("document.dynamic.userdefined.ddp_@@@_sqlParams", sqlParams)

        is = new ByteArrayInputStream("****".getBytes("UTF-8"));
        dataContext.storeStream(is, props);
    }
 //
 //    authorConfigRoot.tables[tableIndex].collect { table ->
 //        table.each { sources ->
 //            sources.value.findAll{it.type == "sql"}.each { source ->
 //                def sqlStatementId = source.id
 //                def sqlStatement = designerConfigRoot.sources.find{ it.id == sqlStatementId }.sqlStatement
 //                def sqlParams = source.sqlParams
 //
 //                props.setProperty("document.dynamic.userdefined.ddp_@@@_sqlStatementId", sqlStatementId)
 //                props.setProperty("document.dynamic.userdefined.ddp_@@@_sqlStatement", sqlStatement)
 //                props.setProperty("document.dynamic.userdefined.ddp_@@@_sqlParams", sqlParams)
 // 
 //        is = new ByteArrayInputStream("****".getBytes("UTF-8"));
 //                dataContext.storeStream(is, props);
 //            }
 //        }
 //    }
}
