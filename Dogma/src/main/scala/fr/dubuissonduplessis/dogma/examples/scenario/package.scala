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
package fr.dubuissonduplessis.dogma.examples

import fr.dubuissonduplessis.dogma.event.ExternalEvent

/**
 * Provides tools to build and execute a scenario, viewed as sequence of external events.
 *
 * ==Overview==
 * This package includes three main elements:
 *  - [[fr.dubuissonduplessis.dogma.examples.scenario.EventHandler ScenarioEventHandler]]: a scenario executor.
 *  - [[fr.dubuissonduplessis.dogma.examples.scenario.ScenariiDB ScenariiDB]]: a database of scenarii.
 *  - [[fr.dubuissonduplessis.dogma.examples.scenario.ScenariiRunner ScenariiRunner]]: runs scenarii from a database.
 *
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
package object scenario {
  /**
   * A scenario as a list of external event.
   */
  type Scenario = List[ExternalEvent]
  /**
   * Builds an empty scenario.
   */
  def Scenario(): Scenario =
    List()

  type ScenarioEventHandler = EventHandler
  type ScenarioEventHandlerBuilder = () => ScenarioEventHandler

  /**
   * Determines whether a ScenarioEventHandler has event(s) to
   * proceed.
   *
   * @return true if the ScenarioEventHandler has event(s) to
   * proceed else false
   */
  def canProceed(c: ScenarioEventHandler): Boolean =
    c.events.size > 0

  /**
   * Processes the first event of a given ScenarioEventHandler.
   *
   */
  def proceed(c: ScenarioEventHandler): Unit =
    c.processFirstEvent
}
