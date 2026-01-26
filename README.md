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

* <a href="https://stackoverflow.com/questions/13042008/java-util-nosuchelementexception-scanner-reading-user-input"><code>NoSuchElementException</code></a>

---
