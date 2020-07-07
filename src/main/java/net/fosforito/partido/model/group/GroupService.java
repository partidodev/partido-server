package net.fosforito.partido.model.group;

import net.fosforito.partido.api.GroupsApi;
import net.fosforito.partido.model.bill.Bill;
import net.fosforito.partido.model.bill.BillRepository;
import net.fosforito.partido.model.checkout.CheckoutReport;
import net.fosforito.partido.model.checkout.CompensationPayment;
import net.fosforito.partido.model.report.Balance;
import net.fosforito.partido.model.report.Report;
import net.fosforito.partido.model.split.Split;
import net.fosforito.partido.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Service
public class GroupService {

  private static final Logger LOGGER = LoggerFactory.getLogger(GroupService.class);

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

  //TODO: close checked out bills and send an email to all group members
  public CheckoutReport checkoutGroup(Long groupId) {
    List<Balance> currentGroupBalances = createActualGroupReport(groupId).getBalances();
    List<Balance> positiveBalances = new LinkedList<>();
    List<Balance> negativeBalances = new LinkedList<>();

    List<CompensationPayment> compensationPayments = new LinkedList<>();

    currentGroupBalances.forEach(balance -> {
      if (balance.getBalance() > 0) {
        positiveBalances.add(balance);
      } else if (balance.getBalance() < 0) {
        negativeBalances.add(balance);
      }
      // else balance is 0 and ignored
    });

    positiveBalances.sort(Comparator.comparingDouble(Balance::getBalance));
    Collections.reverse(positiveBalances);
    negativeBalances.sort(Comparator.comparingDouble(Balance::getBalance));

    int positiveBalanceCounter = 0;
    double positiveBalanceRest = positiveBalances.get(positiveBalanceCounter).getBalance();
    double negativeBalanceRest;

    for (Balance negativeBalance : negativeBalances) {
      negativeBalanceRest = negativeBalance.getBalance();
      do {
        if (negativeBalanceRest * -1 > positiveBalanceRest) {
          compensationPayments.add(new CompensationPayment(
              negativeBalance.getUser(),
              positiveBalances.get(positiveBalanceCounter).getUser(),
              positiveBalanceRest
          ));
          negativeBalanceRest += positiveBalanceRest;
          positiveBalanceCounter++;
          positiveBalanceRest = positiveBalances.get(positiveBalanceCounter).getBalance();
        } else if (negativeBalanceRest * -1 < positiveBalanceRest) {
          compensationPayments.add(new CompensationPayment(
              negativeBalance.getUser(),
              positiveBalances.get(positiveBalanceCounter).getUser(),
              negativeBalanceRest * -1
          ));
          negativeBalanceRest = 0;
          positiveBalanceRest += negativeBalanceRest;
        } else { // negativeBalanceRest * -1 == positiveBalanceRest
          compensationPayments.add(new CompensationPayment(
              negativeBalance.getUser(),
              positiveBalances.get(positiveBalanceCounter).getUser(),
             negativeBalanceRest * -1
          ));
          negativeBalanceRest = 0;
          positiveBalanceCounter++;
          positiveBalanceRest = positiveBalances.get(positiveBalanceCounter).getBalance();
        }
      } while (negativeBalanceRest != 0);
    }

    return new CheckoutReport(LocalDateTime.now(), compensationPayments);
  }
}
