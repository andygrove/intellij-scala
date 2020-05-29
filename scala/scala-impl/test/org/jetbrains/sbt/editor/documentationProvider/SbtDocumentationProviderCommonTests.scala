package org.jetbrains.sbt.editor.documentationProvider

trait SbtDocumentationProviderCommonTests {
  self: SbtDocumentationProviderTestBase =>

  def testSbtDescriptionShouldBeWrappedInDefaultScaladocTemplate(): Unit =
    doGenerateDocTest(
      s"""val ${CARET}someKey = SettingKey[Int]("some-key", "This is description for some-key")""",
      s"""$DocStart
         |${DefinitionStart}val <b>someKey</b>: <a href="psi_element://sbt.SettingKey"><code>SettingKey</code></a>[<a href="psi_element://scala.Int"><code>Int</code></a>]$DefinitionEnd
         |$ContentStart
         |This is description for some-key
         |$ContentEnd
         |$DocEnd
         |""".stripMargin
    )

  def testSettingKey(): Unit = doGenerateSbtDocDescriptionTest(
    s"""val ${CARET}someKey = SettingKey[Int]("some-key", "$commonDescription")""",
    commonDescription
  )

  def testAttributeKey(): Unit = doGenerateSbtDocDescriptionTest(
    s"""val ${CARET}someKey = AttributeKey[Int]("some-key", "$commonDescription")""",
    commonDescription
  )

  def testTaskKey(): Unit = doGenerateSbtDocDescriptionTest(
    s"""val ${CARET}someKey = TaskKey[Int]("some-key", "$commonDescription")""",
    commonDescription
  )

  def testInputKey(): Unit = doGenerateSbtDocDescriptionTest(
    s"""val ${CARET}someKey = InputKey[Int]("some-key", "$commonDescription")""",
    commonDescription
  )

  def testKeyWithLabelAndDescription(): Unit = doGenerateSbtDocDescriptionTest(
    s"""val ${CARET}someKey = SettingKey[Int]("some-key", "$commonDescription")""",
    commonDescription
  )

  def testKeyWithLabelAndDescriptionAndRank(): Unit = doGenerateSbtDocDescriptionTest(
    s"""val ${CARET}someKey = SettingKey[Int]("some-key", "$commonDescription", KeyRanks.APlusSetting)""",
    commonDescription
  )

  def testKeyWithLabelAndDescriptionAndExtend(): Unit = doGenerateSbtDocDescriptionTest(
    s"""val someKey = SettingKey[Int]("some-key")
       |val ${CARET}someKey1 = SettingKey[Int]("some-key-1", "$commonDescription", someKey)
       |""".stripMargin,
    commonDescription
  )

  def testKeyWithLabelAndDescriptionAndRankAndExtend(): Unit = doGenerateSbtDocDescriptionTest(
    s"""val someKey = SettingKey[Int]("some-other-key")
       |val ${CARET}someKey1 = SettingKey[Int]("some-key", "$commonDescription", KeyRanks.APlusSetting, someOtherKey)
       |""".stripMargin,
    commonDescription
  )

  def testKeyWithReferenceDescription(): Unit = doGenerateSbtDocDescriptionTest(
    s"""val someValue: String = "some text"
       |val ${CARET}someKey = SettingKey[Int](someValue)
       |""".stripMargin,
    s"""<i>someValue</i>"""
  )

  def testUseLabelAsDescriptionIfDescriptionIsMissing(): Unit = doGenerateSbtDocDescriptionTest(
    s"""val ${CARET}someKey = SettingKey[Int]("some-key-label")""",
    "some-key-label"
  )

  def testDoNotDetectDocumentationForNonKeyApplyMethod(): Unit = doGenerateDocTest(
    s"""val ${CARET}someKey = SomeUnknownClass[Int]("some-key", "$commonDescription")""",
    null: String
  )
}
