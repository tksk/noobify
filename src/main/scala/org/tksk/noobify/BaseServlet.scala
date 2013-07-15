package org.tksk.noobify

import org.scalatra.ScalatraServlet
import org.scalatra.ActionResult
import java.io.File
import org.scalatra.NotFound

trait BaseServlet { self: ScalatraServlet =>  

  val defaultAppVersion = "1.0"

  def getAppDir(app: String): Either[ActionResult, File] = {
    val dir = new File(getServletContext().getRealPath(s"/apps/${app}"))
    if(dir.exists() && dir.isDirectory()) Right(dir) else Left(NotFound("no such app: "+ app))
  }

  def getAppFileContent(app: String, relative: String): Either[ActionResult, File] = {
    getAppDir(app).right map { dir => new File(dir, relative) }
  }

  def calcRelativePath(base: String, file: File) =
    new File(base).toURI().relativize(file.toURI).getPath.replace('\\', '/')

}
