package com.todos.db

import java.util.UUID

import anorm._
import com.todos.model.{Comment, Todo}
import javax.inject.{Inject, Singleton}
import play.api.db.DBApi
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait TodoRepository {
  def getAllTodos(): Future[List[Todo]]

  def findById(id: UUID): Future[Option[Todo]]

  def add(id: UUID, title: String): Future[UUID]

  def addComment(todoId: UUID, comment: Comment): Future[UUID]

  def update(todo: Todo): Future[Unit]

  def updateCompleteFlag(todoId: UUID, completed: Boolean): Future[Int]

  def delete(id: UUID): Future[Unit]
}

@Singleton
class TodoRepositoryImpl @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) extends TodoRepository {
  case class TodoDBO(
      id: UUID,
      title: String,
      completed: Boolean
  )

  case class CommentDBO(
      id: UUID,
      content: String,
      todoid: UUID
  )

  val todoParser: RowParser[TodoDBO] = Macro.namedParser[TodoDBO]
  val commentParser: RowParser[CommentDBO] = Macro.namedParser[CommentDBO]
  private val db = dbapi.database("default")

  override def add(id: UUID, title: String): Future[UUID] =
    Future {
      db.withConnection { implicit connection =>
        SQL"""
        insert into "todo" ( "id", "title", "completed" ) values (
          ${id}::uuid, ${title}, false
        )
      """.executeInsert(SqlParser.scalar[UUID].single)
      }
    }(ec)

  override def findById(id: UUID): Future[Option[Todo]] = {
    val todoOptionalFuture: Future[Option[TodoDBO]] = findTodoDBO(id)
    todoOptionalFuture.flatMap(
      todoOpt => {
        todoOpt.fold(
          Future.successful(Option.empty[Todo])
        )(todo => {
          val eventualComments: Future[List[Comment]] =
            findTodoCommentDBOs(id).map(comments => comments.map(comment => Comment(id = comment.id, content = comment.content)))
          eventualComments.map(comments => Some(Todo(id = todo.id, title = todo.title, completed= todo.completed, comments = comments)))
        })
      }
    )
  }

  private def findTodoDBO(id: UUID): Future[Option[TodoDBO]] =
    Future {
      db.withConnection { implicit connection =>
        SQL"select * from todo where id = ${id}::uuid".as(todoParser.singleOpt)
      }
    }(ec)

  private def findTodoCommentDBOs(todoId: UUID) =
    Future {
      db.withConnection { implicit connection =>
        SQL"select * from comment where todoId = ${todoId}::uuid".as(commentParser.*)
      }
    }(ec)

  override def getAllTodos(): Future[List[Todo]] = {
    val groupedCommentsFuture: Future[Map[UUID, List[CommentDBO]]] = getAllCommentDBOs().map(comments => comments.groupBy(_.todoid))
    val todosFuture: Future[List[TodoDBO]] = getAllTodoDBOs()
    for {
      groupedComments <- groupedCommentsFuture
      todoDBOs <- todosFuture
    } yield
      todoDBOs map { todoDBO =>
        Todo(id = todoDBO.id,
             title = todoDBO.title,
             completed = todoDBO.completed,
             comments = groupedComments(todoDBO.id).map(comment => Comment(id = comment.id, content = comment.content)))
      }

  }

  private def getAllTodoDBOs(): Future[List[TodoDBO]] =
    Future {
      db.withConnection { implicit connection =>
        SQL"select * from todo".as(todoParser.*)
      }
    }(ec)

  private def getAllCommentDBOs() =
    Future {
      db.withConnection { implicit connection =>
        SQL"select * from comments".as(commentParser.*)
      }
    }(ec)


  override def delete(id: UUID): Future[Unit] = {
    for {
      _ <- deleteComments(id)
      _ <- deleteTodo(id)
    } yield Unit
  }

  private def deleteTodo(id: UUID) =
    Future {
      db.withConnection { implicit connection =>
        SQL"delete from todo where id = ${id}::uuid".executeUpdate()
      }
    }(ec)


  private def deleteComments(id: UUID) =
    Future {
      db.withConnection { implicit connection =>
        SQL"delete from comment where todoid = ${id}::uuid".executeUpdate()
      }
    }(ec)


  override def addComment(todoId: UUID, comment: Comment): Future[UUID] =
    Future {
      db.withConnection { implicit connection =>
        SQL"""
        insert into "comment" ( "id", "content", "todoid" ) values (
          ${comment.id}::uuid, ${comment.content}, ${todoId}::uuid
        )
      """.executeInsert(SqlParser.scalar[UUID].single)
      }
    }(ec)

  override def update(todo: Todo): Future[Unit] = {
    for {
      _ <- updateTodo(todo)
      _ <- updateComments(todo.id, todo.comments)
    } yield Unit
  }


  private def updateTodo(updated: Todo) =
    Future {
      db.withConnection { implicit connection =>
        SQL"update todo set completed=${updated.completed}, title=${updated.title} where id = ${updated.id}::uuid".executeUpdate()
      }
    }(ec)

  private def updateComments(todoId: UUID, comments: List[Comment]): Future[Unit] = {
    for {
      _ <- deleteComments(todoId)
      _ <- Future.sequence(comments.map(comment => addComment(todoId, comment)))
    } yield Unit
  }



  override def updateCompleteFlag(todoId: UUID, completed: Boolean): Future[Int] =
    Future {
      db.withConnection { implicit connection =>
        SQL"update todo set completed=${completed} where id = ${todoId}::uuid".executeUpdate()
      }
    }(ec)
}
