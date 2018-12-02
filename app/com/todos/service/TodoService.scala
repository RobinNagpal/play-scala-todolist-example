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

  def deleteTodo(id: UUID): Future[Unit]

  def updateCompleteFlag(id: UUID, isCompleted: Boolean): Future[Unit]

}

@Singleton
class TodoServiceImpl extends TodoService {

  override def getTodos(): Future[List[Todo]] = {
    Future.successful(List(
      Todo(id = UUID.randomUUID(), title = "Title 1", completed = false, comments = List.empty),
      Todo(id = UUID.randomUUID(), title = "Title 2", completed = false, comments = List.empty),
      Todo(id = UUID.randomUUID(), title = "Title 3", completed = false, comments = List.empty)
    ))
  }

  override def createTodo(cmd: CreateTodoCommand): Future[Todo] = {
    Future.successful(Todo(id = UUID.randomUUID(), title = "Title 2", completed = false, comments = List.empty))
  }

  override def editTodo(cmd: EditTodoCommand): Future[Todo] = {
    Future.successful(Todo(id = UUID.randomUUID(), title = "Title 2", completed = false, comments = List.empty))
  }

  def deleteTodo(id: UUID): Future[Unit] = Future.unit

  def updateCompleteFlag(id: UUID, isCompleted: Boolean): Future[Unit] = Future.unit

}
