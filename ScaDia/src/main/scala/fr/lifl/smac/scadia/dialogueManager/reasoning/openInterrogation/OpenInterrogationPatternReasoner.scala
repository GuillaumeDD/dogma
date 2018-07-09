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

import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Answer
import fr.dubuissonduplessis.dogma.event.DialogicEvent
import fr.lifl.smac.scadia.dialogueManager.reasoning._
import fr.dubuissonduplessis.dogma.ditpp.informationSeeking.SetQuestion
import fr.dubuissonduplessis.dogma.ditpp.feedback.ExecNegativeAutoFB
import fr.dubuissonduplessis.dogma.ditpp.feedback.ExecNegativeAutoFB

trait OpenInterrogationPatternReasoner {
  this: PatternReasonerThing =>

  protected abstract class InitiatorOpenInterrogation(
    val dialogueGame: OpenInterrogationGame,
    val instance: DialogueGameInstance) extends DialogueGameReasoning {

    val initiator: Interlocutor = dialogueGame.initiator
    val partner: Interlocutor = dialogueGame.partner
    val goal: QuestionType = dialogueGame.goal

    require(initiator == me, s"$me cannot play the role of initiatior in $instance (initiator=$initiator, partner=$partner)")

    protected object ReasoningState extends Enumeration {
      type ReasoningState = ReasoningState.Value
      val INIT, ENTRY, IN_PROGRESS, SUCCESS, FAILURE = Value
    }
    import ReasoningState._

    private var currentState = ENTRY
    private var lastPlayedState = INIT
    private var answerContent: PropContent = _

    private def computeState(state: Set[GameActionCommitment]): Unit =
      {
        currentState = if (instance.getPast.isEmpty) {
          ENTRY
        } else if (instance.getPast.size == 1) {
          IN_PROGRESS
        } else {
          // Determination of answer content
          instance.getPast.collectFirst({
            case Answer(loc, content) if loc == partner =>
              content.asInstanceOf[PropContent]
          }) match {
            case Some(content) =>
              answerContent = content
              SUCCESS
            case None =>
              FAILURE
          }
        }
      }

    def update(state: Set[GameActionCommitment]): Unit = {
      // Compute new state
      computeState(state)
    }

    def play(): (Boolean, Option[DialogicEvent]) =
      {
        if (currentState != lastPlayedState) {
          currentState match {
            case ENTRY =>
              lastPlayedState = ENTRY
              (true, playSuccessfulEntry(goal))
            case IN_PROGRESS =>
              lastPlayedState = IN_PROGRESS
              playWaiting(goal)
              (true, None)
            case FAILURE =>
              lastPlayedState = FAILURE
              interpretIgnorance(goal, partner)
              failureReached(goal, partner)
              (true, None)
            case SUCCESS =>
              lastPlayedState = SUCCESS
              interpretAnswer(goal, partner, answerContent)
              successReached(goal, partner, answerContent)
              (true, None)
          }
        } else {
          (false, None)
        }
      }

    protected def playSuccessfulEntry(goal: QuestionType): Option[DialogicEvent]
    protected def playWaiting(goal: QuestionType): Unit
    protected def interpretAnswer(goal: QuestionType, partner: Interlocutor, answerContent: PropContent): Unit
    protected def interpretIgnorance(goal: QuestionType, partner: Interlocutor): Unit
    protected def successReached(goal: QuestionType, partner: Interlocutor, answerContent: PropContent): Unit
    protected def failureReached(goal: QuestionType, partner: Interlocutor): Unit
    protected def generateQuestion(goal: QuestionType): SetQuestion[QuestionType] =
      SetQuestion(me, goal)
  }

  protected abstract class PartnerOpenInterrogation(
    val dialogueGame: OpenInterrogationGame,
    val instance: DialogueGameInstance) extends DialogueGameReasoning {

    val initiator: Interlocutor = dialogueGame.initiator
    val partner: Interlocutor = dialogueGame.partner
    val goal: QuestionType = dialogueGame.goal

    require(partner == me, s"$me cannot play the role of partner in $instance  (initiator=$initiator, partner=$partner)")

    protected object ReasoningState extends Enumeration {
      type ReasoningState = ReasoningState.Value
      val INIT, ENTRY, IN_PROGRESS, SUCCESS, FAILURE = Value
    }
    import ReasoningState._

    private var currentState = ENTRY
    private var lastPlayedState = INIT
    private var questionContent: QuestionType = _
    private var answerContent: PropContent = _

    private def computeState(state: Set[GameActionCommitment]): Unit =
      {
        currentState = if (instance.getPast.isEmpty) {
          ENTRY
        } else if (instance.getPast.size == 1) {
          // Determination of question content
          instance.getPast.collectFirst({
            case SetQuestion(loc, content) if loc == initiator =>
              content.asInstanceOf[QuestionType]
          }) match {
            case Some(content) =>
              questionContent = content
            case None =>
              throw new IllegalStateException(s"${instance} state is invalid: cannot find a question in the past")
          }
          IN_PROGRESS
        } else {
          // Determination of answer content
          instance.getPast.collectFirst({
            case Answer(loc, content) if loc == partner =>
              content.asInstanceOf[PropContent]
          }) match {
            case Some(content) =>
              answerContent = content
              SUCCESS
            case None =>
              FAILURE
          }
        }
      }

    def update(state: Set[GameActionCommitment]): Unit = {
      // Compute new state
      computeState(state)
    }

    def play(): (Boolean, Option[DialogicEvent]) =
      {
        if (currentState != lastPlayedState) {
          currentState match {
            case ENTRY =>
              lastPlayedState = ENTRY
              // Waiting for a question
              playWaiting()
              (true, None)
            case IN_PROGRESS =>
              lastPlayedState = IN_PROGRESS
              // A question has been asked
              interpretQuestion(questionContent, initiator)
              // Response generation
              (true, Some(generateResponse(questionContent, dialogueGame.resolves(_, goal))))
            case FAILURE =>
              lastPlayedState = FAILURE
              failureReached(questionContent)
              (true, None)
            case SUCCESS =>
              lastPlayedState = SUCCESS
              successReached(questionContent, answerContent)
              (true, None)
          }
        } else {
          (false, None)
        }
      }
    protected def playWaiting(): Unit
    protected def interpretQuestion(q: QuestionType, initiator: Interlocutor): Unit
    protected def generateResponse(q: QuestionType, resolves: PropContent => Boolean): DialogicEvent
    protected def successReached(q: QuestionType, answerContent: PropContent): Unit
    protected def failureReached(q: QuestionType): Unit

    protected def generateAnswer(answerContent: PropContent): Answer[PropContent] =
      Answer(me, answerContent)

    protected def generateExecNegativeAutoFB(): ExecNegativeAutoFB[QuestionType] =
      ExecNegativeAutoFB(me, dialogueGame.goal)
  }
}
