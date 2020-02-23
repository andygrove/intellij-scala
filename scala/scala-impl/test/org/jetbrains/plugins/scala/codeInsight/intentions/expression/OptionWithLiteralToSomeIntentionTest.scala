package org.jetbrains.plugins.scala.codeInsight.intentions.expression

import org.jetbrains.plugins.scala.codeInsight.intention.expression.OptionWithLiteralToSomeIntention
import org.jetbrains.plugins.scala.codeInsight.intentions.ScalaIntentionTestBase

/**
  * Nikolay.Tropin
  * 07-Nov-17
  */
class OptionWithLiteralToSomeIntentionTest extends ScalaIntentionTestBase {
  override val familyName = OptionWithLiteralToSomeIntention.familyName

  def testString() {
    doTest(
      s"""${caretTag}Option("constant")""",
      """Some("constant")""")
  }

  def testInt(): Unit = {
    doTest(
      s"Option(${caretTag}1)",
      "Some(1)"
    )
  }

  def testNull(): Unit = {
    checkIntentionIsNotAvailable(s"Option$caretTag(null)")
  }
}

