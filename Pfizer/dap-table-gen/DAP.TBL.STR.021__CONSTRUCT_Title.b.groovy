import java.util.Properties;
import java.io.InputStream;
import java.util.regex.Pattern
import com.boomi.execution.ExecutionUtil;
import groovy.json.JsonSlurper

logger = ExecutionUtil.getBaseLogger();

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def designerConfig = props.getProperty("document.dynamic.userdefined.ddp_designerConfig")
    def designerConfigRoot = new JsonSlurper().parseText(designerConfig)

    if (designerConfigRoot.tableTitle) {
        def tableTitle = designerConfigRoot.tableTitle.titleText
        def stringReplacementsArr = designerConfigRoot.tableTitle.replacements
        // println stringReplacementsArr.getClass()
        // String tableTitle = props.getProperty("document.dynamic.userdefined.ddp_tableTitle")
        
        // def replacementsArr = []
        // def replacements = 
        // println tableTitle

        if (tableTitle.contains("<TableNameVar>")) {
            String sqlParams = props.getProperty("document.dynamic.userdefined.ddp_sqlParams")
                                                      .replaceAll(" ","")
                                                      // .replaceAll(";","_")
            // println sqlParams
            tableTitle = tableTitle.replaceAll("TableNameVar", "TableNameVar_" + sqlParams)
        }
        // logger.warning("tableTitle: " + tableTitle)


        // Enclose section containing placeholder in squre brackets. If the replacement is NO_VALUE,
        // the entire bracketted section will be removed.
        String titleWithReplacements = applyPlaceholderReplacements(tableTitle, props, stringReplacementsArr)
        // logger.warning("titleWithReplacements: " + titleWithReplacements)
        println titleWithReplacements
        props.setProperty("document.dynamic.userdefined.DDP_TableTitle", titleWithReplacements)
    }

    dataContext.storeStream(is, props);
}
    println ""

private String applyPlaceholderReplacements(String str, Properties props, ArrayList stringReplacementsArr) {
    /*
     * Iterate over placeholders. Send placeholder names into closure as 'name'.
     * regex: <(.*?)> :  Capture anything inside <...>
     */
    (str =~ /<(.*?)>/).collect{match -> match[1]}.unique().each() { name ->
        // logger.warning("name:" + name)
        String replacementValue = (((
                 props.getProperty("document.dynamic.userdefined.ddp_" + name) ?:
                 props.getProperty("document.dynamic.userdefined." + name)
            ) ?: ExecutionUtil.getDynamicProcessProperty("DPP_" + name)
            ) ?: ExecutionUtil.getDynamicProcessProperty(name)
            ) ?: "NO_VALUE"

        name = name.replaceAll(/([()])/,/\\$1/) // escape regex metachars
        Pattern pattern = Pattern.compile(/<${name}>/)
        // logger.warning("pattern:" + pattern.toString())
        str = pattern.matcher(str).replaceAll(applyStringReplacements(name, replacementValue, stringReplacementsArr))
    }
    str = str - ~/\[[^\]\[]*?NO_VALUE.*?\]/ - ~/ +$/
    str = str.replaceAll(/[\[\]]/, "").replaceAll(/ {2,}/, " ").replaceAll(/ +$/, "")
    return str
}

private String applyStringReplacements(String name, String replacementValue, ArrayList stringReplacementsArr) {
  stringReplacementsArr.findAll{it.placeholderName == name}.each{
    replacementValue = replacementValue.replaceAll(it.searchFor, it.replaceWith)
  }
  return replacementValue
}
