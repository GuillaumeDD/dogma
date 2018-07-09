/*******************************************************************************
 * Copyright (c) 2014 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> - initial API and implementation
 ******************************************************************************/
package fr.dubuissonduplessis.dogma.examples.scenario

/**
 * Runner that plays scenario in a database.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @see Implementations in package [[fr.dubuissonduplessis.dogma.examples.scenario.impl impl]].
 */
trait ScenariiRunner {
  this: ScenariiDB =>
  /**
   * Plays a scenario from the scenarii database.
   * It should realizes 3 main tasks:
   * 1) Choose the scenario from the DB
   * 2) Run the scenario
   * 3) Optionally, ask if a new scenario is to be played
   * @see [[fr.dubuissonduplessis.dogma.examples.scenario.ScenariiRunner#run]]
   */
  def play(): Unit

  /**
   * Executes the scenario event handler until its end.
   */
  protected def execute(handler: ScenarioEventHandler): Unit

  /**
   * Runs the scenario which name is given in parameter.
   * This method calls the [[fr.dubuissonduplessis.dogma.examples.scenario.ScenariiRunner#execute execute]] procedure.
   */
  protected def run(name: String): Unit = {
    require(scenarii.contains(name))

    // Get the events of the scenario
    val events = scenarii(name)._1

    // Get the builder of the ScenarioEventHandler
    val builder = scenarii(name)._2
    // Creation of the scenario handler
    val scenarioHandler = builder()

    // Populate the scenario handler with the events defined by the scenario
    for (event <- events) {
      scenarioHandler.enqueue(event)
    }

    // Execution of the scenario handler
    execute(scenarioHandler)
  }
}
