import java.util.Properties;
import java.io.InputStream;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput;

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def jsonSlurper = new JsonSlurper()
    def root = jsonSlurper.parseText(is.getText())
    def interests = root.interests

    interests.each() { it.setValue([it.value[0].replaceAll(";", ';;;')]) }


    def outData = JsonOutput.prettyPrint(JsonOutput.toJson(root))

    is = new ByteArrayInputStream(outData.getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}
