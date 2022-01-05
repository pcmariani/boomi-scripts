/* --- @props
    SOME_DPP=2019-01-25T08:24:42.000Z
*/

/*
 * Convert date to epoch
 */
import java.util.Properties;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import com.boomi.execution.ExecutionUtil;

String propName = "SOME_DPP"

String dateToConvert = ExecutionUtil.getDynamicProcessProperty(propName);

long epoch = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(dateToConvert).getTime()/1000;

ExecutionUtil.setDynamicProcessProperty(propName, epoch.toString(), false);


for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);
    dataContext.storeStream(is, props);
}
