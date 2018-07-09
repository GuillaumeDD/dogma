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
package fr.lifl.smac.scadia.dialogueManager

import fr.dubuissonduplessis.dogma.dialogueManager.ExplicitDialogueManager
import fr.dubuissonduplessis.dogma.dialogue.impl.TwoInterlocutors
import fr.dubuissonduplessis.dogma.conversationalGameBoard.impl.DefaultConversationalGameBoard
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.internalEvent.impl.DialogueGameInternalEventGenerator
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation
import fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.contextualisation.impl.ExplicitRefereeWithAllCombinationAllowed
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationOperations
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationGame
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.PrintableCommitmentStore
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.PrintableGameManager
import fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma
import fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.PrintableExplicitDogma
import fr.lifl.smac.scadia.dialoguePattern.PropositionalGoal
import fr.lifl.smac.scadia.dialoguePattern.OpenInterrogationPattern
import fr.dubuissonduplessis.dogma.event.ExtraDialogicEvent
import fr.dubuissonduplessis.dogma.event.DialogicEvent
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.game.DialogueGames
import scala.collection.mutable
import fr.dubuissonduplessis.dogma.ditpp.informationSeeking.SetQuestion
import fr.dubuissonduplessis.dogma.ditpp.feedback.ExecNegativeAutoFB
import fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Answer

/**
 * Represents a dialogue manager for a two-interlocutor dialogue based on the
 * Dogma approach. This abstract class determines by default the following items:
 * - communication games: contextualisation
 * - dialogue games: "OpenInterrogation" patterns
 * - all combinations of dialogue games are allowed in dialogue game proposition
 * - the interpretation algorithm clashes for all non regular event
 * - the toString method is given a default implementation
 *
 */
trait DialogueManager extends ExplicitDialogueManager
  // 1) Two interlocutors dialogue
  with TwoInterlocutors
  with DefaultConversationalGameBoard
  with DialogueGameInternalEventGenerator
  // 2) Communication Games: contextualisation
  with ContextualisationGame
  with ContextualisationOperations
  // 3) Special Explicit Dogma to deal with Contextualisation...
  with dogma.contextualisation.ExplicitReferee
  // ... default implementation where all combinations of patterns are allowed
  with ExplicitRefereeWithAllCombinationAllowed
  // 4) Printing utilities
  with PrintableCommitmentStore
  with PrintableGameManager
  with PrintableExplicitDogma
  // 5) Dialogue Patterns
  with PropositionalGoal
  with OpenInterrogationPattern {

  // INTERPRETATION
  // By default, crash when:
  // - an extra-dialogic event occurs
  protected def printCGB: Unit = {
    println("################")
    println(this)
    println("################")
  }
  protected def computeExtraDialogicEvent(evt: ExtraDialogicEvent): Unit = {
    printCGB
    ???
  }
  // - a forbidden dialogic event occurs
  protected def computeForbidden(da: DialogicEvent): Unit = {
    printCGB
    ???
  }
  // - an unexpected dialogic event occurs
  protected def computeUnexpected(da: DialogicEvent): Unit = {
    printCGB
    ???
  }
  // - a non-priority dialogic event occurs
  protected def computeNonPriority(da: DialogicEvent): Unit = {
    printCGB
    ???
  }

  // Regular dialogic event occurrence => update of conversational board
  protected def computeStandard(da: DialogicEvent): Unit = {
    process(da)
  }

  // PRINTING UTILITIES
  private def lastSpeakerToString: String =
    this.lastSpeaker match {
      case Some(speaker) => speaker.toString
      case _ => "unknown"
    }
  override def toString: String =
    "lastEvents=" + lastEvents.mkString("<", ", ", ">") + "\n" +
      "lastSpeaker=" + lastSpeakerToString + "\n" +
      gameManagerToString + "\n" +
      commitmentStoreToString +
      explicitDogmaToString
}
