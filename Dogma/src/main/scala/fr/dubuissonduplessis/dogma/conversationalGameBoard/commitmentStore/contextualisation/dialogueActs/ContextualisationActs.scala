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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs

import fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct
import fr.dubuissonduplessis.dogma.game.language.AbstractGameProposition
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.game.language.GameProposition
import fr.dubuissonduplessis.dogma.description.EventDescription
import fr.dubuissonduplessis.dogma.description.dialogueAct.GenericDialogueActEventDescription
import fr.dubuissonduplessis.dogma.instantiable.Variable
import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.game.language._
import fr.dubuissonduplessis.dogma.instantiable.Instantiable
import fr.dubuissonduplessis.dogma.game.language.instantiable._
import fr.dubuissonduplessis.dogma.description.dialogueAct.DialogueActEventDescription
import fr.dubuissonduplessis.dogma.description.Description
import fr.dubuissonduplessis.dogma.game.language.CombinationSymbol._

// prop.in
/**
 * Contextualisation dialogue act of proposition to enter a dialogue game (or a combination).
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
sealed abstract class propIn extends StandardDialogueAct[AbstractGameProposition] {
  val name = propIn.name
}

/**
 * Factories and extractors for [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.propIn propIn]].
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
object propIn {
  val name = "prop.in"
  /**
   * Builds this dialogue act from a given locutor and a given semantic content.
   *
   */
  def apply(
    locutor: Interlocutor,
    content: AbstractGameProposition): propIn =
    propInImpl(locutor, content)

  /**
   * Extracts the locutor and the semantic content of this dialogue act from an event, if possible.
   *
   */
  def unapply(evt: Event): Option[(Interlocutor, AbstractGameProposition)] = {
    evt match {
      case act: propIn =>
        Some((act.locutor, act.content))
      case _ =>
        None
    }
  }

  /**
   * Builds an event description of this act from a locutor and from an under-specified, unconstrained semantic content.
   * @see [[fr.dubuissonduplessis.dogma.description.dialogueAct.DialogueActEventDescription]]
   */
  def apply(
    interlocutor: Interlocutor,
    variable: Variable[AbstractGameProposition]): EventDescription =
    new DialogueActEventDescription[AbstractGameProposition, EventDescription] {
      def name: String = propIn.name
      def locutor: Interlocutor = interlocutor
      def content: Variable[AbstractGameProposition] = variable

      protected def variableBindingFrom(evt: Event): Option[(Variable[AbstractGameProposition], AbstractGameProposition)] =
        evt match {
          case propIn(locutor, cont) =>
            Some((content, cont))
        }

      protected def buildFrom(content: AbstractGameProposition): EventDescription =
        propIn(locutor, content)
    }

  private case class propInImpl(
    locutor: Interlocutor,
    content: AbstractGameProposition)
    extends propIn

  //e.g., propIn(interlocutor, X ~> Y)
  /**
   * Builds an event description of this act from a locutor and from an uninstantiated composite dialogue game proposition.
   * @see [[fr.dubuissonduplessis.dogma.game.language.instantiable]]
   */
  def apply(
    interlocutor: Interlocutor,
    content: VariableCombination): EventDescription =
    AbstractGamePropositionEventDescription(
      interlocutor, propIn.name, content, (locutor: Interlocutor, name: String, content: AbstractGameProposition) => propIn.this(locutor, content))

  //e.g., propIn(interlocutor, X << g0)
  /**
   * Builds an event description of this act from a locutor and from a semi-instantiated composite dialogue game proposition.
   * @see [[fr.dubuissonduplessis.dogma.game.language.instantiable]]
   */
  def apply(
    interlocutor: Interlocutor,
    content: CompositeGamePropositionMatcher): EventDescription =
    CompositeGamePropositionMatcherEventDescription(
      interlocutor, propIn.name, content, (locutor: Interlocutor, name: String, content: AbstractGameProposition) => propIn.this(locutor, content))
}

