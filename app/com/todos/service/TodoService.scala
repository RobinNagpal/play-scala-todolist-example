package com.todos.service

import java.util.UUID

import com.todos.api.{CreateTodoCommand, EditTodoCommand}
import com.todos.model.Todo
import javax.inject.Singleton

import scala.concurrent.Future

trait TodoService {
  def getTodos(): Future[List[Todo]]

  def createTodo(cmd: CreateTodoCommand): Future[Todo]

  def editTodo(cmd: EditTodoCommand): Future[Todo]

}

@Singleton
class TodoServiceImpl extends TodoService {

  override def getTodos(): Future[List[Todo]] = {
    Future.successful(List(
      Todo(id = UUID.randomUUID(), title = "Title 1"),
      Todo(id = UUID.randomUUID(), title = "Title 2"),
      Todo(id = UUID.randomUUID(), title = "Title 3")
    ))
  }

  override def createTodo(cmd: CreateTodoCommand): Future[Todo] = {
    Future.successful(Todo(id = UUID.randomUUID(), title = "Title 2"))
  }

  override def editTodo(cmd: EditTodoCommand): Future[Todo] = {
    Future.successful(Todo(id = UUID.randomUUID(), title = "Title 2"))
  }
}
