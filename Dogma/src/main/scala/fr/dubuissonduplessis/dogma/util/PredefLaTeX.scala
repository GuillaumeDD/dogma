/**
 * *****************************************************************************
 * Copyright (c) 2014 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> - initial API and implementation
 * ****************************************************************************
 */
package fr.dubuissonduplessis.dogma.util

/**
 * Provides some predefined LaTeX representation.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
object PredefLaTeX {
  // LaTeX Constants
  val emptySet: String = """\emptyset"""
  val emptyList: String = """\emptyset"""
  val latexNewline = """\\"""

  /**
   * Mapping between special LaTeX characters and their
   * translation.
   *
   */
  val escapedCharacter = scala.collection.immutable.ListMap( // Order is important!
    //java.util.regex.Pattern.quote("""\""") -> """\\textbackslash{}""",
    java.util.regex.Pattern.quote("{") -> """\\{""",
    java.util.regex.Pattern.quote("}") -> """\\}""",
    java.util.regex.Pattern.quote("#") -> """\\#""",
    java.util.regex.Pattern.quote("$") -> """\\$""",
    java.util.regex.Pattern.quote("%") -> """\\%""",
    java.util.regex.Pattern.quote("&") -> """\\&""",
    java.util.regex.Pattern.quote("^") -> """\\textasciicircum{}""",
    java.util.regex.Pattern.quote("_") -> """\\_""",
    java.util.regex.Pattern.quote("~") -> """\\textasciitilde{}""")

  def escapeLaTeXSpecialCharacters(s: String): String =
    escapedCharacter.foldLeft(s) {
      case (resultingString, (initialChar, replacementChar)) =>
        resultingString.replaceAll(initialChar, replacementChar)
    }

  def textInMathMode(latexRepr: String): String =
    """\textnormal{""" + latexRepr + """}"""

  def toLatexList(latexRepr: List[String]): String =
    """\liste{
    """ +
      latexRepr.map(textInMathMode(_)).mkString(", \\\\\n") +
      """
    }"""

  def toLatexSet(latexRepr: List[String], newline: String): String =
    """\ensemble{
    """ +
      latexRepr.map(textInMathMode(_)).mkString(", " + newline) +
      """
    }"""

  /**
   * Emphasizes a LaTeX representation as a new one.
   */
  def newWrapping(latexRepr: String): String =
    """\nouveau{""" + latexRepr + """}"""
}
