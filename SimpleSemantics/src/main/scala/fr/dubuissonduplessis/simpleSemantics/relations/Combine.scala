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
package fr.dubuissonduplessis.simpleSemantics.relations
import fr.dubuissonduplessis.simpleSemantics.question.Question
import fr.dubuissonduplessis.predicateCalculus.Individual
import fr.dubuissonduplessis.simpleSemantics.proposition._
import fr.dubuissonduplessis.simpleSemantics.question._
import fr.dubuissonduplessis.simpleSemantics.shortAnswer.ShortAns

object Combine {
  def apply(p: Proposition, q: Question)(implicit semCompatible: (Individual, Question) => Boolean): Option[Proposition] =
    if (Relevant(p, q)) {
      Some(p)
    } else {
      None
    }

  def apply(ans: ShortAns, q: Question)(implicit semCompatible: (Individual, Question) => Boolean): Option[ShortAns] =
    if (Relevant(ans, q)) {
      Some(ans)
    } else {
      None
    }
}
