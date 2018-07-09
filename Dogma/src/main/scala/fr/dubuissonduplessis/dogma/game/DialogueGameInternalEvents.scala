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

import fr.dubuissonduplessis.dogma.event.InternalEvent
import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.instantiable.impl.Instantiated
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.gameInstance.DialogueGameInstances

/**
 * Module providing internal events related to dialogue games.
 *
 * These internal events can be presented relatively to the:
 *  - entry conditions: [[fr.dubuissonduplessis.dogma.game.DialogueGameInternalEvents#EntryConditionsReached EntryConditionsReached]],
 *  [[fr.dubuissonduplessis.dogma.game.DialogueGameInternalEvents#FullEntryConditionsReached FullEntryConditionsReached]]
 *  - exit conditions:
 *     - failure: [[fr.dubuissonduplessis.dogma.game.DialogueGameInternalEvents#FailureConditionsReached FailureConditionsReached]],
 *     [[fr.dubuissonduplessis.dogma.game.DialogueGameInternalEvents#FullFailureConditionsReached FullFailureConditionsReached]]
 *     - success: [[fr.dubuissonduplessis.dogma.game.DialogueGameInternalEvents#SuccessConditionsReached SuccessConditionsReached]],
 *     [[fr.dubuissonduplessis.dogma.game.DialogueGameInternalEvents#FullSuccessConditionsReached FullSuccessConditionsReached]]
 *     - failure or success: [[fr.dubuissonduplessis.dogma.game.DialogueGameInternalEvents#ExitConditionsReached ExitConditionsReached]]
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @see [[fr.dubuissonduplessis.dogma.event.InternalEvent]]
 */
trait DialogueGameInternalEvents {
  this: Dialogue with DialogueGameInstances =>

  /**
   * An internal event triggered by the reaching of an internal state of the system.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected abstract class StateEvent extends InternalEvent {
    def name: String
    /**
     *
     * @return the interlocutor that produced the event that generated this event if it exists, else None
     */
    def lastSpeaker: Option[Interlocutor]
  }

  /**
   * An internal event related to a dialogue game instance.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected abstract class DialogueGameEvent extends StateEvent {
    /**
     * @return the dialogue game instance related to this event
     */
    def game: DialogueGameInstance
  }

  /**
   * An internal event related to a dialogue game instance but not to a dialogue
   * participant.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected abstract class GlobalEvent extends DialogueGameEvent {
    override def toString: String = name + "(" + game + ")"
  }

  /**
   * A dialogue game event related to a specific dialogue participant.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected abstract class IndividualEvent extends DialogueGameEvent {
    def participant: Interlocutor
    override def toString: String = name + "(" + game + ", " + participant + ")"
  }

  /**
   *
   * Entry conditions reached internal event for a given dialogue game instance
   * and a given dialogue participant.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   */
  protected case class EntryConditionsReached(
    game: DialogueGameInstance,
    participant: Interlocutor)(val lastSpeaker: Option[Interlocutor])
    extends IndividualEvent
    with Instantiated[EntryConditionsReached] {
    def name: String = "EntryConditionsReached"
  }

  /**
   * Full entry conditions reached internal event for a given dialogue game instance.
   * Said differently, the '''conjonction''' of entry conditions of the dialogue participants
   * is met.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected case class FullEntryConditionsReached(
    game: DialogueGameInstance)(val lastSpeaker: Option[Interlocutor])
    extends GlobalEvent
    with Instantiated[FullEntryConditionsReached] {
    def name: String = "FullEntryConditionsReached"
  }

  /**
   * Failure conditions reached internal event for a given dialogue game instance
   * and a given dialogue participant.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   */
  protected case class FailureConditionsReached(
    game: DialogueGameInstance,
    participant: Interlocutor)(val lastSpeaker: Option[Interlocutor])
    extends IndividualEvent
    with Instantiated[FailureConditionsReached] {
    def name: String = "FailureConditionsReached"
    override def toString: String = name + "(" + game + ", " + participant + ")"
  }

  /**
   * Full failure conditions reached internal event for a given dialogue game instance.
   * Said differently, the '''disjonction''' of failure conditions of the dialogue participants
   * is met.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   */
  protected case class FullFailureConditionsReached(
    game: DialogueGameInstance)(val lastSpeaker: Option[Interlocutor])
    extends GlobalEvent
    with Instantiated[FullFailureConditionsReached] {
    def name: String = "FullFailureConditionsReached"
  }

  /**
   * Success conditions reached internal event for a given dialogue game instance
   * and for a given dialogue participant.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   */
  protected case class SuccessConditionsReached(
    game: DialogueGameInstance,
    participant: Interlocutor)(val lastSpeaker: Option[Interlocutor])
    extends IndividualEvent
    with Instantiated[SuccessConditionsReached] {
    def name: String = "SuccessConditionsReached"
    override def toString: String = name + "(" + game + ", " + participant + ")"
  }

  /**
   * Full success conditions reached internal event for a given dialogue game instance.
   * Said differently, the '''disjonction''' of success conditions of the dialogue participants
   * is met.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   */
  protected case class FullSuccessConditionsReached(
    game: DialogueGameInstance)(val lastSpeaker: Option[Interlocutor])
    extends GlobalEvent
    with Instantiated[FullSuccessConditionsReached] {
    def name: String = "FullSuccessConditionsReached"
  }

  /**
   * Exit conditions reached internal event for a given dialogue game instance.
   * The reaching of failure or success for one dialogue participant triggers this event.
   * Said differently, the '''disjonction''' between success/failure conditions of the
   * dialogue participants is met.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   */
  protected case class ExitConditionsReached(
    game: DialogueGameInstance)(val lastSpeaker: Option[Interlocutor])
    extends GlobalEvent
    with Instantiated[ExitConditionsReached] {
    def name: String = "ExitConditionsReached"
  }
}
