/**
 * *****************************************************************************
 * Copyright (c) 2014 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Maxime MORGE - initial API and implementation
 *     Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> - initial API and implementation
 * ****************************************************************************
 */
package fr.lifl.smac.scadia

import fr.lifl.smac.scadia.dialogueManager.DialogueManager
import fr.dubuissonduplessis.dogma.event.DialogicEvent

//An agent is initially active and becomes inactive when she has done her job
object AgentState extends Enumeration {
  type AgentState = Value
  val ACTIVE, INACTIVE = Value

  def isActive(state: AgentState): Boolean =
    state == ACTIVE
}

abstract class DialogicalAgent {
  this: DialogueManager =>

  import AgentState._

  def name: String
  private var state_ : AgentState = ACTIVE
  def state: AgentState =
    state_
  def deactivate(): Unit =
    state_ = INACTIVE

  // Goal management
  protected var dialogicGoal: Option[Any] = None

  /**
   * Helper method that determines if the dialogic agent should stop being active,
   * and stop it if it should.
   *
   */
  protected def considerDeactivation(): Unit =
    {
      // Stop if there is no more dialogue game to play
      if (dialogueGameSpace.suggestedDialogueGames.isEmpty &&
        dialogueGameSpace.openedDialogueGames.isEmpty) {
        println(s"$name deactivates itself")
        deactivate()
      }
    }

  /**
   * Perceive algorithm that retrieves messages from the environment.
   * @param env environment in which the dialogic agent takes place
   */
  protected def perceive(env: Environment): Unit = {
    // Helper message to read all the available messages at once
    def readMessage(msg: List[DialogicEvent]): Unit =
      {
        msg match {
          case firstDialogicEvent :: otherEvents =>
            // Enqueueing the event in the dialogue manager
            this.enqueue(firstDialogicEvent)
            println(s"$name has received the message: $firstDialogicEvent")
            // Continue reading
            readMessage(otherEvents)
          case List() =>
          // Do nothing
        }
      }

    env.mailbox(name) match {
      case List() =>
        println(s"$name has no new message")
      case l =>
        readMessage(l)
    }
  }

  /**
   * Reasoning process of the dialogic agent.
   */
  protected def reason(): Unit = {
    // Reason: while they are events to process in the dialogue manager queue, process
    processAllEvents()
  }

  /**
   * Helper method that sends message from this agent, and re-interpret the messages
   * @param env environment in which the dialogic agent takes place
   * @param events events to be sent
   */
  protected def sendAll(env: Environment, events: List[DialogicEvent]): Unit = {
    if (!events.isEmpty) {
      // Send event to other agent
      env.send(this, events)

      // Self-interpret the event
      this.enqueue(events: _*)
      processAllEvents()
    }
  }

  /**
   * Action method of the dialogic agent
   * @param env environment in which the dialogic agent takes place
   */
  protected def act(env: Environment): Unit = {
    // Recover event from the PatternReasoner
    // Reasoning process is delegated to the dialogue manager
    val nextEvents = this.nextEvents()

    // Send an interpret the events
    sendAll(env, nextEvents)
  }

  /**
   * Initialisation of the dialogic agent.
   * @param env environment in which the dialogic agent takes place
   */
  def init(env: Environment): Unit

  /**
   * Main method that runs the dialogic agent
   * @param env environment in which the dialogic agent takes place
   */
  def run(env: Environment): Unit = {
    perceive(env)
    reason
    act(env)
  }
}
