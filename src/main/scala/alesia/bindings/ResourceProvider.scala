package alesia.bindings

import sessl.util.Logging

import java.io.File

/**
 * Interface for providers of local resources, such as files, directories, or databases etc.
 *
 * @author Roland Ewald
 */
trait ResourceProvider extends Logging {

  /**
   * Retrieves file as resource.
   * @param url the URL specifying which file to provide
   * @return the requested file
   */
  def getResourceAsFile(url: String): Option[File]

}
