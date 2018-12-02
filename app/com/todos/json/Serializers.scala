package com.todos.json

import com.todos.api.{CreateTodoCommand, EditTodoCommand, UpdateCompleteFlagCommand}
import com.todos.model.{Comment, Todo}
import play.api.libs.json.Json
import play.api.mvc.{Result, Results}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Serializers {

  implicit val commentWrites = Json.writes[Comment]
  implicit val todoWrites = Json.writes[Todo]

  implicit val commentReads = Json.reads[Comment]
  implicit val todoReads = Json.reads[Todo]
  implicit val createTodoCmdReads = Json.reads[CreateTodoCommand]
  implicit val editTodoCmdReads = Json.reads[EditTodoCommand]
  implicit val completeTodoCmdReads = Json.reads[UpdateCompleteFlagCommand]


  implicit def transformTodo(todo: Future[Todo]): Future[Result] = todo.map(resp => Results.Ok(Json.toJson(resp)))

  implicit def transformTodos(todos: Future[List[Todo]]): Future[Result] = todos.map(resp => Results.Ok(Json.toJson(resp)))

  implicit def transformUnit(resp: Future[Unit]): Future[Result] = resp.map( _ => Results.Ok)
}
