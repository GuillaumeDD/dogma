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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma

import fr.dubuissonduplessis.dogma.gameInstance.GameInstances
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperationsRequirements
/**
 * Module providing a default printing method for an [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ExplicitDogma ExplicitDogma]].
 *
 * The main method of this module is:
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.PrintableExplicitDogma#explicitDogmaToString explicitDogmaToString]].
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait PrintableExplicitDogma {
  this: ExplicitDogma with GameManagerOperations with GameManagerOperationsRequirements =>
  /**
   * Computes a String representation of an ExplicitDogma.
   */
  protected def explicitDogmaToString: String =
    "expectedDialogueActs=" +
      expectedDescriptionByGameInstance
      .keySet // We retrieve the set of game instances
      .toList.sorted(GameInstanceOrdering.reverse) // we turn the set into a list in order to sort it
      .map(game => "\t" + game.id.id + "(t=" + game.time + ")" + ": " + expectedDescriptionByGameInstance(game).mkString("\n\t\t", "\n\t\t", ""))
      .mkString("\n", "\n", "\n") +
      "forbiddenActs=" +
      forbiddenEvents.keySet.map(loc => loc + "=" +
        forbiddenEvents(loc).mkString("{", ", ", "}"))
      .mkString("\n\t", "\n\t", "\n") +
      "expectedExtraDialogicalActions=" +
      expectedExtraDialogicalActions.keySet.map(loc => loc + "=" +
        expectedExtraDialogicalActions(loc).mkString("{", ", ", "}"))
      .mkString("\n\t", "\n\t", "\n")

}
