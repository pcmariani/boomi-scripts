/* @data
<Opportunity type="Opportunity" url="/services/data/v33.0/sobjects/Opportunity/0064I00000t6lgpQAA">
    <Id>0064I00000t6lgpQAA</Id>
    <wi__Share_to_WI__c>true</wi__Share_to_WI__c>
    <wi__WI_Lead_Id__c>873033.0</wi__WI_Lead_Id__c>
    <wi__Associated_Partner__c>0015800000XLNqRAAX</wi__Associated_Partner__c>
    <Name>G Jones Limited - Gary Jones</Name>
    <CloseDate>2019-03-31</CloseDate>
    <Account type="Account" url="/services/data/v33.0/sobjects/Account/0014I00001kC2GCQA0">
        <Website>http://www.webinfinity.com</Website>
        <Name>G Jones Limited</Name>
    </Account>
    <wi__Contact__r type="Contact" url="/services/data/v33.0/sobjects/Contact/0034I00001tC7ToQAK">
        <Name>Gary Jones</Name>
        <Title>VP of Operations</Title>
        <Email>prospect@newdeal.com</Email>
        <Phone>516-213-2346</Phone>
    </wi__Contact__r>
    <Working_Stage__c>Discover</Working_Stage__c>
    <Solutions__c>Enterprise Flash Storage</Solutions__c>
    <Competitors__c>ANZ Corporation</Competitors__c>
    <Industry__c>Advertising&amp;semi;Communications</Industry__c>
    <Percent_to_Close__c>10</Percent_to_Close__c>
    <StageName>New</StageName>
</Opportunity>
*/

import org.jdom.input.SAXBuilder;  
import org.jdom.Document;  
import org.jdom.Element;
// import org.jdom.Attribute;
// import org.jdom.xpath.XPath;  
import org.jdom.output.XMLOutputter;
import com.boomi.execution.ExecutionUtil;

// Loop through the Process Documents  
for (int i = 0; i < dataContext.getDataCount(); i++) {  
    InputStream is = dataContext.getStream(i)
    Properties props = dataContext.getProperties(i)
  
    // Build XML Document  
    SAXBuilder builder = new SAXBuilder()
    Document doc = builder.build(is)
    Element rootElement = doc.getRootElement()
    // def objectName = rootElement.getName()

    rootElement.setName(rootElement.getName() + "_item")
    rootElement.removeAttribute("type")
    rootElement.removeAttribute("url")
    /*
    rootElement.detach()

    Element newDocRootElement = new Element(objectName)
    Document newDoc = new Document(newDocRootElement)
    newDocRootElement.addContent(rootElement)
    */

    XMLOutputter outputter = new XMLOutputter()
    is = new ByteArrayInputStream(outputter.outputString(doc).getBytes("UTF-8"))
    dataContext.storeStream(is, props)
}
