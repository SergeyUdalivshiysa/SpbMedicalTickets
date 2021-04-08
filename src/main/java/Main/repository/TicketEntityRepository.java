package Main.repository;

import Main.entities.TaskEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface TicketEntityRepository extends CrudRepository<TaskEntity, Integer> {

    @Query(value = "SELECT * FROM task_entity t WHERE t.user_id = :id", nativeQuery = true)
    Collection<TaskEntity> findUserRequests(@Param("id") long userId);

    @Query(value = "SELECT * FROM task_entity ORDER BY last_attempt ASC", nativeQuery = true)
    Collection<TaskEntity> findAllAndSortByLastAttempt();


}
