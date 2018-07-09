/**
 * *****************************************************************************
 * Copyright (c) 2014 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Maxime MORGE - initial API and implementation
 *     Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> - initial API and implementation
 * ****************************************************************************
 */
package fr.lifl.smac.scadia.main

import fr.lifl.smac.scadia.ability.Lying
import fr.lifl.smac.scadia.ability.Reasoning

trait Ascii extends Reasoning with Lying {
  protected type LyingInput = Char
  protected type LyingOutput = Int
  protected type ReasoningInput = Char
  protected type ReasoningOutput = Int

  protected def compute(c: Char) = c.toInt
  protected def lie(c: Char): Int = 42
}
