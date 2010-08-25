package org.parboiled.scala

import org.testng.annotations.Test
import org.scalatest.testng.TestNGSuite
import org.testng.Assert.assertEquals
import org.parboiled.matchers.Matcher
import org.parboiled.support.ToStringFormatter
import org.parboiled.trees.{Filters, GraphUtils}
import test.ParboiledTest

class RecursionTest extends ParboiledTest with TestNGSuite {

  class RecursionParser extends Parser {
    def LotsOfAs: Rule0 = rule {ignoreCase('a') ~ optional(LotsOfAs)}
  }

  val parser = new RecursionParser().withParseTreeBuilding()

  @Test
  def testRuleTreeConstruction() {
    val rule = parser.LotsOfAs
    assertEquals(GraphUtils.printTree(rule.matcher, new ToStringFormatter[Matcher], Filters.preventLoops()),
      """|LotsOfAs
         |  'a/A'
         |  Optional
         |    LotsOfAs
      |""".stripMargin);
  }

  @Test
  def testRecursion() {
    parse(parser.LotsOfAs, "aAAAa") {
      assertEquals(parseTree,
         """|[LotsOfAs] 'aAAAa'
            |  ['a/A'] 'a'
            |  [Optional] 'AAAa'
            |    [LotsOfAs] 'AAAa'
            |      ['a/A'] 'A'
            |      [Optional] 'AAa'
            |        [LotsOfAs] 'AAa'
            |          ['a/A'] 'A'
            |          [Optional] 'Aa'
            |            [LotsOfAs] 'Aa'
            |              ['a/A'] 'A'
            |              [Optional] 'a'
            |                [LotsOfAs] 'a'
            |                  ['a/A'] 'a'
            |                  [Optional]
            |""".stripMargin)
    }
  }

}