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
package fr.dubuissonduplessis.dogma.instantiable

import scala.collection.immutable.Set

/**
 * Defines a generator in the instantiation process. A generator is an instantiable that
 * produces variable bindings given a trigger.
 *
 * Default implementations are available for the following cases:
 *  - an instantiated generator (see [[fr.dubuissonduplessis.dogma.instantiable.impl.InstantiatedGenerator]])
 *  - a non-generator generator (see [[fr.dubuissonduplessis.dogma.instantiable.impl.NonGenerator]])
 *  - a proxy of generator (see [[fr.dubuissonduplessis.dogma.instantiable.impl.GeneratorProxy]])
 *
 * ==Example==
 * {{{
 *   // Definition of an act : the trigger
 * case class Act(name: String, content: Int)
 *
 * // Definition of a generator of act that is trigger by an act
 * case class EventGenerator(name: String, v: Variable[Int]) extends Generator[Act, Act] {
 *    // Generator part
 *    // This generator fits acts with the same name
 *    def fits(trigger: Act): Boolean =
 *       trigger.name == name
 *
 *    // This generator binds the variable v with the content of an act
 *    // (provided that it has the same name)
 *    protected def bindingsImpl(t: Act): InstantiationSet =
 *       InstantiationSet() + (v, t.content)
 *
 *    // Instantiable part
 *    def isInstantiableWith(s: InstantiationSet): Boolean =
 *       v.isInstantiableWith(s)
 *
 *    def variables: Set[InstantiationVariable] =
 *       Set(v)
 *
 *    protected def instantiateWithImpl(s: InstantiationSet): Act =
 *       Act(name, v.instantiateWith(s))
 * }
 *
 * // Definition of a variable
 * val x = Variable[Int]("X")
 * // Creation of a generator that fits acts which name is "answer"
 * val gen = EventGenerator("answer", x)
 *
 * // Two example acts
 * val a1 = Act("answer", 42)
 * val a2 = Act("agreement", 42)
 *
 * // Does the generator fit these acts?
 * gen.fits(a1)
 * //> res0: Boolean = true
 * gen.fits(a2)
 * //> res1: Boolean = false
 *
 * // What is the binding generated from a1?
 * gen.bindings(a1)
 * //> res2: fr.dubuissonduplessis.dogma.instantiable.InstantiationSet = X -> 42
 *
 * // Try to generate from a1
 * gen.generate(a1)
 * //> res3: Act = Act(answer,42)
 * }}}
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @tparam T type of the  instantiation process output of this instantiable
 * @tparam Trigger type of the trigger of this generator
 */
trait Generator[+T, Trigger] extends Instantiable[T] {

  /**
   * Determines if this generator is triggered by a given trigger.
   * A triggered generator may generate a non-empty instantiation set.
   * This predicate is a precondition of the method [[fr.dubuissonduplessis.dogma.instantiable.Generator#generate]].
   * This means that if fits(t) is true, it implies that this instantiable is instantiable with bindings(t).
   * See algorithm of method [[fr.dubuissonduplessis.dogma.instantiable.Generator#generate]].
   * @param t trigger
   * @return true if this generator is triggered by the trigger, else false
   */
  def fits(t: Trigger): Boolean

  /**
   * Computes the generated instantiation set given a trigger.
   *  - If the trigger does not fit this generator, the generated instantiation set is
   * empty.
   *  - Implements the template method pattern, see method [[fr.dubuissonduplessis.dogma.instantiable.Generator#bindingsImpl]]
   *    to implement the binding computation process.
   * @param t trigger
   * @return the instantiation set computed from the given trigger
   */
  def bindings(t: Trigger): InstantiationSet =
    if (!fits(t)) {
      InstantiationSet()
    } else {
      bindingsImpl(t)
    }

  protected def bindingsImpl(t: Trigger): InstantiationSet

  /**
   * Realizes the binding computation process as well as the instantiation process.
   *  - Precondition: fits(t) must be true.
   *
   * Generate algorithm is equivalent to:
   * {{{
   * this.instantiateWith(bindings(t))
   * }}}
   *
   * @param t trigger
   * @return the result of this instantiation process given the binding set generated
   * by this generator
   */
  def generate(t: Trigger): T =
    {
      require(fits(t), s"$t does not match $this")
      val instSet = bindings(t)
      this.instantiateWith(instSet)
    }
}
