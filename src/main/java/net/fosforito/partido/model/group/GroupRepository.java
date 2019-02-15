package net.fosforito.partido.model.group;

import net.fosforito.partido.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

  List<Group> findAllByUsersIsContaining(User user);
}
