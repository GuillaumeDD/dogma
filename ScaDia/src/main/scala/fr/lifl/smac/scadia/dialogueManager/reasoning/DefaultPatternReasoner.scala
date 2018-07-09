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
package fr.lifl.smac.scadia.dialogueManager.reasoning

import fr.lifl.smac.scadia.dialogueManager.reasoning._
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.lifl.smac.scadia.dialogueManager.reasoning.openInterrogation.MockupOpenInterrogationPatternReasoner
import fr.lifl.smac.scadia.dialoguePattern.OpenInterrogationPattern
import fr.lifl.smac.scadia.dialogueManager.reasoning.contextualisation.MockupContextualisationReasoner

trait DefaultPatternReasoner extends PatternReasoner
  // Default reasoner for interrogation pattern
  with MockupOpenInterrogationPatternReasoner
  // Default reasoner for contextualisation game
  with MockupContextualisationReasoner {
  this: PatternReasonerDialogicalAgent =>

  protected def createInitiatorReasoningModule(
    dg: DialogueGame,
    instance: DialogueGameInstance): DialogueGameReasoning =
    dg.name match {
      case OpenInterrogationGame.name =>
        new MockupInitiatorOpenInterrogation(
          dg.asInstanceOf[OpenInterrogationGame],
          instance)
      case _ =>
        throw new Error(s"Unable to create a reasoning module for game ${dg.name} with goal ${dg.goal}")
    }

  protected def createPartnerReasoningModule(
    dg: DialogueGame,
    instance: DialogueGameInstance): DialogueGameReasoning =
    dg.name match {
      case OpenInterrogationGame.name =>
        new MockupPartnerOpenInterrogation(
          dg.asInstanceOf[OpenInterrogationGame],
          instance)
      case _ =>
        throw new Error(s"Unable to create a reasoning module for game ${dg.name} with goal ${dg.goal}")
    }

  protected def createReasoningModule(communicationGame: CommunicationGame): DialogueGameReasoning =
    communicationGame.name match {
      case ContextualisationGame.name =>
        new MockupPlayerContextualisation(communicationGame)
      case _ =>
        throw new Error(s"Unable to create a reasoning module for game ${communicationGame.name}")

    }
}
