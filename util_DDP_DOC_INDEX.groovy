/*
 * For each document, sets DDP_DOC_INDEX with index
 */
import java.util.Properties;  
import java.io.InputStream;  
import com.boomi.execution.ExecutionUtil;  
    
for ( int i = 0; i < dataContext.getDataCount(); i++ ) {  
   InputStream is = dataContext.getStream(i);  
   Properties props = dataContext.getProperties(i);  

   props.setProperty("document.dynamic.userdefined.DDP_DOC_INDEX", i.toString());

   dataContext.storeStream(is, props);  
}  
