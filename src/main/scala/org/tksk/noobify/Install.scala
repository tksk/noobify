package org.tksk.noobify

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.annotation.WebServlet
import org.scalatra.ScalatraServlet
import org.scalatra.scalate.ScalateSupport
import org.scalatra.ActionResult._
import java.io.File
import org.scalatra.RailsPathPatternParser
import org.scalatra.NotFound
import org.scalatra.ActionResult
import java.io.FileOutputStream
import java.util.zip.ZipOutputStream
import java.util.zip.ZipEntry
import java.io.BufferedInputStream
import java.io.FileInputStream
import scala.io.Source

class Install extends ScalatraServlet with ScalateSupport with BaseServlet with ContentResolver {

  get("/apps/:app/content-:app.zip") {
	val app = params("app")
	redirect(s"/apps/${app}/content-:app-${defaultAppVersion}.zip")
  }

  get("/apps/:app/content-:version.zip") {
	val app = params("app")
    val version = params("version")

    getAppFileContent(app, "meta/cache") match {
      case Left(r) => r
      case Right(dir) =>
        if(!dir.exists) dir.mkdirs
        zipAppContent(app, version) match {
          case Left(r) => r
          case Right(zip) => zip
        }
	}
  }
}
