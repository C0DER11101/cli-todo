# About

This is a very simple CLI-based todo-list generator that I have attempted to build while learning Exceptions, Serialization and Binary I/O.

# Commands

* Supported commands:
	* `create` &rarr; create a project/subtask/task.
	* `delete` &rarr; delete a project/subtask/task.
	* `show-tasks` &rarr; display the tasks.
	* `tick` &rarr; mark a project/subtask/task as done.
	* `edit` &rarr; edit a project/subtask/task.
	* `list` &rarr; list the supported commands.
	* `exit` &rarr; quit the program.

## `create` command

To create a project:
```
create project #<project-name>:<project-id> [:<date>]
```

A project is basically a big task which can have subtasks.
The `project-name` cannot contain white-spaces, you can put a hyphen or an underscore between words.

To create a subtask (a subtask can only be created for a project):
```
create subtask #<project-id>:#<subtask-id> <name> [:<date>]
```

To create a simple task:
```
create task #<task-id> <name> [:<date>]
```

## `delete` command

To delete a project (you cannot delete a project if it has incomplete subtasks):
```
delete project #<project-id>
```

To delete a subtask:
```
delete subtask #<project-id>:#<subtask-id>
```

To delete a task:
```
delete task #<task-id>
```

## `tick` command

To mark a project as done (a project can be marked done only if it doesn't have any undone subtasks):
```
tick project #<project-id>
```

To mark a subtask as done:
```
tick subtask #<subtask-id>
```

To mark a task as done:
```
tick task #<task-id>
```

## `edit` command
To edit a project:
```
edit project #<project-id>
```

To edit a subtask:
```
edit subtask #<subtask-id>
```

To mark a task as done:
```
edit task #<task-id>
```

# `NoSuchElementException` in `Scanner`

While working on my `edit` command I encountered this exception. I was actually using the try-with-resources statement to create a `Scanner` object and then using it to take input from the user. Because the object was created via the try-with-resources statement, it was automatically closed at the end of the block. As a result of this, the standard input stream was also closed which meant that any other `Scanner` object attempting to read from `System.in` will fail and this exception will be thrown.

* <a href="https://stackoverflow.com/questions/13042008/java-util-nosuchelementexception-scanner-reading-user-input"><code>NoSuchElementException</code></a>

# Current version

Currently, one can only create, delete, edit, tick and view  projects/subtasks/tasks. 

---
