/* @data
Text Only in Emojis 💬😃😞💬🤐
*/
import java.util.Properties;
import java.io.InputStream;

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);
    
    def data_in = is.text;
    
    String regex = "[^\\p{L}\\p{N}\\p{P}\\p{Z}\\p{Sm}\\p{Sc}\\p{Sk}\\p{M}\\r\\n]";
    String result = data_in.replaceAll(regex, "");

    String data_out = result;
    
    is = new ByteArrayInputStream(data_out.toString().getBytes())

    dataContext.storeStream(is, props);
}
