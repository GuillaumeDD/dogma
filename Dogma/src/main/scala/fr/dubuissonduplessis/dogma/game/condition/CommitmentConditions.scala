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
package fr.dubuissonduplessis.dogma.game.condition

import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStore
import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.commitment.SemanticTypes
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreQueries

/**
 * Module providing primitives to easily define game conditions related to commitments.
 *
 * It provides various way to deal with the existence of commitments in the commitment store:
 *  - for action commitment, see [[fr.dubuissonduplessis.dogma.game.condition.CommitmentConditions#ExistActionCommitment]]
 *  - for propositional commitment, see [[fr.dubuissonduplessis.dogma.game.condition.CommitmentConditions#ExistPropositionalCommitment]]
 *  - for commitment in general, see [[fr.dubuissonduplessis.dogma.game.condition.CommitmentConditions#ExistCommitment]] and
 *    [[fr.dubuissonduplessis.dogma.game.condition.CommitmentConditions#support]]
 *
 * It provides predefined game conditions to deal with inactive extra-dialogical commitment:
 *  - for action commitment, see [[fr.dubuissonduplessis.dogma.game.condition.CommitmentConditions#InactiveActionCommitment]]
 *  - for propositional commitment, see [[fr.dubuissonduplessis.dogma.game.condition.CommitmentConditions#InactivePropositionalCommitment]]
 *
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait CommitmentConditions extends GameConditions {
  self: GameConditionsRequirements with CommitmentStoreQueries with Commitments with SemanticTypes with Dialogue =>

  /**
   * Game condition that checks if it exists an extra-dialogical action commitment which
   * fulfills a predicate.
   *
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected trait ExistActionCommitment extends GameCondition {
    /**
     * Predicate about the extra-dialogical action commitment
     *
     */
    val predicate: ActionCommitment => Boolean

    /**
     * Computes the condition.
     * @return true if there exists an extra-dialogical action commitment that verifies the predicate, else false
     */
    def apply(): Boolean =
      existsActionCommitmentsWhere(predicate)
  }

  /**
   * Factories for [[fr.dubuissonduplessis.dogma.game.condition.CommitmentConditions#ExistActionCommitment]].
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected object ExistActionCommitment {
    /**
     * Creates a game condition that checks if it exists an action commitment that is equal to the one
     * given in parameter.
     */
    def apply(c: ActionCommitment): ExistActionCommitment =
      ExistActionCommitment(_ == c)

    /**
     * Creates a game condition that checks if it exists an action commitment with a given debtor and
     * a given creditor which content verifies a predicate.
     * @param debtor debtor of the action commitment
     * @param creditor creditor of the action commitment
     * @param predicate predicate about the content of the action commitment
     * @return true if there exists an action commitment with the given debtor and creditor and which content
     * checks the predicate
     */
    def apply(debtor: Interlocutor,
      creditor: Interlocutor,
      predicate: ActionContent => Boolean): ExistActionCommitment =
      ExistActionCommitment(c =>
        c.debtor == debtor &&
          c.creditor == creditor &&
          predicate(c.content))

    /**
     * Creates a game condition that checks if it exists an extra-dialogical action commitment which
     * fulfills a given predicate.
     * @return true if there exists an extra-dialogical action commitment that verifies the predicate, else false
     */
    def apply(predicate: ActionCommitment => Boolean): ExistActionCommitment =
      ExistActionCommitmentImpl(predicate)

    private case class ExistActionCommitmentImpl(
      predicate: ActionCommitment => Boolean) extends ExistActionCommitment
  }

  // ExistCommitment
  /**
   * Game condition that checks if it exists a commitment which fulfills a predicate.
   *
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected trait ExistCommitment extends GameCondition {
    /**
     * Predicate about the commitment
     *
     */
    val predicate: AnyCommitment => Boolean
    /**
     * Computes the condition.
     * @return true if there exists a commitment that verifies the predicate, else false
     */
    def apply(): Boolean =
      existsCommitmentsWhere(predicate)
  }

  /**
   * Factories for [[fr.dubuissonduplessis.dogma.game.condition.CommitmentConditions#ExistCommitment]].
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected object ExistCommitment {
    /**
     * Creates a game condition that checks if it exists a commitment that is equal to the one
     * given in parameter.
     *
     */
    def apply(c: AnyCommitment): ExistCommitment =
      ExistCommitment(_ == c)

    // TODO Scaladoc: link to companion object
    /**
     * Creates a game condition that checks if it exists a commitment which fulfills a given predicate.
     *
     * More specialized (and probably more efficient implementations) can be found for:
     *  - action commitment in [[fr.dubuissonduplessis.dogma.game.condition.CommitmentConditions#ExistActionCommitment]]
     *  - propositional commitment in [[fr.dubuissonduplessis.dogma.game.condition.CommitmentConditions#ExistPropositionalCommitment]]
     *
     * @return true if there exists a commitment that verifies the predicate, else false
     */
    def apply(p: AnyCommitment => Boolean): ExistCommitment =
      ExistCommitmentImpl(p)

    private case class ExistCommitmentImpl(predicate: AnyCommitment => Boolean) extends ExistCommitment
  }

  // Exist Propositional Commitment
  /**
   * Game condition that checks if it exists a propositional commitment which fulfills a predicate.
   *
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected trait ExistPropositionalCommitment extends GameCondition {
    val predicate: PropositionalCommitment => Boolean
    /**
     * Computes the condition.
     * @return true if there exists a propositional commitment that verifies the predicate, else false
     */
    def apply(): Boolean =
      existsPropositionalCommitmentsWhere(predicate)
  }

  /**
   * Factories for [[fr.dubuissonduplessis.dogma.game.condition.CommitmentConditions#ExistPropositionalCommitment]].
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected object ExistPropositionalCommitment {
    /**
     * Creates a game condition that checks if it exists a propositional commitment that is equal to the one
     * given in parameter.
     *
     */
    def apply(c: PropositionalCommitment): ExistPropositionalCommitment =
      ExistPropositionalCommitment(_ == c)

    /**
     * Creates a game condition that checks if it exists a propositional commitment with a given debtor and
     * a given creditor which content verifies a predicate.
     * @param debtor debtor of the propositional commitment
     * @param creditor creditor of the propositional commitment
     * @param predicate predicate about the content of the propositional commitment
     * @return true if there exists an propositional commitment with the given debtor and creditor and which content
     * checks the predicate
     */
    def apply(debtor: Interlocutor,
      creditor: Interlocutor,
      predicate: PropContent => Boolean): ExistPropositionalCommitment =
      ExistPropositionalCommitment(
        c => c.debtor == debtor &&
          c.creditor == creditor &&
          predicate(c.content))

    /**
     * Creates a game condition that checks if it exists a propositional commitment which
     * fulfills a given predicate.
     * @return true if there exists a propositional commitment that verifies the predicate, else false
     */
    def apply(p: PropositionalCommitment => Boolean): ExistPropositionalCommitment =
      ExistPropositionalCommitmentImpl(p)

    private case class ExistPropositionalCommitmentImpl(
      predicate: PropositionalCommitment => Boolean) extends ExistPropositionalCommitment
  }

  // Inactive Action Commitment
  /**
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @constructor Creates a game condition that checks that an extra-dialogical action commitment with
   * a given debtor, creditor and content is inactive.
   */
  protected case class InactiveActionCommitment(
    debtor: Interlocutor,
    creditor: Interlocutor,
    content: ActionContent) extends GameCondition {

    def apply(): Boolean =
      states.forall(
        s => !ExistActionCommitment(C(debtor, creditor, content, s))())
  }

  // Inactive Propositional Commitment
  /**
   * Factories for creating game condition related to inactive propositional commitments.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected object InactivePropositionalCommitment {
    /**
     * Creates a game condition that checks that a propositional commitment with a given debtor, creditor and content
     * is inactive.
     * @return true if the propositional commitment with a given debtor, creditor and content is inactive, else false
     */
    def apply(debtor: Interlocutor,
      creditor: Interlocutor,
      content: PropContent): GameCondition =
      InactivePropositionalCommitmentImpl(
        debtor,
        creditor,
        content)

    /**
     * Creates a game condition that checks that every propositional commitments with a given debtor, a given creditor and
     * which content verifies a predicate should be inactive.
     * @return true if every propositional commitments with a given debtor, a given creditor and
     * which content verifies a predicate is inactive, else false
     */
    def apply(debtor: Interlocutor,
      creditor: Interlocutor,
      predicate: PropContent => Boolean): GameCondition =
      // We check that it does not exist a propositional commitment which content
      // satisfies predicate f
      ExistPropositionalCommitment(debtor, creditor, predicate).neg

    private case class InactivePropositionalCommitmentImpl(
      debtor: Interlocutor,
      creditor: Interlocutor,
      content: PropContent) extends GameCondition {

      def apply(): Boolean =
        states.forall(
          s => !ExistPropositionalCommitment(C(debtor, creditor, content, s))())
    }
  }

  /**
   * Builds a game condition that checks if the commitment store supports the given commitment.
   * @return true if the commitment store supports the given commitment, else false
   */
  protected def support(c: AnyCommitment): GameCondition =
    ExistCommitment(comm => comm == c)
}
