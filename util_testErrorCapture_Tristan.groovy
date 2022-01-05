/* --- @data
{
"orders":[
{"number":1},
{"number":2,
{"number":3}
]
}*/
import java.util.Properties;
import java.io.InputStream;
import groovy.json.JsonSlurper;
import groovy.json.JsonOutput;

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);
    
    String doc = is.getText();
    
    if(doc == null || doc.isEmpty()){ 
		props.setProperty("document.dynamic.userdefined.DDP_Error", "Document is invalid - empty");		
        dataContext.storeStream(is, props);
        continue;
    }
        
    def slurper = new JsonSlurper();
	def jsonDoc = null;
	
	try{
		jsonDoc = slurper.parseText(doc);
	}catch(Exception ex){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);								
		props.setProperty("document.dynamic.userdefined.DDP_Error", sw.toString());			
	}
	    
    is.reset();
    dataContext.storeStream(is, props);
   
}

