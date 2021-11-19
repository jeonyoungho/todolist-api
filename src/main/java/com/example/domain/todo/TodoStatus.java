package com.example.domain.todo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TodoStatus {
   UNCOMPLETED, COMPLETED;
}
