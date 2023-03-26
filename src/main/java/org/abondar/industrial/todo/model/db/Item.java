package org.abondar.industrial.todo.model.db;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;



import java.util.Date;

@Entity
@Table(name = "item")
@Data
public class Item {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotNull
  private String description;

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
