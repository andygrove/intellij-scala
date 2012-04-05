package org.jetbrains.plugins.scala.lang.completion.lookups

import com.intellij.codeInsight.completion.InsertionContext
import org.jetbrains.plugins.scala.lang.completion.handlers.ScalaInsertHandler
import com.intellij.codeInsight.lookup.{LookupElementPresentation, LookupItem}
import org.jetbrains.plugins.scala.lang.psi.PresentationUtil._
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.ScTypeParametersOwner
import org.jetbrains.plugins.scala.lang.psi.api.base.patterns.ScBindingPattern
import org.jetbrains.plugins.scala.lang.psi.api.statements.params.ScParameter
import org.jetbrains.plugins.scala.lang.psi.api.statements.{ScTypeAliasDefinition, ScFunction, ScFun}
import org.jetbrains.plugins.scala.lang.psi.ScalaPsiUtil
import com.intellij.psi._
import org.jetbrains.plugins.scala.lang.psi.api.base.{ScReferenceElement, ScFieldId}
import util.PsiTreeUtil
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.imports.{ScImportSelectors, ScImportStmt}
import com.intellij.codeInsight.AutoPopupController
import org.jetbrains.plugins.scala.lang.psi.api.ScalaFile
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScReferenceExpression
import org.jetbrains.plugins.scala.lang.psi.impl.ScalaPsiElementFactory
import org.jetbrains.plugins.scala.annotator.intention.ScalaImportClassFix
import org.jetbrains.plugins.scala.lang.psi.types.{ScType, ScSubstitutor}
import com.intellij.openapi.util.Condition
import org.jetbrains.plugins.scala.lang.refactoring.util.ScalaNamesUtil
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef.{ScTemplateDefinition, ScObject}
import org.jetbrains.plugins.scala.lang.psi.types.result.{Success, TypingContext}
import org.jetbrains.plugins.scala.extensions.{toPsiMemberExt, toPsiClassExt, toPsiNamedElementExt}

/**
 * @author Alefas
 * @since 22.03.12
 */
