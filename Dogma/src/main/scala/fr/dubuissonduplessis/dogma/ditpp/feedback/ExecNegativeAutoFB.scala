/**
 * *****************************************************************************
 * Copyright (c) 2013 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> - initial API and implementation
 * ****************************************************************************
 */
package fr.dubuissonduplessis.dogma.ditpp.feedback

import fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct
import fr.dubuissonduplessis.dogma.ditpp.impl.AutoFeedbackDialogueAct
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor

case class ExecNegativeAutoFB[Content](
  locutor: Interlocutor,
  content: Content) extends StandardDialogueAct[Content] with AutoFeedbackDialogueAct {
  val name: String = "execNegativeAutoFB"
}
