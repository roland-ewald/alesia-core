package alesia.results

import james.resultreport.ResultReport
import james.resultreport.ResultReportSection
import alesia.planning.execution.ExecutionStepResult
import james.resultreport.ResultReportGenerator
import james.resultreport.renderer.rtex.RTexResultReportRenderer
import java.io.File
import sessl.TableView
import james.resultreport.dataview.TableDataView
import alesia.planning.execution.PlanState
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

  override def storeReport(report: PlanExecutionReport, scenarioName: String, target: File): Unit = {

    checkDirectory(target)

    val resultReport = new ResultReport(scenarioName,
      s"This is a report on the plan execution of scenario'${scenarioName}' by ALeSiA.")

    report.init.foreach(s => resultReport.addSection(renderScenario(s)))
    report.actions.foreach(a => resultReport.addSection(renderActionExecution(a)))

    (new ResultReportGenerator).generateReport(resultReport, new RTexResultReportRenderer, target)
  }

  def checkDirectory(dir: File) = {
    if (!dir.exists())
      dir.mkdir()
    require(dir.isDirectory(), s"'${dir.getAbsolutePath}' must be a directory.")
  }

  def renderScenario(initialState: StateReport): ResultReportSection = {
    val s = new ResultReportSection("Initial Problem", "This section describes the problem as submitted to the system")
    s.addDataView(createPlanStateTable(initialState.planState, "The initial state"))
    s
  }

  def renderActionExecution(stepResult: ActionExecutionReport): ResultReportSection = {
    val s = new ResultReportSection(
      s"Executing Action ${stepResult.name}", "")
    s.addDataView(createPlanStateTable(stepResult.before, "The state before execution."))
    s.addDataView(createPlanStateTable(stepResult.after, "The state after execution."))
    s
  }

  def createPlanStateTable(ps: PlanState, title: String): TableDataView = {
    val tableData = for (s <- ps) yield Array[String](convertSpecialChars(s._1), s._2.toString)
    new TableDataView((Array("Literal", "True?") :: tableData.toList).toArray, title)
  }

}