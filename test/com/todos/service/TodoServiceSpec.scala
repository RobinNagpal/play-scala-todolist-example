package com.todos.service

import java.util.UUID

import com.todos.api.{AddCommentCommand, CreateTodoCommand, EditCommentCommand, EditTodoCommand}
import com.todos.db.TodoRepository
import com.todos.model.{Comment, Todo}
import org.mockito.ArgumentMatchers.{any, eq => equalArg}
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import scala.concurrent.Future

class TodoServiceSpec extends PlaySpec with GuiceOneAppPerSuite with MockitoSugar with ScalaFutures {

  "Todo Service" should {
    "be able to add a new todo item" in {
      val repo = mock[TodoRepository]
      val service = new TodoServiceImpl(repo)
      val title = "Todo title"
      val todo = Todo(id = UUID.randomUUID(), title = title, completed = false, comments = List.empty)

      when(repo.add(any[UUID], equalArg(title))) thenReturn Future.successful(todo.id)
      when(repo.findById(todo.id)) thenReturn Future.successful(Some(todo))

      whenReady(service.createTodo(CreateTodoCommand(title))) { result => {
        result mustBe todo
      }
      }
    }

    "be able to add a comment to todo item" in {
      val repo = mock[TodoRepository]
      val service = new TodoServiceImpl(repo)

      val content = "Comment content"
      val comment = Comment(id = UUID.randomUUID(), content = content)
      val todo = Todo(
        id = UUID.randomUUID(),
        title = "Todo title",
        completed = false,
        comments = List(comment)
      )

      when(repo.addComment(any[UUID], any[Comment])) thenReturn Future.successful(comment.id)
      when(repo.findById(todo.id)) thenReturn Future.successful(Some(todo))

      whenReady(service.addComment(todo.id, AddCommentCommand(content))) { result => {
        result mustBe todo
      }
      }
    }

    "be able to find todo" in {
      val repo = mock[TodoRepository]
      val service = new TodoServiceImpl(repo)

      val todo = Todo(
        id = UUID.randomUUID(),
        title = "Todo title",
        completed = false,
        comments = List(Comment(id = UUID.randomUUID(), content = "Comment content 1"),
          Comment(id = UUID.randomUUID(), content = "Comment content 2"))
      )

      when(repo.findById(todo.id)) thenReturn Future.successful(Some(todo))


      whenReady(service.findById(todo.id)) { result => {
        result mustBe Some(todo)
      }
      }
    }


    "be able to delete todo" in {
      val repo = mock[TodoRepository]
      val service = new TodoServiceImpl(repo)

      val todo = Todo(
        id = UUID.randomUUID(),
        title = "Todo title",
        completed = false,
        comments = List.empty
      )

      when(repo.delete(todo.id)) thenReturn Future.unit


      whenReady(service.deleteTodo(todo.id)) { result => {
        result mustEqual ((): Unit)
      }
      }
    }

    "be able to mark todo as completed" in {
      val repo = mock[TodoRepository]
      val service = new TodoServiceImpl(repo)

      val todo = Todo(
        id = UUID.randomUUID(),
        title = "Todo title",
        completed = false,
        comments = List.empty
      )

      when(repo.updateCompleteFlag(todo.id, true)) thenReturn Future.successful(1)


      whenReady(service.updateCompleteFlag(todo.id, true)) { result => {
        result mustEqual ((): Unit)
      }
      }
    }

    "be able to edit a todo" in {
      val repo = mock[TodoRepository]
      val service = new TodoServiceImpl(repo)

      val todo = Todo(
        id = UUID.randomUUID(),
        title = "Todo title",
        completed = false,
        comments = List(Comment(id = UUID.randomUUID(), content = "Comment content 1"))
      )

      when(repo.update(todo)) thenReturn Future.unit
      when(repo.findById(todo.id)) thenReturn Future.successful(Some(todo))

      val editResult = service.editTodo(EditTodoCommand(
        id = todo.id,
        title = todo.title,
        completed = false,
        comments = todo.comments.map(comment => EditCommentCommand(id = comment.id, content = comment.content))
      ))

      whenReady(editResult) { result => {
        result mustBe todo
      }
      }
    }


  }

}
