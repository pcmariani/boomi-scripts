/* @props
DPP_SourceIDsToQuery=OPS ,  FON,CMM,  MI ,TOT,MICME,FS,
DPP_SourceIDsToBlock=  b,FON   , MICME, , 
*/

import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;

String sourcesToQuery = ExecutionUtil.getDynamicProcessProperty("DPP_SourceIDsToQuery")
if (sourcesToQuery) {

    String sourcesToQuery_out

    ArrayList sourcesToQuery_arr = sourcesToQuery.replaceAll(/\s*,\s*/,",").split(",")
    
    String sourcesToBlock = ExecutionUtil.getDynamicProcessProperty("DPP_SourceIDsToBlock")
    if (sourcesToBlock) {

        if (sourcesToBlock.toLowerCase().trim() == "block_all") {
            sourcesToQuery_out = "BLOCK_ALL"
        }
        else {
            ArrayList sourcesToBlock_arr = sourcesToBlock.replaceAll(/\s*,\s*/,",").split(",")
            sourcesToQuery_arr.removeAll(sourcesToBlock_arr)
            sourcesToQuery_out = sourcesToQuery_arr.join(",")
        }
    }    
    else {
        sourcesToQuery_out = sourcesToQuery_arr.join(",")    
    }

    ExecutionUtil.setDynamicProcessProperty("DPP_SourceIDsToQuery",sourcesToQuery_out,false)
}

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    dataContext.storeStream(is, props);
}

