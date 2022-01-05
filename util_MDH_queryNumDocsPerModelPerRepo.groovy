import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;
logger = ExecutionUtil.getBaseLogger();

String UNIVERSE_NAME = ExecutionUtil.getDynamicProcessProperty("MODEL_NAME")
int QUERY_REQUEST_LIMIT = (ExecutionUtil.getDynamicProcessProperty("QUERY_REQUEST_LIMIT") ?: 200) as int
int BATCH_LIMIT = (ExecutionUtil.getDynamicProcessProperty("BATCH_LIMIT") ?: 100000) as int
String NUM_GOLDEN_RECS_OVERRIDE = ExecutionUtil.getDynamicProcessProperty("NUM_GOLDEN_RECS_OVERRIDE")

logger.warning("ATOM_NAME:" + ExecutionUtil.getDynamicProcessProperty("ATOM_NAME"))
logger.warning("MODEL_NAME:" + UNIVERSE_NAME)
logger.warning("UNIVERSE_ID:" + ExecutionUtil.getDynamicProcessProperty("UNIVERSE_ID"))
logger.warning("QUERY_REQUEST_LIMIT:" + QUERY_REQUEST_LIMIT)
logger.warning("NUM_GOLDEN_RECS_OVERRIDE:" + NUM_GOLDEN_RECS_OVERRIDE)

InputStream is = dataContext.getStream(0);
Properties props = dataContext.getProperties(0);

int numGoldenRecords = 0
if (!NUM_GOLDEN_RECS_OVERRIDE) {
    def root = new XmlSlurper().parse(is)
    numGoldenRecords = root.Universe.find() { 
        it.@name.text().toLowerCase() == UNIVERSE_NAME.toLowerCase()
    }.goldenRecords.text() as int
}
else {
    numGoldenRecords = NUM_GOLDEN_RECS_OVERRIDE as int
}

int numBoomiDocsToOutput = Math.ceil( numGoldenRecords / QUERY_REQUEST_LIMIT )

logger.warning("numGoldenRecords:" + numGoldenRecords)
logger.warning("numBoomiDocsToOutput:" + numBoomiDocsToOutput)

int i = 1;
while (i <= (numBoomiDocsToOutput) && i <= BATCH_LIMIT) {

    is = new ByteArrayInputStream((i + 1).toString().getBytes("UTF-8"));
    dataContext.storeStream(is, props);

    i++;
}

logger.warning("BATCH_LIMIT:" + BATCH_LIMIT)
logger.warning("last value of i:" + i)
