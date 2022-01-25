package org.abondar.industrial.todo.exception;

public class ItemNotFoundException extends RuntimeException {

  public ItemNotFoundException() {
    super(MessageUtil.ITEM_NOT_FOUND);
  }
}
