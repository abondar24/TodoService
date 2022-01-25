package org.abondar.industrial.todo.dao;

import org.abondar.industrial.todo.model.db.Item;
import org.abondar.industrial.todo.model.db.ItemNotDone;
import org.abondar.industrial.todo.model.db.ItemStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

  @Modifying(clearAutomatically = true)
  @Query("update Item it set it.description=:description where it.id=:id")
  void updateDescription(
      @Param(value = "id") long id, @Param(value = "description") String description);

  @Modifying(clearAutomatically = true)
  @Query("update Item  it set it.status= :status where it.id= :id")
  void updateStatus(@Param(value = "id") long id, @Param(value = "status") ItemStatus status);

  @Modifying(clearAutomatically = true)
  @Query("update Item  it set it.completedAt= :completed where it.id= :id")
  void updateCompleted(@Param(value = "id") long id, @Param(value = "completed") Date completed);

  List<ItemNotDone> findAllByStatus(ItemStatus status, Pageable pageable);

}
