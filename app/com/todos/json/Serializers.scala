package com.todos.json

import com.todos.api.{UpdateCompleteFlagCommand, CreateTodoCommand, EditTodoCommand}
import com.todos.model.Todo
import play.api.libs.json.Json

trait Serializers {

  implicit val todoWrites = Json.writes[Todo]

  implicit val todoReads = Json.reads[Todo]
  implicit val createTodoCmdReads = Json.reads[CreateTodoCommand]
  implicit val editTodoCmdReads = Json.reads[EditTodoCommand]
  implicit val completeTodoCmdReads = Json.reads[UpdateCompleteFlagCommand]
}
