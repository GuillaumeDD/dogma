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
package fr.dubuissonduplessis.simpleSemantics.shortAnswer

import fr.dubuissonduplessis.simpleSemantics.Sentence
import fr.dubuissonduplessis.predicateCalculus.Individual
import fr.dubuissonduplessis.predicateLogic.BooleanExpression
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet
import fr.dubuissonduplessis.predicateCalculus.Unifiable
import fr.dubuissonduplessis.predicateCalculus.SubstitutionIdentity

/**
 * Represents '''elliptical''' utterances such as "yes", "no", "beer".
 * Short answers can be viewed as underspecified propositions. They are represented
 * as constants.
 */
abstract class ShortAns extends Sentence
  with Unifiable[ShortAns]
  with Individual
  with BooleanExpression[ShortAns]
  with SubstitutionIdentity[ShortAns] {
  def neg: ShortAns
  def negation: Boolean =
    false
}

object ShortAns {
  /**
   * Builds a short answer from a name of a constant.
   * @param name Name of the constant that is the short answer.
   * @return A short answer from the specified name.
   */
  def apply(name: String): ShortAns =
    if (name == "yes")
      Yes
    else if (name == "no")
      No
    else
      ShortAnsImpl(name)

  def apply(ind: Individual): ShortAns =
    if (ind.name == "yes")
      Yes
    else if (ind.name == "no")
      No
    else
      ShortAnsImpl(ind.name)

  private case class ShortAnsImpl(name: String) extends ShortAns {
    def neg: ShortAns =
      NegShortAns[ShortAns](this)
  }
}
