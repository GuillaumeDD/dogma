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
package fr.dubuissonduplessis.simpleSemantics.question

import fr.dubuissonduplessis.predicateCalculus.SubstitutionIdentity
import fr.dubuissonduplessis.predicateCalculus.Variable

/**
 * Represents a wh-question such as "What kind of beer do you want?"
 * as a lambda-abstracts of propositions (e.g., "?X.kind_of_beer(X)").
 *
 * This class represents a Wh-Question as a 1-ary abstracted predicate.
 *
 * @param name Name of the proposition lambda-abstracted .
 * @param variable Variable of the proposition.
 *
 */
case class WhQuestion(
  name: String,
  variable: Variable = Variable("X")) extends AbstractWhQuestion
  with SubstitutionIdentity[WhQuestion] {

  def terms: List[Variable] =
    List(variable)

  override def toString: String =
    "?" + variable + "." + name + "(" + variable + ")"
}
