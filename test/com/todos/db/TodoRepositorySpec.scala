package com.todos.db

import java.util.UUID

import com.todos.model.{Comment, Todo}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import scala.concurrent.ExecutionContext.Implicits.global

class TodoRepositorySpec extends PlaySpec with GuiceOneAppPerSuite with ScalaFutures {

  def todoRepo: TodoRepository = app.injector.instanceOf(classOf[TodoRepository])

  "Todo Repository" should {
    "be able to add a new todo item" in {
      val todo = Todo(id = UUID.randomUUID(), title = "Todo title", completed = false ,comments = List.empty)
      whenReady(todoRepo.add(todo)) { result => {
        result mustBe todo.id
      }
      }
    }

    "be able to add a comment to todo item" in {
      val todo = Todo(
        id = UUID.randomUUID(),
        title = "Todo title",
        completed = false,
        comments = List(Comment(id = UUID.randomUUID(), content = "Comment content"))
      )

      whenReady(todoRepo.add(todo)) { result => {
        result mustBe todo.id
        whenReady(todoRepo.addComment(todo.id, todo.comments.head)) { result => {
          result mustBe todo.comments.head.id
        }
        }
      }
      }
    }

    "be able to find todo with comments" in {
      val todo = Todo(
        id = UUID.randomUUID(),
        title = "Todo title",
        completed = false,
        comments = List(
          Comment(id = UUID.randomUUID(), content = "Comment content 1"),
          Comment(id = UUID.randomUUID(), content = "Comment content 2"))
      )

      val todoOptionFuture =
        for {
          _ <- todoRepo.add(todo)
          _ <- todoRepo.addComment(todo.id, todo.comments(0))
          _ <- todoRepo.addComment(todo.id, todo.comments(1))
          todoWithComments <- todoRepo.findById(todo.id)
        } yield todoWithComments

      whenReady(todoOptionFuture) { result => {
        result mustBe Some(todo)
      }
      }
    }

    "be able to delete todo" in {
      val todo = Todo(
        id = UUID.randomUUID(),
        title = "Todo title",
        completed = false,
        comments = List(
          Comment(id = UUID.randomUUID(), content = "Comment content 1"),
          Comment(id = UUID.randomUUID(), content = "Comment content 2"))
      )

      val todoOptionFuture =
        for {
          _ <- todoRepo.add(todo)
          _ <- todoRepo.addComment(todo.id, todo.comments(0))
          _ <- todoRepo.addComment(todo.id, todo.comments(1))
          _ <- todoRepo.delete(todo.id)
          todoWithComments <- todoRepo.findById(todo.id)
        } yield todoWithComments

      whenReady(todoOptionFuture) { result => {
        result mustBe None
      }
      }
    }

    "be able to mark todo as completed" in {
      val todo = Todo(
        id = UUID.randomUUID(),
        title = "Todo title",
        completed = false,
        comments = List(
          Comment(id = UUID.randomUUID(), content = "Comment content 1"),
          Comment(id = UUID.randomUUID(), content = "Comment content 2"))
      )

      val todoOptionFuture =
        for {
          _ <- todoRepo.add(todo)
          _ <- todoRepo.addComment(todo.id, todo.comments(0))
          _ <- todoRepo.addComment(todo.id, todo.comments(1))
          _ <- todoRepo.updateCompleteFlag(todo.id, true)
          todoWithComments <- todoRepo.findById(todo.id)
        } yield todoWithComments

      whenReady(todoOptionFuture) { result => {
        result mustBe Some(todo.copy(completed = true))
      }
      }
    }

    "be able to edit a todo" in {
      val todo = Todo(
        id = UUID.randomUUID(),
        title = "Todo title",
        completed = false,
        comments = List(
          Comment(id = UUID.randomUUID(), content = "Comment content 1"),
          Comment(id = UUID.randomUUID(), content = "Comment content 2"))
      )

      val updaed = todo.copy(
        comments = List(Comment(id = UUID.randomUUID(), content = "Comment content 3"))
      )

      val todoOptionFuture =
        for {
          _ <- todoRepo.add(todo)
          _ <- todoRepo.addComment(todo.id, todo.comments(0))
          _ <- todoRepo.addComment(todo.id, todo.comments(1))
          _ <- todoRepo.update(updaed)
          todoWithComments <- todoRepo.findById(todo.id)
        } yield todoWithComments

      whenReady(todoOptionFuture) { result => {
        result mustBe Some(updaed)
      }
      }
    }



  }
}
