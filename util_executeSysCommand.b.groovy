/*
 * execute system command and receive output
 */

/* @props
document.dynamic.userdefined.DDP_Command=dir
*/

import java.util.Properties;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import com.boomi.execution.ExecutionUtil;

logger = ExecutionUtil.getBaseLogger();

def LINE_SEPARATOR = System.getProperty("line.separator")
def OS = System.getProperty("os.name").toLowerCase()

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def command = props.getProperty("document.dynamic.userdefined.DDP_Command");
    if (OS =~ /win/) command = "cmd.exe /c " + command
    // logger.warning("Command: " + command);

    def response = new StringBuffer();

    try {
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();

        def reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        def error = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        while ((line = reader.readLine()) != null) {
            response.append(line + LINE_SEPARATOR);
            // logger.warning(line);
        }
        while ((line = error.readLine()) != null) {
            response.append(line + LINE_SEPARATOR);
            // logger.warning(line);
        }
    } catch (Throwable t) {
        logger.warning("Caught throwable: " + t.getMessage());
    }

    // props.setProperty("document.dynamic.userdefined.DDP_CommandResponse", response.toString());

    is = new ByteArrayInputStream(response.toString().getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}

