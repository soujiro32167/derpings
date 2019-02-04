import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable.ArrayBuffer

object SparkMock {
  def isSpace(c: Char): Boolean = {
    " \t\r\n".indexOf(c) != -1
  }

  def splitCommandString(s: String): Seq[String] = {
    val buf = new ArrayBuffer[String]
    var inWord = false
    var inSingleQuote = false
    var inDoubleQuote = false
    val curWord = new StringBuilder
    def endWord() {
      buf += curWord.toString
      curWord.clear()
    }
    var i = 0
    while (i < s.length) {
      val nextChar = s.charAt(i)
      if (inDoubleQuote) {
        if (nextChar == '"') {
          inDoubleQuote = false
        } else if (nextChar == '\\') {
          if (i < s.length - 1) {
            // Append the next character directly, because only " and \ may be escaped in
            // double quotes after the shell's own expansion
            curWord.append(s.charAt(i + 1))
            i += 1
          }
        } else {
          curWord.append(nextChar)
        }
      } else if (inSingleQuote) {
        if (nextChar == '\'') {
          inSingleQuote = false
        } else {
          curWord.append(nextChar)
        }
        // Backslashes are not treated specially in single quotes
      } else if (nextChar == '"') {
        inWord = true
        inDoubleQuote = true
      } else if (nextChar == '\'') {
        inWord = true
        inSingleQuote = true
      } else if (!isSpace(nextChar)) {
        curWord.append(nextChar)
        inWord = true
      } else if (inWord && isSpace(nextChar)) {
        endWord()
        inWord = false
      }
      i += 1
    }
    if (inWord || inDoubleQuote || inSingleQuote) {
      endWord()
    }
    buf
  }
}

class SparkSystepProprtyTest extends FlatSpec with Matchers {

  "Spark system property parser" should "do quotes right" in {
    SparkMock.splitCommandString("a 'b\" c' \"d' e\"") shouldEqual Seq("a", "b\" c", "d' e")
    SparkMock.splitCommandString("-DsomeProp=g.V().hasLabel(\"'enodeb'\")") shouldEqual Seq("-DsomeProp=g.V().hasLabel('enodeb')")
    SparkMock.splitCommandString("-DsomeProp=\"g.V().hasLabel('enodeb').has('thing', 'thing')\"") shouldEqual Seq("-DsomeProp=g.V().hasLabel('enodeb').has('thing', 'thing')")
  }
}
