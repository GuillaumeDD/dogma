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
package fr.dubuissonduplessis.simpleSemantics.proposition

import fr.dubuissonduplessis.simpleSemantics.Sentence
import fr.dubuissonduplessis.predicateLogic.BooleanExpression
import fr.dubuissonduplessis.predicateCalculus.PredN
import fr.dubuissonduplessis.predicateCalculus.Individual
import fr.dubuissonduplessis.predicateCalculus.Unifiable
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet

/**
 * Represents a proposition.
 * Propositions are represented by '''n-ary predicates''' with arguments.
 * For example, `have_a(beer)` is a proposition which name is `have_a` and argument is the constant `beer`.
 *
 */
abstract class Proposition extends Sentence
  with BooleanExpression[Proposition]
  with Unifiable[Proposition]
  with PredN
