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
 * Represents parametrized wh-question such as ?X.definition(paludism, french, X).
 * The two first arguments are the parameters as an individuals.
 * The last argument is the unique variable part of the lambda abstraction.
 *
 * @param name Name of the proposition
 * @param parameter1 First parameter as an individual
 * @param parameter2 Second parameter as an individual
 * @param variable Variable of the question
 *
 */
case class ParametrizedWhQuestion3(
  name: String,
  parameter1: Individual,
  parameter2: Individual,
  variable: Variable = Variable("X")) extends AbstractWhQuestion
  with SubstitutionIdentity[ParametrizedWhQuestion3] {

  def terms: List[AnyUnifiable] =
    List(parameter1, parameter2, variable)

  override def toString: String =
    "?" + variable + "." + name + "(" + parameter1 + ", " + parameter2 + ", " + variable + ")"
}
