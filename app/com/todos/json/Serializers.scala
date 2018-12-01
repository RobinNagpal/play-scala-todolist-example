package com.todos.json

import com.todos.api.{CreateTodoCommand, EditTodoCommand}
import com.todos.model.Todo
import play.api.libs.json.Json

trait Serializers {

  implicit val todoWrites = Json.writes[Todo]

  implicit val todoReads = Json.reads[Todo]
  implicit val createTodoReads = Json.reads[CreateTodoCommand]
  implicit val editTodoReads = Json.reads[EditTodoCommand]
}
