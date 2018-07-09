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
/**
 * *****************************************************************************
 * Copyright (c) 2013 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> - initial API and implementation
 * ****************************************************************************
 */
package fr.dubuissonduplessis

/**
 * Provides traits and default implementation for dealing with
 * predicate calculus.
 *
 * == Predicate Calculus ==
 * This summary comes from [[http://en.wikipedia.org/wiki/Predicate_calculus Wikipedia]].
 * The predicate calculus is a formal language made up of tokens
 * and grammar for creating predicate names, variables and constants.
 * Predicate calculus symbols may represent '''variables''', constant or '''individuals''',
 * functions or '''predicates'''.
 *
 *  - Constants (or '''individuals''') name specific objects or properties in the domain of
 *  discourse (e.g., `john`, `mary`, `scala`)
 *  - '''Variable''' are used to designate general classes or objects or properties in the
 *  domain of discourse.
 *  - Functions (or '''predicate''') denote a mapping of one or more elements in a set (the
 *  domain of the function) into a unique element of another set (the range of the function).
 *  Elements of the domain and range are objects in the world of discourse. Every function
 *  symbol has an associated '''arity''', indicating the number of elements in the domain
 *  mapped onto each element of range.
 *
 * A '''predicate expression''' consists of a function symbol (i.e., a '''predicate name''')
 * followed by its arguments. The arguments of a predicate can be individuals, variables or
 * predicates. A predicate may have zero or more arguments. For example, `love(john, mary)` is
 * a 2-argument predicate which name is "love" and arguments are individuals `john` and `mary`.
 *
 * A function is interpreted by replacing it and its arguments taken from the domain of
 * discourse  by the unique constant that is the function's evaluation. This library does not
 * deal with function's evaluation (e.g. `friend_of(luke)` evaluated to `han_solo`).
 *
 * Finally, '''unification''' is the algorithm that determines a variable substitutions set
 * (if it exists) that makes it possible to match two predicate expressions. For example, a
 * substitution set that makes it possible to match `love(john, mary)` and `love(john, X)`
 * (where `X` is a variable) is `X/mary`.
 *
 * == Overview ==
 * The main trait to see is [[fr.dubuissonduplessis.predicateCalculus.Unifiable]] that defines
 * a unifiable symbol. This package comes with the following specialized traits that may be mixed in:
 *  - [[fr.dubuissonduplessis.predicateCalculus.Individual]] that represents an individual (or a constant).
 *  - [[fr.dubuissonduplessis.predicateCalculus.Variable]] that represents a variable.
 *  - [[fr.dubuissonduplessis.predicateCalculus.PredN]] that represents a predicate expression.
 *
 * 	Subpackage [[fr.dubuissonduplessis.predicateCalculus.substitution]] provides tools to represent
 * 	[[fr.dubuissonduplessis.predicateCalculus.substitution.Substitution]] and
 * 	[[fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet]].
 * == Usage Example ==
 *  {{{
 * import fr.dubuissonduplessis.predicateCalculus._
 *
 * val john = Individual("john")
 * val mary = Individual("mary")
 * val X = Variable("X")
 *
 * val pred01 = PredN("love", List(john, mary))
 * val pred02 = PredN("love", List(john, X))
 *
 * val substitutionSet = pred01.unifyWith(ored02)
 *
 * substitutionSet match {
 * 	case None => println("Impossible unification!")
 * 	case Some(subSet) =>
 * 		val newPred = pred02.substitute(subSet)
 * 		println(newPred)
 * }
 *
 * }}}
 */
package object predicateCalculus {
  /**
   * Type representing a generic unifiable.
   */
  type AnyUnifiable = Unifiable[_ <: Unifiable[_]]

  // TODO Regroup LaTeX utilies with PredefLaTeX
  /**
   * Mapping between special LaTeX characters and their
   * translation.
   */
  val escapedCharacter = scala.collection.immutable.ListMap(
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

  def unifiableToLatex(unifiable: AnyUnifiable): String =
    unifiable match {
      case ind: Individual =>
        """\semInd{""" + escapeLaTeXSpecialCharacters(ind.name) + """}"""
      case variable: Variable =>
        """\semVar{""" + escapeLaTeXSpecialCharacters(variable.name) + """}"""
      case pred: PredN =>
        pred.arity match {
          case 0 =>
            """\semPredZero{""" + escapeLaTeXSpecialCharacters(pred.name) + """}"""
          case 1 =>
            """\semPredUn{""" + escapeLaTeXSpecialCharacters(pred.name) + """}""" +
              """{""" + unifiableToLatex(pred.terms(0)) + """}"""
          case 2 =>
            """\semPredDeux{""" + pred.name + """}""" +
              """{""" + unifiableToLatex(pred.terms(0)) + """}""" +
              """{""" + unifiableToLatex(pred.terms(1)) + """}"""
          case 3 =>
            """\semPredTrois{""" + pred.name + """}""" +
              """{""" + unifiableToLatex(pred.terms(0)) + """}""" +
              """{""" + unifiableToLatex(pred.terms(1)) + """}""" +
              """{""" + unifiableToLatex(pred.terms(2)) + """}"""
          case _ =>
            pred.toString
        }
    }
}
