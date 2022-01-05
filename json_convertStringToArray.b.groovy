/* @data
{
  "Id": "0035E00000IwCF2QAN",
  "firstName": "Pete",
  "AOID": "100265214",
  "lastName": "Mariani30",
  "AOCUserId": "100147668.MN",
  "primaryLanguage": "eng",
  "birthdate": "1974-07-24",
  "contactCurrency": "CHF",
  "department": "Unfallchirurgie/Orthopädie",
  "workPhone": "+492845936534",
  "salutation": "Mr.",
  "nationalities": "DE",
  "title": "ltd. Oberarzt",
  "academicTitleBeforeName": "Mr",
  "receiveNewsletterAOFoundation": true,
  "receiveNewsletterAOCMF": false,
  "receiveNewsletterAOVET": false,
  "receiveNewsletterAOSpine": false,
  "receiveNewsletterAOTrauma": true,
  "AOSpineInterests": "Lumbar_degeneration;Pediatric_adolescent_deformity",
  "AOTraumaInterests": "Orthogeriatrics_Osteoporosis",
  "AOCMFInterests": "Reconstructive_surgery;Trauma_Fractures;Pediatric_OMS;Sleep_Apnea_Treatment;Imaging_navigation_computer_assisted_surgery",
  "AOVETInterests": "Theriogenology;Pharma_Toxicology;Oncology",
  "contactStatus": "Active",
  "deceased": false,
  "gender": "Male",
  "deliveryCity": "Phila",
  "deliveryCountry": "DE",
  "workStreet": "Fahrner Straße 133",
  "workCity": "Duisburg",
  "workDepartment": "Unfallchirurgie/Orthopädie",
  "workPostalCode": "47169",
  "medicalSchoolGraduationYear": "N/A",
  "account": "AOUser",
  "certificateName": "",
  "eventBadgeDisplayName": "",
  "areasOfInterest": "AOCMF;AOTRAUMA",
  "workCountry": "DE"
}
*/
/* props
dpp_hello=world
*/

import java.util.Properties;
import java.io.InputStream;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput;

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def jsonSlurper = new JsonSlurper()
    def root = jsonSlurper.parseText(is.getText())

    root.findAll { it.getKey().endsWith("Interests") }.each { 
        root.remove(it.getKey())
        root.put("${it.getKey()}", it.getValue().split(";"))
    }

    // Syntax for appending element to JSON object (put, left shift):
    //      root.put("a", "b")
    //      root << ["a": ["hello","world"]]

    def outData = JsonOutput.prettyPrint(JsonOutput.toJson(root))

    is = new ByteArrayInputStream(outData.getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}
 
