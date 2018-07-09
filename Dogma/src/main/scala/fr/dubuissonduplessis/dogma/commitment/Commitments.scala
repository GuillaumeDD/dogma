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
package fr.dubuissonduplessis.dogma.commitment

import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.instantiable.Generator
import fr.dubuissonduplessis.dogma.triggerable.Triggerable
import com.typesafe.scalalogging.slf4j.LazyLogging
import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.description.Description
import fr.dubuissonduplessis.dogma.gameInstance.GameInstances
import fr.dubuissonduplessis.dogma.triggerable.impl.Untriggered
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.instantiable.impl.Instantiated
import fr.dubuissonduplessis.dogma.instantiable.impl.NonGenerator
import fr.dubuissonduplessis.dogma.instantiable.impl.InstantiatedGenerator
import fr.dubuissonduplessis.dogma.instantiable.impl.InstantiableProxy
import fr.dubuissonduplessis.dogma.instantiable.impl.GeneratorProxy
import fr.dubuissonduplessis.dogma.instantiable.Variable
import fr.dubuissonduplessis.dogma.instantiable.InstantiationSet
import fr.dubuissonduplessis.dogma.instantiable.InstantiationVariable
import fr.dubuissonduplessis.dogma.description.EventDescription
import fr.dubuissonduplessis.dogma.operation.Operation

