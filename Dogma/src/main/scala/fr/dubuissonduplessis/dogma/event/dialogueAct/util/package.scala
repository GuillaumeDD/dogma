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
package fr.dubuissonduplessis.dogma.event.dialogueAct

package object util {
  def dialogueActToLatex(da: DialogueAct): String =
    """\DABis{""" + da.name + """}{""" + fr.dubuissonduplessis.dogma.dialogue.util.toLatex(da.locutor) + """}"""

  def standardDialogueActToLatex(name: String, locutor: String, content: String): String =
    """\printDA{""" + name + """}{""" + locutor + """}{""" + content + """}"""

  def standardDialogueActToLatex[T](da: StandardDialogueAct[T])(toLatex: T => String): String = {
    standardDialogueActToLatex(
      da.name,
      fr.dubuissonduplessis.dogma.dialogue.util.toLatex(da.locutor),
      toLatex(da.content))
  }

  def nonStandardDialogueActToLatex(name: String, locutor: String, content1: String, content2: String): String =
    """\DATriple{""" + name + """}{""" + locutor + """}{""" + content1 + """}{""" + content2 + """}"""

  def nonStandardDialogueActToLatex[T1, T2](da: NonStandardDialogueAct[T1, T2])(toLatex1: T1 => String, toLatex2: T2 => String): String = {
    nonStandardDialogueActToLatex(da.name,
      fr.dubuissonduplessis.dogma.dialogue.util.toLatex(da.locutor),
      toLatex1(da.content1),
      toLatex2(da.content2))
  }
}
