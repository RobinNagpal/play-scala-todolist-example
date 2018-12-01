import com.google.inject.AbstractModule
import com.todos.service.{TodoService, TodoServiceImpl}
import javax.inject.Singleton
import play.api.{Configuration, Environment}

/**
  * Sets up custom components for Play.
  *
  * https://www.playframework.com/documentation/latest/ScalaDependencyInjection
  */
class Module(environment: Environment, configuration: Configuration)
    extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[TodoService]).to(classOf[TodoServiceImpl]).in(classOf[Singleton])
  }
}
