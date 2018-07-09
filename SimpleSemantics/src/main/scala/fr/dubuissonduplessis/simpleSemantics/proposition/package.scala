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

/**
 * == Overview ==
 * Propositions are represented by '''n-ary predicates''' with constants (or individuals) representing
 * their arguments. For example, `have_a(beer)` is a proposition which name is `have_a` and argument is
 * the constant `beer`. This semantic representation has to be used as a '''reduced semantic representation'''
 * with a domain-dependent level of granularity. A proposition must be a subclass of
 * [[fr.dubuissonduplessis.simpleSemantics.proposition.Proposition]].
 *
 * This package contains classes tools to build:
 *  - 0-ary proposition (e.g. `raining`): [[fr.dubuissonduplessis.simpleSemantics.proposition.Proposition0]]
 *  - 1-ary proposition which argument is a constant (e.g. `have_a(beer)`): [[fr.dubuissonduplessis.simpleSemantics.proposition.Proposition1]]
 *  - 2-ary proposition to deal with understanding: [[fr.dubuissonduplessis.simpleSemantics.proposition.Und]]
 *
 * Sub-packages includes more evolved propositions that deal with:
 *  - [[fr.dubuissonduplessis.simpleSemantics.question.Question]]: see [[fr.dubuissonduplessis.simpleSemantics.proposition.questionProposition]]
 *  - Actions: [[fr.dubuissonduplessis.simpleSemantics.proposition.actionProposition]]
 *  - Dialogue Game proposition: [[fr.dubuissonduplessis.simpleSemantics.proposition.gameProposition]]
 */
package object proposition
