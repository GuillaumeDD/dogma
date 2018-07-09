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
package fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.propositional

import fr.dubuissonduplessis.dogma.commitment.SemanticTypes
import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.description.dialogueAct.SemanticConstraint
import fr.dubuissonduplessis.dogma.instantiable.Variable
import fr.dubuissonduplessis.dogma.instantiable.impl.Instantiated

/**
 * Module that defines the goal of a question-based dialogue game as well as
 * some utility methods.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait QuestionGoal {
  this: Commitments with SemanticTypes with PropositionalGoal =>

  /**
   * Represents the type of an abstract question.
   */
  type QuestionType

  /**
   * Helper method that should try to cast a given object into
   * an abstract question.
   * It should throw an exception if the cast is impossible.
   *
   */
  protected def castQuestionGoal(o: Any): QuestionType

  /**
   * Computes the proposition indicating that an answer to a given question
   * cannot be found.
   */
  def fail(question: QuestionType): PropContent
  /**
   * Resolves relation that determines if a proposition resolves a question.
   *
   */
  def resolves: (PropContent, QuestionType) => Boolean
  /**
   * Relevant relation that determines if a proposition is relevant to a question.
   */
  def relevant: (PropContent, QuestionType) => Boolean

  /**
   * Strictly relevant relation that determines if a propostion is relevant to a
   * question but does not resolve it.
   */
  def strictlyRelevant: (PropContent, QuestionType) => Boolean =
    (p: PropContent, q: QuestionType) => relevant(p, q) && !resolves(p, q)

  /**
   * Abstract semantic constraint involving a question.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected abstract class SemanticQuestionConstraint extends SemanticConstraint[PropContent]
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

  protected case class StrictlyRelevantConstraint(
    question: QuestionType,
    variable: Variable[PropContent]) extends SemanticQuestionConstraint {
    val name: String = "strictlyRelevant"
    def apply(prop: PropContent): Boolean =
      strictlyRelevant(prop, question)
  }

  protected case class RelevantConstraint(
    question: QuestionType,
    variable: Variable[PropContent]) extends SemanticQuestionConstraint {
    val name: String = "relevant"
    def apply(prop: PropContent): Boolean =
      relevant(prop, question)
  }

  protected case class ResolvesConstraint(
    question: QuestionType,
    variable: Variable[PropContent]) extends SemanticQuestionConstraint {
    val name: String = "resolves"
    def apply(prop: PropContent): Boolean =
      resolves(prop, question)
  }

  /**
   * Abstract trait that provides helper methods to access question relations
   * (resolves, relevant, strictly relevant).
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  trait QuestionConstraint {
    def goal: QuestionType

    def strictlyRelevantConstraint(v: Variable[PropContent]): SemanticConstraint[PropContent] =
      StrictlyRelevantConstraint(goal, v)

    def relevantConstraint(v: Variable[PropContent]): SemanticConstraint[PropContent] =
      RelevantConstraint(goal, v)

    def resolvesConstraint(v: Variable[PropContent]): SemanticConstraint[PropContent] =
      ResolvesConstraint(goal, v)
  }
}
