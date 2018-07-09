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
package fr.dubuissonduplessis.dogma.commitment.util

import fr.dubuissonduplessis.dogma.commitment.SemanticTypes
import fr.dubuissonduplessis.dogma.description.Description
import fr.dubuissonduplessis.dogma.description.compositeDescription.PersistentConditional
import fr.dubuissonduplessis.dogma.description.EventDescription
import fr.dubuissonduplessis.dogma.description.compositeDescription.Alternative
import fr.dubuissonduplessis.dogma.description.compositeDescription.Conditional
import fr.dubuissonduplessis.dogma.operation.Operation
import fr.dubuissonduplessis.dogma.event.dialogueAct.DialogueAct
import fr.dubuissonduplessis.dogma.event.DialogicEventFromLocutor
import fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct
import fr.dubuissonduplessis.dogma.game.language.AbstractGameProposition
import fr.dubuissonduplessis.dogma.game.language.GameProposition
import fr.dubuissonduplessis.dogma.event.dialogueAct.NonStandardDialogueAct
import fr.dubuissonduplessis.dogma.game.language.EmbeddedGameProposition
import fr.dubuissonduplessis.dogma.game.language.PreSequenceGameProposition
import fr.dubuissonduplessis.dogma.game.language.SequenceGameProposition
import fr.dubuissonduplessis.dogma.event.InternalEvent
import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.event.ExtraDialogicEvent
import fr.dubuissonduplessis.dogma.description.StraightforwardEventDescription
import fr.dubuissonduplessis.dogma.description.dialogueAct.DialogueActEventDescription
import fr.dubuissonduplessis.dogma.description.dialogueAct.ConstrainedDialogueActEventDescription
import fr.dubuissonduplessis.dogma.description.dialogueAct.SemanticConstraint
import fr.dubuissonduplessis.dogma.description.dialogueAct.NonStandardDialogueActEventDescription
import fr.dubuissonduplessis.dogma.description.dialogueAct.ConstrainedNonStandardDialogueActEventDescription
import fr.dubuissonduplessis.dogma.description.dialogueAct.ConstrainedLeftNonStandardDialogueActEventDescription
import fr.dubuissonduplessis.dogma.description.dialogueAct.LeftNonStandardDialogueActEventDescription
import fr.dubuissonduplessis.dogma.description.dialogueAct.RightNonStandardDialogueActEventDescription
import fr.dubuissonduplessis.dogma.description.dialogueAct.ConstrainedRightNonStandardDialogueActEventDescription
import fr.dubuissonduplessis.dogma.game.language.instantiable.AbstractGamePropositionEventDescription
import fr.dubuissonduplessis.dogma.game.language.instantiable.CompositeGamePropositionMatcherEventDescription
import fr.dubuissonduplessis.dogma.game.language.instantiable.SemiInstantiatedVariableCombination
import fr.dubuissonduplessis.dogma.game.language.CombinationSymbol
import fr.dubuissonduplessis.dogma.util.PredefLaTeX

trait SemanticTypesToLaTeX {
  this: SemanticTypes =>
  /**
   * Computes the LaTeX representation of a proposition.
   */
  protected def propToLatex(prop: PropContent): String
  /**
   * Computes the LaTeX representation of an action.
   */
  protected def actionToLatex(prop: ActionContent): String

  /**
   * Computes the LaTeX representation of any semantic content.
   * @throws TypeConstraintException thrown if the given content can not be identified to a legal semantic content
   */
  protected def semanticContentToLatex(content: Any): String =
    content match {
      case prop: AbstractGameProposition =>
        dialogueGamePropositionToLatex(prop)
      case other =>
        domainSemanticContentToLatex(other)
    }

  /**
   * Computes the LaTeX representation of a domain semantic content (action, proposition, question, etc.).
   *  @return TypeConstraintException thrown if the given content can not be identified to a legal semantic content
   */
  protected def domainSemanticContentToLatex(content: Any): String
  /* Use: 
   *  - def propToLatex(prop: PropContent): String
   *  - def actionToLatex(prop: ActionContent): String
   */

