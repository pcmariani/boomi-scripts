/* data
hello world
*/

/* @props
DPP_STAB_LOT_NUMBER=AAA----30±2 °C/75±5% RH---BBB
#DPP_STAB_LOT_NUMBER=CCC----10C_90RH---DDD
*/

import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;
 
logger = ExecutionUtil.getBaseLogger();
 
def batchesArr = ExecutionUtil.getDynamicProcessProperty("DPP_STAB_LOT_NUMBER").split(/,\s*/)
 
def lotMap = [:]
 
batchesArr.each() { batch ->
 
    def batchArr = batch.split("---")
 
    // define Attributes
    def lotNumber = batchArr[0].trim()
    def storageCondition = batchArr[1].trim()
    def storageOrientation = batchArr[2].trim()
    // logger.warning("Storage Condition: " + storageCondition.toString())
   
    // add to lotMap
    lotMap["${lotNumber}"] = (lotMap["${lotNumber}"] ? lotMap["${lotNumber}"] + "@" : "") + storageCondition
   
    // set DDPs
    Properties props = dataContext.getProperties(0);
 
    props.setProperty("document.dynamic.userdefined.DDP_LotNumber", lotNumber)
    props.setProperty("document.dynamic.userdefined.DDP_OriginalStorageCondition", storageCondition)
    def storageConditionFormatted = ""
    if (storageCondition.contains("/")) {
        storageConditionFormatted = storageCondition
    }
    else {
        storageConditionFormatted = storageCondition    .replaceAll(/(?<!T)C/," °C")
                                                        .replaceAll(/TC_/,"Thermal Cycle ")
                                                        .replaceAll(/(?<!% )RH/,"% RH")
                                                        .replaceAll(/_/,"/")
    }
    props.setProperty("document.dynamic.userdefined.DDP_StorageCondition", storageConditionFormatted)
    props.setProperty("document.dynamic.userdefined.DDP_StorageOrientation", storageOrientation)
   
    // set storageConditionArr DDPs
    def storageConditionArr = storageCondition.split(/\s*[_\/]\s*/)
    props.setProperty("document.dynamic.userdefined.DDP_Temperature", storageConditionArr[0].trim()
                                                        /* .replaceAll(/±\d/,"") */
                                                        .replaceAll(/[^\d-±]/,"")
                                                        )
    if (storageConditionArr.size() > 1) {
        props.setProperty("document.dynamic.userdefined.DDP_Humidity", storageConditionArr[1].trim()
                                                        /* .replaceAll(/±\d/,"") */
                                                        .replaceAll(/[^\d-±]/,"")
                                                        )
    }
   
    is = new ByteArrayInputStream(batchArr[0].trim().getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}
 
// logger.warning(lotMap.toString())
// logger.warning("lotMap.values().join().contains(@): " + lotMap.values().join().contains("@"))
 
if (lotMap.values().join().contains("@")) ExecutionUtil.setDynamicProcessProperty("DPP_HasMultipleStorageConditions", "true", false)
// logger.warning("DPP_HasMultipleStorageConditions: " + ExecutionUtil.getDynamicProcessProperty("DPP_HasMultipleStorageConditions"))
