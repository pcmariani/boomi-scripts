/* @data
<contact>
  <interest_areas>
    <interest_area>HUB.AORECON</interest_area>
    <interest_area>HUB.AOF</interest_area>
    <interest_area>HUB.AOCMF</interest_area>
    <interest_area>HUB.AOTRM</interest_area>
    <interest_area>HUB.AOCMF</interest_area>
  </interest_areas>
</contact>
*/

import java.util.Properties;
import java.io.InputStream;

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def root = new XmlParser().parseText(is.text)
    def interest_areas = root.interest_areas.interest_area.sort { it.text() }
    
    if (interest_areas) {
        
        root.interest_areas.head().children().clear()  // head() equal to first array element "[0]"
        interest_areas.each { root.interest_areas.head().append( it ) }
        
    }
    
    def newXmlStr = groovy.xml.XmlUtil.serialize(root).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim()
    is = new ByteArrayInputStream(newXmlStr.getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}
