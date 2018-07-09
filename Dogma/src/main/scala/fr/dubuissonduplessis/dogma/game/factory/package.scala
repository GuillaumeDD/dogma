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
package fr.dubuissonduplessis.dogma.game

import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.gameInstance.GameInstances

/**
 * Provides modules to deal with (communication and dialogue) game factories.
 *
 * It contains two main modules:
 *  - [[fr.dubuissonduplessis.dogma.game.factory.GameFactories]] that defines game factories
 *  - [[fr.dubuissonduplessis.dogma.game.factory.GameRepositories]] that implements a repository of factories that takes
 *  advantage of the module [[fr.dubuissonduplessis.dogma.game.factory.GameFactories]]
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
package object factory {
  /**
   * Module requirements for [[fr.dubuissonduplessis.dogma.game.factory.GameFactories]] as a type.
   */
  type GameFactoriesRequirements = Dialogue with DialogueGames with CommunicationGames with GameInstances
}
