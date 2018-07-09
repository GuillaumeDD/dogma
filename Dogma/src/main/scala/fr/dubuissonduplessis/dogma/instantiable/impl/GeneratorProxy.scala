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
import fr.dubuissonduplessis.dogma.instantiable.InstantiationSet

/**
 * Represents a generator that delegates its generator methods.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @tparam T type of the  instantiation process output of this instantiable
 * @tparam Trigger type of the trigger of this generator
 * @tparam Content type of the instantiation process output of the proxied generator
 */
trait GeneratorProxy[+T, Trigger, Content]
  extends Generator[T, Trigger] {
  /**
   * The proxied generator
   */
  def generator: Generator[Content, Trigger]

  def fits(t: Trigger): Boolean =
    generator.fits(t)

  protected def bindingsImpl(t: Trigger): InstantiationSet =
    generator.bindings(t)
}
