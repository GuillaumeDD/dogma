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
package fr.lifl.smac.scadia.dialogueManager.reasoning.contextualisation

import fr.lifl.smac.scadia.dialogueManager.reasoning.PatternReasonerThing
import fr.dubuissonduplessis.dogma.description._
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.accIn
import fr.dubuissonduplessis.dogma.game.language.GameProposition
import fr.dubuissonduplessis.dogma.game.language.GameProposition
import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation
import fr.dubuissonduplessis.dogma.game.language.AbstractGameProposition
import fr.dubuissonduplessis.dogma.event.DialogicEvent
import fr.dubuissonduplessis.dogma.game.language.AnyGameProposition
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.refIn
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.propOut
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.accOut
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.refOut

trait ContextualisationPatternReasoner {
  this: PatternReasonerThing =>

  protected def contextualisationCommunicationGame: CommunicationGame =
    communicationGameSpace.find(_.name == ContextualisationGame.name).get

  protected abstract class PlayerContextualisation(
    val instance: GameInstance) extends DialogueGameReasoning {

    protected object ReasoningState extends Enumeration {
      type ReasoningState = ReasoningState.Value
      val VOID, ENTRY, EMBEDDING, EXIT_PROPOSITION, EXIT_CONFIRMATION = Value
    }
    import ReasoningState._

    // Generic to a reasoning module
    private var currentState_ : ReasoningState = _
    protected def currentState: ReasoningState = currentState_
    private var lastPlayedState_ : ReasoningState = _
    protected def lastPlayedState: ReasoningState = lastPlayedState_

    // Specific to contextualisation reasoning module
    private val otherLocutor =
      if (interlocutor01 == me) {
        interlocutor02
      } else {
        interlocutor01
      }
    private var currentGameProposition_ : Option[GameProposition[_]] = _
    protected def currentGameProposition: Option[GameProposition[_]] =
      currentGameProposition_
    private var parentGameProposition_ : Option[AbstractGameProposition] = _
    protected def parentGameProposition: Option[AbstractGameProposition] =
      parentGameProposition_

    /**
     * Compute the state of the algorithm from the state of the contextualisation game.
     * @param state
     */
    private def computeState(state: Set[GameActionCommitment]): Unit =
      {
        val mySortedCommitments =
          state
            // Select commitments regarding this dialogue participant
            .filter(_.debtor == me)
            // Only consider commitments expecting an event
            .filter(!_.expectedDescription.isEmpty)
            .toList // List conversion for order
            .sorted(CommitmentOrdering.reverse) // Sort them by descending creation time (latest first)

        if (mySortedCommitments.isEmpty) {
          // There is nothing to do in contextualisation
          currentState_ = VOID
          currentGameProposition_ = None
        } else {
          // Select the latest expected action description
          val latestDialogueAct = mySortedCommitments.head.content

          // Hacking part to detect the current state
          // 1: Compute the parent game proposition if it exists
          computeParentAbstractGamePropositionFrom(latestDialogueAct)
          // 2: Try to turn the description into instantiated events
          val events = eventsFromDescription(latestDialogueAct)
          // 3: Compute the game proposition that is under discussion
          computeGamePropositionFrom(events)
          // 4: Compute the state of the contextualisation game
          currentState_ = events match {
            case l if l.size == 0 =>
              // If no event could have been generated, 
              // then it is an embedding proposition
              EMBEDDING
            case l if l.size == 1 =>
              // If one event has been generated,
              // then it is an exit proposition
              EXIT_PROPOSITION
            case l if l.size == 2 =>
              // If two events have been generated,
              // then it is either an accIn/refIn or a accOut/refOut
              val entryDA = l.exists({
                case accIn(x, prop) =>
                  true
                case _ =>
                  false
              })
              if (entryDA) {
                ENTRY
              } else {
                EXIT_CONFIRMATION
              }
            case _ =>
              throw new Error(s"Unable to process the state of the contextualisation game: $latestDialogueAct")
          }
        }
      }

    private def computeGamePropositionFrom(e: List[Event]): Unit =
      e match {
        case List() =>
          currentGameProposition_ = None
        case head :: tail =>
          head match {
            case contextualisation.dialogueActs(dp, name, content) =>
              content match {
                case prop: GameProposition[_] =>
                  currentGameProposition_ = Some(prop)
                case _ =>
                  computeGamePropositionFrom(tail)

              }
            case _ =>
              computeGamePropositionFrom(tail)
          }
      }

    private def computeParentAbstractGamePropositionFrom(d: Description): Unit = {
      d match {
        case contextualisation.dialogueActs(dialogueActName, dp, combinationSymbol, gProp) =>
          parentGameProposition_ = Some(gProp)
        case _ =>
          parentGameProposition_ = None
      }
    }

    def update(state: Set[GameActionCommitment]): Unit = {
      // Compute new state
      computeState(state)
    }

    def play(): (Boolean, Option[DialogicEvent]) =
      {
        currentState match {
          case VOID =>
            lastPlayedState_ = VOID
            (true, playNothingToDo())
          case ENTRY =>
            lastPlayedState_ = ENTRY
            // Creation of the dialogue game from the game proposition
            val g = createGameInstanceFrom(currentGameProposition.get, otherLocutor, me)
            if (g.getEntryConditionsForInitiator()() && g.getEntryConditionsForPartner()()) {
              (true, playSuccessfulEntry(currentGameProposition.get))
            } else {
              (true, playFailureEntry(currentGameProposition.get))
            }
          case EMBEDDING =>
            lastPlayedState_ = EMBEDDING
            (true, playEmbedding(parentGameProposition.get))
          case EXIT_PROPOSITION =>
            lastPlayedState_ = EXIT_PROPOSITION
            (true, playExitConditionsReached(currentGameProposition.get))
          case EXIT_CONFIRMATION =>
            lastPlayedState_ = EXIT_CONFIRMATION
            // Determination of the dialogue game from the dialogue game space
            val g = dialogueGameSpace
              .openedDialogueGames
              .map(_.dialogueGame)
              .find(game => game.name == currentGameProposition.get.name &&
                game.goal == currentGameProposition.get.goal)
              .get
            if (g.getExitConditionsFor(me)()) {
              (true, playSuccessExitConditionsReached(currentGameProposition.get))
            } else {
              (true, playFailureExitConditionsReached(currentGameProposition.get))
            }

        }
      }

    protected def playNothingToDo(): Option[DialogicEvent]
    protected def playSuccessfulEntry(gProp: AnyGameProposition): Option[DialogicEvent]
    protected def playFailureEntry(gProp: AnyGameProposition): Option[DialogicEvent]
    protected def playEmbedding(parentGProp: AbstractGameProposition): Option[DialogicEvent]
    protected def playExitConditionsReached(gProp: AnyGameProposition): Option[DialogicEvent]
    protected def playSuccessExitConditionsReached(gProp: AnyGameProposition): Option[DialogicEvent]
    protected def playFailureExitConditionsReached(gProp: AnyGameProposition): Option[DialogicEvent]

    // Helper
    protected def generateAccIn(gProp: AnyGameProposition): DialogicEvent =
      accIn(me, gProp)
    protected def generateRefIn(gProp: AnyGameProposition): DialogicEvent =
      refIn(me, gProp)
    protected def generatePropOut(gProp: AnyGameProposition): DialogicEvent =
      propOut(me, gProp)
    protected def generateAccOut(gProp: AnyGameProposition): DialogicEvent =
      accOut(me, gProp)
    protected def generateRefOut(gProp: AnyGameProposition): DialogicEvent =
      refOut(me, gProp)
  }

}
