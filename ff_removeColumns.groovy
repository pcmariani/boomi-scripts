/* @data
@file('..\..\pfizer_jsonArraysToText_data.json')
*/
import java.util.Properties;
import java.io.InputStream;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput;
import groovy.json.JsonBuilder;

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def root = new JsonSlurper().parseText(is.getText())
    def builder = new JsonBuilder(root)

    root.eachWithIndex { object, index ->
        object.each { item ->
            def key = item.getKey()
            def val = item.getValue()

            if (val.getClass().toString().contains("ArrayList")) {
                builder.content[index]."$key" = JsonOutput.toJson(val) 
            }
        }
    }

    def outData = builder.toPrettyString()
       
    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}
