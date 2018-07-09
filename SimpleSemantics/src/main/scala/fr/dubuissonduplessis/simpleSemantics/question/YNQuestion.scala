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
package fr.dubuissonduplessis.simpleSemantics.question
import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet

/**
 * Represents a Yes/No Question such as "Do you want a beer?" as an operator
 * "?" about a proposition.
 * @param prop The proposition that truth is being asked.
 */
case class YNQuestion(
  prop: Proposition) extends Question {
  def name: String = "?" + prop.toString
  def terms: List[Proposition] =
    List(prop)

  def substitute(subSet: SubstitutionSet): YNQuestion =
    YNQuestion(prop.substitute(subSet))

  override def toString: String =
    name
}
