package com.todos.api

import java.util.UUID

case class CreateTodoCommand(
    title: String
)

case class EditCommentCommand(
    id: UUID,
    content: String
)

case class EditTodoCommand(
    id: UUID,
    title: String,
    completed: Boolean,
    comments: List[EditCommentCommand]
)

case class UpdateCompleteFlagCommand(
    isCompleted: Boolean
)
