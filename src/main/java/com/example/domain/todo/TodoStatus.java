package com.example.domain.todo;

public enum TodoStatus {
   UNCOMPLETED, COMPLETED;

   public Boolean isEqualTo(TodoStatus status) {
      return this.name() == status.name();
   }
}
