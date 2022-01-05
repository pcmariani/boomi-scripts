/* @data
<RecordQueryResponse resultCount="1" totalCount="1">
    <Record recordId="42a3bf7b-ddf9-4f40-8e49-4f5cbf520167" createdDate="2020-02-10T14:38:19Z" updatedDate="2021-03-16T14:11:04Z">
        <Fields>
            <contact>
                <expertises>
                    <expertise>
                        <clinical_division>HUB.AOSPI</clinical_division>
                        <expertise_level>HUB.HIGH</expertise_level>
                    </expertise>
                    <expertise>
                        <clinical_division>HUB.AOSPI</clinical_division>
                        <expertise_level>HUB.MEDIUM</expertise_level>
                    </expertise>
                    <expertise>
                        <clinical_division>HUB.AOSPI</clinical_division>
                        <expertise_level>HUB.LOW</expertise_level>
                    </expertise>
                    <expertise>
                        <clinical_division>HUB.AOTRM</clinical_division>
                        <expertise_level>HUB.HIGH</expertise_level>
                    </expertise>
                    <expertise>
                        <clinical_division>HUB.AOTRM</clinical_division>
                        <expertise_level>HUB.MEDIUM</expertise_level>
                        <expertise>Hip_and_pelvis_surgery;Knee_surgery;Shoulder_and_elbow_surgery;Joint_preservation_and_osteotomies;Periprosthetic_fracture_surgery</expertise>
                    </expertise>
                    <expertise>
                        <clinical_division>HUB.AOTRM</clinical_division>
                        <expertise_level>HUB.LOW</expertise_level>
                    </expertise>
                    <expertise>
                        <clinical_division>HUB.AOCMF</clinical_division>
                        <expertise_level>HUB.HIGH</expertise_level>
                        <expertise>Hip_and_pelvis_surgery;Knee_surgery;bhal;blah</expertise>
                    </expertise>
                    <expertise>
                        <clinical_division>HUB.AOCMF</clinical_division>
                        <expertise_level>HUB.MEDIUM</expertise_level>
                        <expertise>Hip_and_pelvis_surgery;Knee_surgery;Shoulder_and_elbow_surgery;Joint_preservation_and_osteotomies;Periprosthetic_fracture_surgery</expertise>
                    </expertise>
                    <expertise>
                        <clinical_division>HUB.AOCMF</clinical_division>
                        <expertise_level>HUB.LOW</expertise_level>
                    </expertise>
                    <expertise>
                        <clinical_division>HUB.AOVET</clinical_division>
                        <expertise_level>HUB.HIGH</expertise_level>
                    </expertise>
                    <expertise>
                        <clinical_division>HUB.AOVET</clinical_division>
                        <expertise_level>HUB.MEDIUM</expertise_level>
                    </expertise>
                    <expertise>
                        <clinical_division>HUB.AOVET</clinical_division>
                        <expertise_level>HUB.LOW</expertise_level>
                    </expertise>
                    <expertise>
                        <clinical_division>HUB.AORECON</clinical_division>
                        <expertise_level>HUB.HIGH</expertise_level>
                    </expertise>
                    <expertise>
                        <clinical_division>HUB.AORECON</clinical_division>
                        <expertise_level>HUB.MEDIUM</expertise_level>
                    </expertise>
                    <expertise>
                        <clinical_division>HUB.AORECON</clinical_division>
                        <expertise_level>HUB.LOW</expertise_level>
                    </expertise>
                </expertises>
                <birthday>1994-03-09</birthday>
                <graduation_year>2018</graduation_year>
            </contact>
        </Fields>
    </Record>
</RecordQueryResponse>
*/

import java.util.Properties;
import java.io.InputStream;
import java.text.SimpleDateFormat
import java.time.*
import com.boomi.execution.ExecutionUtil;

logger = ExecutionUtil.getBaseLogger();

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def root = new XmlSlurper().parseText(is.text)
    def contact = root.Record.Fields.contact

    // do calculations, return integer values
    int yearsInPractice = getYearsInPractice(contact.graduation_year.toString())
    int age = getAge(contact.birthday.toString())
    int countExpertisesHigh = getCountExpertises(contact.expertises,"high")
    int countExpertisesMedium = getCountExpertises(contact.expertises,"medium")

    // business logic: determine professionalExperienceLevel (PEL)
    String professionalExperienceLevel
    if ( yearsInPractice >= 18
        || age > 45
        || countExpertisesHigh >= 4 ) {
        professionalExperienceLevel = "High"
    }
    else if ( (yearsInPractice > 8 && yearsInPractice < 18)
        || (age > 35 && age < 45)
        || countExpertisesHigh == 3
        || countExpertisesMedium >= 6 ) {
        professionalExperienceLevel = "Medium"
    }
    else professionalExperienceLevel = "Low"

    // props.setProperty("document.dynamic.userdefined.DDP_ProfessionalExperienceLevel",professionalExperienceLevel)

    logger.warning("\nYears In Practice ........ " + yearsInPractice.toString())
    logger.warning("Age ...................... " + age.toString())
    logger.warning("Count Expertises High .... " + countExpertisesHigh.toString())
    logger.warning("Count Expertises Medium .. " + countExpertisesMedium.toString() + "\n---")
    logger.warning("PEL: " + professionalExperienceLevel + "\n")

    def outData = groovy.xml.XmlUtil.serialize(root).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim()
    is = new ByteArrayInputStream(outData.getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}

// Years in Practice (YIP)
private int getYearsInPractice(graduationYear) {
    if (graduationYear) {
        Date date = new Date();
        def df_year = new SimpleDateFormat("yyyy")
        def currentYear = df_year.format(date) as int
        return currentYear - graduationYear.toInteger()
    }
    else return 0
}

// Age
private int getAge(birthday) {
if (birthday) {
    LocalDate today = LocalDate.now()
    LocalDate birthdayParsed = LocalDate.parse(birthday)
    return Period.between(birthdayParsed,today).getYears()
    } 
    else return 0
}

// Declared Expertises (DE)
private int getCountExpertises(expertises,level) {
    int count = 0
    expertises.children().findAll() {
        it.expertise != "" &&
        it.expertise_level.toString().toLowerCase().contains(level.toLowerCase())
        }.each() {
            count += it.expertise.toString().split(";").size()
        }
    return count
}

