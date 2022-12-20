/* @data
  @file('../boomi-scripts/rowcolspan_data.htm')
*/
// @noresult

import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;

logger = ExecutionUtil.getBaseLogger();

def rowStart = 3
def rowEnd = -1
def colStart = 0
def colEnd = 1

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
  InputStream is = dataContext.getStream(i);
  Properties props = dataContext.getProperties(i);

  def root = new XmlSlurper().parse(is)

  root.'**'.findAll() { node -> node.name() == "table" }.eachWithIndex() { table, e ->

    rowEnd = getEndIndex(table.tr.size(), rowEnd)
    colEnd = getEndIndex(table.tr.children().size(), colEnd)

    def rowSpanArr = getRowSpanAndDeletionArr(table, rowStart, rowEnd, colStart, colEnd)

    table.tr[rowStart..rowEnd].eachWithIndex() { tr, r ->
      tr.children()[colStart..colEnd].eachWithIndex() { it, s ->
        // println r + " " + countArr[r][s] + " " + delArrRev[r][s].getClass() + " " + tr.children()[0..1][s]
        if (rowSpanArr[r][s] == "DELETE") it.replaceNode{}
        else if (rowSpanArr[r][s].getClass() == java.lang.Integer) {
          it.@rowSpan = rowSpanArr[r][s].toString()
        }
      }
    }
  }

  def outData = groovy.xml.XmlUtil.serialize(root).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim()

  File fileOut = new File("../boomi-scripts/pfizer_rowcolspan_output.htm")
  fileOut.write outData

  outData = ""
  is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"));
  dataContext.storeStream(is, props);
}

// traverse backwards through the columns (the TH's and TD's which are the
// children of the TR). If the item above the current one is the same, start
// a counter to count how many of the same items there are. If the next item
// above is different then the counter resets.
def getRowSpanAndDeletionArr(table, rowStart, rowEnd, colStart, colEnd) {
    def tdsPrevsArr = []
    def countArr = []
    def delArr = []
    table.tr[rowStart..rowEnd].reverse().eachWithIndex() { tr, r ->
      def numCols = colEnd - colStart + 1
      countArr[r] = [0]*numCols
      delArr[r] = [null]*numCols
      tr.children()[colStart..colEnd].eachWithIndex() { it, s ->
        // if the value of the previous element is the same,
        if (it == tdsPrevsArr[s]) {
          // set the counter for this element to the preveious + 1
          countArr[r][s] = countArr[r-1][s] + 1
          // set the action for the previous element to delete
          delArr[r-1][s] = "DELETE"
        }
        // if the counter for the previous element is not 0, 
        // meaning repeating group has ended
        else if (countArr[r-1][s] != 0){ 
          // set the action of the previous element to rowspan number
          delArr[r-1][s] = (countArr[r-1][s] + 1)
        }
        tdsPrevsArr[s] = it
      }
    }
    return delArr.reverse()
}

def getEndIndex(numItems, endIndex) {
  if (endIndex == -1) return numItems - 1
  else return endIndex
}
