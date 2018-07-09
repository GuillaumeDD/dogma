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
package fr.dubuissonduplessis.dogma.game

import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.gameInstance.GameInstances
import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor

/**
 * Module providing the abstract base class to deal with communication games.
 *
 * Communication games are dedicated to general interaction processes that happens in all kinds of
 * dialogues (e.g., information evaluation, mutual understanding, turn-taking, etc.) and are
 * always activated. Their structures come down to dialogue rules.
 *
 * Its main component is [[fr.dubuissonduplessis.dogma.game.CommunicationGames#CommunicationGame]].
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait CommunicationGames {
  self: Dialogue with Commitments with GameInstances =>

  protected object CommunicationGame {
    val creationTime: Time = currentTime
  }

  /**
   * Abstract base class that defines a communication game.
   *
   * A communication game definition can rely on the commitments module [[fr.dubuissonduplessis.dogma.commitment.Commitments]]
   * to declare its rules.
   *
   * The instantiation of sub-class of CommunicationGame should be based on factory, see [[fr.dubuissonduplessis.dogma.game.factory.GameFactories]].
   *
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected abstract class CommunicationGame extends GameInstance {
    /**
     * Unique name of the communication game (for instance, "evaluation", "contextualisation")
     */
    def name: String

    /**
     * Dialogue participants involved in this communication games
     */
    def dialogueParticipants: Set[Interlocutor]

    /**
     * Creation time of this communication game
     */
    val t: Time = CommunicationGame.creationTime

    def isDialogueGame: Boolean =
      false

    /**
     * Computes the rules of the communication game for a given dialogue participant.
     * The dialogue participant should be part of the participants of this game.
     * @param speaker a dialogue participant that should be a participant of this communication game
     * @return the rules of the communication game for a given dialogue participant
     */
    def getRulesFor(speaker: Interlocutor): Set[AnyGameCommitment]

    override def equals(other: Any): Boolean =
      other match {
        case that: CommunicationGame =>
          (this.id == that.id) && (this.name == that.name)
        case _ => false
      }

    override def hashCode(): Int =
      41 * (41 + id.hashCode()) + name.hashCode()

    override def toString: String = name + "(" + id + ")"
  }
}
