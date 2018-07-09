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
package fr.dubuissonduplessis.dogma.ditpp

import Dimension._

/**
 * Trait that augments a dialogue act with a DIT++ dimension.
 * Before mixin this trait, checks that the '''implemented child traits''' do not suit your needs:
 *  - [[fr.dubuissonduplessis.dogma.ditpp.impl.TaskDialogueAct]]: defines a "task" dialogue act.
 *  - [[fr.dubuissonduplessis.dogma.ditpp.impl.DialogueStructureDialogueAct]]: defines a "dialogue structure management" dialogue act.
 *  - [[fr.dubuissonduplessis.dogma.ditpp.impl.AutoFeedbackDialogueAct]]: defines an "auto-feedback" dialogue act.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
trait DITDialogueAct {
  /**
   * Dimension of the dialogue act
   * @return the dimension of the dialogue act
   */
  def dimension: Dimension
}
