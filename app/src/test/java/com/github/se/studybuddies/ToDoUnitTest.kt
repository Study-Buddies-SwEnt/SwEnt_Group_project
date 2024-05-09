package com.github.se.studybuddies

import com.github.se.studybuddies.data.todo.ToDo
import com.github.se.studybuddies.data.todo.ToDoStatus
import java.time.LocalDate
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ToDoUnitTest {
  @Test
  fun testEmptyToDo() {
    // Act
    val date = LocalDate.now()
    val emptyToDo = ToDo("test", "test", date, "test", ToDoStatus.STARTED)
    assert(emptyToDo.uid == "test")
    assert(emptyToDo.name == "test")
    assert(emptyToDo.description == "test")
    assert(emptyToDo.dueDate == date)
    assert(emptyToDo.status == ToDoStatus.STARTED)
  }
}
