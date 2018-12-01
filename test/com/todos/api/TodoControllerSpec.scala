package com.todos.api

import java.util.UUID

import com.todos.json.Serializers
import com.todos.model.Todo
import com.todos.service.TodoService
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Future

/**
  * Add your spec here.
  * You can mock out a whole application including requests, plugins etc.
  *
  * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
  */
class TodoControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting with MockitoSugar with Serializers {

  "TodoController" should {

    "return the list of todos" in {
      val todoService = mock[TodoService]
      val controller = new TodoController(stubControllerComponents(), todoService)


      val todo1 = Todo(id = UUID.randomUUID(), title = "Title 1")
      val todo2 = Todo(id = UUID.randomUUID(), title = "Title 2")
      val todos = List(todo1, todo2)
      when(todoService.getTodos()) thenReturn Future.successful(todos)

      val response = controller.getTodos().apply(FakeRequest(GET, "/"))
      status(response) mustBe OK
      contentType(response) mustBe Some("application/json")
      val result = contentAsJson(response).as[List[Todo]]
      result mustBe todos
    }

    /*
        "render the index page from the application" in {
          val controller = inject[TodoController]
          val home = controller.index().apply(FakeRequest(GET, "/"))

          status(home) mustBe OK
          contentType(home) mustBe Some("text/html")
          contentAsString(home) must include ("Welcome to Play")
        }


        "render the index page from the router" in {
          val request = FakeRequest(GET, "/")
          val home = route(app, request).get

          status(home) mustBe OK
          contentType(home) mustBe Some("text/html")
          contentAsString(home) must include ("Welcome to Play")
        }
        */
  }
}
