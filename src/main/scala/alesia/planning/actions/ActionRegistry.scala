package alesia.planning.actions

import scala.collection.mutable.ListBuffer
import org.reflections.Reflections
import sessl.util.ReflectionHelper
import sessl.util.Logging
import alesia.utils.DynamicLoading

/**
 * Registry for all specified actions. Loads these by scanning the class path once it is called for the first time, via reflection.
 * Can be configured to scan various (super-) packages via the Java property <code>alesia.planning.actions.packages</code>,
 * which expects a comma-separated list of package names.
 *
 * @author Roland Ewald
 */
object ActionRegistry extends Logging {

  val defaultActionSuperPackage = "alesia.planning.actions"

  val propertyToAddCustomPath = "alesia.planning.actions.packages"

  private[this] var actionSpecs: Seq[ActionSpecification] = scanSpecifications()

  def actionSpecifications = actionSpecs

  /**
   * Retrieves package names to be scanned (along with their sub-packages) for action specifications.
   * @return list of package names
   */
  def packagesNamesForActionSpecs(): Seq[String] = {
    val packageNames = ListBuffer[String]()
    packageNames += defaultActionSuperPackage
    val customPackageNames = System.getProperty(propertyToAddCustomPath)
    if (customPackageNames != null)
      packageNames ++= customPackageNames.split(",").map(_.trim)
    packageNames.toList
  }

  /**
   * Rescans all action specification, should only used for testing and debugging purposes.
   */
  protected[planning] def rescanActionSpecifications() {
    actionSpecs = scanSpecifications()
  }

  /**
   * Scans action specifications from the class path.
   */
  private[this] def scanSpecifications(): Seq[ActionSpecification] = {
    val packageNames = packagesNamesForActionSpecs().reverse
    logger.info("Scanning action specifications in the following packages: " + packageNames.map("'" + _ + "'").mkString(","))
    val rv = DynamicLoading.loadObjects[ActionSpecification](packageNames:_*)
    logger.info("Loaded the following action specifications:\n " + rv.map(_.toString).mkString("\n"))
    rv
  }
}