/* @data
<customers>
  <customer>
    <name>Customer A</name>
    <territory>001;002;003</territory>
    <type>direct</type>
  </customer>
  <customer>
    <name>Customer B</name>
    <territory>002;004</territory>
    <type>channel</type>
  </customer>
</customers>
*/

/*
 * From Boomi Community
 * https://community.boomi.com/s/article/howtosplitandrepeatvalueswithinanxmlelementusinggroovy
 */ 

import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.Namespace;  
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.jdom.output.XMLOutputter;

// Set the full path to the XML element containing the values to split.
String xpathElementToSplit = "/customers/customer/territory";

// Set the delimiter character separating the values.
String delimiter = ";";

// Loop through the Process Documents
for ( int i = 0; i < dataContext.getDataCount(); i++ ) {

     InputStream is = dataContext.getStream(i);
     Properties props = dataContext.getProperties(i);

     // Build XML Document
     SAXBuilder builder = new SAXBuilder();
     Document doc = builder.build(is);

     XPath x = XPath.newInstance(xpathElementToSplit);
     /* OPTIONAL: If XML data has namespaces, uncomment this section and declare
        all namespaces present in data as follows:
     x.addNamespace("<NAMESPACE_PREFIX>", "<NAMESPACE_URI>");
     END OPTIONAL */


     // Select multiple nodes and loop through them
     myElements = x.selectNodes(doc);

     for (Element myElement : myElements) {
        // Get the element name & value
        String elementName = myElement.getName();
        String elementValue = myElement.getText();
        Namespace elementNS = myElement.getNamespace();

        // Get parent element and remove current element
        Element parentElement = myElement.getParent();
        myElement.detach();

        // Loop through parts and add new Elements to parent
        String[] parts = elementValue.split(delimiter);
        for (int j=0; j<parts.length; j++) {

           newElement = new Element(elementName, elementNS).addContent(parts[j]);  
           parentElement.addContent(newElement);
        }
     }

     XMLOutputter outputter = new XMLOutputter();
     is = new ByteArrayInputStream(outputter.outputString(doc).getBytes());
     dataContext.storeStream(is, props);
}
