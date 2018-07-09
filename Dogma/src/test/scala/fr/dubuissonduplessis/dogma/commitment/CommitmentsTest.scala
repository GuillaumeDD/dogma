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

import org.scalatest.FunSuite
import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.gameInstance.GameInstances
import fr.dubuissonduplessis.dogma.dialogue.impl.TwoInterlocutors
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.dialogue.X
import fr.dubuissonduplessis.dogma.dialogue.Y
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Inform
import fr.dubuissonduplessis.dogma.description.EventToEventDescription
import fr.dubuissonduplessis.dogma.description.EventToEventDescription

class CommitmentsTest extends FunSuite {
  trait SampleCommitments extends Commitments
    with Dialogue
    with SemanticTypes
    with GameInstances
    with TwoInterlocutors {

    // Dialogue Settings
    protected def interlocutor01: Interlocutor = X
    protected def interlocutor02: Interlocutor = Y

    protected type ActionContent = Int
    protected type PropContent = String

    protected def gameInstanceOrdering = ???

    // Sample Commitments
    val gInstance = new GameInstance {
      def id: GameID =
        GameID("test")
      def isDialogueGame: Boolean =
        false
    }

    val solutionTime = currentTime
    val actionCommitment = C(X, Y, 42, CommitmentState.Ina)
    val gameActionCommitment = C(gInstance, X, Y, Inform(X, "Hello!"))
    val propCommitment = C(Y, X, "dogma")

    val inactiveActionCommitment = C(X, Y, 42, CommitmentState.Ina)
    val inactiveGameActionCommitment = C(gInstance, X, Y, Inform(X, "Hello!"), CommitmentState.Ina)
    val inactivePropCommitment = C(Y, X, "dogma", CommitmentState.Ina)

    val failedActionCommitment = C(X, Y, 42, CommitmentState.Fal)
    val failedGameActionCommitment = C(gInstance, X, Y, Inform(X, "Hello!"), CommitmentState.Fal)
    val failedPropCommitment = C(Y, X, "dogma", CommitmentState.Fal)

    val violatedActionCommitment = C(X, Y, 42, CommitmentState.Vio)
    val violatedGameActionCommitment = C(gInstance, X, Y, Inform(X, "Hello!"), CommitmentState.Vio)
    val violatedPropCommitment = C(Y, X, "dogma", CommitmentState.Vio)

    val fulfilledActionCommitment = C(X, Y, 42, CommitmentState.Ful)
    val fulfilledGameActionCommitment = C(gInstance, X, Y, Inform(X, "Hello!"), CommitmentState.Ful)
    val fulfilledPropCommitment = C(Y, X, "dogma", CommitmentState.Ful)

    val cancelledActionCommitment = C(X, Y, 42, CommitmentState.Cnl)
    val cancelledGameActionCommitment = C(gInstance, X, Y, Inform(X, "Hello!"), CommitmentState.Cnl)
    val cancelledPropCommitment = C(Y, X, "dogma", CommitmentState.Cnl)
  }

  // State accessor
  test("Commitments: isActive") {
    new SampleCommitments {
      assert(!actionCommitment.isActive)
      assert(gameActionCommitment.isActive)
      assert(propCommitment.isActive)
    }
  }

  // Transformer
  test("Commitments: updateTime") {
    new SampleCommitments {
      val newTime = solutionTime + 42

      val newCommitment01 = actionCommitment.updateTime(newTime)
      assert(newCommitment01.t == newTime)

      val newCommitment02 = gameActionCommitment.updateTime(newTime)
      assert(newCommitment02.t == newTime)

      val newCommitment03 = propCommitment.updateTime(newTime)
      assert(newCommitment03.t == newTime)
    }
  }

  test("Commitments: inactive") {
    new SampleCommitments {
      assert(actionCommitment.inactive == inactiveActionCommitment)
      assert(gameActionCommitment.inactive == inactiveGameActionCommitment)
      assert(propCommitment.inactive == inactivePropCommitment)
    }
  }

