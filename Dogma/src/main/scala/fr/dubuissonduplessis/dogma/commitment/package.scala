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
import fr.dubuissonduplessis.dogma.gameInstance.GameInstances

/**
 * Provides modules related to commitments for Dogma.
 *
 * It includes two main modules:
 *  - [[fr.dubuissonduplessis.dogma.commitment.SemanticTypes]]
 *  - [[fr.dubuissonduplessis.dogma.commitment.Commitments]]
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
package object commitment {
  /**
   * Module requirements for [[fr.dubuissonduplessis.dogma.commitment.Commitments]] as a type.
   */
  type CommitmentsRequirements = Dialogue with SemanticTypes with GameInstances
}
