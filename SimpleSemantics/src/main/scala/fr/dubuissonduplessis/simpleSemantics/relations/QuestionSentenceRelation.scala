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
package fr.dubuissonduplessis.simpleSemantics.relations

import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition
import fr.dubuissonduplessis.simpleSemantics.question.Question
import fr.dubuissonduplessis.predicateCalculus.Individual
import fr.dubuissonduplessis.simpleSemantics.shortAnswer.ShortAns
import fr.dubuissonduplessis.simpleSemantics.Sentence

trait QuestionSentenceRelation {
  def apply(s: Sentence, q: Question)(implicit semCompatible: (Individual, Question) => Boolean): Boolean
  def apply(p: Proposition, q: Question)(implicit semCompatible: (Individual, Question) => Boolean): Boolean
  def apply(ans: ShortAns, q: Question)(implicit semCompatible: (Individual, Question) => Boolean): Boolean
}
