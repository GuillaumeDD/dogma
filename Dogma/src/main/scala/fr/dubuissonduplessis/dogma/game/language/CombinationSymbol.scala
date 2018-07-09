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
package fr.dubuissonduplessis.dogma.game.language

/**
 * Symbols of combinations of dialogue game proposition.
 *
 * It includes symbols of sequence (";"), pre-sequence ("~>") and embedding ("<").
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
object CombinationSymbol extends Enumeration {
  type CombinationSymbol = Value
  val Sequence, PreSequence, Embedding = Value

  /**
   * Turns a combination symbol into a standardized string representation
   */
  def toString(comb: CombinationSymbol): String =
    comb match {
      case Sequence =>
        ";"
      case PreSequence =>
        "~>"
      case Embedding =>
        "<"
    }
}
