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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.util

import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperationsRequirements
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations
import fr.dubuissonduplessis.dogma.util.PredefLaTeX
import fr.dubuissonduplessis.dogma.game.language.GameProposition
import fr.dubuissonduplessis.dogma.game.language.AbstractGameProposition

/**
 * Module providing utilities to compute the LaTeX representation of a game manager.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait GameManagerToLaTeX {
  this: GameManagerOperations with GameManagerOperationsRequirements =>

  import PredefLaTeX._

  protected def gamesToLatex: String =
    """
    \engagementJeux{
      % Suggested Games
    """ +
      textInMathMode(suggestedDialogueGamesToLatex) +
      """
    }{
      % Opened Games
      """ +
      textInMathMode(openedDialogueGamesToLatex) +
      """
    }{
      % Closed Games
      """ +
      textInMathMode(closedDialogueGamesToLatex) +
      """
    }{
      % Salient Games
      """ +
      salientGamesToLatex +
      """
    }"""

  protected def dialogueGamePropositionToLatex(gameProp: AbstractGameProposition): String

  // Helper method
  private def salientGamesToLatex: String =
    if (this.salientDialogueGames.isEmpty) {
      """\aucunJeu"""
    } else {
      this.salientDialogueGames
        .map(g => textInMathMode(g.id.toString))
        .mkString(", ")
    }

  private def isNew(game: DialogueGameInstance): Boolean =
    game.time == currentTime - 1

  private def suggestedDialogueGamesToLatex: String = {
    val (newGames, oldGames) =
      sortGames(suggestedDialogueGames).partition(isNew(_))
    val games =
      // We wrap new games in a special macro
      newGames.map(g => newWrapping(dialogueGameToLatex(g))) ++
        oldGames.map(dialogueGameToLatex(_))

    games.mkString(", ")
  }

  private def openedDialogueGamesToLatex: String = {
    val (newGames, oldGames) =
      sortGames(openedDialogueGames).partition(isNew(_))
    val games =
      // We wrap new games in a special macro
      newGames.map(g => newWrapping(dialogueGameToLatex(g))) ++
        oldGames.map(dialogueGameToLatex(_))

    games.mkString(", ")
  }

  private def closedDialogueGamesToLatex: String = {
    val (newGames, oldGames) =
      sortGames(closedDialogueGames).partition(isNew(_))
    val games =
      // We wrap new games in a special macro
      newGames.map(g => newWrapping(dialogueGameToLatex(g))) ++
        oldGames.map(dialogueGameToLatex(_))

    games.mkString(", ")
  }

  private def sortGames(dialogueGames: Set[DialogueGameInstance]): List[DialogueGameInstance] =
    dialogueGames.toList.sorted(GameInstanceOrdering.reverse)

  private def dialogueGameToLatex(dg: DialogueGameInstance): String =
    """\jeu{""" +
      dialogueGamePropositionToLatex(dg.gameProposition) +
      """}{""" + dg.id + """}"""
}
