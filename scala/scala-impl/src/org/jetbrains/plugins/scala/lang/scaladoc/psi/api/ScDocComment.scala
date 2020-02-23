package org.jetbrains.plugins.scala
package lang
package scaladoc
package psi
package api


import _root_.org.jetbrains.plugins.scala.lang.psi.api.ScalaPsiElement
import com.intellij.psi.PsiDocCommentOwner
import com.intellij.psi.javadoc.{PsiDocComment, PsiDocTag}

/**
* User: Alexander Podkhalyuzin
* Date: 22.07.2008
*/
trait ScDocComment extends PsiDocComment with ScalaPsiElement {
  def version: Int

  override def findTagsByName(name: String): Array[PsiDocTag]

  def findTagsByName(filter: String => Boolean): Array[PsiDocTag]

  override def getOwner: PsiDocCommentOwner
}