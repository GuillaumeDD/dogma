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

import fr.dubuissonduplessis.dogma.dialogue.Dialogue

/**
 * Provides modules to deal with dialogue game instances.
 * Its main module is [[fr.dubuissonduplessis.dogma.gameInstance.DialogueGameInstances]].
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
package object gameInstance {
  /**
   * Module requirements for [[fr.dubuissonduplessis.dogma.gameInstance.GameInstances]] as a type.
   */
  type GameInstancesRequirements = Dialogue
}
