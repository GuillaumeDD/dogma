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
 * Represents an instantiated generator, i.e. a generator that does not contain variable.
 * It should not be confused with a [[fr.dubuissonduplessis.dogma.instantiable.impl.NonGenerator]].
 * This latter can not fit a trigger whereas an instantiated generator can.
 *
 * ==Example==
 * {{{
 *  case class Act(name: String, content: Int) extends InstantiatedGenerator[Act, Act] {
 *     def fits(t: Act): Boolean =
 *        this == t
 *  }
 *
 * val a1 = Act("answer", 42)
 * val a2 = Act("agreement", 42)
 *
 * a1.fits(Act("answer", 42))
 * //> res0: Boolean = true
 * a2.fits(Act("agreement", 42))
 * //> res1: Boolean = false
 * }}}
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @tparam This type of this instantiated generator
 * @tparam Trigger type of the trigger of this generator
 */
trait InstantiatedGenerator[This, Trigger] extends Generator[This, Trigger]
  with Instantiated[This] {
  this: This =>

  protected def bindingsImpl(t: Trigger): InstantiationSet =
    InstantiationSet()
}
