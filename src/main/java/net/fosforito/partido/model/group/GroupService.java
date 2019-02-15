package net.fosforito.partido.model.group;

import net.fosforito.partido.model.bill.Bill;
import net.fosforito.partido.model.bill.BillRepository;
import net.fosforito.partido.model.report.Balance;
import net.fosforito.partido.model.report.Report;
import net.fosforito.partido.model.split.Split;
import net.fosforito.partido.model.user.User;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {

  private final BillRepository billRepository;

  private final GroupRepository groupRepository;

  @Inject
  public GroupService(BillRepository billRepository,
                      GroupRepository groupRepository) {
    this.billRepository = billRepository;
    this.groupRepository = groupRepository;
  }

  public Report createActualGroupReport(Long groupId) {
    List<Bill> bills = billRepository.findAllByGroupIdAndClosed(groupId, false);
    List<User> users = groupRepository.findById(groupId).get().getUsers();

    List<Balance> balances = new ArrayList<>();

    for (User user : users) {
      double balanceAmount = 0;
      for (Bill bill : bills) {
        for (Split split : bill.getSplits()) {
          if (split.getDebtor().equals(user)) {
            balanceAmount += ( split.getPaid() - ( bill.getTotalAmount() / bill.getParts() * split.getPartsOfBill() ) );
          }
        }
      }
      balances.add(new Balance(user, balanceAmount));
    }
    return new Report(LocalDateTime.now(), balances);
  }
}
