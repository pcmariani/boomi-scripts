/* @data
*/
/* @props
    #DPP_Key=val
    #document.dynamic.userdefined.ddp_Filename=Complex Protein_20220102-0203-#[13-12-1].xlsx
    document.dynamic.userdefined.ddp_Filename=Complex Protein_20220102-0203.xlsx
*/
import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;

logger = ExecutionUtil.getBaseLogger()

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i)
    Properties props = dataContext.getProperties(i)

    def filename = props.getProperty("document.dynamic.userdefined.ddp_Filename")
      def rawMatch = (filename =~ /#\[(\d+)-(\d+)-(\d+)\]/)
      if (rawMatch) {
        matches = rawMatch[0]
        props.setProperty("document.dynamic.userdefined.ddp_ModalityId", matches[1])
        logger.warning("ddp_ModalityId: " + matches[1])
        props.setProperty("document.dynamic.userdefined.ddp_ModalityTemplateId", matches[2])
        logger.warning("ddp_ModalityTemplateId: " + matches[2])
        props.setProperty("document.dynamic.userdefined.ddp_ModalityTemplateVerNo", matches[3])
        logger.warning("ddp_ModalityTemplateVerNo: " + matches[3])
      }
      else {
        logger.severe("Filename does not contain metadata string")
      }

    dataContext.storeStream(is, props)
}


