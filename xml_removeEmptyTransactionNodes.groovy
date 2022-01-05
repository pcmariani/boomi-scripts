/* @data (contact)
<contact>
  <id>100265221X</id>
  <ao_id>100265221X</ao_id>
  <academic_title_pre>Mr</academic_title_pre>
  <academic_title_post>MD, DUDE</academic_title_post>
  <addresses>
    <address>
      <country>HUB.GB</country>
      <type>HUB.WORK</type>
    </address>
  </addresses>
  <birthday>1977-07-18</birthday>
  <email_main>peteymarinara@hotmail.com</email_main>
  <emails>
    <email>
      <address>peteymarinara@hotmail.com</address>
      <type>HUB.MAIN</type>
    </email>
  </emails>
  <employment_orgs>
    <employment_org/>
  </employment_orgs>
  <first_name>Petey301</first_name>
  <gender>HUB.MALE</gender>
  <graduation_year>2000</graduation_year>
  <interest_areas>
    <interest_area>HUB.AOCMF</interest_area>
    <interest_area>HUB.AOTRM</interest_area>
    <interest_area>HUB.AOVET</interest_area>
  </interest_areas>
  <last_name>Marinara301</last_name>
  <middle_name/>
  <newsletter_subscriptions>
    <subscription>
      <clinical_division>HUB.AOF</clinical_division>
    </subscription>
    <subscription>
      <clinical_division>HUB.AOTRM</clinical_division>
      <is_subscribed>true</is_subscribed>
    </subscription>
    <subscription>
      <clinical_division>HUB.AOCMF</clinical_division>
      <is_subscribed></is_subscribed>
    </subscription>
    <subscription>
      <clinical_division>HUB.AOSPI</clinical_division>
      <is_subscribed></is_subscribed>
    </subscription>
    <subscription>
      <clinical_division>HUB.AOVET</clinical_division>
      <is_subscribed></is_subscribed>
    </subscription>
    <subscription>
      <clinical_division>HUB.AORECON</clinical_division>
    </subscription>
  </newsletter_subscriptions>
  <status>HUB.ACTIVE</status>
  <suffix/>
  <user_id>pmarinara</user_id>
</contact>
*/
/* data (event)
<event>
   <id>f0c0d203-b921-4</id>
   <event_id>f0c0d203-b921-4</event_id>
   <accounting_cost_center>6200</accounting_cost_center>
   <event_details>
      <url>https://int2-aofoundation.cs84.force.com/eventapi__router?event=a1R5E000000SHbJ&amp;site=a0a5E000000pKCB</url>
      <type> </type>
      <territory> </territory>
      <organizer1_aoid>100000441</organizer1_aoid>
      <organizer2_aoid>100265510</organizer2_aoid>
      <organizing_country>HUB.CH</organizing_country>
      <name>Test Event (Educational Event)</name>
      <lms_id> </lms_id>
   </event_details>
   <addresses>
      <address>
         <city>Stockholm</city>
         <country>HUB.SE</country>
         <type>HUB.HOSTING</type>
      </address>
   </addresses>
   <languages>
      <language>
         <language>HUB.JPN</language>
         <type>HUB.PRIMARY</type>
      </language>
      <language>
         <language> </language>
         <type>HUB.SECONDARY</type>
      </language>
   </languages>
</event>
*/

import java.util.Properties;
import java.io.InputStream;

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def root = new XmlSlurper().parseText(is.text)

    def concatAll_is_subscribed = ""

    root.newsletter_subscriptions.children().each() {
        if (!it.is_subscribed || it.is_subscribed == "") {
            it.replaceNode{}
        }
        else {
            concatAll_is_subscribed += it.is_subscribed    
        }
    }
    
    if (concatAll_is_subscribed == "") {
        root.newsletter_subscriptions.replaceNode{}
    }

    def outData = groovy.xml.XmlUtil.serialize(root).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim()
    is = new ByteArrayInputStream(outData.getBytes("UTF-8"));

    dataContext.storeStream(is, props);
}
