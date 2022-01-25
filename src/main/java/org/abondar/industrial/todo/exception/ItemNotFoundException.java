package org.abondar.industrial.todo.exception;

public class ItemNotFoundException extends RuntimeException {

  public ItemNotFoundException() {
    super("Item not found by provided id");
  }
}
