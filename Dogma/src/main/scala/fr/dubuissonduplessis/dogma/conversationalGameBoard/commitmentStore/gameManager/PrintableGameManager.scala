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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager

import fr.dubuissonduplessis.dogma.gameInstance.DialogueGameInstances
import fr.dubuissonduplessis.dogma.dialogue.Dialogue
/**
 * Module providing a default printing method for a game manager.
 *
 * The main method of this module is:
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.PrintableGameManager#gameManagerToString gameManagerToString]].
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait PrintableGameManager {
  this: Dialogue with GameManager with GameManagerQueries with DialogueGameInstances =>

  private def sort(dialogueGames: Set[DialogueGameInstance]): List[DialogueGameInstance] =
    dialogueGames.toList.sorted(GameInstanceOrdering.reverse)

  /**
   * Computes a string representation of a game manager.
   */
  protected def gameManagerToString: String =
    "Dialogue Games:\n" +
      "\tSuggested=" + sort(suggestedDialogueGames).mkString("{", ", ", "}") + "\n" +
      "\tOpened=" + sort(openedDialogueGames).mkString("[", " < ", "]") + "\n" +
      "\tClosed=" + sort(closedDialogueGames)
      // Print the state of the game for each participant
      .map(closedInstance => (closedInstance,
        // Recover the state for each participant in a pair (participant, gameState for the participant)
        interlocutors.map(i =>
          (i, gameStateFor(closedInstance, i)))
        // Make a string of the set of pair
        .mkString(", ")))

      .mkString("{", ", ", "}") + "\n" +
      "\tSalient game(s)= " + salientGames.mkString("{", ", ", "}") + "\n" +
      "Communication Games= " + communicationGames.mkString("{", ", ", "}")
}
