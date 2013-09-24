package alesia.bindings

import java.io.File
import java.net.URL
import java.net.URI

/**
 * Default implementation of {@link ResourceProvider}. Only works for local resources.
 *
 * @see ResourceProvider
 * @author Roland Ewald
 */
object LocalResourceProvider extends ResourceProvider {

  override def getResourceAsFile(url: String): Option[File] =
    Some(new File(new URI(url)))
}