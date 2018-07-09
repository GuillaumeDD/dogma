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
package fr.dubuissonduplessis.simpleSemantics.proposition.questionProposition

import fr.dubuissonduplessis.predicateLogic.BooleanExpression
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet
import scala.collection.immutable.List
import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition
import fr.dubuissonduplessis.simpleSemantics.question.Question
import fr.dubuissonduplessis.predicateCalculus.Unifiable
/**
 * Represents a 1-ary question proposition, i.e. a proposition which unique argument
 * is a question.
 *
 */
abstract class QuestionProposition1 extends QuestionProposition {
  def terms: List[Question] =
    List(question)
}