//acc.in
/**
 * Contextualisation dialogue act of acceptance to enter a dialogue game (or a combination).
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
sealed abstract class accIn extends StandardDialogueAct[AbstractGameProposition] {
  val name = accIn.name
}

/**
 * Factories and extractors for [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.accIn accIn]].
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
object accIn {
  val name = "acc.in"

  /**
   * Builds this dialogue act from a given locutor and a given semantic content.
   *
   */
  def apply(
    locutor: Interlocutor,
    content: AbstractGameProposition): accIn =
    accInImpl(locutor, content)

  /**
   * Extracts the locutor and the semantic content of this dialogue act from an event, if possible.
   *
   */
  def unapply(evt: Event): Option[(Interlocutor, AbstractGameProposition)] = {
    evt match {
      case act: accIn =>
        Some((act.locutor, act.content))
      case _ =>
        None
    }
  }
  /**
   * Builds an event description of this act from a locutor and from an under-specified, unconstrained semantic content.
   * @see [[fr.dubuissonduplessis.dogma.description.dialogueAct.DialogueActEventDescription]]
   */
  def apply(
    interlocutor: Interlocutor,
    variable: Variable[AbstractGameProposition]): EventDescription =
    new DialogueActEventDescription[AbstractGameProposition, EventDescription] {
      def name: String = accIn.name
      def locutor: Interlocutor = interlocutor
      def content: Variable[AbstractGameProposition] = variable

      protected def variableBindingFrom(evt: Event): Option[(Variable[AbstractGameProposition], AbstractGameProposition)] =
        evt match {
          case accIn(locutor, cont) =>
            Some((content, cont))
        }

      protected def buildFrom(content: AbstractGameProposition): EventDescription =
        accIn(locutor, content)
    }

  private case class accInImpl(
    locutor: Interlocutor,
    content: AbstractGameProposition)
    extends accIn
  /**
   * Builds an event description of this act from a locutor and from an uninstantiated composite dialogue game proposition.
   * @see [[fr.dubuissonduplessis.dogma.game.language.instantiable]]
   */
  def apply(
    interlocutor: Interlocutor,
    content: VariableCombination): EventDescription =
    AbstractGamePropositionEventDescription(
      interlocutor, accIn.name, content, (locutor: Interlocutor, name: String, content: AbstractGameProposition) => accIn.this(locutor, content))
}

// ref.in
/**
 * Contextualisation dialogue act of refusal to enter a dialogue game (or a combination).
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
sealed abstract class refIn extends StandardDialogueAct[AbstractGameProposition] {
  val name = refIn.name
}

/**
 * Factories and extractors for [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.refIn refIn]].
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
object refIn {
  val name = "ref.in"

  /**
   * Builds this dialogue act from a given locutor and a given semantic content.
   *
   */
  def apply(
    locutor: Interlocutor,
    content: AbstractGameProposition): refIn =
    refInImpl(locutor, content)

  /**
   * Extracts the locutor and the semantic content of this dialogue act from an event, if possible.
   *
   */
  def unapply(evt: Event): Option[(Interlocutor, AbstractGameProposition)] = {
    evt match {
      case act: refIn =>
        Some((act.locutor, act.content))
      case _ =>
        None
    }
  }
  /**
   * Builds an event description of this act from a locutor and from an under-specified, unconstrained semantic content.
   * @see [[fr.dubuissonduplessis.dogma.description.dialogueAct.DialogueActEventDescription]]
   */
  def apply(
    interlocutor: Interlocutor,
    variable: Variable[AbstractGameProposition]): EventDescription =
    new DialogueActEventDescription[AbstractGameProposition, EventDescription] {
      def name: String = refIn.name
      def locutor: Interlocutor = interlocutor
      def content: Variable[AbstractGameProposition] = variable

      protected def variableBindingFrom(evt: Event): Option[(Variable[AbstractGameProposition], AbstractGameProposition)] =
        evt match {
          case refIn(locutor, cont) =>
            Some((content, cont))
        }

      protected def buildFrom(content: AbstractGameProposition): EventDescription =
        refIn(locutor, content)
    }

  private case class refInImpl(
    locutor: Interlocutor,
    content: AbstractGameProposition)
    extends refIn
  /**
   * Builds an event description of this act from a locutor and from an uninstantiated composite dialogue game proposition.
   * @see [[fr.dubuissonduplessis.dogma.game.language.instantiable]]
   */
  def apply(
    interlocutor: Interlocutor,
    content: VariableCombination): EventDescription =
    AbstractGamePropositionEventDescription(
      interlocutor, refIn.name, content, (locutor: Interlocutor, name: String, content: AbstractGameProposition) => refIn.this(locutor, content))

}

