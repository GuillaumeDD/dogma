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
/**
 * *****************************************************************************
 * Copyright (c) 2013 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> - initial API and implementation
 * ****************************************************************************
 */
package fr.dubuissonduplessis.predicateCalculus
import substitution._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SubstitutionSetSuite extends FunSuite {
  trait TestSubstitutionSet {
    val var01 = Variable("X")
    val var02 = Variable("Y")

    val ind01 = Individual("ind01")
    val pred01a = PredN("city",
      List(ind01))

    val subSet = SubstitutionSet()
    val sub01 = Substitution(var01, ind01)
    val sub01a = Substitution(var01, pred01a)
    val sub02 = Substitution(var02, pred01a)

    val sub03 = Substitution(Variable("X"), Variable("X"))
  }

  test("SubstitutionSet: + and binding") {
    new TestSubstitutionSet {
      assert((subSet).binding(var01) === None)
      assert((subSet + sub01).binding(var01) === Some(ind01))
      assert((subSet + sub01 + sub01a).binding(var01) === Some(pred01a))

      assert((subSet + sub01 + sub02).binding(var01) === Some(ind01))
      assert((subSet + sub01 + sub02).binding(var02) === Some(pred01a))

      // We do not add self substitution (identity function)
      assert((subSet + sub03).substitutions.size === 0)
    }
  }

  test("SubstitutionSet: isBound") {
    new TestSubstitutionSet {
      assert(!(subSet).isBound(var01))
      assert((subSet + sub01).isBound(var01))
      assert((subSet + sub01 + sub01a).isBound(var01))

      assert((subSet + sub01 + sub02).isBound(var01))
      assert((subSet + sub01 + sub02).isBound(var02))
    }
  }

  test("SubstitutionSet: substitutions") {
    new TestSubstitutionSet {
      assert((subSet).substitutions === Set())
      assert((subSet + sub01).substitutions ===
        Set(Substitution(var01, ind01)))
      assert((subSet + sub01 + sub01a).substitutions ===
        Set(Substitution(var01, pred01a)))

      assert((subSet + sub01 + sub02).substitutions ===
        Set(Substitution(var01, ind01), Substitution(var02, pred01a)))
    }
  }
}
