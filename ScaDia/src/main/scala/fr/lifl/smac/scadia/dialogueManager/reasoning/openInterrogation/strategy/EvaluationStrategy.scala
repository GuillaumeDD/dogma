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
package fr.lifl.smac.scadia.dialogueManager.reasoning.openInterrogation.strategy
import fr.lifl.smac.scadia.dialogueManager.reasoning._
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor

trait EvaluationStrategy {
  this: PatternReasonerThing =>

  protected def evaluate(
    goal: QuestionType,
    partner: Interlocutor,
    answerContent: PropContent): Unit
}
