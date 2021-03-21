package Main.repository;

import Main.entities.TaskEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketEntityRepository extends CrudRepository<TaskEntity, Integer> {

}