  protected def dialogueGamePropositionToLatex(gameProp: AbstractGameProposition): String =
    gameProp match {
      case GameProposition(name, goal) =>
        """\semPredUn{""" + PredefLaTeX.escapeLaTeXSpecialCharacters(name) + """}""" +
          """{""" + semanticContentToLatex(goal) + """}"""
      case EmbeddedGameProposition(g1, g2) =>
        """\combinaisonEmboitement{""" + dialogueGamePropositionToLatex(g1) + """}{""" + dialogueGamePropositionToLatex(g2) + """}"""
      case SequenceGameProposition(g1, g2) =>
        """\combinaisonSequence{""" + dialogueGamePropositionToLatex(g1) + """}{""" + dialogueGamePropositionToLatex(g2) + """}"""
      case PreSequenceGameProposition(g1, g2) =>
        """\combinaisonPreSequence{""" + dialogueGamePropositionToLatex(g1) + """}{""" + dialogueGamePropositionToLatex(g2) + """}"""
      case other =>
        other.toString
    }

  private def semiInstantiatedVariableCombinationToLatex(v: SemiInstantiatedVariableCombination): String =
    {
      val v1Str = fr.dubuissonduplessis.dogma.instantiable.util.toLatex(v.v1)
      val v2Str = semanticContentToLatex(v.prop0)

      v.symbol match {
        case CombinationSymbol.Sequence =>
          """\combinaisonSequence{""" + v1Str + """}{""" + v2Str + """}"""
        case CombinationSymbol.PreSequence =>
          """\combinaisonPreSequence{""" + v1Str + """}{""" + v2Str + """}"""
        case CombinationSymbol.Embedding =>
          """\combinaisonEmboitement{""" + v1Str + """}{""" + v2Str + """}"""
      }
    }

  protected def operationToLatex(op: Operation): String
  protected def semanticConstraintToLatex[T](constraint: SemanticConstraint[T]): String

  /**
   * Computes the LaTeX representation of a description.
   */
  protected def descriptionToLatex(prop: Description): String =
    prop match {
      case Conditional(premisse, operation) =>
        """\conditionalActions{""" + descriptionToLatex(premisse) + """}{""" +
          operationToLatex(operation) + """}"""
      case PersistentConditional(premisse, operation) =>
        """\persistentConditionalActions{""" + descriptionToLatex(premisse) + """}{""" +
          operationToLatex(operation) + """}"""
      case Alternative(left, right) =>
        descriptionToLatex(left) + """ \dgOR{} """ + descriptionToLatex(right)
      case eventDesc: EventDescription =>
        eventDescriptionToLatex(eventDesc)
    }

