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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.util

import fr.dubuissonduplessis.dogma.conversationalGameBoard.ConversationalGameBoard
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.util.CommitmentStoreToLaTeX
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.util.GameManagerToLaTeX
import fr.dubuissonduplessis.dogma.dialogue.impl.TwoInterlocutors
import fr.dubuissonduplessis.dogma.event.DialogicEventFromLocutor
import fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct
import fr.dubuissonduplessis.dogma.event.dialogueAct.NonStandardDialogueAct
import fr.dubuissonduplessis.dogma.event.dialogueAct.DialogueAct
import fr.dubuissonduplessis.dogma.game.language.AbstractGameProposition
import fr.dubuissonduplessis.dogma.game.language.GameProposition
import fr.dubuissonduplessis.dogma.game.language.EmbeddedGameProposition
import fr.dubuissonduplessis.dogma.game.language.SequenceGameProposition
import fr.dubuissonduplessis.dogma.game.language.PreSequenceGameProposition
import fr.dubuissonduplessis.dogma.operation.Operation
import fr.dubuissonduplessis.dogma.event.InternalEvent
import fr.dubuissonduplessis.dogma.util.PredefLaTeX

/**
 * Module providing utilities to compute the LaTeX representation of a conversational game board.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait ConversationalGameBoardToLaTeX extends CommitmentStoreToLaTeX with GameManagerToLaTeX {
  this: ConversationalGameBoard with TwoInterlocutors =>

  import PredefLaTeX._

  private val firstInterlocutor = interlocutor01
  private val secondInterlocutor = interlocutor02

  protected def cgbToLatex: String =
    """	
\isGDDLight{""" + firstInterlocutor + """}{""" + secondInterlocutor + """}
  {
    % Tour de parole
    """ +
      movesToLatex +
      """
  }""" +
      """{
    % Prop/Crt
  """ +
      propCommitmentsToLatex(firstInterlocutor, secondInterlocutor) +
      """
  }
  {
  	% Action/Crt and Fal
""" +
      actionCommitmentsToLatex(firstInterlocutor, secondInterlocutor) +
      """
  }
  {
    % Dialogic Action
	""" +
      dialogicActionCommitmentsToLatex(firstInterlocutor, secondInterlocutor) +
      """
  }
  {
""" + gamesToLatex +
      """    
  }
    """
  //      """    
  //  }
  //    """
  // Old CGB
  //    """
  //\isGDD{""" + firstInterlocutor + """}{""" + secondInterlocutor + """}
  //  {
  //    % Tour de parole
  //    """ +
  //      movesToLatex +
  //      """
  //  }
  //  {
  //""" + commitmentStoreToLatex(firstInterlocutor) +
  //      """
  //  }
  //  {
  //""" + commitmentStoreToLatex(secondInterlocutor) +
  //      """
  //  }
  //  {
  //""" + gamesToLatex +
  //      """    
  //  }
  //    """

  var movePerLine: Int = 3

  // Helper methods
  private def newMove(m: Move): Boolean =
    m.t == currentTime - 1

  private def movesToLatex: String = {
    if (this.lastMoves.isEmpty) {
      emptyList
    } else {
      """\liste{
    """ +
        {
          val moves = this.lastMoves
            .map(m => textInMathMode(moveToLatex(m)))
            // Operations to add a LaTeX newline every n move (defined by movePerLine)
            .sliding(movePerLine, movePerLine)
            .map(_ :+ latexNewline)
            .flatten
            .toList
            .dropRight(1)

          (moves.slice(0, moves.size - 1)
            .map(str => {
              if (str != latexNewline) {
                str + ", "
              } else {
                str
              }
            })
            // We had a coma except to the last element
            :+ moves.last)
            .mkString
        } +
        """
    }"""
    }
  }

  private def moveToLatex(m: Move): String =
    if (newMove(m)) {
      newWrapping("""\tour{""" + m.t + """}{""" + dialogicEventFromLocutorToLatex(m.event) + """}""")
    } else {
      """\tour{""" + m.t + """}{""" + dialogicEventFromLocutorToLatex(m.event) + """}"""
    }

  protected def operationToLatex(op: Operation): String =
    op match {
      case Create(creator, commitment) =>
        """\create{""" + fr.dubuissonduplessis.dogma.dialogue.util.toLatex(creator) + """}{""" +
          commitmentToLatex(commitment) + """}"""
      case Cancel(creator, commitment) =>
        """\cancel{""" + fr.dubuissonduplessis.dogma.dialogue.util.toLatex(creator) + """}{""" +
          commitmentToLatex(commitment) + """}"""
      case Failure(creator, commitment) =>
        """\failure{""" + fr.dubuissonduplessis.dogma.dialogue.util.toLatex(creator) + """}{""" +
          // Commitment State to fail as a shortcut
          commitmentToLatex(commitment.failed) + """}"""
      case _ =>
        op.toString
    }

  protected def internalEventToLatex(evt: InternalEvent): String =
    evt match {
      case e: GlobalEvent =>
        e.name + "(" + e.game.id + ")"
      case e: IndividualEvent =>
        e.name + "(" + e.game.id + ", " +
          fr.dubuissonduplessis.dogma.dialogue.util.toLatex(e.participant) + ")"
      case _ =>
        evt.toString
    }

}
