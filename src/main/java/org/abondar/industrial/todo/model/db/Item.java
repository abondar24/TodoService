package org.abondar.industrial.todo.model.db;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "item")
@Data
public class Item {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotNull private String description;

  @NotNull
  @Enumerated(EnumType.STRING)
  private ItemStatus status;

  @NotNull
  @Column(name = "created_date")
  private Date createdAt;

  @NotNull
  @Column(name = "due_date")
  private Date dueDate;

  @Column(name = "completed_date")
  private Date completedAt;
}