// prop.out
/**
 * Contextualisation dialogue act of proposition to exit a dialogue game.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
sealed abstract class propOut[T] extends StandardDialogueAct[GameProposition[T]] {
  val name = propOut.name
}

/**
 * Factories and extractors for [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.propOut propOut]].
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
object propOut {
  val name = "prop.out"

  /**
   * Builds this dialogue act from a given locutor and a given semantic content.
   *
   */
  def apply[T](
    locutor: Interlocutor,
    content: GameProposition[T]): propOut[T] =
    propOutImpl(locutor, content)

  /**
   * Extracts the locutor and the semantic content of this dialogue act from an event, if possible.
   *
   */
  def unapply(evt: Event): Option[(Interlocutor, AnyGameProposition)] = {
    evt match {
      case act: propOut[_] =>
        Some((act.locutor, act.content))
      case _ =>
        None
    }
  }
  /**
   * Builds an event description of this act from a locutor and from an under-specified, unconstrained semantic content.
   * @see [[fr.dubuissonduplessis.dogma.description.dialogueAct.DialogueActEventDescription]]
   */
  def apply(
    interlocutor: Interlocutor,
    variable: Variable[AnyGameProposition]): EventDescription =
    new DialogueActEventDescription[AnyGameProposition, EventDescription] {
      def name: String = propOut.name
      def locutor: Interlocutor = interlocutor
      def content: Variable[AnyGameProposition] = variable

      protected def variableBindingFrom(evt: Event): Option[(Variable[AnyGameProposition], AnyGameProposition)] =
        evt match {
          case propOut(locutor, cont) =>
            Some((content, cont))
        }

      protected def buildFrom(content: AnyGameProposition): EventDescription =
        propOut(locutor, content)
    }

  private case class propOutImpl[T](
    locutor: Interlocutor,
    content: GameProposition[T])
    extends propOut[T]
}

// acc.out
/**
 * Contextualisation dialogue act of acceptance to exit a dialogue game.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
sealed abstract class accOut[T] extends StandardDialogueAct[GameProposition[T]] {
  val name = accOut.name
}

/**
 * Factories and extractors for [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.accOut accOut]].
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
object accOut {
  val name = "acc.out"

  /**
   * Builds this dialogue act from a given locutor and a given semantic content.
   *
   */
  def apply[T](
    locutor: Interlocutor,
    content: GameProposition[T]): accOut[T] =
    accOutImpl(locutor, content)

  /**
   * Extracts the locutor and the semantic content of this dialogue act from an event, if possible.
   *
   */
  def unapply(evt: Event): Option[(Interlocutor, AnyGameProposition)] = {
    evt match {
      case act: accOut[_] =>
        Some((act.locutor, act.content))
      case _ =>
        None
    }
  }
  /**
   * Builds an event description of this act from a locutor and from an under-specified, unconstrained semantic content.
   * @see [[fr.dubuissonduplessis.dogma.description.dialogueAct.DialogueActEventDescription]]
   */
  def apply(
    interlocutor: Interlocutor,
    variable: Variable[AnyGameProposition]): EventDescription =
    new DialogueActEventDescription[AnyGameProposition, EventDescription] {
      def name: String = accOut.name
      def locutor: Interlocutor = interlocutor
      def content: Variable[AnyGameProposition] = variable

      protected def variableBindingFrom(evt: Event): Option[(Variable[AnyGameProposition], AnyGameProposition)] =
        evt match {
          case accOut(locutor, cont) =>
            Some((content, cont))
        }

      protected def buildFrom(content: AnyGameProposition): EventDescription =
        accOut(locutor, content)
    }

  private case class accOutImpl[T](
    locutor: Interlocutor,
    content: GameProposition[T])
    extends accOut[T]
}

