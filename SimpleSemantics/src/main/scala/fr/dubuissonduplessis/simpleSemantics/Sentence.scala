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
package fr.dubuissonduplessis.simpleSemantics

import fr.dubuissonduplessis.predicateCalculus._
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet

/**
 * Represents a semantic unit in the semantic representation. This semantic unit may be a:
 *  - [[fr.dubuissonduplessis.simpleSemantics.question.Question]] (see sub-package [[fr.dubuissonduplessis.simpleSemantics.question]])
 *  - [[fr.dubuissonduplessis.simpleSemantics.proposition.Proposition]] (see sub-package [[fr.dubuissonduplessis.simpleSemantics.proposition]])
 *  - [[fr.dubuissonduplessis.simpleSemantics.shortAnswer.ShortAns]] (see sub-package [[fr.dubuissonduplessis.simpleSemantics.shortAnswer]])
 *  - [[fr.dubuissonduplessis.simpleSemantics.action.Action]] (see sub-package [[fr.dubuissonduplessis.simpleSemantics.action]])
 */
abstract class Sentence
  extends Unifiable[Sentence]

