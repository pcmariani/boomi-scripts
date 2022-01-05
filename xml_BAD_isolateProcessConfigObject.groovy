/* @props
    ProcessConfig=@file("Sfdc/sfdc_processConfig.xml")
    EndpointName=testEndpoint
    ObjectName=AccountProduct
    AltObjectName=
    ErrMsg_InvalidObjectType=TestThereWasAnError
*/
import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;
logger = ExecutionUtil.getBaseLogger();


def processConfig = ExecutionUtil.getDynamicProcessProperty("ProcessConfig");
def endpointName = ExecutionUtil.getDynamicProcessProperty("EndpointName");
def objectName = ExecutionUtil.getDynamicProcessProperty("ObjectName");
def altObjectName = ExecutionUtil.getDynamicProcessProperty("AltObjectName");
def ErrMsg_InvalidObjectType = ExecutionUtil.getDynamicProcessProperty("ErrMsg_InvalidObjectType");
def newProcessConfig = "";
def altProcessConfig = "";

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);
    
    if (processConfig.contains("<${objectName}>") || processConfig.contains("<${objectName} ")) {

        newProcessConfig = processConfig.replaceAll(/(?s).*(<${objectName}(?!_| altConfig).*?<\/${objectName}>).*/, "\$1");

        if (altObjectName) {
            
            if (processConfig.contains(altObjectName)) {
            
                altProcessConfig = processConfig.replaceAll(/(?s).*(<${objectName} altConfig="${altObjectName}">.*?<\/${objectName}>).*/, "\$1");

            } else {
                
                newProcessConfig = "ERROR ${endpointName}: ${altObjectName} ${ErrMsg_InvalidObjectType}";
                
            }
        }
    }

    else if (processConfig.contains("altConfig=\"${objectName}\"")) {

        newProcessConfig = processConfig.replaceAll(/(?s).*(<(.*?) altConfig="${objectName}".*?.*?<\/\2>).*/, "\$1");
        
    }
    
    else {
        newProcessConfig = "ERROR ${endpointName}: ${objectName} ${ErrMsg_InvalidObjectType}";

    }
 
    ExecutionUtil.setDynamicProcessProperty("ProcessConfig", newProcessConfig.replaceAll(/(?<=>)\s+/, ""), false);    
    ExecutionUtil.setDynamicProcessProperty("AltProcessConfig", altProcessConfig.replaceAll(/(?<=>)\s+/, ""), false);    
    
    logger.info("ProcessConfig: " + ExecutionUtil.getDynamicProcessProperty("ProcessConfig"));
    logger.info("AltProcessConfig: " + ExecutionUtil.getDynamicProcessProperty("AltProcessConfig"));

    
    dataContext.storeStream(is, props);
}