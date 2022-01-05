/* @props
EPOCH=1548422682
*/

/*
 * Convert epoch to date
 * date format in this example is yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
 */
import java.util.Properties;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import com.boomi.execution.ExecutionUtil;

String propName = "EPOCH"

String epochToConvert = ExecutionUtil.getDynamicProcessProperty(propName);

String dateStr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date (epochToConvert.toLong()*1000));

ExecutionUtil.setDynamicProcessProperty(propName, dateStr, false);


for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    dataContext.storeStream(is, props);
}
