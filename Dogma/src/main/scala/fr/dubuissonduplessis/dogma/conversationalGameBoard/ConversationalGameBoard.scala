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
package fr.dubuissonduplessis.dogma.conversationalGameBoard

import fr.dubuissonduplessis.dogma.event.DialogicEventFromLocutor
import fr.dubuissonduplessis.dogma.event.ExternalEvent
import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.ExplicitCommitmentStore
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import scala.collection.mutable
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.impl.GameManagerPriorities
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.impl.CommitmentStoreLike
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.internalEvent.impl.DialogueGameInternalEventGenerator
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.impl.CommitmentStorePriorities
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.impl.GameManagerLike

/**
 * Abstract base class for a conversational game board with default implementations for the
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStore commitment store]], the
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManager game manager]] and the
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.internalEvent.InternalEventGenerator internal event generator]].
 *
 * The conversational game board represents the public layer of the information-state of an interaction system.
 * In Dogma, this public layer is made up of a commitment store and a game manager (i.e. an
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.ExplicitCommitmentStore ExplicitCommitmentStore]]).
 * This abstract base class aims at providing a basis for the implementation of a conversational game board.
 * It puts default implementations together to make easier the building of a conversational game board.
 *
 * The conversational game board deals with the occurrences of [[fr.dubuissonduplessis.dogma.event.ExternalEvent external events]]
 * in two ways.
 * First, it is an '''event store''' that saves the history of the interaction in term of occurred events (see
 * methods [[fr.dubuissonduplessis.dogma.conversationalGameBoard.ConversationalGameBoard#lastEvents lastEvents]] and
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.ConversationalGameBoard#lastSpeaker lastSpeaker]]).
 * Next, it handles the '''evolution process''' of the conversational game board (see methods
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.ConversationalGameBoard#init init]] and
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.ConversationalGameBoard#process process]]).
 * Default implementation of the event store and the evolution process are given in the
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.impl sub-package 'impl']].
 *
 *
 * A conversational game board aims to be used for [[fr.dubuissonduplessis.dogma.dialogueManager dialogue management]].
 * To this purpose, a conversational game board can be augmented by two modules: the
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ExplicitReferee referee module]] and the
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ConventionalBehaviourManager conversational behaviour manager module]].
 *
 *
 * ==Usage Example==
 * Example of the instantiation of a conversational game board for a
 * [[fr.dubuissonduplessis.dogma.dialogue.impl.TwoInterlocutors two-participant dialogue]]:
 * {{{
 * import fr.dubuissonduplessis.dogma.dialogue
 * import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
 *
 * new ConversationalGameBoard // Creation of a new conversational game board
 * with impl.DefaultConversationalGameBoard // Default implementation of the 'process' method
 * with impl.EventsStore // Default implementation of the events mecanism
 * with dialogue.impl.TwoInterlocutors // Conversational board for a two-participant dialogue
 * {
 *    protected def interlocutor01: Interlocutor =
 *       Interlocutor("Alice")
 *    protected def interlocutor02: Interlocutor =
 *       Interlocutor("Bob")
 *
 *    // DIALOGUE ACT EXAMPLE
 *    import fr.dubuissonduplessis.dogma.event.dialogueAct.DialogueAct
 *    // Greetings dialogue act
 *    case class Greetings(locutor: Interlocutor) extends DialogueAct {
 *       val name = "Greetings"
 *    }
 *
 *    // USAGE EXAMPLE
 *    this.init()
 *
 *    // Processing the occurrence of a greeting act
 *    this.process(Greetings(Interlocutor("Alice")))
 *
 *    this.lastSpeaker // Some(Alice)
 *    this.lastEvents // List(Greetings(Alice))
 * }
 * }}}
 *
 * ==Default Implementations==
 * This section references the default implementations involved in the conversational game board.
 *
 * Default implementations for the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStore commitment store]]:
 *  - [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.impl.CommitmentStoreLike]]
 *  - [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.impl.CommitmentStorePriorities]]
 *
 * Default implementations for the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManager game manager]]:
 *  - [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.impl.GameManagerLike]]
 *  - [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.impl.GameManagerPriorities]]
 *
 * Default implementation for the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.internalEvent.InternalEventGenerator internal event generator]]:
 *  - [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.internalEvent.impl.DialogueGameInternalEventGenerator]]
 *
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @see Explicit commitment store: [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.ExplicitCommitmentStore]]
 * @see Default implementation of the event store:
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.impl.EventsStore impl.EventsStore]].
 *  @see Default implementation of the evolution process:
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.impl.DefaultConversationalGameBoard impl.DefaultConversationalGameBoard]].
 */
trait ConversationalGameBoard extends ExplicitCommitmentStore
  // Default Implementations
  with GameManagerLike
  with GameManagerPriorities
  with DialogueGameInternalEventGenerator
  with CommitmentStoreLike
  with CommitmentStorePriorities {

  /**
   * A dialogue move: a dialogic event produced by a dialogue participant at a specific time.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @param speaker dialogue participant who produces this move
   * @param t production time of this move
   * @param event dialogic event produced by the speaker
   */
  protected case class Move(
    speaker: Interlocutor,
    t: Time,
    event: DialogicEventFromLocutor) {
    override def toString: String = "(" + speaker + ", " + t + ", " + event + ")"
  }

  /**
   * Initializes the conversational game board.
   */
  protected def init(): Unit

  /**
   * Carries out the evolution of a conversational game board triggered by a given event.
   */
  protected def process(e: ExternalEvent): Unit

  /**
   * Returns the last speaker that produced a communicative action, if it exists.
   */
  protected def lastSpeaker: Option[Interlocutor]

  /**
   * Returns the last events that have been processed.
   */
  protected def lastEvents: List[Event]

  /**
   * Returns the last moves that have been processed.
   */
  protected def lastMoves: List[Move]
}
