/* @data
@file('../boomi-scripts/pfizer_xlsx2csv_example.xlsx')
*/
/* @props
document.dynamic.userdefined.ddp_HeaderRowNum=1
document.dynamic.userdefined.ddp_IncludeHeaderRow=true
document.dynamic.userdefined.ddp_RowLimit=5
*/

import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;

logger = ExecutionUtil.getBaseLogger()

def LINE_SEPARATOR = System.getProperty("line.separator")

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def reader = new BufferedReader(new InputStreamReader(is))
    def data = new StringBuffer()

    XSSFWorkbook wBook = new XSSFWorkbook(is)
    XSSFSheet sheet = wBook.getSheetAt(0)

    Iterator<Row> rowIterator = sheet.iterator()

    def includeHeaderRow = props.getProperty("document.dynamic.userdefined.ddp_IncludeHeaderRow") ?: "true"

    int headerRowNum = (props.getProperty("document.dynamic.userdefined.ddp_HeaderRowNum") ?: "1" ) as int
    if (headerRowNum < 1) headerRowNum = 1

    int rowLimit = (props.getProperty("document.dynamic.userdefined.ddp_RowLimit") ?: "9999999" ) as int
    if (rowLimit < 2) rowLimit = 2

    rowLimit = rowLimit + headerRowNum + 1

    int rowNum = 1
    int numCells

    while (rowIterator.hasNext() && rowNum < rowLimit) {
        Boolean suppressRow = false
        Row row = rowIterator.next()
        if (rowNum >= headerRowNum) {
            if (rowNum == headerRowNum) {
                numCells = row.getLastCellNum()
                println includeHeaderRow
                if (includeHeaderRow != "true") suppressRow = true
            }
            if (!suppressRow) {
                for (cellNum = 0; cellNum < numCells; cellNum++) {
                    Cell cell = row.getCell(cellNum)
                    data.append("\"${cell != null ? cell : ''}\"${cellNum < numCells-1 ? ',' : ''}")
                }
                data.append(LINE_SEPARATOR)
            }
        }
        rowNum++
    } 
    
    is = new ByteArrayInputStream(data.toString().getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}
