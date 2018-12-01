package com.todos.api

case class CreateTodoCommand(
    title: String,
    description: String
)

case class EditTodoCommand(
    title: String,
    description: String
)
