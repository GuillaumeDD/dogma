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

import fr.dubuissonduplessis.dogma.instantiable.Instantiable
import com.typesafe.scalalogging.slf4j.LazyLogging

/**
 * Abstract dialogue game proposition.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
abstract class AbstractGameProposition extends Instantiable[AbstractGameProposition]
  with LazyLogging {

  /**
   * Turns the game proposition into a right associative one.
   * @return a new game proposition which is right associative.
   */
  def rightAssociative(): AbstractGameProposition

  /**
   * Turns the game proposition into a left associative one.
   * @return a new game proposition which is left associative.
   */
  def leftAssociative(): AbstractGameProposition

  /**
   * Computes the game proposition minus its most left part, if this
   * proposition is composite. For example:
   *  - g1: GameProposition returns None
   *  - g1 ~> g2 ~> g3 returns Some(g2~>g3)
   *  - (g2 << g1) ~> g0 returns Some(g1 ~> g0)
   *
   * @return a new game proposition which corresponds to this game proposition minus its most left part,
   * or None if this proposition is not composite.
   */
  def dropLeft(): Option[AbstractGameProposition]

  /**
   * Determines the most left game proposition that is being dropped by dropLeft, if this
   * proposition is composite. For example:
   *  - g1: GameProposition returns None
   *  - g1 ~> g2 ~> g3 returns Some(g1)
   *  - (g2 << g1) ~> g0 returns Some(g2)
   *
   * @return Returns the most left game proposition that is being dropped by dropLeft, or None if this
   * proposition is not composite.
   */
  def getLeft(): Option[AnyGameProposition]

  /**
   * Determines if a given game proposition is included into this game proposition
   * @return true if this game proposition is included into this game proposition, else false
   */
  def contains(prop: AbstractGameProposition): Boolean

  /**
   * Builds a pre-sequence combination
   *
   */
  def ~>(gProp: AbstractGameProposition): PreSequenceGameProposition =
    PreSequenceGameProposition(this, gProp).rightAssociative
  /**
   * Builds a sequence combination
   */
  def /(gProp: AbstractGameProposition): SequenceGameProposition =
    SequenceGameProposition(this, gProp).rightAssociative

  /**
   * Builds an embedded combination
   */
  def <<(gProp: AbstractGameProposition): EmbeddedGameProposition =
    EmbeddedGameProposition(this, gProp).rightAssociative
}
