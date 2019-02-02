package net.fosforito.partido.model.bill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

  List<Bill> findAllByGroupId(Long groupId);

  List<Bill> findAllOpenByGroupId(Long groupId);
}
