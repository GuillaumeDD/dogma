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
package fr.dubuissonduplessis.dogma.examples.scenario.impl

import fr.dubuissonduplessis.dogma.examples.scenario.ScenariiRunner
import fr.dubuissonduplessis.dogma.examples.scenario.ScenariiDB
import scala.collection.immutable.SortedMap
import fr.dubuissonduplessis.dogma.examples.scenario._
import scala.io.StdIn._

/**
 * CLI scenario runner that interacts with a user.
 * It asks the user which scenario is to be ran, runs it, and makes it possible to play
 * another scenario from the DB.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait ScenarioRunnerWithUser extends ScenariiRunner {
  this: ScenariiDB =>

  def play(): Unit = {
    var continue = true
    
    // License info
    println("""Dogma  Copyright (C) 2014 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> 
This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.
This is free software, and you are welcome to redistribute it
under certain conditions; type `show c' for details.
""")
    
    do {
      try {
        // Choose a scenario and execute it
        run(choose())
      } catch {
        case e: Throwable =>
          System.err.println(e)
      }

      // Try to play another scenario
      if (scenarii.size > 1) {
        println("Continue?")
        continue = readBoolean()
      } else {
        continue = false
      }
    } while (continue)
  }

  protected def choose(): String = {
    val choiceList = SortedMap(((1 to scenarii.size) zip scenarii.keys).toMap.toArray: _*)
    choiceList.size match {
      case 0 => throw new Error("No scenario!")
      case 1 =>
        println("Choosing: " + choiceList(1))
        choiceList(1)
      case _ =>
        println("Choose your scenario: (1 <= i <= " + choiceList.size + ")")
        println(choiceList.mkString("\n"))
        println("Scenario n°?")
        var n = 0
        var firstAttempt = true
        do {
          if (!firstAttempt) {
            println("Please, enter a valid entry (1 <= i <= " + choiceList.size + ")")
          }
          n = readInt()
          firstAttempt = false
        } while (n <= 0 || n > choiceList.size)
        println(choiceList(n) + " has been chosen!")
        choiceList(n)
    }
  }

  /**
   * Executes a scenario. Requires a user to enter a key to process an event.
   * @param c Scenario to be executed
   */
  def execute(c: ScenarioEventHandler) = {
    while (canProceed(c)) {
      println("------------------")
      println("Executing")
      proceed(c)
      println(c)
      readLine()
    }
  }
}
