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
String xPathElementTopic = "contact/interests/interest/topic";
String xPathElementClinicalDivision = "contact/interests/interest/topic";

// Set the delimiter character separating the values.
String delimiter = ";";

// Loop through the Process Documents
for ( int i = 0; i < dataContext.getDataCount(); i++ ) {

     InputStream is = dataContext.getStream(i);
     Properties props = dataContext.getProperties(i);

     // Build XML Document
     SAXBuilder builder = new SAXBuilder();
     Document doc = builder.build(is);

     XPath xTopic = XPath.newInstance(xPathElementTopic);
     // XPath xClinicalDivision = XPath.newInstance(xPathElementClinicalDivision);

     // Select multiple nodes and loop through them
     myElementsTopic = xTopic.selectNodes(doc);
     // myElementsClinicalDivision = xClinicalDivision.selectNodes(doc);

     for (Element myElementTopic : myElementsTopic) {
        // Get the element name & value
        String elementNameTopic = myElementTopic.getName();
        String elementValueTopic = myElementTopic.getText();
        Namespace elementNSTopic = myElementTopic.getNamespace();

        // String elementNameClinicalDivision = myElementTopic.getName();
        // String elementValueClinicalDivision = myElementTopic.getText();

        // Get parent element and remove current element
        Element parentElement = myElementTopic.getParent();
        Element elClinDiv = parentElement.
        myElementTopic.detach();

        // Loop through parts and add new Elements to parent
        String[] parts = elementValueTopic.split(delimiter);
        for (int j=0; j<parts.length; j++) {

           newElement = new Element(elementNameTopic, elementNSTopic).addContent(parts[j]);  
           parentElement.addContent(newElement);
        }
     }

     XMLOutputter outputter = new XMLOutputter();
     is = new ByteArrayInputStream(outputter.outputString(doc).getBytes());
     dataContext.storeStream(is, props);
}
