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
package fr.dubuissonduplessis.dogma.ditpp.impl

import fr.dubuissonduplessis.dogma.ditpp.DITDialogueAct
import fr.dubuissonduplessis.dogma.ditpp.Dimension

/**
 * Implemented trait that defines a dialogue act in the "dialogue structure management" dimension.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
trait DialogueStructureDialogueAct extends DITDialogueAct {
  import Dimension._
  def dimension: Dimension = DialogueStructureManagement
}
