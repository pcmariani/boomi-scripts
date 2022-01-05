/* @data
seg0	seg1	seg2	seg3	seg4	seg5
200	AAA	BBB	CCC	DDD	EEE
250	AAA	BBB	CCC	YY	ZZZ
100	AAA	BBB	111	222	333
100	000	000	222	222	222
*/
import java.util.Properties;
import java.io.InputStream;

def delimiter = "\t";
def columnsToRemove = [1,3];
def LINE_SEPARATOR = System.getProperty("line.separator");


for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);
    
    reader = new BufferedReader(new InputStreamReader(is));
    outData = new StringBuffer();
    
    while ((line = reader.readLine()) != null ) {

        def lineArr = line.split((delimiter.matches("([\\*\\|\\^\\+\\?])") ? "\\" : "") + delimiter);
        def removedColumnesLineArr = [];

        for ( int j = 0; j < lineArr.size(); j++ ) {
    
            if (!columnsToRemove.contains(j)) {
                
                removedColumnesLineArr.push(lineArr[j]);
            }
        }
        
        outData.append(removedColumnesLineArr.join(delimiter) + LINE_SEPARATOR);
    }
    
    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}