  private def eventDescriptionToLatex(desc: EventDescription): String =
    if (desc.isNegation) {
      """$\neg$""" + eventDescriptionToLatex(desc.neg)
    } else {
      desc match {
        // Straightforward description (a fully instantiated event)
        case eventDesc: StraightforwardEventDescription =>
          eventToLatex(eventDesc.event)

        // Dialogue act with an uninstantiated content
        case da: DialogueActEventDescription[_, _] =>
          fr.dubuissonduplessis.dogma.event.dialogueAct.util.standardDialogueActToLatex(
            da.name,
            fr.dubuissonduplessis.dogma.dialogue.util.toLatex(da.locutor),
            fr.dubuissonduplessis.dogma.instantiable.util.toLatex(da.content))

        // Dialogue act with an uninstantiated content and a constraint
        case da: ConstrainedDialogueActEventDescription[_, _] =>
          val constraintStr = semanticConstraintToLatex(da.constraint)

          fr.dubuissonduplessis.dogma.event.dialogueAct.util.standardDialogueActToLatex(
            da.name,
            fr.dubuissonduplessis.dogma.dialogue.util.toLatex(da.locutor),
            fr.dubuissonduplessis.dogma.instantiable.util.toLatex(da.content)) +
            // Constraint representation
            s""" \\avec{} $constraintStr"""

        // Dialogue act with uninstantiated contents
        case da: NonStandardDialogueActEventDescription[_, _] =>
          fr.dubuissonduplessis.dogma.event.dialogueAct.util.nonStandardDialogueActToLatex(
            da.name,
            fr.dubuissonduplessis.dogma.dialogue.util.toLatex(da.locutor),
            fr.dubuissonduplessis.dogma.instantiable.util.toLatex(da.content1),
            fr.dubuissonduplessis.dogma.instantiable.util.toLatex(da.content2))

        // Dialogue act with uninstantiated contents and a constraint
        case da: ConstrainedNonStandardDialogueActEventDescription[_, _] =>
          val constraintStr = semanticConstraintToLatex(da.constraint)

          fr.dubuissonduplessis.dogma.event.dialogueAct.util.nonStandardDialogueActToLatex(
            da.name,
            fr.dubuissonduplessis.dogma.dialogue.util.toLatex(da.locutor),
            fr.dubuissonduplessis.dogma.instantiable.util.toLatex(da.content1),
            fr.dubuissonduplessis.dogma.instantiable.util.toLatex(da.content2)) +
            // Constraint representation
            s""" \\avec{} $constraintStr"""

        // Dialogue act with a right uninstantiated content
        case da: LeftNonStandardDialogueActEventDescription[_, _, _] =>
          fr.dubuissonduplessis.dogma.event.dialogueAct.util.nonStandardDialogueActToLatex(
            da.name,
            fr.dubuissonduplessis.dogma.dialogue.util.toLatex(da.locutor),
            semanticContentToLatex(da.content1),
            fr.dubuissonduplessis.dogma.instantiable.util.toLatex(da.content2))

        // Dialogue act with a right uninstantiated content and a constraint
        case da: ConstrainedLeftNonStandardDialogueActEventDescription[_, _, _] =>
          val constraintStr = semanticConstraintToLatex(da.constraint)

          fr.dubuissonduplessis.dogma.event.dialogueAct.util.nonStandardDialogueActToLatex(
            da.name,
            fr.dubuissonduplessis.dogma.dialogue.util.toLatex(da.locutor),
            semanticContentToLatex(da.content1),
            fr.dubuissonduplessis.dogma.instantiable.util.toLatex(da.content2)) +
            // Constraint representation
            s""" \\avec{} $constraintStr"""

        // Dialogue act with a left uninstantiated content
        case da: RightNonStandardDialogueActEventDescription[_, _, _] =>
          fr.dubuissonduplessis.dogma.event.dialogueAct.util.nonStandardDialogueActToLatex(
            da.name,
            fr.dubuissonduplessis.dogma.dialogue.util.toLatex(da.locutor),
            fr.dubuissonduplessis.dogma.instantiable.util.toLatex(da.content1),
            semanticContentToLatex(da.content2))

        // Dialogue act with uninstantiated content and a constraint
        case da: ConstrainedRightNonStandardDialogueActEventDescription[_, _, _] =>
          val constraintStr = semanticConstraintToLatex(da.constraint)

          fr.dubuissonduplessis.dogma.event.dialogueAct.util.nonStandardDialogueActToLatex(
            da.name,
            fr.dubuissonduplessis.dogma.dialogue.util.toLatex(da.locutor),
            fr.dubuissonduplessis.dogma.instantiable.util.toLatex(da.content1),
            semanticContentToLatex(da.content2)) +
            // Constraint representation
            s""" \\avec{} $constraintStr"""

        // Dialogue act uninstantiated game proposition
        case da: AbstractGamePropositionEventDescription[_] =>
          fr.dubuissonduplessis.dogma.event.dialogueAct.util.standardDialogueActToLatex(
            da.name,
            fr.dubuissonduplessis.dogma.dialogue.util.toLatex(da.locutor),
            fr.dubuissonduplessis.dogma.game.language.instantiable.util.toLatex(da.content))

        // Dialogue act uninstantiated game proposition
        case da: CompositeGamePropositionMatcherEventDescription[_] =>
          fr.dubuissonduplessis.dogma.event.dialogueAct.util.standardDialogueActToLatex(
            da.name,
            fr.dubuissonduplessis.dogma.dialogue.util.toLatex(da.locutor),
            semiInstantiatedVariableCombinationToLatex(da.content))
      }
    }

  protected def eventToLatex(evt: Event): String =
    evt match {
      case e: InternalEvent =>
        internalEventToLatex(e)
      case e: ExtraDialogicEvent =>
        extraDialogicEventToLatex(e)
      case e: DialogicEventFromLocutor =>
        dialogicEventFromLocutorToLatex(e)
    }

  protected def internalEventToLatex(evt: InternalEvent): String

  protected def extraDialogicEventToLatex(evt: ExtraDialogicEvent): String

  protected def dialogicEventFromLocutorToLatex(evt: DialogicEventFromLocutor): String =
    evt match {
      case da: NonStandardDialogueAct[_, _] =>
        fr.dubuissonduplessis.dogma.event.dialogueAct.util.nonStandardDialogueActToLatex(da)(semanticContentToLatex, semanticContentToLatex)
      case da: StandardDialogueAct[_] =>
        fr.dubuissonduplessis.dogma.event.dialogueAct.util.standardDialogueActToLatex(da)(semanticContentToLatex)
      case da: DialogueAct =>
        fr.dubuissonduplessis.dogma.event.dialogueAct.util.dialogueActToLatex(da)
      case other =>
        other.toString
    }
}
