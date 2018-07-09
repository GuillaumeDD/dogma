/*******************************************************************************
 * Copyright (c) 2014 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Maxime MORGE
 *     Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> - initial API and implementation
 ******************************************************************************/
package fr.lifl.smac.scadia.dialogueManager.reasoning.openInterrogation.strategy.impl

import fr.lifl.smac.scadia.dialogueManager.reasoning.openInterrogation.strategy.EvaluationStrategy
import fr.lifl.smac.scadia.dialogueManager.reasoning._
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor

//A skeptical strategy to evaluate content
trait SkepticalEvaluation extends EvaluationStrategy {
  this: PatternReasonerThing =>

  protected def evaluate(
    goal: QuestionType,
    partner: Interlocutor,
    answerContent: PropContent): Unit =
    println(s"$me does not believe $answerContent")
}

//A credulous strategy to evaluate the content
trait CredulousEvaluation extends EvaluationStrategy {
  this: PatternReasonerThing =>

  protected def evaluate(
    goal: QuestionType,
    partner: Interlocutor,
    answerContent: PropContent): Unit =
    println(s"$me believes $answerContent")
}
