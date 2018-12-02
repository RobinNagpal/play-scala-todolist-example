package com.todos.service

import java.util.UUID

import com.todos.api.{CreateTodoCommand, EditTodoCommand}
import com.todos.db.TodoRepository
import com.todos.model.{Comment, Todo}
import javax.inject.{Inject, Singleton}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait TodoService {

  def getTodos(): Future[List[Todo]]

  def findById(id: UUID): Future[Option[Todo]]

  def createTodo(cmd: CreateTodoCommand): Future[Todo]

  def editTodo(cmd: EditTodoCommand): Future[Todo]

  def deleteTodo(id: UUID): Future[Unit]

  def updateCompleteFlag(id: UUID, isCompleted: Boolean): Future[Unit]

}

@Singleton
class TodoServiceImpl @Inject()(todoRepo: TodoRepository) extends TodoService {

  override def getTodos(): Future[List[Todo]] = todoRepo.getAllTodos()

  override def findById(id: UUID): Future[Option[Todo]] = todoRepo.findById(id)

  override def createTodo(cmd: CreateTodoCommand): Future[Todo] = {
    for {
      todoId <- todoRepo.add(UUID.randomUUID(), cmd.title)
      todo <- todoRepo.findById(todoId)
    } yield todo.getOrElse(throw new RuntimeException("was not able to create todo"))
  }

  override def editTodo(cmd: EditTodoCommand): Future[Todo] = {
    for {
      todoOpt <- todoRepo.findById(cmd.id)
      _ <- {
        val todo = todoOpt.getOrElse(throw new RuntimeException("was not able to create todo"))
        todoRepo.update(
          todo.copy(
            title = cmd.title,
            completed = cmd.completed,
            comments = cmd.comments.map(commendCmd => Comment(id = UUID.randomUUID(), content = commendCmd.content))
          ))

      }
      updatedTodo <- todoRepo.findById(cmd.id)
    } yield updatedTodo.getOrElse(throw new RuntimeException("was not able to create todo"))
  }

  def deleteTodo(id: UUID): Future[Unit] = todoRepo.delete(id)

  def updateCompleteFlag(id: UUID, isCompleted: Boolean): Future[Unit] = todoRepo.updateCompleteFlag(id, isCompleted).map(_ => Unit)

}
