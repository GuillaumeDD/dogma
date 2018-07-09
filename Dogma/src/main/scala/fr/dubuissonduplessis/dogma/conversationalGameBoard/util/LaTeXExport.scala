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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.util

import fr.dubuissonduplessis.dogma.conversationalGameBoard.ConversationalGameBoard
import fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ExplicitDogma
import fr.dubuissonduplessis.dogma.dialogue.impl.TwoInterlocutors

trait LaTeXExport extends ConversationalGameBoardToLaTeX with ExplicitDogmaToLaTeX {
  this: ConversationalGameBoard with ExplicitDogma with TwoInterlocutors =>

  override def toString: String =
    cgbToLatex + "\n" +
      explicitDogmaToLatex

}
