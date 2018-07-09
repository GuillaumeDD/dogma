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
package fr.dubuissonduplessis.dogma.instantiable.impl

import fr.dubuissonduplessis.dogma.instantiable.Generator
import scala.collection.immutable.Set
import fr.dubuissonduplessis.dogma.instantiable.InstantiationSet
import com.typesafe.scalalogging.slf4j.LazyLogging

/**
 * Represents a generator that is never triggered.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @tparam This type of this instantiated generator
 * @tparam Trigger type of the trigger of this generator
 */
trait NonGenerator[This, Trigger] extends Generator[This, Trigger] with LazyLogging {

  /**
   * A NonGenerator does not return any binding.
   * @return An empty instantiation set
   */
  protected def bindingsImpl(t: Trigger): InstantiationSet = {
    logger.error(s"Method binding has been called on: $this")
    InstantiationSet()
  }

  /**
   * A NonGenerator is never triggered.
   * @return False
   */
  def fits(e: Trigger): Boolean = {
    logger.error(s"Method fits has been called on: $this")
    false
  }
}
