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
package fr.dubuissonduplessis.dogma.game

import com.typesafe.scalalogging.slf4j.LazyLogging
/**
 * Provides the language to manage dialogue game propositions and their combinations (pre-sequence, sequence, embedding, etc.)
 *
 * A dialogue game proposition may refer to:
 *  - a simple dialogue game proposition that refers to a single
 *    dialogue game (see [[fr.dubuissonduplessis.dogma.game.language.GameProposition]])
 *  - a composite dialogue game proposition that refers to a combination of dialogue games
 *    (see [[fr.dubuissonduplessis.dogma.game.language.AbstractComposedGameProposition]])
 *
 * Current combinations include:
 *  - Pre-sequence: [[fr.dubuissonduplessis.dogma.game.language.PreSequenceGameProposition]]
 *  - Sequence: [[fr.dubuissonduplessis.dogma.game.language.SequenceGameProposition]]
 *  - Embedding: [[fr.dubuissonduplessis.dogma.game.language.EmbeddedGameProposition]]
 *
 * Note that the API makes it easy to build combinations from dialogue game propositions:
 * {{{
 * val questionProp1 = GameProposition("interrogation", "To be or not to be?")
 * val questionProp2 = GameProposition("interrogation", "Do you want to speak about Shakespeare?")
 * val requestProp = GameProposition("request","Give me the book 'Hamlet'")
 *
 *  // Build a combination from isolated dialogue game proposition
 * (questionProp2 ~> questionProp1) / requestProp
 * //> res0: fr.dubuissonduplessis.dogma.game.language.SequenceGameProposition =
 * // (interrogation(Do you want to speak about Shakespeare?)~>interrogation(To be or not to be?));request(Give me the book 'Hamlet')
 * }}}
 *
 * Sub-package [[fr.dubuissonduplessis.dogma.game.language.instantiable]] provides ways to build uninstantiated event description
 * based on dialogue game propositions.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
package object language extends LazyLogging {
  /**
   * Type of any simple dialogue game proposition
   */
  type AnyGameProposition = GameProposition[_]

  /**
   * Returns the set of game proposition constituting of an abstract proposition.
   * Basically, it returns:
   *  - gProp: GameProposition => gProp
   *  - gProp1 ~> gProp2 => linearize(gProp1) ++ linearize(gProp2)
   *  - gProp1 / gProp2 => linearize(gProp1) ++ linearize(gProp2)
   *  - gProp1 << gProp2 => linearize(gProp1) ++ linearize(gProp2)
   *
   * @param prop dialogue game proposition that is linearized.
   * @return the set of game proposition constituting of an abstract proposition.
   */
  def linearize(prop: AbstractGameProposition): Set[AnyGameProposition] =
    prop match {
      case AbstractComposedGameProposition(g1, g2) =>
        linearize(g1) ++ linearize(g2)
      case GameProposition(name, goal) =>
        Set(GameProposition(name, goal))
    }

  /**
   * Determines the difference between extended and prop by consuming extended
   * against prop.
   * If extended is a left-extension of prop, it returns the addition of
   * game propositions needed to transform prop into extended by left-extension.
   * If extended is not a left-extension of prop, it returns the set of game proposition
   * constituting extended. For example,
   *  - g1 will be consumed against g1 as the empty Set()
   *  - (g1 ~> g2 ~> g3) will be consumed against g3 as Set(g1, g2)
   *  - (((g0 < g1) ; g2) ~> g3) will be consumed against g3 as Set(g0, g1, g2)
   *
   * @param extended the "bigger" game proposition, potentially a left-extension of prop.
   * @param prop a "smaller" game proposition.
   * @return If extended is a left-extension of prop, it returns the addition of
   * game propositions needed to transform prop into extended by left-extension.
   * If extended is not a left-extension of prop, it returns the set of game proposition
   * constituting extended.
   */
  def consume(extended: AbstractGameProposition,
    prop: AbstractGameProposition): Set[AnyGameProposition] = {
    require(extended.instantiated)
    require(prop.instantiated)

    def consume0(
      extended0: AbstractGameProposition,
      prop0: AbstractGameProposition): Set[AnyGameProposition] =
      if (extended0 != prop0) {
        extended0.dropLeft() match {
          case None => // we cannot drop more proposition (we got a SimpleGameProposition, i.e. a leaf)
            extended0 match {
              case prop @ GameProposition(name, goal) =>
                Set(prop) // the only proposition is extended0
              case other =>
                throw new Error("We should have a GameProposition, but we have something else: " + other)
            }

          case Some(newProp) =>
            logger.debug(s"We add ${extended0.getLeft()} and we continue with $newProp (versus $prop0)")
            extended0.getLeft() match {
              case None =>
                consume0(newProp, prop0)
              case Some(leftGameProp) =>
                consume0(newProp, prop0) + leftGameProp
            }
        }
      } else {
        Set()
      }

    consume0(extended.rightAssociative(), prop.rightAssociative())
  }

  /**
   * Determines if extended is strictly a left-extension of prop.
   * This relation is not reflexive. For example,
   * <ul>
   * <li> g1 is NOT a left-extension of g1
   * <li> g2 ~> g1 is a left-extension of g1
   * <li> g1 ~> g2 is NOT a left-extension of g1
   * </ul>
   * @param extended Potential extension of prop.
   * @param prop Extended proposition
   * @return True if extended is a left-extension of prop, else false
   */
  def isAnExtensionOf(
    extended: AbstractGameProposition,
    prop: AbstractGameProposition): Boolean = {
    require(extended.instantiated)
    require(prop.instantiated)

    def isAnExtensionOf0(
      extended0: AbstractGameProposition,
      prop0: AbstractGameProposition): Boolean = {
      if (extended0 != prop0) {
        extended0.dropLeft() match {
          case None => false
          case Some(other) =>
            isAnExtensionOf0(other, prop0)
        }
      } else {
        // Proposition are identical
        true
      }
    }

    val extendedRightAssociative = extended.rightAssociative()
    val propRightAssociative = prop.rightAssociative()
    if (extendedRightAssociative != propRightAssociative) {
      isAnExtensionOf0(extendedRightAssociative, propRightAssociative)
    } else {
      false
    }
  }

}
