package com.todos.model

import java.util.UUID

case class Todo(
    id: UUID,
    title: String,
    completed: Boolean,
    comments: List[Comment]
)

case class Comment(
    id: UUID,
    content: String
)
