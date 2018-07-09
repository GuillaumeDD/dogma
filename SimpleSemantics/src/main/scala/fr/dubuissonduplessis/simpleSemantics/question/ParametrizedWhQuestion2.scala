/*******************************************************************************
 * Copyright (c) 2014 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> - initial API and implementation
 ******************************************************************************/
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
import fr.dubuissonduplessis.predicateCalculus.Variable
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet
import fr.dubuissonduplessis.predicateCalculus.Individual
import fr.dubuissonduplessis.predicateCalculus._

/**
 * Represents parametrized wh-question such as ?X.synonym(paludism, X).
 * The first argument is the parameter as an individual.
 * The last argument is the unique variable part of the lambda abstraction.
 *
 * @param name Name of the proposition
 * @param parameter Parameter as an individual
 * @param variable Variable of the question
 *
 */
case class ParametrizedWhQuestion2(
  name: String,
  parameter: Individual,
  variable: Variable = Variable("X")) extends AbstractWhQuestion
  with SubstitutionIdentity[ParametrizedWhQuestion2] {

  def terms: List[AnyUnifiable] =
    List(parameter, variable)

  override def toString: String =
    "?" + variable + "." + name + "(" + parameter + ", " + variable + ")"
}