class ScalaLookupItem(val element: PsiNamedElement, _name: String, containingClass0: Option[PsiClass] = None) extends {
  val name: String = if (ScalaNamesUtil.isKeyword(_name) && _name != "this") "`" + _name + "`" else _name
} with LookupItem[PsiNamedElement](element, name) {

  private var _isClassName: Boolean = false
  def isClassName_=(t: Boolean) {_isClassName = t}
  def isClassName: Boolean = _isClassName

  private var _isRenamed: Option[String] = None
  def isRenamed_=(t: Option[String]) {_isRenamed = t}
  def isRenamed: Option[String] = _isRenamed

  private var _isAssignment: Boolean = false
  def isAssignment_=(t: Boolean) {_isAssignment = t}
  def isAssignment: Boolean = _isAssignment

  private var _substitutor: ScSubstitutor = ScSubstitutor.empty
  def substitutor_=(t: ScSubstitutor) {_substitutor = t}
  def substitutor: ScSubstitutor = _substitutor

  private var _shouldImport: Boolean = false
  def shouldImport_=(t: Boolean) {_shouldImport = t}
  def shouldImport: Boolean = _shouldImport

  private var _isOverloadedForClassName: Boolean = false
  def isOverloadedForClassName_=(t: Boolean) {_isOverloadedForClassName = t}
  def isOverloadedForClassName: Boolean = _isOverloadedForClassName

  private var _isNamedParameter: Boolean = false
  def isNamedParameter_=(t: Boolean) {_isNamedParameter = t}
  def isNamedParameter: Boolean = _isNamedParameter

  private var _isDeprecated: Boolean = false
  def isDeprecated_=(t: Boolean) {_isDeprecated = t}
  def isDeprecated: Boolean = _isDeprecated

  private var _isUnderlined: Boolean = false
  def isUnderlined_=(t: Boolean) {_isUnderlined = t}
  def isUnderlined: Boolean = _isUnderlined

  private var _isInImport: Boolean = false
  def isInImport_=(t: Boolean) {_isInImport = t}
  def isInImport: Boolean = _isInImport

  private var _isInStableCodeReference: Boolean = false
  def isInStableCodeReference_=(t: Boolean) {_isInStableCodeReference = t}
  def isInStableCodeReference: Boolean = _isInStableCodeReference

  private var _usedImportStaticQuickfixKey: Boolean = false
  def usedImportStaticQuickfix_=(t: Boolean) {_usedImportStaticQuickfixKey = t}
  def usedImportStaticQuickfix: Boolean = _usedImportStaticQuickfixKey

  private var _elementToImport: PsiNamedElement = null
  def elementToImport_=(t: PsiNamedElement) {_elementToImport = t}
  def elementToImport: PsiNamedElement = _elementToImport
  
  private var _someSmartCompletion: Boolean = false
  def someSmartCompletion_=(t: Boolean) {_someSmartCompletion = t}
  def someSmartCompletion: Boolean = _someSmartCompletion
  
  private var _typeParametersProblem: Boolean = false
  def typeParametersProblem_=(t: Boolean) {_typeParametersProblem = t}
  def typeParametersProblem: Boolean = _typeParametersProblem

  private var _typeParameters: Seq[ScType] = Seq.empty
  def typeParameters_=(t: Seq[ScType]) {_typeParameters = t}
  def typeParameters: Seq[ScType] = _typeParameters

  private var _bold: Boolean = false
  def bold_=(t: Boolean) {_bold = t}
  def bold: Boolean = _bold

  private var _etaExpanded: Boolean = false
  def etaExpanded_=(t: Boolean) {_etaExpanded = t}
  def etaExpanded: Boolean = _etaExpanded

  def isNamedParameterOrAssignment = isNamedParameter || isAssignment

  val containingClass = containingClass0.getOrElse(ScalaPsiUtil.nameContext(element) match {
    case memb: PsiMember => memb.containingClass
    case _ => null
  })

  override def equals(o: Any): Boolean = {
    if (!super.equals(o)) return false
    o match {
      case s: ScalaLookupItem => if (isNamedParameter != s.isNamedParameter || containingClass != s.containingClass) return false
      case _ =>
    }
    true
  }

  override def renderElement(presentation: LookupElementPresentation) {
    val tailText: String = element match {
      case t: ScFun => {
        if (t.typeParameters.length > 0) t.typeParameters.map(param => presentationString(param, substitutor)).
          mkString("[", ", ", "]")
        else ""
      }
      case t: ScTypeParametersOwner => {
        t.typeParametersClause match {
          case Some(tp) => presentationString(tp, substitutor)
          case None => ""
        }
      }
      case p: PsiTypeParameterListOwner if p.getTypeParameters.length > 0 => {
        p.getTypeParameters.map(ptp => presentationString(ptp)).mkString("[", ", ", "]")
      }
      case _ => ""
    }
    element match {
      //scala
      case fun: ScFunction => {
        val scType = if (!etaExpanded) fun.returnType.getOrAny else fun.getType(TypingContext.empty).getOrAny
        presentation.setTypeText(presentationString(scType, substitutor))
        val tailText1 = if (isAssignment) {
          " = " + presentationString(fun.paramClauses, substitutor)
        } else {
          tailText + (
            if (!isOverloadedForClassName) presentationString(fun.paramClauses, substitutor)
            else "(...)"
            ) + (
            if (shouldImport && isClassName && containingClass != null)
              " " + containingClass.getPresentation.getLocationString
            else if (isClassName && containingClass != null)
              " in " + containingClass.name + " " + containingClass.getPresentation.getLocationString
            else ""
            )
        }
        if (!etaExpanded)
          presentation.setTailText(tailText1)
        else presentation.setTailText(" _")
      }
      case fun: ScFun => {
        presentation.setTypeText(presentationString(fun.retType, substitutor))
        val paramClausesText = fun.paramClauses.map(_.map(presentationString(_, substitutor)).
          mkString("(", ", ", ")")).mkString
        presentation.setTailText(tailText + paramClausesText)
      }
      case bind: ScBindingPattern => {
        presentation.setTypeText(presentationString(bind.getType(TypingContext.empty).getOrAny, substitutor))
      }
      case f: ScFieldId => {
        presentation.setTypeText(presentationString(f.getType(TypingContext.empty).getOrAny, substitutor))
      }
      case param: ScParameter => {
        val str: String =
          presentationString(param.getRealParameterType(TypingContext.empty).getOrAny, substitutor)
        if (isNamedParameter) {
          presentation.setTailText(" = " + str)
        } else {
          presentation.setTypeText(str)
        }
      }
      case clazz: PsiClass => {
        val location: String = clazz.getPresentation.getLocationString
        presentation.setTailText(tailText + " " + location, true)
        if (name == "this" || name.endsWith(".this")) {
          clazz match {
            case t: ScTemplateDefinition =>
              t.getTypeWithProjections(TypingContext.empty, true) match {
                case Success(tp, _) =>
                  presentation.setTypeText(tp.presentableText)
                case _ =>
              }
            case _ =>
          }
        }
      }
      case alias: ScTypeAliasDefinition => {
        presentation.setTypeText(presentationString(alias.aliasedType.getOrAny, substitutor))
      }
      case method: PsiMethod => {
        val str: String = presentationString(method.getReturnType, substitutor)
        if (isNamedParameter) {
          presentation.setTailText(" = " + str)
        } else {
          presentation.setTypeText(str)
          val params =
            if (!isOverloadedForClassName) presentationString(method.getParameterList, substitutor)
            else "(...)"
          val tailText1 = tailText + params + (
            if (shouldImport && isClassName && containingClass != null)
              " " + containingClass.getPresentation.getLocationString
            else if (isClassName && containingClass != null)
              " in " + containingClass.name + " " + containingClass.getPresentation.getLocationString
            else ""
            )
          presentation.setTailText(tailText1)
        }
      }
      case f: PsiField => {
        presentation.setTypeText(presentationString(f.getType, substitutor))
      }
      case _ =>
    }
    if (presentation.isReal)
      presentation.setIcon(element.getIcon(0))
    var itemText: String =
      if (isRenamed == None) if (isClassName && shouldImport) {
        if (containingClass != null) containingClass.name + "." + name
        else name
      } else name
      else name + " <= " + element.name
    if (someSmartCompletion) itemText = "Some(" + itemText + ")"
    presentation.setItemText(itemText)
    presentation.setStrikeout(isDeprecated)
    presentation.setItemTextBold(bold)
    if (ScalaPsiUtil.getSettings(element.getProject).SHOW_IMPLICIT_CONVERSIONS)
      presentation.setItemTextUnderlined(isUnderlined)
  }

  private def simpleInsert(context: InsertionContext) {
    new ScalaInsertHandler().handleInsert(context, this)
  }

  override def handleInsert(context: InsertionContext) {
    if (getInsertHandler != null) super.handleInsert(context)
    else if (isClassName) {
      PsiDocumentManager.getInstance(context.getProject).commitDocument(context.getDocument)
      val startOffset = context.getStartOffset
      var ref: ScReferenceElement = PsiTreeUtil.findElementOfClassAtOffset(context.getFile, startOffset, classOf[ScReferenceElement], false)
      val useFullyQualifiedName = PsiTreeUtil.getParentOfType(ref, classOf[ScImportStmt]) != null &&
        PsiTreeUtil.getParentOfType(ref, classOf[ScImportSelectors]) == null //do not complete in sel
      if (ref == null) return
      val file = ref.getContainingFile

      element match {
        case cl: PsiClass if isRenamed != None => //do nothing
        case cl: PsiClass =>
          while (ref.getParent != null && ref.getParent.isInstanceOf[ScReferenceElement] &&
            (ref.getParent.asInstanceOf[ScReferenceElement].qualifier match {
              case Some(r) => r != ref
              case _ => true
            }))
            ref = ref.getParent.asInstanceOf[ScReferenceElement]
          val addDot = if (cl.isInstanceOf[ScObject] && isInStableCodeReference) "." else ""
          val newRef = ref.createReplacingElementWithClassName(useFullyQualifiedName, cl)
          ref.getNode.getTreeParent.replaceChild(ref.getNode, newRef.getNode)
          newRef.bindToElement(cl)
          if (addDot == ".") {
            context.setLaterRunnable(new Runnable {
              def run() {
                AutoPopupController.getInstance(context.getProject).scheduleAutoPopup(
                  context.getEditor, new Condition[PsiFile] {
                    def value(t: PsiFile): Boolean = t == context.getFile
                  }
                )
              }
            })
          }
        case _ =>
          simpleInsert(context)
          if (containingClass != null) {
            val document = context.getEditor.getDocument
            PsiDocumentManager.getInstance(file.getProject).commitDocument(document)
            file match {
              case scalaFile: ScalaFile =>
                val elem = scalaFile.findElementAt(startOffset + (if (someSmartCompletion) 5 else 0))
                def qualifyReference(ref: ScReferenceExpression) {
                  val newRef = ScalaPsiElementFactory.createExpressionFromText(
                    containingClass.name + "." + ref.getText,
                    containingClass.getManager).asInstanceOf[ScReferenceExpression]
                  ref.getNode.getTreeParent.replaceChild(ref.getNode, newRef.getNode)
                  newRef.qualifier.get.asInstanceOf[ScReferenceExpression].bindToElement(containingClass)
                }
                elem.getParent match {
                  case ref: ScReferenceExpression if !usedImportStaticQuickfix =>
                    if (shouldImport) qualifyReference(ref)
                  case ref: ScReferenceExpression =>
                    if (!shouldImport) qualifyReference(ref)
                    else {
                      if (elementToImport == null) {
                        //import static
                        ref.bindToElement(element, Some(containingClass))
                      } else {
                        ScalaPsiUtil.nameContext(elementToImport) match {
                          case memb: PsiMember =>
                            val containingClass = memb.containingClass
                            if (containingClass != null && containingClass.qualifiedName != null) {
                              ScalaImportClassFix.getImportHolder(ref, ref.getProject).addImportForPsiNamedElement(elementToImport, null)
                            }
                          case _ =>
                        }
                      }
                    }
                  case _ =>
                }
              case _ =>
            }
          }
      }
    } else simpleInsert(context)
  }
}

object ScalaLookupItem {
  def unapply(item: ScalaLookupItem): Option[PsiNamedElement] = {
    Some(item.element)
  }
}
