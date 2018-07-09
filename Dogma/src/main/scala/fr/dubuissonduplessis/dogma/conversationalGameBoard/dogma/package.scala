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
package fr.dubuissonduplessis.dogma.conversationalGameBoard

/**
 * Provides two core modules, the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ExplicitReferee referee module]]
 * and the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ConventionalBehaviourManager conventional behaviour module]].
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
package object dogma {
  /**
   * Type representing the mixin between the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ExplicitReferee referee module]]
   * and the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ConventionalBehaviourManager conventional behaviour module]].
   */
  type ExplicitDogma = ExplicitReferee with ConventionalBehaviourManager
}
