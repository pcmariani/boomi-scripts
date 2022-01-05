/* @data
@file('../../pfizer_csvToXlxs_data.csv')

*/
/* @props
#document.dynamic.userdefined.ddp_HeaderRowNum=1
#document.dynamic.userdefined.ddp_IncludeHeaderRow=true
#document.dynamic.userdefined.ddp_RowLimit=5
*/

import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.util.CellRangeAddress;

logger = ExecutionUtil.getBaseLogger()

def LINE_SEPARATOR = System.getProperty("line.separator")

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def reader = new BufferedReader(new InputStreamReader(is))
    def data = new StringBuffer()

    XSSFWorkbook workBook = new XSSFWorkbook();
    XSSFSheet sheet = workBook.createSheet("sheet1");

    String currentLine=null;
    int RowNum=0;

    while ((currentLine = reader.readLine()) != null) {
        def str = currentLine.split(",");
        XSSFRow currentRow=sheet.createRow(RowNum);
        for(int j=0; j<str.size(); j++) {
            currentRow.createCell(j).setCellValue(str[j]);
        }
        RowNum++;
    }

    sheet.addMergedRegion(CellRangeAddress.valueOf("A1:B1"));

    FileOutputStream fileOutputStream =  new FileOutputStream("../../pfizer_csvToXlxs_output.xlsx");
    workBook.write(fileOutputStream);
    fileOutputStream.close();

    /* ByteArrayOutputStream bos = new ByteArrayOutputStream(); */
    /* workBook.write(bos); */
    
    /* byte[] bytes = bos.toByteArray(); */
    /* is = new ByteArrayInputStream(bytes); */
    /* def newIs = is.text.replace("^M","") */
    
    /* is = new ByteArrayInputStream(newIs.toString().getBytes("UTF-8")); */
    /* dataContext.storeStream(is, props); */
}
