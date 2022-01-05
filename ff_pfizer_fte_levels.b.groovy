/* @data
@file('..\..\ff_pfizer_fte_levels_data.csv')
*/
import java.util.Properties;
import java.io.InputStream;

def LINE_SEPARATOR = System.getProperty("line.separator");

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);
    
    reader = new BufferedReader(new InputStreamReader(is));
    outData = new StringBuffer();
    def headerRow = reader.readLine()
    
    def levelsArr = []
    def levelPrev
    int j = 0

    while ((line = reader.readLine()) != null && j < 100) {
        def lineArr = line.split(",")

        def id = lineArr[0]
        def name = lineArr[1]
        def level = lineArr[2].toInteger()

        if (level == 1) {
            levelsArr = []
        } else if (level < levelPrev) {
            levelsArr = levelsArr.take(level)
        }
        
        levelsArr[level-1] = name

        // outData.append(id + "," + name + "," + level + "...." + levelsArr + LINE_SEPARATOR);
        outData.append(id + " ... " + level + " ... " + levelsArr + LINE_SEPARATOR);

        levelPrev = level
        j++
    }
    
    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}
