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
package fr.lifl.smac.scadia.dialoguePattern

import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.instantiable.Variable
import fr.dubuissonduplessis.dogma.commitment.SemanticTypes
import fr.dubuissonduplessis.dogma.description.dialogueAct.SemanticConstraint
import fr.dubuissonduplessis.dogma.instantiable.impl.Instantiated

trait PropositionalGoal {
  this: Commitments with SemanticTypes =>

  /**
   * Type of the question
   */
  protected type QuestionType

  /**
   * Generates the proposition corresponding to a failure to answer 'question'
   * @param question Question that has not been answered
   */
  protected def fail(question: QuestionType): PropContent

  /**
   * Resolves relation that determines whether or not an answer is resolving regarding the question
   * @return True is the proposition resolves the question, else false
   */
  protected def resolves: (PropContent, QuestionType) => Boolean

  protected def castQuestionGoal(o: Any): QuestionType

  // Variable used for the dialogue game definition
  protected lazy val P = Variable[PropContent]("P")

  /**
   * Semantic constraint involving a question.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  abstract class SemanticQuestionConstraint extends SemanticConstraint[PropContent]
    with Instantiated[SemanticQuestionConstraint] {
    /**
     * Question involved in the semantic constraint.
     *
     */
    def question: QuestionType
    /**
     * Variable representing the propositional semantic content that should be checked.
     */
    def variable: Variable[PropContent]
    override def toString: String =
      s"$name($variable, $question)"
  }

  /**
   * Factories for SemanticQuestionConstraint.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  object SemanticQuestionConstraint {
    /**
     * Builds a SemanticQuestionConstraint from a name, a question, a variable and the
     * function defining the constraint.
     * @param constraintName name of the constraint
     * @param q question involved in the constraint
     * @param v variable representing the propositional semantic content that should be checked
     * @param constraint function representing the body of the constraint
     *
     */
    def apply(
      constraintName: String,
      q: QuestionType,
      v: Variable[PropContent],
      constraint: (PropContent) => Boolean): SemanticQuestionConstraint =
      new SemanticQuestionConstraint {
        val name: String = constraintName
        val question: QuestionType = q
        val variable: Variable[PropContent] = v
        def apply(p: PropContent): Boolean =
          constraint(p)
      }
  }

  /**
   * Base trait for a question dialogue pattern. It provides the resolves constraint.
   *
   */
  protected trait QuestionConstraint {
    def goal: QuestionType
    def resolves: (PropContent, QuestionType) => Boolean = PropositionalGoal.this.resolves
    def resolvesConstraint(v: Variable[PropContent]): SemanticConstraint[PropContent] =
      SemanticQuestionConstraint("resolves", goal, v, (p: PropContent) => resolves(p, goal))
  }
}
