/* @data
Appearance (Visible Particles),Visible Particulates,1,1,10
Appearance,Clarity,1,2,7
Appearance,Coloration,1,3,10
pH,pH,1,4,4
UV Spectroscopy,Protein Concentration,1,5,10
Subvisible Particles,Subvisible Particles >= 10µm,1,6,10
Subvisible Particles,Subvisible Particles >= 25µm,1,7,10
ICE,Acidic Species,1,8,9
ICE,Main Species,1,9,9
ICE,Basic Species,1,10,9
SE-HPLC,Monomer,2,11,9
SE-HPLC,HMMS,2,12,8
CGE (reducing),Heavy Chain + Light Chain,2,13,9
CGE (reducing),Fragments,2,14,9
CGE (non-reducing),Intact IgG,2,15,8
CGE (non-reducing),Fragments,2,16,8
Cell-based Bioassay,Relative Potency,2,17,10
AEX-HPLC,Anti-CD3 mAb,2,18,9
CEX-HPLC,Anti-BCMA mAb,2,19,9
CEX-HPLC,Anti-BCMA/Anti-CD3 Bispecific Clip,2,20,9
DYE_INGRESS,Dye Ingress,2,21,7
ENDOTOXIN,Endotoxin,2,22,7
Sterility,Sterility,2,23,7
*/

import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;

logger = ExecutionUtil.getBaseLogger();

def LINE_SEPARATOR = System.getProperty("line.separator");

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def dataArr = is.text.split("\r?\n")
    def widthsFinalArr = []
    def outData = new StringBuffer()

    (1..2).each() { t ->
        // array of ints to contain raw widths
        def rawWidthsArr = []

        // parse raw widths from source data into rawWidthsArr
        dataArr.each() { line -> 
            def lineArr = line.split(",")
            def tableIndex = lineArr[2]
            def rawWidth = lineArr[4] as int
            if (tableIndex as int == t) rawWidthsArr.push(rawWidth)
        }
        // logger.warning("table: " + t + " " + rawWidthsArr)

        // calculate percentage multiplier
        def multiplier = 100 / rawWidthsArr.sum()

        // create array with widths as percentages 
        // can add up to more or less than 100 because of rounding
        def widthsArr = rawWidthsArr.collect { (it.multiply(multiplier) as double).round() }
        // logger.warning("widthsArr: " + widthsArr.toString())

        // calculate the difference between 100 and the sum of widths
        def difference = 100 - widthsArr.sum()
        // logger.warning("difference: " + difference)

        /* Distribute the difference among the highest/lowest widths */

        // create a sorted version of the widthsArr
        def widthsArrSorted = widthsArr.clone().sort()
        if (difference < 0) widthsArrSorted.reverse()

        // loops as many times as the difference between 100 and the sum of widths
        for (j=0;j<Math.abs(difference);j++) {
            // find the value of sorted array in the original arrray
            def width = widthsArr.indexOf(widthsArrSorted[j])
            // add one or subtract one from it
            if (difference > 0) widthsArr[width] = widthsArr[width] + 1
            else  widthsArr[width] = widthsArr[width] - 1
        }
        // logger.warning("widthsArr: " + widthsArr.toString() + " Total: " + widthsArr.sum())

        widthsFinalArr += widthsArr
    }
    // logger.warning("widthsFinalArr: " + widthsFinalArr.toString() + " size: " + widthsFinalArr.size())
    
    /* Put adjested widths back into table */
    dataArr.eachWithIndex() { line, index ->
        def lineArr = line.split(",")
        def tableIndex = lineArr[2]
        lineArr[4] = widthsFinalArr[index]
        outData.append(lineArr.join(",") + LINE_SEPARATOR)
    }

    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}
