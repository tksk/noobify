package org.tksk.noobify

import org.scalatra.ScalatraServlet
import java.util.zip.ZipOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.io.BufferedInputStream
import java.io.FileInputStream
import org.scalatra.NotFound
import org.scalatra.ActionResult
import java.io.FileOutputStream
import org.fusesource.scalate.util.Log
import java.net.URLEncoder
import org.scalatra.scalate.ScalateSupport
import org.joda.time.Days

trait ContentResolver extends BaseServlet with ScalateSupport with Log { self: ScalatraServlet => 
  
  def encode(zos: ZipOutputStream, base: String, files: Iterable[File]) {

    val buffer = new Array[Byte](1024)

    def encodeCore(files: Iterable[File]) {
      for(file <- files) {
        if(file.isDirectory) {
          encodeCore(file.listFiles)
        } else {
          if(file.getName().endsWith(".mustache")) {
            val zem = new ZipEntry(calcRelativePath(base, file).dropRight(".mustache".length))
            zos.putNextEntry(zem);
            val rendered = mustache(file.getPath, "parameterized" -> params)
            zos.write(rendered.getBytes)
          }

          val ze = new ZipEntry(calcRelativePath(base, file))
          zos.putNextEntry(ze);
          val is = new BufferedInputStream(new FileInputStream(file))
          Stream.continually(is.read(buffer)).takeWhile(0 <= _).foreach(zos.write(buffer, 0, _))
          is.close
        }
      }
    }

    encodeCore(files)
  }

  def purgeOldCache(app: String) {
    import org.scala_tools.time.Imports._
    getAppFileContent(app, "meta/cache").right map { dir =>
      dir.listFiles foreach { file =>
        val days = Days.daysBetween(new DateTime(file.lastModified), DateTime.now)

        if(days.getDays > 14) {
          file.delete
        }
      }
    }
  }

  def zipAppContent(app: String, version: String): Either[ActionResult, File] = {
    val contentDir = getAppFileContent(app, s"content-${version}")

    contentDir match {
      case Left(_) => contentDir
      case Right(dir) if !dir.exists =>
        Left(NotFound(s"no such version: ${version} of ${app}"))

      case Right(dir) =>
        import collection.JavaConversions._

        val serializedQParams = for{ 
            (key, values) <- request.getParameterMap.toSeq.sortBy(_._1)
                if key != "invalidate"
            value <- values } yield key+"="+value

        val query = URLEncoder.encode(Option(serializedQParams.mkString("&")) match {
          case Some("") => ""
          case Some(q) => "-" + q
          case None => ""
        }, "UTF-8")

        getAppFileContent(app, s"meta/cache/${app}-${version}${query}.zip").right map { zip =>
          val invalidate = params.contains("invalidate")

          if(!invalidate && zip.exists) {
            zip
          } else {
            val contents = Array(dir)
            zip.delete // if invalidate
            val zos = new ZipOutputStream(new FileOutputStream(zip))
            encode(zos, dir.getPath, contents)
            zos.close
            zip
          }
        }
    }
  }
}