package com.todos.api

import java.util.UUID

import com.todos.json.Serializers
import com.todos.model.Todo
import com.todos.service.TodoService
import javax.inject._
import play.api.libs.json.{JsError, Json, Reads}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class TodoController @Inject()(cc: ControllerComponents, todoService: TodoService) extends AbstractController(cc) with Serializers {

  implicit def transformTodo(todo: Future[Todo]): Future[Result] = todo.map(resp => Ok(Json.toJson(resp)))

  implicit def transformTodos(todos: Future[List[Todo]]): Future[Result] = todos.map(resp => Ok(Json.toJson(resp)))

  implicit def transformUnit(resp: Future[Unit]): Future[Result] = resp.map( _ => Ok)

  def health() = Action { implicit request: Request[AnyContent] =>
    Ok("OK")
  }

  def getTodos = Action.async { request =>
    todoService.getTodos()
  }

  def createTodo = Action.async(validateJson[CreateTodoCommand]) { request =>
    todoService.createTodo(request.body)
  }

  def editTodo = Action.async(validateJson[EditTodoCommand]) { request =>
    todoService.editTodo(request.body)
  }

  def deleteTodo(id: UUID) = Action.async { request: Request[AnyContent] =>
    todoService.deleteTodo(id)
  }

  def updateCompleteFlag(id: UUID) = Action.async(validateJson[UpdateCompleteFlagCommand]) { request =>
    todoService.updateCompleteFlag(id, request.body.isCompleted)
  }

  private def validateJson[A: Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )
}
