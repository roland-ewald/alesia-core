package alesia.results

import james.resultreport.ResultReport
import james.resultreport.ResultReportSection

/**
 * Render result reports.
 *
 * In functionality, this is similar to the result report renderer in JAMES II,
 * but the structure of the report is already given.
 *
 * @author Roland Ewald
 */
trait ReportResultRenderer {

  /**
   * Store a report in some format to the given file and directory.
   * Note that depending on the report, multiple files might be stored,
   * so the fileName only specifies the name of the 'main' report file.
   *
   * @param report the report to be rendered
   * @param fileName the name of the major file containing the report
   * @param dirName the directory name in which to store the report file
   */
  def storeReport(report: PlanExecutionReport, fileName: String, dirName: String): Unit

}

/**
 * A default implementation of [[ReportResultRenderer]], based on the default
 * result renderer provided by JAMES II.
 */
object RLatexPlanningResultRenderer extends ReportResultRenderer {

  override def storeReport(report: PlanExecutionReport, fileName: String, dirName: String): Unit = {

    val resultReport = new ResultReport("Plan Execution Result", "This plan was generated by ALeSiA")

    report.init.map(s => resultReport.addSection(generateInitialDescription(s)))

    //TODO
  }

  def generateInitialDescription(sr: StateReport): ResultReportSection = {
    val s = new ResultReportSection("Initial Problem", "This section describes the problem as submitted to the system")
    s
  }

}