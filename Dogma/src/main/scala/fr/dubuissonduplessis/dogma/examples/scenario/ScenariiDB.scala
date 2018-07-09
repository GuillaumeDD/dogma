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
package fr.dubuissonduplessis.dogma.examples.scenario

import scala.collection.immutable.SortedMap
import scala.collection.immutable.ListMap
import scala.collection.mutable

/**
 * A mutable scenarii database.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait ScenariiDB {
  protected val scenarii: mutable.Map[String, (Scenario, ScenarioEventHandlerBuilder)] =
    mutable.ListMap()

  /**
   * Factory of default ScenarioEventHandlerBuilder.
   */
  def defaultBuilder(): ScenarioEventHandlerBuilder

  /**
   * Adds a scenario to the database.
   * @param name name of the scenario
   * @param is scenario as a sequence of external events
   * @param builder builder of the ScenarioEventHandlerBuilder to be used when
   * executing this scenario
   */
  def add(
    name: String,
    is: Scenario,
    builder: ScenarioEventHandlerBuilder = defaultBuilder()): Unit =
    scenarii += (name -> (is, builder))

  /**
   * Adds scenarii to the database as a list of scenario defined by a
   * pair (name, sequence of external events).
   */
  def add(scenariiList: List[(String, Scenario)]): Unit =
    for ((name, is) <- scenariiList) {
      add(name, is)
    }
}
