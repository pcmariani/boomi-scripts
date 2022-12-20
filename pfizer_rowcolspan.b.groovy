/* @data
  @file('../boomi-scripts/rowcolspan_data.htm')
*/
// @noresult

import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;

logger = ExecutionUtil.getBaseLogger();

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
  InputStream is = dataContext.getStream(i);
  Properties props = dataContext.getProperties(i);

  def root = new XmlSlurper().parse(is)

  root.'**'.findAll() { node -> node.name() == "table" }.each() { table ->

    /*
     * --- RowSpan ---
     */
    def rowSpanMap = [:]

    table.tr[0..1].eachWithIndex() { tr, r ->
      tr.children().eachWithIndex() { th, h ->
        if (!rowSpanMap."$h") {
          if (th.text() == "") rowSpanMap."$h" = ["HAS_EMPTY",th.text()]
          else rowSpanMap."$h" = ["---",th.text()]
        }
        else {
          if (rowSpanMap."$h"[0] == "HAS_EMPTY") rowSpanMap."$h"[1] = th.text()
          if (th.text() == "") rowSpanMap."$h" = ["HAS_EMPTY",rowSpanMap."$h"[1]]
        }
      }
    }
    // logger.warning(rowSpanMap.toString())

    table.tr[0].eachWithIndex() { tr, r ->
      tr.children().eachWithIndex() { th, h ->
      // logger.warning(h + " " + th + " " + rowSpanMap."$h")
      if (rowSpanMap."$h"[0] == "HAS_EMPTY") {
        // logger.warning(h + " " + rowSpanMap."$h")
        if (th.text()) th.value = rowSpanMap."$h"[1]
        else th << rowSpanMap."$h"[1]
          th.@rowSpan = "2"
        }
      }
    }

    table.tr[1].eachWithIndex() { tr, r ->
      tr.children().eachWithIndex() { th, h ->
        if (rowSpanMap."$h"[0] == "HAS_EMPTY") th.replaceNode {}
      }
    }


    /*
     * --- ColSpan ---
     */

    def colSpanMap = [:]
    def prevAssay = ""
    def assayIndex = 1

    table.tr[0].children().eachWithIndex() { assay, j ->
      if (assay.text() == prevAssay) assayIndex++
      else assayIndex = 1
      if (assay.text()) colSpanMap."${assay}" = assayIndex
      prevAssay = assay.text()
    }
    // logger.warning(colSpanMap)

    prevAssay = ""
    table.tr[0].children().eachWithIndex() { assay, j ->
      def colSpanVal = colSpanMap."${assay}"
      if (colSpanVal > 1) {
        if (assay.text() != prevAssay) assay.@colspan = colSpanVal.toString()
        else assay.replaceNode{}
        // logger.warning(j + " " + assay + " " + colSpanMap."${assay}")
      }
      prevAssay = assay.text()
    }
  }

  def outData = groovy.xml.XmlUtil.serialize(root).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim()

  File fileOut = new File("../boomi-scripts/pfizer_rowcolspan_output.htm")
  fileOut.write outData

  is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"));
  dataContext.storeStream(is, props);
}

