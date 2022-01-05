/* @data
BTx-ARD,BTx-ARD Portfolio Project Rep
BTx-ARD,BTx-BIT
BTx-ARD,BTx-MSBC
BTx-ARD,BTx-PPL1
BTx-ARD,BTx-PPL2
BTx-ARD,BTx-PPL3
BTx-ARD,BTx-QCSM
BTx-ARD,BTx-VATT
BTx-BRD,BTx-BRD Portfolio Project Rep
Not Sure,BTx-CLD
Not Sure,BTx-CPD
Not Sure,BTx-CPPD
Not Sure,BTx-DS GMP Mfg
Not Sure,BTx-DS non-GMP Mfg
Not Sure,BTx-GTx PD
Not Sure,BTx-PPD
BTx-BTx Portfolio Operations,BTx-Outsourcing Specialist
BTx-BTx Portfolio Operations,BTx-Project Manager
BTx-BTx Portfolio Operations,BTx-PSTL
BTx-BTx Portfolio Operations,PTx-Tech Dev Personnel
BTx-BTx Portfolio Operations,BTx-Cell Based Development
BTx-BTx Portfolio Operations,BTx-Analytical Development
BTx-PhRD,BTx-DP DCOE
BTx-PhRD,BTx-DP Dev Mfg
GCS-Clinical Supply,GCS-Supply Chain Coordinator
GCS-Clinical Supply,GCS-Workchain Coordinator
LPEH-Bios Pharm Sci Bioanalytical Sci,LPEH-ARD Bioanalytical Development
LPEH-Bios Pharm Sci Bioprocess Dev,LPEH-DS Dev & Tech Transfer
*/

import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;

logger = ExecutionUtil.getBaseLogger()

def LINE_SEPARATOR = System.getProperty("line.separator")

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i)
    Properties props = dataContext.getProperties(i)

    reader = new BufferedReader(new InputStreamReader(is))
    outData = new StringBuffer()

    def rwtArr = []

    while ((line = reader.readLine()) != null ) {
        /* outData.append(line + LINE_SEPARATOR) */
        rwtArr.push(line.split(","))
    }

    rwtArr.transpose().each {
        outData.append(it.join(",")+ LINE_SEPARATOR)
    }

    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"))
    dataContext.storeStream(is, props)
}


