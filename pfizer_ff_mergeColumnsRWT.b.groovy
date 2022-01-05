/* @data
BTx-ARD,BTx-ARD,BTx-ARD,BTx-ARD,BTx-ARD,BTx-ARD,BTx-ARD,BTx-ARD,BTx-ARD,Not Sure,Not Sure,Not Sure,Not Sure,Not Sure,Not Sure,Not Sure,BTx-BTx Portfolio Operations,BTx-BTx Portfolio Operations,BTx-BTx Portfolio Operations,BTx-BTx Portfolio Operations,BTx-BTx Portfolio Operations,BTx-BTx Portfolio Operations,BTx-PhRD,BTx-PhRD,GCS-Clinical Supply,GCS-Clinical Supply,LPEH-Bios Pharm Sci Bioanalytical Sci,LPEH-Bios Pharm Sci Bioanalytical Sci
BTx-ARD Portfolio Project Rep,BTx-BIT,BTx-MSBC,BTx-PPL1,BTx-PPL2,BTx-PPL3,BTx-QCSM,BTx-VATT,BTx-BRD Portfolio Project Rep,BTx-CLD,BTx-CPD,BTx-CPPD,BTx-DS GMP Mfg,BTx-DS non-GMP Mfg,BTx-GTx PD,BTx-PPD,BTx-Outsourcing Specialist,BTx-Project Manager,BTx-PSTL,PTx-Tech Dev Personnel,BTx-Cell Based Development,BTx-Analytical Development,BTx-DP DCOE,BTx-DP Dev Mfg,GCS-Supply Chain Coordinator,GCS-Workchain Coordinator,LPEH-ARD Bioanalytical Development,LPEH-DS Dev & Tech Transfer
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

    def lineArr = reader.readLine().split(",")

    def prevItem
    def prevItemStartIndex = 0

    lineArr.eachWithIndex { item, j ->
        if (item != prevItem) {
            if (j != 0 && prevItemStartIndex != j) {
                outData.append(prevItemStartIndex + "," + j + LINE_SEPARATOR)
                /* logger.warning(prevItem + " " + prevItemStartIndex + " " + j) */
            }
            prevItemStartIndex = j+1
        }
        if (j == lineArr.size()-1 && prevItemStartIndex != j+1) {
            outData.append(prevItemStartIndex + "," + (j+1) + LINE_SEPARATOR)
            /* logger.warning(item + " " + prevItemStartIndex + " " + (j+1)) */
        }
        prevItem = item
    }
    /* logger.warning(outData.toString()) */

    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"))
    dataContext.storeStream(is, props)
}


