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
import fr.dubuissonduplessis.dogma.commitment.util.CommitmentsToLaTeX

/**
 * Module providing utilities to compute the LaTeX representation of the exploitation of a conversational game board.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait ExplicitDogmaToLaTeX {
  this: ConversationalGameBoard with ExplicitDogma with CommitmentsToLaTeX =>

  def explicitDogmaToLatex: String =
    """
\isDogma
{
""" +
      expectedDialogueActByGameInstanceToLatex +
      """
}
{
""" +
      forbiddenDialogueActsToLatex +
      """
}
    """

  protected def forbiddenDialogueActsToLatex: String =
    {
      val orderedGameInstances = forbiddenEventsByGameInstance
        .keySet // We retrieve the set of game instances
        .toList
        .sorted(GameInstanceOrdering.reverse)
      if (orderedGameInstances.isEmpty) {
        """\aucunActe"""
      } else {
        // For each game instance, we print the expected actions
        (for (g <- orderedGameInstances)
          yield (
          // Print the game
          """ \nouveauJeu{""" + g.id + """}""" + "\n" +
          // Print the expected actions of the game
          forbiddenEventsByGameInstance(g)
          .map(a => """\acteAttendu{""" + descriptionToLatex(a) + """}""")
          .mkString("\n")))
          .mkString("\n")
      }
    }

  protected def expectedDialogueActByGameInstanceToLatex: String =
    {
      val orderedGameInstances = expectedDescriptionByGameInstance
        .keySet // We retrieve the set of game instances
        .filter(_.isDialogueGame) // We keep dialogue games
        .toList
        .sorted(GameInstanceOrdering.reverse)
      // Communication Games
      val expDialogueActsByCommunicationGames = expectedDescriptionByCommunicationGames

      if (orderedGameInstances.isEmpty && expDialogueActsByCommunicationGames.isEmpty) {
        """\aucunActe"""
      } else {
        // For each game instance, we print the expected actions
        (for (g <- orderedGameInstances)
          yield (
          // Print the game
          """ \nouveauJeu{""" + g.id + """}""" +
          // Print the expected actions of the game
          expectedDescriptionByGameInstance(g)
          .map(a => """\acteAttendu{""" + descriptionToLatex(a) + """}""")
          .mkString("\n")))
          .mkString("\n") +
          """ \nouveauJeu{""" +
          communicationGames.map(_.id).mkString(", ") + """} """ +
          (expDialogueActsByCommunicationGames
            .map(a => """\acteAttendu{""" + descriptionToLatex(a) + """}""")
            .mkString("\n"))
      }

    }
}
