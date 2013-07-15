import org.scalatra._
import javax.servlet.ServletContext
import org.tksk.noobify.Install
import org.tksk.noobify.StaticContent

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new Install with StaticContent, "/*")
  }
}
