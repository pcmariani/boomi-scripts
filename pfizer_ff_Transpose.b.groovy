/* @data
Analytical Procedure|^|Analytical Procedure|^|Analytical Procedure|^|iCE|^|iCE|^|Test First Key|^|Appearance|^|SE-HPLC
Quality Attribute|^|Quality Attribute|^|Quality Attribute|^|Basic Species|^|Main Species|^|Test Second Key|^|Visible Particulates|^|HMMS
Clinical Acceptance Criteria|^|Clinical Acceptance Criteria|^|Clinical Acceptance Criteria|^|Report Results (%) (Report Results (%))|^|Report Results (%) (Report Results (%))|^|NR|^|Report Results (Report Results)|^|<= 5.0% HMMS (<= 5.0% HMMS)
Lot Number|^|18-000391|^|Results|^|5.0|^|73.1|^|NR|^|Essentially free from visible particulates|^|0.6
Lot Number|^|18L150K101|^|Results|^|7.8|^|70.8|^|NR|^|Essentially free from visible particulates|^|0.5
*/
/* @props
    #DPP_Key=val
    #document.dynamic.userdefined.ddp_Key=val
*/
import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;

def NEWLINE = System.lineSeparator()
def IFS = /\|\^\|/  // Input Field Separator
def OFS = "|^|"     // Output Field Separator

logger = ExecutionUtil.getBaseLogger()

def LINE_SEPARATOR = System.getProperty("line.separator")

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i)
    Properties props = dataContext.getProperties(i)

    reader = new BufferedReader(new InputStreamReader(is))
    outData = new StringBuffer()

    def dataArr = []
    while ((line = reader.readLine()) != null ) {
        def linedataArr = line.split(/\s*$IFS\s*/) 
        dataArr << linedataArr
    }

    // def transposedDataArr = dataArr.transpose()

    dataArr.transpose().each { line ->
        outData.append(line.join("\t") + NEWLINE)
    }


    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"))
    dataContext.storeStream(is, props)
}