// ref.out
/**
 * Contextualisation dialogue act of refusal to exit a dialogue game.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
sealed abstract class refOut[T] extends StandardDialogueAct[GameProposition[T]] {
  val name = refOut.name
}

/**
 * Factories and extractors for [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.refOut refOut]].
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
object refOut {
  val name = "ref.out"

  /**
   * Builds this dialogue act from a given locutor and a given semantic content.
   *
   */
  def apply[T](
    locutor: Interlocutor,
    content: GameProposition[T]): refOut[T] =
    refOutImpl(locutor, content)

  /**
   * Extracts the locutor and the semantic content of this dialogue act from an event, if possible.
   *
   */
  def unapply(evt: Event): Option[(Interlocutor, AnyGameProposition)] = {
    evt match {
      case act: refOut[_] =>
        Some((act.locutor, act.content))
      case _ =>
        None
    }
  }
  /**
   * Builds an event description of this act from a locutor and from an under-specified, unconstrained semantic content.
   * @see [[fr.dubuissonduplessis.dogma.description.dialogueAct.DialogueActEventDescription]]
   */
  def apply(
    interlocutor: Interlocutor,
    variable: Variable[AnyGameProposition]): EventDescription =
    new DialogueActEventDescription[AnyGameProposition, EventDescription] {
      def name: String = refOut.name
      def locutor: Interlocutor = interlocutor
      def content: Variable[AnyGameProposition] = variable

      protected def variableBindingFrom(evt: Event): Option[(Variable[AnyGameProposition], AnyGameProposition)] =
        evt match {
          case refOut(locutor, cont) =>
            Some((content, cont))
        }

      protected def buildFrom(content: AnyGameProposition): EventDescription =
        refOut(locutor, content)
    }

  private case class refOutImpl[T](
    locutor: Interlocutor,
    content: GameProposition[T])
    extends refOut[T]
}

// continue
/**
 * Contextualisation dialogue act of continuation of playing a dialogue game.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
sealed abstract class continue[T] extends StandardDialogueAct[GameProposition[T]] {
  val name = continue.name
}

/**
 * Factories and extractors for [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.continue continue]].
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
object continue {
  val name = "continue"

  /**
   * Builds this dialogue act from a given locutor and a given semantic content.
   *
   */
  def apply[T](
    locutor: Interlocutor,
    content: GameProposition[T]): continue[T] =
    continueImpl(locutor, content)

  /**
   * Extracts the locutor and the semantic content of this dialogue act from an event, if possible.
   *
   */
  def unapply(evt: Event): Option[(Interlocutor, AnyGameProposition)] = {
    evt match {
      case act: continue[_] =>
        Some((act.locutor, act.content))
      case _ =>
        None
    }
  }
  /**
   * Builds an event description of this act from a locutor and from an under-specified, unconstrained semantic content.
   * @see [[fr.dubuissonduplessis.dogma.description.dialogueAct.DialogueActEventDescription]]
   */
  def apply(
    interlocutor: Interlocutor,
    variable: Variable[AnyGameProposition]): EventDescription =
    new DialogueActEventDescription[AnyGameProposition, EventDescription] {
      def name: String = continue.name
      def locutor: Interlocutor = interlocutor
      def content: Variable[AnyGameProposition] = variable

      protected def variableBindingFrom(evt: Event): Option[(Variable[AnyGameProposition], AnyGameProposition)] =
        evt match {
          case continue(locutor, cont) =>
            Some((content, cont))
        }

      protected def buildFrom(content: AnyGameProposition): EventDescription =
        continue(locutor, content)
    }

  private case class continueImpl[T](
    locutor: Interlocutor,
    content: GameProposition[T])
    extends continue[T]
}
