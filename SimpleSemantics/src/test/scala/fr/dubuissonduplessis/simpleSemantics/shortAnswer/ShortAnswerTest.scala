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
package fr.dubuissonduplessis.simpleSemantics.shortAnswer

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import fr.dubuissonduplessis.predicateCalculus.Variable
import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition1
import fr.dubuissonduplessis.predicateCalculus.Individual
import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition0
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet
import fr.dubuissonduplessis.predicateCalculus.substitution.Substitution

@RunWith(classOf[JUnitRunner])
class ShortAnswerSuite extends FunSuite {
  trait TestShortAnswer {
    val yes = Yes
    val no = No
    val ans = ShortAns("test")
  }

  test("ShortAnswer: name") {
    new TestShortAnswer {
      assert(yes.name === "yes")
      assert(no.name === "no")
      assert(ans.name === "test")
    }
  }

  test("ShortAnswer: equality") {
    new TestShortAnswer {
      assert(yes predEquals yes)
      assert(!(yes predEquals no))
      assert(no predEquals no)
      assert(!(no predEquals yes))
    }
  }

  test("ShortAnswer: neg") {
    new TestShortAnswer {
      // Yes and no special case
      assert(yes.neg === no)
      assert(no.neg === yes)
      assert(yes.neg predEquals no)
      assert(no.neg predEquals yes)

      assert(!(ans.neg predEquals ans))
      assert(ans.neg.neg predEquals ans)
    }
  }
}
