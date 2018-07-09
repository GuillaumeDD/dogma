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

import scala.collection.mutable.ListBuffer
import fr.lifl.smac.scadia.AgentState._
import scala.util.Random
import scala.collection.mutable
import fr.dubuissonduplessis.dogma.event.DialogicEvent

trait Environment {
  private val agents_ = ListBuffer[DialogicalAgent]()
  private val mailboxes_ = mutable.Map[String, mutable.Queue[DialogicEvent]]()

  /**
   * Agents in the MAS
   * @return the agents in the MAS
   */
  def agents: List[DialogicalAgent] =
    agents_.toList
  /**
   * Active agents in the MAS
   * @return the active agents in the MAS
   */
  def activeAgents: List[DialogicalAgent] =
    agents.filter(a => isActive(a.state))

  def subscribe(agent: DialogicalAgent): Unit =
    {
      require(!agents.contains(agent), s"$agent is already registered!")
      // Subscription
      agents_ += agent
      // Mail box creation
      mailboxes_ += (agent.name -> mutable.Queue())
    }

  // MESSAGE SENDING TOOLS
  def mailbox(agent: String): List[DialogicEvent] =
    if (mailboxes_(agent).isEmpty) {
      List()
    } else {
      // Get all the messages
      mailboxes_(agent).dequeueAll(msg => true).toList
    }

  def broadcast(e: DialogicEvent): Unit =
    for (a <- agents) {
      mailboxes_(a.name).enqueue(e)
    }

  def send(sender: DialogicalAgent, e: DialogicEvent): Unit = {
    println(s"${sender.name} sends $e")
    for (a <- agents.filter(_ != sender)) {
      mailboxes_(a.name).enqueue(e)
    }
  }
  def send(sender: DialogicalAgent, events: List[DialogicEvent]): Unit =
    for (evt <- events) {
      send(sender, evt)
    }

  // MAS EXECUTION
  def init(): Unit = {
    println("--- INIT START")
    // Basic scheduler
    val scheduler = Random.shuffle(activeAgents)
    for (a <- scheduler) {
      a.init(this)
    }
    println("--- INIT FINISHED")
  }

  def run(): Unit = {
    while (agents.exists(a => isActive(a.state))) {
      // Basic scheduler
      val scheduler = Random.shuffle(activeAgents)
      for (a <- scheduler) {
        a.run(this)
      }
      println("-----")
    }
  }
}
