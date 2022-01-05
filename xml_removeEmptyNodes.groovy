/* @data
<contact>
  <id>100057290</id>
  <ao_id>100057290</ao_id>
  <addresses>
    <address>
      <type>HUB.HOME</type>
    </address>
    <address>
      <city>Taiyuan</city>
      <country>HUB.CN</country>
      <department>Orthopaedics</department>
      <organization>Second Hospital of Shanxi Medical University</organization>
      <postal_code>030001</postal_code>
      <street_lines>No.213 Mailbox, Shanxi Provincial Medical Universi</street_lines>
      <type>HUB.WORK</type>
    </address>
    <address>
      <type>HUB.DELIVERY</type>
    </address>
  </addresses>
  <emails>
    <email>
      <address>aocmtest+lackofrantisek_hotmail.com@gmail.com</address>
      <type>HUB.MAIN</type>
    </email>
    <email>
      <type>HUB.ASSISTANT</type>
    </email>
    <email>
      <type>HUB.MAINCC</type>
    </email>
  </emails>
  <phones>
    <phone>
      <extension></extension>
      <number>03513365426</number>
      <type>HUB.MAIN</type>
    </phone>
    <phone>
      <extension></extension>
      <number>13803403737</number>
      <type>HUB.MOBILE</type>
    </phone>
    <phone>
      <extension></extension>
      <number>02133060099</number>
      <type>HUB.FAX</type>
    </phone>
    <phone>
      <type>HUB.HOME</type>
    </phone>
    <phone>
      <type>HUB.ASSISTANT</type>
    </phone>
  </phones>
  <languages>
    <language>
      <language>HUB.KOR</language>
      <type>HUB.PRIMARY</type>
    </language>
    <language>
      <language></language>
      <type>HUB.SECONDARY</type>
    </language>
  </languages>
  <user_id>zhaobin</user_id>
</contact>
*/
import java.util.Properties;
import java.io.InputStream;

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def root = new XmlSlurper().parseText(is.text)
    root.depthFirst().findAll() { it.type.text() && it.type.text() == it.text() }.each() { it.replaceNode{} }
    
    def outData = groovy.xml.XmlUtil.serialize(root).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim()
    is = new ByteArrayInputStream(outData.getBytes("UTF-8"));

    dataContext.storeStream(is, props);
}
