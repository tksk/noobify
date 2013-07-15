package org.tksk.noobify

import org.scalatra.scalate.ScalateSupport
import org.scalatra.ScalatraServlet
import scala.io.Source
import org.fusesource.scalate.util.Log
import javax.servlet.ServletRequest
import javax.servlet.http.HttpServletRequest
import java.io.File
import scala.xml.NodeSeq
import org.apache.tika.Tika
import org.apache.tika.mime.MediaType

trait StaticContent extends ScalatraServlet with ScalateSupport with BaseServlet with Log {

  //val prettifyExtensions = Seq(".sh", ".cmd", ".bat", ".mustache")
  val mustacheExtension = ".mustache"

  get("/apps/:app/") {
    val app = params("app")
    val index = "index.md"
    getAppFileContent(app, index) match {
      case Left(r) => pass
      case Right(file) if !file.exists => pass
      case Right(file) => redirect(s"/apps/${app}/${index}")
    }
  }

  get("/apps/:app/:name.md") {
    val app = params("app")
    val name = params("name")
    import com.tristanhunt.knockoff.DefaultDiscounter._
    import com.tristanhunt.knockoff._

    getAppFileContent(app, name+".md") match {
      case Left(r) => r
      case Right(file) =>
        contentType="text/html"

        val source = Source.fromFile(file).getLines.mkString("\n")
        ssp("/meta/ssp/markdown.ssp", "app" -> app, "path" -> name, 
            "content" -> toXHTML(knockoff(source)))
    }
  }

  def mustacheOption = {
    multiParams("captures") match {
      case app :: path :: Nil =>
        getAppFileContent(app, path + ".mustache") match {
          case Left(_) => None
          case Right(file) => if(file.exists) Some(file.getPath) else None
        }
      case _ => None
    }
  }

  val textExtensions = Seq(("application", "javascript"), ("application", "xml"), ("application", "json"))

  def canPrettify(app: String, path: String) = {
    val tika = new Tika
    getAppFileContent(app, path) match {
      case Left(_) => false
      case Right(file) if file.isDirectory => false
      case Right(file) =>
        val mtype = MediaType.parse(tika.detect(file))

        (mtype.getType, mtype.getSubtype) match {
          case ("text", "x-web-markdown") => false
          case ("text", _) => true
          case tuple@(_, _) if textExtensions.contains(tuple) => true
          case _ => false
        }
    }
  }

  def prettify(app: String, path: String) = {
    getAppFileContent(app, path) match {
      case Left(_) => pass
      case Right(file) =>
        contentType="text/html"

        val content = Source.fromFile(file).getLines.mkString("\n")
        ssp("/meta/ssp/prettify.ssp", "app" -> app, "path" -> path, "content" -> content, "params"->Nil)
      }
  }

  get("""^\/apps\/(.+?)\/(.+)$""".r) {
    if(!params.contains("raw")) {
      multiParams("captures") match {
        case app :: path :: Nil if canPrettify(app, path) => 
          prettify(app, path)
        case _ => pass
      }
    } else {
      pass
    }
  }

  def getParams = {
    params.toSeq.filter{ case(k,v) =>  k != "captures" && k != "raw"}
  }

  get("""^\/apps\/(.+?)\/(.+)$""".r) { // foo.bar => foo.bar.mustache
    val pattern = """.*?(\.[^.]+)\.mustache""".r

    mustacheOption match {
      case Some(path@pattern(ext)) if !params.contains("raw") =>
        multiParams("captures") match {
          case app :: segments :: Nil /* if canPrettify(app, segments) */=>

            contentType="text/html"
            val src = mustache(path, "parameterized" -> params)
            ssp("/meta/ssp/prettify.ssp", "app" -> app, "path" -> segments,
                "content" -> src, "params" -> getParams)
          case _ => pass
        }
      case Some(path) => mustache(path, "parameterized" -> params)
      case _ => pass
    }
  }

  get("*/jetty-dir.css") {
    redirect("/static/css/markdown.css")
  }

}
