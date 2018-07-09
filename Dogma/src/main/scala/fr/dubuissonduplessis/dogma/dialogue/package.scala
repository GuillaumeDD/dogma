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
package fr.dubuissonduplessis.dogma

/**
 * Provides classes for dealing with the dialogue situation.
 * It contains the class [[fr.dubuissonduplessis.dogma.dialogue.Interlocutor]].
 *
 * This package also provides the module [[fr.dubuissonduplessis.dogma.dialogue.Dialogue]].
 *
 * == Overview ==
 * The main class to use is [[fr.dubuissonduplessis.dogma.dialogue.Interlocutor]]:
 * {{{
 * scala> val bob = Interlocutor("Bob")
 * bob: Interlocutor = Bob
 * }}}
 *
 * It also provides default interlocutors "x" and "y":
 * {{{
 * scala> val interlocutor: Interlocutor = X
 * interlocutor: Interlocutor = x
 *
 * scala> val otherInterlocutor: Interlocutor = Y
 * otherInterlocutor: Interlocutor = y
 * }}}
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
package object dialogue {
  /**
   * Default dialogue participant with name "x"
   */
  object X extends Interlocutor("x")

  /**
   * Default dialogue participant with name "y"
   */
  object Y extends Interlocutor("y")
}
