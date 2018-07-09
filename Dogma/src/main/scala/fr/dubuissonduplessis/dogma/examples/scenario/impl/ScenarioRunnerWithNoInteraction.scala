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
import fr.dubuissonduplessis.dogma.examples.scenario._

/**
 * Runs entirely the first scenario of a database.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait ScenarioRunnerWithNoInteraction extends ScenariiRunner {
  this: ScenariiDB =>

  def play(): Unit = {
    require(scenarii.size > 0, "The scenarii DB is empty")

    // Run the first scenario in the database
    val (firstScenarioName, _) = scenarii.head
    run(firstScenarioName)
  }

  /**
   * Executes completely a scenario without requiring a user to enter a key to process an event.
   *
   */
  def execute(c: ScenarioEventHandler) = {
    while (canProceed(c)) {
      println(s"% Executing: ${c.events.head}")
      proceed(c)
      println(c)
      println("""\clearpage""")
    }
  }
}
