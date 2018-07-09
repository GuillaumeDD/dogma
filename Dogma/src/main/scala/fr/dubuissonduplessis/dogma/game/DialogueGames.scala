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
import fr.dubuissonduplessis.dogma.game.condition.GameConditions
import fr.dubuissonduplessis.dogma.game.condition.GameConditionsRequirements
import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.gameInstance.DialogueGameInstances
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor

/**
 * Module providing the abstract base class to deal with dialogue games.
 *
 * Dialogue games represent conventions between interlocutors. Hence, they are supposed to be shared and
 * bilateral structures. Dialogue games are a particular kind of joint activity temporarily activated
 * during the dialogue for a '''specific goal''' (e.g., information-seeking game, action-seeking game, etc.).
 * Dialogue games are defined by:
 *  - '''entry conditions''' which are conditions that must hold at the beginning of the game,
 *    expressed in terms of extra-dialogical commitments,
 *  - '''exit conditions''' which are conditions that motivate an exit of the game, divided into:
 *     - '''success conditions''' which are conditions that define a state of success in
 *       terms of extra-dialogical commitments;
 *     - '''failure conditions''' which are conditions that define a state of failure in terms
 *        of extra-dialogical commitments.
 *  - '''dialogue rules''' which are specifications of dialogue rules expressed in terms of dialogical commitments
 *    (such as what dialogue act a participant is committed to play) where constraints on the
 *     semantic contents of dialogue acts could be specified, and
 *  - '''effects''' (special case of dialogue rules) which are specifications of effects of dialogue
 *    acts on the '''information state''' involving the creation of extra-dialogical commitments.
 *
 * Its main component is [[fr.dubuissonduplessis.dogma.game.DialogueGames#DialogueGame]].
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait DialogueGames extends GameConditions {
  self: Dialogue with GameConditionsRequirements with Commitments with DialogueGameInstances =>
  /**
   * Role of a participant of a dialogue game.
   * It includes: initiator and partner.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected object Role extends Enumeration {
    type Role = Value
    val INITIATOR, PARTNER = Value
  }
  import Role._

  // Dialogue Game  
  /**
   * Abstract base class that defines a dialogue game.
   *
   * A dialogue game definition can rely on:
   *  - modules from package [[fr.dubuissonduplessis.dogma.game.condition]] to define the entry and exit conditions
   *    (see, in particular, module [[fr.dubuissonduplessis.dogma.game.condition.CommitmentConditions]])
   *  - the commitments module [[fr.dubuissonduplessis.dogma.commitment.Commitments]] to declare the rules and effects
   *
   * The instantiation of sub-class of DialogueGame should be based on factory, see [[fr.dubuissonduplessis.dogma.game.factory.GameFactories]].
   *
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected abstract class DialogueGame {

    /**
     * Type of the goal of the dialogue game.
     */
    type GoalType

    /**
     * Dialogue participant who is the initiator of this dialogue game.
     */
    def initiator: Interlocutor
    /**
     * Dialogue participant who is the partner in this dialogue game.
     */
    def partner: Interlocutor
    /**
     * Name of this dialogue game.
     */
    def name: String
    /**
     * Goal or theme of this dialogue game.
     */
    def goal: GoalType

    // Dialogue Participants
    /**
     * Determines the role of a given dialogue participant in this dialogue game.
     * The dialogue participant should be one of the participants of the dialogue game.
     * @throws IllegalArgumentException thrown if the given dialogue participant is not a participant of this game
     * @return the role of a given dialogue participant (initiator or partner)
     */
    def role(interlocutor: Interlocutor): Role = {
      require(isInitiator(interlocutor) || isPartner(interlocutor), s"Speaker not found: $interlocutor")
      if (isInitiator(interlocutor)) {
        INITIATOR
      } else if (isPartner(interlocutor)) {
        PARTNER
      } else {
        throw new IllegalArgumentException(s"$interlocutor is not part of the game")
      }
    }

    /**
     * Determines if a given dialogue participant is the initiator of this dialogue game.
     * @return true if the given dialogue participant is the initiator of this dialogue game, else false
     */
    def isInitiator(i: Interlocutor): Boolean = (this.initiator == i)
    /**
     * Determines if a given dialogue participant is the partner of this dialogue game.
     * @return true if the given dialogue participant is the partner of this dialogue game, else false
     */
    def isPartner(p: Interlocutor): Boolean = (this.partner == p)
    /**
     * Dialogue participants of this dialogue game.
     */
    def dialogueParticipants: Set[Interlocutor] = Set(initiator, partner)

    // Entry and exit conditions
    /**
     * Computes the entry conditions of this dialogue game for a given dialogue participant.
     * @return the entry conditions of this dialogue game for a given dialogue participant
     */
    def getEntryConditionsFor(speaker: Interlocutor): GameCondition
    /**
     * Computes the entry conditions of this dialogue game for the initiator.
     * @return the entry conditions of this dialogue game for the initiator
     */
    def getEntryConditionsForInitiator(): GameCondition =
      getEntryConditionsFor(initiator)
    /**
     * Computes the entry conditions of this dialogue game for the partner.
     * @return the entry conditions of this dialogue game for the partner
     */
    def getEntryConditionsForPartner(): GameCondition =
      getEntryConditionsFor(partner)

    /**
     * Computes the exit conditions of this dialogue game for a given dialogue participant.
     * @return the exit conditions of this dialogue game for a given dialogue participant
     */
    def getExitConditionsFor(speaker: Interlocutor): GameCondition =
      getSuccessExitConditionsFor(speaker) || getFailureExitConditionsFor(speaker)
    /**
     * Computes the exit conditions of this dialogue game for the initiator.
     * @return the exit conditions of this dialogue game for the initiator
     */
    def getExitConditionsForInitiator(): GameCondition =
      getSuccessExitConditionsForInitiator || getFailureExitConditionsForInitiator

    /**
     * Computes the exit conditions of this dialogue game for the partner.
     * @return the exit conditions of this dialogue game for the partner
     */
    def getExitConditionsForPartner(): GameCondition =
      getSuccessExitConditionsForPartner || getFailureExitConditionsForPartner

    /**
     * Computes the success exit conditions of this dialogue game for a given dialogue participant.
     * @return the success exit conditions of this dialogue game for a given dialogue participant
     */
    def getSuccessExitConditionsFor(speaker: Interlocutor): GameCondition
    /**
     * Computes the success exit conditions of this dialogue game for the initiator.
     * @return the success exit conditions of this dialogue game for the initiator
     */
    def getSuccessExitConditionsForInitiator(): GameCondition =
      getSuccessExitConditionsFor(initiator)

    /**
     * Computes the success exit conditions of this dialogue game for the partner.
     * @return the success exit conditions of this dialogue game for the partner
     */
    def getSuccessExitConditionsForPartner(): GameCondition =
      getSuccessExitConditionsFor(partner)

    /**
     * Computes the failure exit conditions of this dialogue game for a given dialogue participant.
     * @return the failure exit conditions of this dialogue game for a given dialogue participant
     */
    def getFailureExitConditionsFor(speaker: Interlocutor): GameCondition
    /**
     * Computes the failure exit conditions of this dialogue game for the initiator.
     * @return the failure exit conditions of this dialogue game for the initiator
     */
    def getFailureExitConditionsForInitiator(): GameCondition =
      getFailureExitConditionsFor(initiator)
    /**
     * Computes the failure exit conditions of this dialogue game for the partner.
     * @return the failure exit conditions of this dialogue game for the partner
     */
    def getFailureExitConditionsForPartner(): GameCondition =
      getFailureExitConditionsFor(partner)

    // Rules and effects
    /**
     * Computes the rules of this dialogue game for a specific instance, and for a given dialogue participant.
     * The given dialogue participant should be part of the participants of this game.
     * @param instance instance of the dialogue game for which the rules are generated
     * @return the rules of this dialogue game for a specific instance, and for a given dialogue participant
     */
    def getRulesFor(speaker: Interlocutor)(instance: DialogueGameInstance): Set[AnyGameCommitment]
    /**
     * Computes the rules of this dialogue game for a specific instance, and for the initiator of this dialogue game.
     *
     * @param instance instance of the dialogue game for which the rules are generated
     * @return the rules of this dialogue game for a specific instance, and for the initiator of this dialogue game
     */
    def getRulesForInitiator(instance: DialogueGameInstance): Set[AnyGameCommitment] =
      getRulesFor(initiator)(instance)
    /**
     * Computes the rules of this dialogue game for a specific instance, and for the partner of this dialogue game.
     *
     * @param instance instance of the dialogue game for which the rules are generated
     * @return the rules of this dialogue game for a specific instance, and for the partner of this dialogue game
     */
    def getRulesForPartner(instance: DialogueGameInstance): Set[AnyGameCommitment] =
      getRulesFor(partner)(instance)

    /**
     * Computes the effects of this dialogue game for a specific instance, and for a given dialogue participant.
     * The given dialogue participant should be part of the participants of this game.
     * @param instance instance of the dialogue game for which the rules are generated
     * @return the effects of this dialogue game for a specific instance, and for a given dialogue participant
     */
    def getPropositionalEffectsFor(speaker: Interlocutor)(instance: DialogueGameInstance): Set[AnyGameCommitment]
    /**
     * Computes the effects of this dialogue game for a specific instance, and for the initiator of this dialogue game.
     *
     * @param instance instance of the dialogue game for which the rules are generated
     * @return the effects of this dialogue game for a specific instance, and for the initiator of this dialogue game
     */
    def getPropositionalEffectsForInitiator(instance: DialogueGameInstance): Set[AnyGameCommitment] =
      getPropositionalEffectsFor(initiator)(instance)
    /**
     * Computes the effects of this dialogue game for a specific instance, and for the partner of this dialogue game.
     *
     * @param instance instance of the dialogue game for which the rules are generated
     * @return the effects of this dialogue game for a specific instance, and for the partner of this dialogue game
     */
    def getPropositionalEffectsForPartner(instance: DialogueGameInstance): Set[AnyGameCommitment] =
      getPropositionalEffectsFor(partner)(instance)

    override def equals(other: Any): Boolean = other match {
      case that: DialogueGame =>
        this.initiator == that.initiator &&
          this.partner == that.partner &&
          this.name == that.name &&
          this.goal == that.goal
      case _ => false
    }

    override def hashCode: Int =
      41 * (
        41 * (
          41 * (
            41 + initiator.hashCode) + partner.hashCode) + name.hashCode) + goal.hashCode

    override def toString: String =
      name + "(" + goal + ")"
  }
}
