package alesia.results

import java.io.File

import scala.collection.mutable.ListBuffer

import alesia.planning.DomainSpecificPlanningProblem
import alesia.planning.actions.ActionDeclaration
import alesia.planning.actions.ActionFormula
import alesia.planning.actions.Conjunction
import alesia.planning.actions.Disjunction
import alesia.planning.actions.FalseFormula
import alesia.planning.actions.Literal
import alesia.planning.actions.Negation
import alesia.planning.actions.PrivateLiteral
import alesia.planning.actions.PublicLiteral
import alesia.planning.actions.TrueFormula
import alesia.planning.execution.PlanState
import james.resultreport.ResultReport
import james.resultreport.ResultReportGenerator
import james.resultreport.ResultReportSection
import james.resultreport.dataview.TableDataView
import james.resultreport.renderer.rtex.RTexResultReportRenderer
import sessl.utils.doclet.LatexStringConverter.convertSpecialChars

/**
 * Render result reports.
 *
 * In functionality, this is similar to the result report renderer in JAMES II,
 * but the structure of the report is already given so that the actual report for
 * the JAMES II result report renderer is created here as well.
 *
 * @author Roland Ewald
 */
trait ReportResultRenderer {

  /**
   * Store a report in some format to the given directory.
   * Note that depending on the report, multiple files might be stored,
   * where `scenarioName` specifies the name of report and that of the 'main' report file.
   *
   * @param report the report to be rendered
   * @param scenarioName the name of the scenario and thus the report (may be used as title and file name)
   * @param target the directory in which to store the report file
   */
  def storeReport(report: PlanExecutionReport, scenarioName: String, target: File): Unit

}

/**
 * A default implementation of [[ReportResultRenderer]], based on the default
 * result renderer provided by JAMES II (which uses R and LaTeX for report generation).
 */
object DefaultResultRenderer extends ReportResultRenderer {

  val trueSymbol = raw"\top"

  val falseSymbol = raw"\bot"

  override def storeReport(report: PlanExecutionReport, scenarioName: String, target: File): Unit = {

    checkDirectory(target)

    val resultReport = new ResultReport(scenarioName,
      s"This is a report on the plan execution of scenario'${scenarioName}' by ALeSiA.")

    report.init.foreach(s => resultReport.addSection(describeScenario(s)))
    report.actions.foreach(a => resultReport.addSection(describeActionExecution(report, a)))

    (new ResultReportGenerator).generateReport(resultReport, new RTexResultReportRenderer, target)
  }

  def checkDirectory(dir: File) = {
    if (!dir.exists())
      dir.mkdir()
    require(dir.isDirectory(), s"'${dir.getAbsolutePath}' must be a directory.")
  }

  def describeScenario(sr: StateReport): ResultReportSection = {
    val desc = new ResultReportSection("Initial Problem",
      "This section describes the problem as submitted to the system")
    desc.addDataView(describePlanState(sr.state.context.planState, "The initial state"))
    desc
  }

  def describeActionExecution(planExecution: PlanExecutionReport, actionExecution: ActionExecutionReport): ResultReportSection = {
    val desc = new ResultReportSection(s"Step ${actionExecution.step} Executing Action ${actionExecution.name}", "")

    planExecution.init.map { r =>
      desc.addDataView(describeActionDeclaration(r.state.problem.declaredActions(actionExecution.index)))
      desc.addDataView(describeDomainAction(r.state.problem)(r.state.problem.actions(actionExecution.index)))
    }

    desc.addDataView(describePlanState(actionExecution.before, "The state before execution."))
    desc.addDataView(describePlanState(actionExecution.after, "The state after execution."))
    desc
  }

  def describePlanState(ps: PlanState, title: String): TableDataView = {
    val tableData = for (s <- ps.toSeq.sortBy(_._1)) yield Array[String](convertSpecialChars(s._1), s._2.toString)
    new TableDataView((Array("Literal", "True?") :: tableData.toList).toArray, title)
  }

  def describeActionDeclaration(a: ActionDeclaration): TableDataView = {
    val tableData = ListBuffer[Array[String]]()
    tableData += Array("Pre-codition", actionFormulaToLatex(a.preCondition))
    for (e <- a.effect.zipWithIndex) {
      val prefix = s"Effect ${e._2 + 1} "
      tableData += Array(prefix + "precondition", actionFormulaToLatex(e._1.condition))
      tableData += Array(prefix + "nondeterministic?", e._1.nondeterministic.toString)
      tableData += Array(prefix + "additions", e._1.add.map(singleLiteralToLatex).mkString(","))
      tableData += Array(prefix + "deletions", e._1.del.map(singleLiteralToLatex).mkString(","))
    }
    new TableDataView((Array("Action Property", "Value") :: tableData.toList).toArray, s"Declaration of action ${a.name}")
  }

  def describeDomainAction(p: DomainSpecificPlanningProblem)(a: p.DomainAction): TableDataView = {
    val tableData = ListBuffer[Array[String]]()
    new TableDataView((Array("Formal Action Property", "Value") :: tableData.toList).toArray, s"Formal definition of action ${a.name}")
  }

  def actionFormulaToLatex(a: ActionFormula): String = {
    def traverse(a: ActionFormula): String = a match {
      case TrueFormula => trueSymbol
      case FalseFormula => falseSymbol
      case l: Literal => literalToLatex(l)
      case Negation(e) => raw"\neg" + traverse(e)
      case Conjunction(l, r) => "(" + traverse(l) + raw"\wedge" + traverse(r) + ")"
      case Disjunction(l, r) => "(" + traverse(l) + raw"\vee" + traverse(r) + ")"
      case _ => "?"
    }
    "$" + traverse(a) + "$"
  }

  def literalToLatex(l: Literal): String = l match {
    case PrivateLiteral(l) => " " + convertSpecialChars(l) + " "
    case PublicLiteral(l) => raw"\mathbf{" + convertSpecialChars(l) + "}"
  }

  def singleLiteralToLatex(l: Literal) = "$" + literalToLatex(l) + "$"

}