  test("Commitments: failed") {
    new SampleCommitments {
      assert(actionCommitment.failed == failedActionCommitment)
      assert(gameActionCommitment.failed == failedGameActionCommitment)
      assert(propCommitment.failed == failedPropCommitment)
    }
  }

  test("Commitments: violated") {
    new SampleCommitments {
      assert(actionCommitment.violated == violatedActionCommitment)
      assert(gameActionCommitment.violated == violatedGameActionCommitment)
      assert(propCommitment.violated == violatedPropCommitment)
    }
  }

  test("Commitments: fulfilled") {
    new SampleCommitments {
      assert(actionCommitment.fulfilled == fulfilledActionCommitment)
      assert(gameActionCommitment.fulfilled == fulfilledGameActionCommitment)
      assert(propCommitment.fulfilled == fulfilledPropCommitment)
    }
  }

  test("Commitments: cancelled") {
    new SampleCommitments {
      assert(actionCommitment.cancelled == cancelledActionCommitment)
      assert(gameActionCommitment.cancelled == cancelledGameActionCommitment)
      assert(propCommitment.cancelled == cancelledPropCommitment)
    }
  }

  // Other
  test("Commitments: ordering") {
    new SampleCommitments {
      val newTime = solutionTime + 42
      val newCommitment = actionCommitment.updateTime(newTime)

      assert(CommitmentOrdering.gt(newCommitment, actionCommitment))
      assert(CommitmentOrdering.equiv(actionCommitment, actionCommitment))
    }
  }

  // Factories
  test("Commitments: C(x,y,a)") {
    new SampleCommitments {
      val comm = C(X, Y, 24)
      assert(comm.t == currentTime)
      assert(comm.debtor == X)
      assert(comm.creditor == Y)
      assert(comm.content == 24)
      assert(comm.state == CommitmentState.default)
    }
  }

  test("Commitments: C(x,y,a,s)") {
    new SampleCommitments {
      val comm = C(X, Y, 24, CommitmentState.Ful)
      assert(comm.t == currentTime)
      assert(comm.debtor == X)
      assert(comm.creditor == Y)
      assert(comm.content == 24)
      assert(comm.state == CommitmentState.Ful)
    }
  }

  test("Commitments: C(x,y,Va)")(pending)
  test("Commitments: C(x,y,Va,s)")(pending)

  test("Commitments: C(x,y,p)") {
    new SampleCommitments {
      val comm = C(X, Y, "dogma")
      assert(comm.t == currentTime)
      assert(comm.debtor == X)
      assert(comm.creditor == Y)
      assert(comm.content == "dogma")
      assert(comm.state == CommitmentState.default)
    }
  }

  test("Commitments: C(x,y,p,s)") {
    new SampleCommitments {
      val comm = C(X, Y, "dogma", CommitmentState.Ina)
      assert(comm.t == currentTime)
      assert(comm.debtor == X)
      assert(comm.creditor == Y)
      assert(comm.content == "dogma")
      assert(comm.state == CommitmentState.Ina)
    }
  }
  test("Commitments: C(x,y,Vp)")(pending)
  test("Commitments: C(x,y,Vp,s)")(pending)

  test("Commitments: C(g,x,y,a)") {
    new SampleCommitments {
      val comm = C(gInstance, X, Y, Inform(X, "Hello!"))
      assert(comm.game == gInstance)
      assert(comm.t == currentTime)
      assert(comm.debtor == X)
      assert(comm.creditor == Y)
      assert(comm.content == EventToEventDescription(Inform(X, "Hello!")))
      assert(comm.state == CommitmentState.default)
    }
  }
  test("Commitments: C(g,x,y,a,s)") {
    new SampleCommitments {
      val comm = C(gInstance, X, Y, Inform(X, "Hello!"), CommitmentState.Vio)
      assert(comm.game == gInstance)
      assert(comm.t == currentTime)
      assert(comm.debtor == X)
      assert(comm.creditor == Y)
      assert(comm.content == EventToEventDescription(Inform(X, "Hello!")))
      assert(comm.state == CommitmentState.Vio)
    }
  }
}
