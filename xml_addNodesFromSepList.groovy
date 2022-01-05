/* @data
<contact op="UPDATE" grid="13e533d4-3e32-4705-b9b9-147db6679ddb" ts="08-18-2020T14:57:16.000+0000">
  <id>100265214</id>
  <ao_id>100265214</ao_id>
  <academic_title_pre>Mr</academic_title_pre>
  <addresses>
    <address>
      <city>Phila</city>
      <country>HUB.DE</country>
      <type>HUB.DELIVERY</type>
    </address>
  </addresses>
  <birthday>1974-07-24</birthday>
  <board_certifications>
    <board_certification>
      <clinical_division>HUB.AOCMF</clinical_division>
    </board_certification>
  </board_certifications>
  <email_main>aocmtest+petemariani2@gmail.com</email_main>
  <emails>
    <email>
      <address>aocmtest+petemariani2@gmail.com</address>
      <type>HUB.MAIN</type>
    </email>
  </emails>
  <expertises>
    <expertise>
      <clinical_division>HUB.AOSPI</clinical_division>
      <expertise_level>HUB.HIGH</expertise_level>
    </expertise>
    <expertise>
      <clinical_division>HUB.AORECON</clinical_division>
      <expertise_level>HUB.LOW</expertise_level>
    </expertise>
  </expertises>
  <first_name>Pete2</first_name>
  <gender>HUB.MALE</gender>
  <graduation_year>N/A</graduation_year>
  <interest_areas>
    <interest_area>HUB.AOCMF</interest_area>
    <interest_area>HUB.AOTRM</interest_area>
  </interest_areas>
  <interests>
    <interest>
      <clinical_division>HUB.AOSPI</clinical_division>
      <topic>HUB.DEGENERATION;HUB.PEDIATRICDEFORMITY</topic>
    </interest>
    <interest>
      <clinical_division>HUB.AOTRM</clinical_division>
      <topic>HUB.OSTEOPOROSIS</topic>
    </interest>
    <interest>
      <clinical_division>HUB.AOCMF</clinical_division>
      <topic>HUB.CRANIORECON;HUB.CRANIOTRAUMA;HUB.PEDIATRICSURGERY;HUB.SNORING;HUB.IMAGING</topic>
    </interest>
    <interest>
      <clinical_division>HUB.AOVET</clinical_division>
      <topic>HUB.THERIOGENOLOGY;HUB.PHARMACOLOGY;HUB.ONCOLOGY</topic>
    </interest>
    <interest>
      <clinical_division>HUB.AORECON</clinical_division>
    </interest>
  </interests>
  <job_title>ltd. Oberarzt</job_title>
  <languages>
    <language>
      <language>HUB.ENG</language>
      <type>HUB.PRIMARY</type>
    </language>
  </languages>
  <last_name>Mariani30</last_name>
</contact>
*/


import java.util.Properties;
import java.io.InputStream;

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def root = new XmlSlurper().parseText(is.text)

        root.interests.interest.each() { topicNode ->
            def topicArr = topicNode.topic.text().split(";")
            topicNode.topic.replaceNode{}

            for (k = 0; k < topicArr.size(); k++) {
                def newNode = new Node(topicNode,"topic",topicArr[k]) 
            }
            // topicArr.each() {
            //     def newNode = new Node(topicNode,"topic",it) 
            // }
        }

    def outData = groovy.xml.XmlUtil.serialize(root).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim()
    is = new ByteArrayInputStream(outData.getBytes("UTF-8"));

    dataContext.storeStream(is, props);
}
