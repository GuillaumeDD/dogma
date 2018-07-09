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
package fr.lifl.smac.scadia.dialogueManager.reasoning.openInterrogation

import fr.lifl.smac.scadia.dialogueManager.reasoning._
import fr.dubuissonduplessis.dogma.event.DialogicEvent
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.ditpp.informationSeeking.SetQuestion
import fr.lifl.smac.scadia.dialogueManager.reasoning.openInterrogation.strategy.ResponseInterpretationStrategies
import fr.lifl.smac.scadia.dialogueManager.reasoning.openInterrogation.strategy.EvaluationStrategy
import fr.lifl.smac.scadia.dialogueManager.reasoning.openInterrogation.strategy.QuestionInterpretationStrategy
import fr.lifl.smac.scadia.dialogueManager.reasoning.openInterrogation.strategy.ContentGeneratorStrategy

trait MockupOpenInterrogationPatternReasoner
  extends OpenInterrogationPatternReasoner
  with ResponseInterpretationStrategies
  with EvaluationStrategy
  with QuestionInterpretationStrategy
  with ContentGeneratorStrategy {
  self: PatternReasonerDialogicalAgent =>

  protected class MockupInitiatorOpenInterrogation(
    dialogueGame: OpenInterrogationGame,
    instance: DialogueGameInstance) extends InitiatorOpenInterrogation(
    dialogueGame,
    instance) {

    protected def playSuccessfulEntry(goal: QuestionType): Option[DialogicEvent] =
      {
        val question = generateQuestion(goal)
        println(s"$me asks a question: $goal")
        Some(question)
      }

    protected def playWaiting(goal: QuestionType): Unit =
      println(s"$me is waiting for an answer")

    protected def failureReached(goal: QuestionType, partner: Interlocutor): Unit = {
      println(s"$me sees that question '$goal' has not been answered")
    }

    protected def interpretAnswer(
      goal: QuestionType,
      partner: Interlocutor,
      answerContent: PropContent): Unit =
      // Delegation: ResponseInterpretationStrategies
      self.interpretAnswer(goal, partner, answerContent)

    protected def interpretIgnorance(goal: QuestionType, partner: Interlocutor): Unit =
      // Delegation: ResponseInterpretationStrategies
      self.interpretIgnorance(goal, partner)

    protected def successReached(goal: QuestionType, partner: Interlocutor, answerContent: PropContent): Unit = {
      // Delegation: EvaluationStrategy
      self.evaluate(goal, partner, answerContent)
    }
  }

  protected class MockupPartnerOpenInterrogation(
    dialogueGame: OpenInterrogationGame,
    instance: DialogueGameInstance) extends PartnerOpenInterrogation(
    dialogueGame,
    instance) {
    protected def playWaiting(): Unit =
      println(s"$me is waiting for a question")
    protected def interpretQuestion(q: QuestionType, initiator: Interlocutor): Unit =
      // Delegation: QuestionInterpretationStrategy
      self.interpretQuestion(q)
    protected def generateResponse(q: QuestionType, resolves: PropContent => Boolean): DialogicEvent =
      self.dialogicGoal match {
        case None =>
          println(s"$me ignores a resolving answer to question $q")
          generateExecNegativeAutoFB()
        case Some(g) =>
          // Delegation: ContentGeneratorStrategy
          val answerContent = generateContent(q)
          println(s"$me has sent answer $answerContent to question $q")
          generateAnswer(answerContent)
      }

    protected def successReached(q: QuestionType, answerContent: PropContent): Unit = {
      println(s"$me has successfully produced an answer")
    }
    protected def failureReached(q: QuestionType): Unit = {
      println(s"$me has admit to ignore an answer")
    }

  }
}