/**
 * Module providing the definitions and implementations of propositional commitment and (game) action
 * commitment. It includes an implicit ordering on commitment. It depends on the module
 * [[fr.dubuissonduplessis.dogma.commitment.SemanticTypes]] which defines semantic types.
 *
 *
 * Here follows an example of a commitments module:
 * {{{
 * // Definition of a commitments module
 * new Commitments with SemanticTypes with TwoInterlocutors with GameInstances {
 *
 *    // Dialogue situation
 *    protected def interlocutor01: Interlocutor =
 *       Interlocutor("Alice")
 *
 *    protected def interlocutor02: Interlocutor =
 *       Interlocutor("Bob")
 *
 *    // See fr.dubuissonduplessis.dogma.gameInstance.GamesInstances
 *    protected def gameInstanceOrdering: Ordering[GameInstance] =
 *       ???
 *
 *    // Semantic types configuration
 *    protected type ActionContent = Int
 *    protected type PropContent = String
 *
 *    // Creation of several commitments
 *    // Propositional commitment from Alice to Bob about the capital city of France
 *    val propCommitment = C(interlocutor01, interlocutor02, "Paris is the French capital city")
 *
 *    assert(propCommitment.debtor == Interlocutor("Alice"))
 *    assert(propCommitment.creditor == Interlocutor("Bob"))
 *    assert(propCommitment.content == "Paris is the French capital city")
 *    assert(propCommitment.state == CommitmentState.default)
 *
 *    // Action commitment from Bob to Alice to execute action 42
 *    val actionCommitment = C(interlocutor02, interlocutor01, 42)
 *    assert(actionCommitment.debtor == Interlocutor("Bob"))
 *    assert(actionCommitment.creditor == Interlocutor("Alice"))
 *    assert(actionCommitment.content == 42)
 *    assert(actionCommitment.state == CommitmentState.default)
 * }
 * }}}
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait Commitments {
  this: CommitmentsRequirements =>

  /**
   * Possible states of a propositional or action commitment.
   *
   * It includes the following states:
   *  - inactive (Ina)
   *  - cancelled (Cnl)
   *  - created (Crt)
   *  - failure (Fal)
   *  - fulfilled (Ful)
   *  - violated (Vio)
   *
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected object CommitmentState extends Enumeration {
    type CommitmentState = Value
    val Ina, Cnl, Crt, Fal, Ful, Vio = Value

    /**
     * Determines if a given state can be assigned to a propositional commitment
     * @return true if the given state can be assigned to a propositional commitment, else false
     */
    def isPropositionalState(s: CommitmentState): Boolean =
      (s == Crt) || (s == Fal) || (s == Cnl)

    /**
     * All commitment states
     */
    def states: Set[CommitmentState] =
      values

    /**
     * Determines if a given state is an active state
     * @return true if the given state is an active state, else false
     */
    def activeState(s: CommitmentState): Boolean =
      (s == Crt)

    /**
     * The default state. Set to 'Crt', which is the default state in which a commitment is
     * after its creation.
     */
    def default: CommitmentState =
      Crt
  }
  import CommitmentState._

  /**
   * Commitment states explicitly represented in the system (all states except Ina)
   */
  protected def states: Set[CommitmentState] =
    Set(CommitmentState.Crt, CommitmentState.Fal, CommitmentState.Ful, CommitmentState.Cnl, CommitmentState.Vio)

  /**
   * Abstract base class representing a commitment.
   * Commitments are represented as immutable object.
   *
   * A commitment takes the form: C(x,y,content,state,t) where:
   *  - x is the debtor of the commitment
   *  - y is the creditor of the commitment
   *  - content is the content of the commitment
   *  - state is the current state of the commitment
   *  - t represents the last time this commitment has changed its state
   *
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @tparam U type of the content of the commitment
   */
  protected sealed abstract class Commitment[U]
    extends Generator[Commitment[_], Event]
    with Triggerable
    with LazyLogging {

    /**
     * Last modification time of this commitment
     */
    def t: Time
    def debtor: Interlocutor
    def creditor: Interlocutor
    def content: U
    def state: CommitmentState

    /**
     * Update the time slot of this commitment
     * @return a new commitment with the updated time
     */
    def updateTime(newT: Time): Commitment[U]

    // State Transformation
    /**
     * Turns this commitment into an inactive one.
     * Note that it does not update its time.
     * @return a new commitment in state inactive
     */
    def inactive: Commitment[U]
    /**
     * Turns this commitment into a failed one.
     * Note that it does not update its time.
     * @return a new commitment in state failed
     */
    def failed: Commitment[U]
    /**
     * Turns this commitment into a violated one.
     * Note that it does not update its time.
     * @return a new commitment in state violated
     */
    def violated: Commitment[U]
    /**
     * Turns this commitment into a fulfilled one.
     * Note that it does not update its time.
     * @return a new commitment in state fulfilled
     */
    def fulfilled: Commitment[U]
    /**
     * Turns this commitment into a cancelled one.
     * Note that it does not update its time.
     * @return a new commitment in state cancelled
     */
    def cancelled: Commitment[U]

    /**
     * Determines if this commitment is active.
     * An active commitment is a commitment in state 'Crt'.
     * @return true if this commitment is active, else false
     */
    def isActive: Boolean =
      CommitmentState.activeState(this.state)

    // Equality
    def canEqual(other: Any): Boolean

    override def equals(other: Any): Boolean = other match {
      case that: Commitment[_] => (that canEqual this) &&
        this.debtor == that.debtor &&
        this.creditor == that.creditor &&
        this.content == that.content &&
        this.state == that.state
      case _ => false
    }
    override def hashCode: Int =
      41 * (
        41 * (
          41 + debtor.hashCode) + creditor.hashCode) + content.hashCode

    override def toString: String =
      "C(" + debtor + ", " + creditor + ", " + content + ", " + state + ", " + t + ")"
  }

  /**
   * Type representing all propositional and (game) action commitments
   */
  protected type AnyCommitment = Commitment[_]
  /**
   * Type representing all game (action) commitments
   */
  protected type AnyGameCommitment = GameCommitment[_]

  /**
   * Sorts commitments by ascending time.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected implicit object CommitmentOrdering extends Ordering[AnyCommitment] {
    def compare(c1: AnyCommitment, c2: AnyCommitment): Int =
      (c1.t - c2.t).toInt
  }

  // Action Commitment

  /**
   * An extra-dialogical action commitment.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected sealed abstract class ActionCommitment extends Commitment[ActionContent]

  /**
   * Factories for extra-dialogical action commitment.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected object ActionCommitment {
    /**
     * Creates an extra-dialogical action commitment
     * @param debtor debtor of this commitment
     * @param creditor creditor of this commitment
     * @param content action that is the content of this commitment
     * @param state state of the commitment
     * @param t time of this commitment
     * @return an extra-dialogical action commitment
     */
    protected[Commitments] def apply(
      debtor: Interlocutor,
      creditor: Interlocutor,
      content: ActionContent,
      state: CommitmentState = CommitmentState.default,
      t: Time = currentTime): ActionCommitment =
      ActionCommitmentImpl(debtor, creditor, content, state, t)

    /**
     * Builds an instantiable generator of an extra-dialogical action commitment with an unspecified
     * content.
     * @param debtor debtor of the commitment
     * @param creditor creditor of the commitment
     * @param variable variable that represents the unspecified content of this commitment
     * @param state state of the commitment
     * @param t time of the commitment
     * @return an instantiable generator of an extra-dialogical action commitment with an unspecified content
     */
    protected[Commitments] def apply(
      debtor: Interlocutor,
      creditor: Interlocutor,
      variable: Variable[ActionContent],
      state: CommitmentState,
      t: Time): ActionCommitment =
      ActionCommitmentGenerator(debtor, creditor, variable, state, t)

    // Implementation of an action commitment
    private case class ActionCommitmentImpl protected[Commitments] (
      debtor: Interlocutor,
      creditor: Interlocutor,
      content: ActionContent,
      state: CommitmentState = CommitmentState.default,
      t: Time = currentTime) extends ActionCommitment
      with Generator[ActionCommitment, Event]
      with InstantiatedGenerator[ActionCommitment, Event]
      with Untriggered {
      import CommitmentState._

      def fits(e: Event): Boolean =
        false

      protected def update(
        debtor: Interlocutor = this.debtor,
        creditor: Interlocutor = this.creditor,
        content: ActionContent = this.content,
        state: CommitmentState = this.state,
        t: Time = this.t): ActionCommitment =
        ActionCommitmentImpl(debtor, creditor, content, state, t)

      def updateTime(newT: Time): ActionCommitment =
        update(t = newT)
      // State Transformation
      def inactive: ActionCommitment =
        update(state = Ina)
      def violated: ActionCommitment =
        update(state = Vio)
      def fulfilled: ActionCommitment =
        update(state = Ful)
      def cancelled: ActionCommitment =
        update(state = Cnl)
      def failed: ActionCommitment =
        update(state = Fal)

      // Equality
      def canEqual(other: Any): Boolean = other.isInstanceOf[ActionCommitment]
    }

    private case class ActionCommitmentGenerator protected[Commitments] (
      debtor: Interlocutor,
      creditor: Interlocutor,
      variable: Variable[ActionContent],
      state: CommitmentState = CommitmentState.default,
      t: Time = currentTime) extends ActionCommitment
      with Generator[ActionCommitment, Event]
      with Untriggered {
      import CommitmentState._
      def content: ActionContent =
        throw new IllegalStateException("Uninstantiated action commitment")
      override def hashCode: Int =
        41 * (
          41 * (
            41 + debtor.hashCode) + creditor.hashCode) + variable.hashCode
      // Instantiation
      protected def instantiateWithImpl(s: InstantiationSet): ActionCommitment =
        ActionCommitment(debtor, creditor, variable.instantiateWith(s), state, t)

      def isInstantiableWith(s: InstantiationSet): Boolean =
        variable.isInstantiableWith(s)

      def variables: Set[InstantiationVariable] =
        Set(variable)

      // This commitment is not a matcher
      protected def bindingsImpl(t: Event): InstantiationSet =
        InstantiationSet()

      def fits(e: Event): Boolean =
        false

      protected def update(
        debtor: Interlocutor = this.debtor,
        creditor: Interlocutor = this.creditor,
        state: CommitmentState = this.state,
        t: Time = this.t): ActionCommitment =
        ActionCommitmentGenerator(debtor, creditor, variable, state, t)

      def updateTime(newT: Time): ActionCommitment =
        update(t = newT)
      // State Transformation
      def inactive: ActionCommitment =
        update(state = Ina)
      def violated: ActionCommitment =
        update(state = Vio)
      def fulfilled: ActionCommitment =
        update(state = Ful)
      def cancelled: ActionCommitment =
        update(state = Cnl)
      def failed: ActionCommitment =
        update(state = Fal)

      // Equality
      def canEqual(other: Any): Boolean = other.isInstanceOf[ActionCommitmentGenerator]

      override def toString: String =
        "C(" + debtor + ", " + creditor + ", " + variable + ", " + state + ")"
    }
  }

  // Propositional Commitment
  /**
   * Extra-dialogical propositional commitment.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected sealed abstract class PropositionalCommitment extends Commitment[PropContent]

  /**
   * Factories for propositional commitment.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  private object PropositionalCommitment {
    /**
     * Creates an extra-dialogical propositional commitment.
     * @param debtor debtor of this commitment
     * @param creditor creditor of this commitment
     * @param content propositional content of this commitment
     * @param state state of this commitment
     * @param t time of this commitment
     * @return an extra-dialogical propositional commitment
     */
    protected[Commitments] def apply(
      debtor: Interlocutor,
      creditor: Interlocutor,
      content: PropContent,
      state: CommitmentState = CommitmentState.default,
      t: Time = currentTime): PropositionalCommitment =
      PropositionalCommitmentImpl(debtor, creditor, content, state, t)

    /**
     * Builds an instantiable generator of an extra-dialogical propositional commitment with an unspecified
     * content.
     * @param debtor debtor of this commitment
     * @param creditor creditor of this commitment
     * @param variable variable that represents the unspecified content of this commitment
     * @param state state of this commitment
     * @param t time of this commitment
     * @return an instantiable generator of an extra-dialogical action commitment with an unspecified content
     */
    protected[Commitments] def apply(
      debtor: Interlocutor,
      creditor: Interlocutor,
      variable: Variable[PropContent],
      state: CommitmentState,
      t: Time): PropositionalCommitment =
      PropositionalCommitmentGenerator(debtor, creditor, variable, state, t)

    // Implementation of propositional commitment
    private case class PropositionalCommitmentImpl protected[Commitments] (
      debtor: Interlocutor,
      creditor: Interlocutor,
      content: PropContent,
      state: CommitmentState = CommitmentState.default,
      t: Time = currentTime) extends PropositionalCommitment
      with Instantiated[PropositionalCommitment]
      with NonGenerator[PropositionalCommitment, Event]
      with Untriggered {

      def update(
        debtor: Interlocutor = this.debtor,
        creditor: Interlocutor = this.creditor,
        content: PropContent = this.content,
        state: CommitmentState = state,
        t: Time = this.t): PropositionalCommitment =
        PropositionalCommitmentImpl(debtor, creditor, content, state, t)

      def updateTime(newT: Time): PropositionalCommitment =
        update(t = newT)
      // State Transformation
      def inactive: PropositionalCommitment =
        update(state = Ina)
      def violated: PropositionalCommitment =
        update(state = Vio)
      def fulfilled: PropositionalCommitment =
        update(state = Ful)
      def cancelled: PropositionalCommitment =
        update(state = Cnl)
      def failed: PropositionalCommitment =
        update(state = Fal)

      def canEqual(other: Any): Boolean = other.isInstanceOf[PropositionalCommitment]
    }

    private case class PropositionalCommitmentGenerator protected[Commitments] (
      debtor: Interlocutor,
      creditor: Interlocutor,
      variable: Variable[PropContent],
      state: CommitmentState = CommitmentState.default,
      t: Time = currentTime) extends PropositionalCommitment
      with NonGenerator[PropositionalCommitment, Event]
      with Untriggered {

      def content: PropContent =
        throw new IllegalStateException("Uninstantiated propositional commitment")
      override def hashCode: Int =
        41 * (
          41 * (
            41 + debtor.hashCode) + creditor.hashCode) + variable.hashCode

      // Instantiation
      protected def instantiateWithImpl(s: InstantiationSet): PropositionalCommitment =
        PropositionalCommitment(debtor, creditor, variable.instantiateWith(s), state, t)

      def isInstantiableWith(s: InstantiationSet): Boolean =
        variable.isInstantiableWith(s)

      def variables: Set[InstantiationVariable] =
        Set(variable)

      def update(
        debtor: Interlocutor = this.debtor,
        creditor: Interlocutor = this.creditor,
        variable: Variable[PropContent] = this.variable,
        state: CommitmentState = state,
        t: Time = this.t): PropositionalCommitment =
        PropositionalCommitmentGenerator(debtor, creditor, variable, state, t)

      def updateTime(newT: Time): PropositionalCommitment =
        update(t = newT)
      // State Transformation
      def inactive: PropositionalCommitment =
        update(state = Ina)
      def violated: PropositionalCommitment =
        update(state = Vio)
      def fulfilled: PropositionalCommitment =
        update(state = Ful)
      def cancelled: PropositionalCommitment =
        update(state = Cnl)
      def failed: PropositionalCommitment =
        update(state = Fal)

      def canEqual(other: Any): Boolean = other.isInstanceOf[PropositionalCommitmentGenerator]
      override def toString: String =
        "C(" + debtor + ", " + creditor + ", " + variable + ", " + state + ")"
    }
  }

  // Game Commitment
  /**
   * A commitment which is contextualized into a game.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   * @tparam U type of the content of this commitment
   */
  protected sealed trait GameCommitment[U] extends Commitment[U] {
    /**
     * Game instance forming the context of this commitment
     *
     */
    def game: GameInstance

    def updateTime(newT: Time): GameCommitment[U]
    def canEqual(other: Any): Boolean = other.isInstanceOf[GameCommitment[_]]
  }

  // Game Action Commitment
  /**
   * Dialogical game action commitment.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected case class GameActionCommitment protected[Commitments] (
    game: GameInstance,
    debtor: Interlocutor,
    creditor: Interlocutor,
    content: Description,
    state: CommitmentState = CommitmentState.default,
    t: Time = currentTime)
    extends Commitment[Description]
    with GameCommitment[Description]
    with GeneratorProxy[GameActionCommitment, Event, Description]
    with InstantiableProxy[GameActionCommitment, Description] {
    def generator: Description =
      content
    protected def instantiable: Description =
      content
    protected def update(instantiable: Description): GameActionCommitment =
      update(content = instantiable)

    def update(game: GameInstance = this.game,
      debtor: Interlocutor = this.debtor,
      creditor: Interlocutor = this.creditor,
      content: Description = this.content,
      state: CommitmentState = this.state,
      t: Time = this.t): GameActionCommitment =
      GameActionCommitment(game, debtor, creditor, content, state, t)
    override def canEqual(other: Any): Boolean =
      other.isInstanceOf[GameActionCommitment]

    override def equals(other: Any): Boolean = other match {
      case that: GameActionCommitment => (that canEqual this) &&
        this.debtor == that.debtor &&
        this.creditor == that.creditor &&
        this.content == that.content &&
        this.game == that.game &&
        this.state == that.state
      case _ => false
    }

    override def hashCode: Int =
      41 * (
        41 * (
          41 * (
            41 + game.hashCode) + debtor.hashCode) + creditor.hashCode) + content.hashCode

    override def toString: String =
      super.toString + ":" + game.id

    def updateTime(newT: Time): GameActionCommitment =
      update(t = newT)
    // State Transformation
    def inactive: GameActionCommitment =
      update(state = Ina)
    def violated: GameActionCommitment =
      update(state = Vio)
    def fulfilled: GameActionCommitment =
      update(state = Ful)
    def cancelled: GameActionCommitment =
      update(state = Cnl)
    def failed: GameActionCommitment =
      update(state = Fal)

    def persistent: Boolean =
      isActive && content.persistent

    def isViolatedBy(e: Event): Boolean =
      content.isViolatedBy(e)

    def isSatisfiedBy(a: Event): Boolean =
      isActive && content.isSatisfiedBy(a)

    def isConcernedBy(e: Event): Boolean =
      isActive && content.isConcernedBy(e)

    def expects(a: Event): Boolean =
      isActive && content.expects(a)

    def operationToExecute(e: Event): Option[Operation] =
      if (isConcernedBy(e)) {
        content.operationToExecute(e)
      } else {
        None
      }

    def expectedEvent(): List[EventDescription] =
      if (isActive) {
        content.expectedEvent()
      } else {
        List()
      }

    def expectedDescription(): Option[Description] =
      if (isActive) {
        content.expectedDescription()
      } else {
        None
      }
  }

  // Factories
  /**
   * Creates an action commitment.
   * Its state is set to the default one and the time is set to the current one.
   *
   * @param x debtor of this commitment
   * @param y creditor of this commitment
   * @param a action content of this commitment
   * @return a new action commitment with default state and its time set to the current time
   */
  protected def C(x: Interlocutor, y: Interlocutor, a: ActionContent): ActionCommitment =
    ActionCommitment(x, y, a)

  /**
   * Creates an action commitment.
   * Its time is set to the current one.
   *
   * @param x debtor of this commitment
   * @param y creditor of this commitment
   * @param a action content of this commitment
   * @param s state of this commitment
   * @return a new action commitment with its time set to the current time
   */
  protected def C(x: Interlocutor, y: Interlocutor, a: ActionContent, s: CommitmentState): ActionCommitment =
    ActionCommitment(x, y, a, s)

  /**
   * Creates an instantiable generator of an action commitment with an unspecified content.
   *
   * @param x debtor of this commitment
   * @param y creditor of this commitment
   * @param variable variable that represents the unspecified content of this commitment
   * @return an instantiable generator of an action commitment with an unspecified content
   */
  protected def C(x: Interlocutor, y: Interlocutor, variable: Variable[ActionContent]): ActionCommitment =
    ActionCommitment(x, y, variable, CommitmentState.default, currentTime)

  /**
   * Creates an instantiable generator of an action commitment with an unspecified content.
   *
   * @param x debtor of this commitment
   * @param y creditor of this commitment
   * @param variable variable that represents the unspecified content of this commitment
   * @param s state of this commitment
   * @return an instantiable generator of an action commitment with an unspecified content
   */
  protected def C(x: Interlocutor, y: Interlocutor, variable: Variable[ActionContent], s: CommitmentState): ActionCommitment =
    ActionCommitment(x, y, variable, s, currentTime)

  /**
   * Creates a propositional commitment.
   * Its time is set to the current one.
   *
   * @param x debtor of this commitment
   * @param y creditor of this commitment
   * @param prop propositional content of this commitment
   * @param s state of this commitment
   * @return a new propositional commitment with its time set to the current time
   */
  protected def C(x: Interlocutor, y: Interlocutor, prop: PropContent, s: CommitmentState): PropositionalCommitment =
    PropositionalCommitment(x, y, prop, s)

  /**
   * Creates a propositional commitment.
   * Its state is set to the default one and the time is set to the current one.
   *
   * @param x debtor of this commitment
   * @param y creditor of this commitment
   * @param prop propositional content of this commitment
   * @return a new propositional commitment with default state and its time set to the current time
   */
  protected def C(x: Interlocutor, y: Interlocutor, prop: PropContent): PropositionalCommitment =
    PropositionalCommitment(x, y, prop)

  /**
   * Creates an instantiable generator of a propositional commitment with an unspecified content.
   *
   * @param x debtor of this commitment
   * @param y creditor of this commitment
   * @param variable variable that represents the unspecified content of this commitment
   * @param s state of this commitment
   * @return an instantiable generator of a propositional commitment with an unspecified content
   */
  protected def C(x: Interlocutor, y: Interlocutor, variable: Variable[PropContent], s: CommitmentState): PropositionalCommitment =
    PropositionalCommitment(x, y, variable, s, currentTime)

  /**
   * Creates an instantiable generator of a propositional commitment with an unspecified content.
   *
   * @param x debtor of this commitment
   * @param y creditor of this commitment
   * @param variable variable that represents the unspecified content of this commitment
   * @return an instantiable generator of a propositional commitment with an unspecified content
   */
  protected def C(x: Interlocutor, y: Interlocutor, variable: Variable[PropContent]): PropositionalCommitment =
    PropositionalCommitment(x, y, variable, CommitmentState.default, currentTime)

  /**
   * Creates a game action commitment.
   * Its state is set to the default one and the time is set to the current one.
   *
   * @param g game instance which is the context of this commitment
   * @param x debtor of this commitment
   * @param y creditor of this commitment
   * @param a content of this commitment
   * @return a new game action commitment with default state and its time set to the current time
   */
  protected def C(g: GameInstance, x: Interlocutor, y: Interlocutor, a: Description): GameActionCommitment =
    GameActionCommitment(g, x, y, a)

  /**
   * Creates a game action commitment.
   * Its time is set to the current one.
   *
   * @param g game instance which is the context of this commitment
   * @param x debtor of this commitment
   * @param y creditor of this commitment
   * @param a content of this commitment
   * @param s state of this commitment
   * @return a new game action commitment with its time set to the current time
   */
  protected def C(g: GameInstance, x: Interlocutor, y: Interlocutor, a: Description, s: CommitmentState): GameActionCommitment =
    GameActionCommitment(g, x, y, a, s)

}
