/*******************************************************************************
 * Copyright (c) 2014 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> - initial API and implementation
 ******************************************************************************/
package fr.dubuissonduplessis.dogma.game.language.instantiable

package object util {
  def toLatex(v: VariableCombination): String = {
    v match {
      case PreSequenceCombination(v1, v2) =>
        val v1Str = fr.dubuissonduplessis.dogma.instantiable.util.toLatex(v1)
        val v2Str = fr.dubuissonduplessis.dogma.instantiable.util.toLatex(v2)
        s"""\combinaisonPreSequence{$v1Str}{$v2Str}"""
      case SequenceCombination(v1, v2) =>
        val v1Str = fr.dubuissonduplessis.dogma.instantiable.util.toLatex(v1)
        val v2Str = fr.dubuissonduplessis.dogma.instantiable.util.toLatex(v2)
        s"""\combinaisonSequence{$v1Str}{$v2Str}"""
      case EmbeddedCombination(v1, v2) =>
        val v1Str = fr.dubuissonduplessis.dogma.instantiable.util.toLatex(v1)
        val v2Str = fr.dubuissonduplessis.dogma.instantiable.util.toLatex(v2)
        s"""\combinaisonEmboitement{$v1Str}{$v2Str}"""
    }
  }
}
