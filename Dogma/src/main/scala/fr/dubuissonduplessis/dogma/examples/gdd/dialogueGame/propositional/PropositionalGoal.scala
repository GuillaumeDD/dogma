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

import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.instantiable.Variable
import fr.dubuissonduplessis.dogma.commitment.SemanticTypes
import fr.dubuissonduplessis.dogma.description.dialogueAct.SemanticConstraint
import fr.dubuissonduplessis.dogma.instantiable.impl.Instantiated
import fr.dubuissonduplessis.dogma.instantiable.impl.InstantiableProxy

/**
 * Module that defines the goal of a proposition-based dialogue game as well as
 * some utility methods.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait PropositionalGoal {
  this: Commitments with SemanticTypes =>

  /**
   * Helper method that should try to cast a given object into
   * a proposition.
   * It should throw an exception if the cast is impossible.
   *
   */    
  protected def castPropositionalContent(o: Any): PropContent

  /**
   * Computes the negation of a proposition.
   */
  def propNeg(p: PropContent): PropContent

  /**
   * Predicate that determines if a proposition is a correction of another one.
   * @return true if the second proposition is a correction of the first one, else false
   */
  def correction(p1: PropContent, p2: PropContent): Boolean

  case class CorrectionConstraint(
    variable1: Variable[PropContent],
    variable2: Variable[PropContent]) extends SemanticConstraint[(PropContent, PropContent)]
    with InstantiableProxy[SemiInstantiatedCorrectionConstraint, PropContent] {

    val name = "correction"

    def apply(props: (PropContent, PropContent)): Boolean =
      correction(props._1, props._2)

    // Instantiation stuff
    protected def instantiable: Variable[PropContent] =
      variable1

    protected def update(content: PropContent): SemiInstantiatedCorrectionConstraint =
      SemiInstantiatedCorrectionConstraint(
        content,
        variable2)

    override def toString: String =
      s"$name($variable1, $variable2)"
  }

  case class SemiInstantiatedCorrectionConstraint(
    content1: PropContent,
    variable2: Variable[PropContent]) extends SemanticConstraint[(PropContent, PropContent)]
    with Instantiated[SemiInstantiatedCorrectionConstraint] {

    val name = "correction"

    def apply(props: (PropContent, PropContent)): Boolean =
      correction(props._1, props._2)

    override def toString: String =
      s"$name($content1, $variable2)"
  }

  // Propositional variables
  lazy val P = Variable[PropContent]("P")
  lazy val P1 = Variable[PropContent]("P1")
  lazy val P2 = Variable[PropContent]("P2")

  // Negation of the propositional variables
  lazy val negP = Variable.withTransformation[PropContent]("P")(propNeg)
  lazy val negP1 = Variable.withTransformation[PropContent]("P1")(propNeg)
  lazy val negP2 = Variable.withTransformation[PropContent]("P2")(propNeg)
}
