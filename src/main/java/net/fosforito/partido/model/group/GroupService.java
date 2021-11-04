package net.fosforito.partido.model.group;

import net.fosforito.partido.model.bill.Bill;
import net.fosforito.partido.model.bill.BillRepository;
import net.fosforito.partido.model.checkout.CheckoutReport;
import net.fosforito.partido.model.checkout.CompensationPayment;
import net.fosforito.partido.model.report.Balance;
import net.fosforito.partido.model.report.Report;
import net.fosforito.partido.model.split.Split;
import net.fosforito.partido.model.user.User;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
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

  /**
   * Creates an overview list with current balances of all members in a specific group.
   * Open bills can be closed if the corresponding parameter is set to true. This is needed
   * for group checkout processes.
   * @param groupId ID of the group to get balances for
   * @return Report with balances
   */
  public Report createActualGroupReport(Long groupId) {
    List<Bill> bills = billRepository.findAllByGroupIdAndClosed(groupId, false);
    List<User> users = groupRepository.findById(groupId).get().getUsers();

    List<Balance> balances = new ArrayList<>();

    for (User user : users) {
      BigDecimal balanceAmount = BigDecimal.ZERO;
      for (Bill bill : bills) {
        for (Split split : bill.getSplits()) {
          if (split.getDebtor().equals(user)) {
            balanceAmount = balanceAmount.add(
                split.getPaid().subtract(
                    bill.getTotalAmount()
                        .divide(bill.getParts(), 5, RoundingMode.HALF_UP)
                        .multiply(split.getPartsOfBill())
                )
            );
          }
        }
      }
      balances.add(new Balance(user, balanceAmount));
    }
    return new Report(LocalDateTime.now(), balances);
  }

  /**
   * Used to close all open bills in a group after a checkout.
   * @param groupId Id of the group which bills should be closed
   */
  public void closeOpenBills(Long groupId) {
    List<Bill> bills = billRepository.findAllByGroupIdAndClosed(groupId, false);
      for (Bill bill : bills) {
        bill.setClosed(true);
        billRepository.save(bill);
      }
    }

  /**
   * Checkout a specific group, close all current bills of the group and notify all group members
   * about the checkout with tips on how to compensate all balances.
   * @param groupId ID of the group to check out
   * @return Report with checkout and compensation payment information
   */
  public CheckoutReport checkoutGroup(Long groupId) {
    List<Balance> currentGroupBalances = createActualGroupReport(groupId).getBalances();
    List<Balance> positiveBalances = new LinkedList<>();
    List<Balance> negativeBalances = new LinkedList<>();
    List<CompensationPayment> compensationPayments = new LinkedList<>();

    currentGroupBalances.forEach(balance -> {
      if (balance.getBalance().compareTo(BigDecimal.ZERO) > 0) {
        positiveBalances.add(balance);
      } else if (balance.getBalance().compareTo(BigDecimal.ZERO) < 0) {
        negativeBalances.add(balance);
      }
      // else balance is 0 and ignored
    });

    // Checkout group only if balances are not all zero.
    // If there is at least 1 positive balance, there MUST be at
    // least 1 negative balance and vice versa.
    if (positiveBalances.size() == 0 || negativeBalances.size() == 0) {
      return null;
    }

    Collections.sort(positiveBalances);
    Collections.reverse(positiveBalances); //descending (highest amounts first)
    Collections.sort(negativeBalances); //ascending (lowest amount first)

    int positiveBalanceCounter = 0;
    BigDecimal positiveBalanceRest = positiveBalances.get(positiveBalanceCounter).getBalance();
    BigDecimal negativeBalanceRest;

    for (Balance negativeBalance : negativeBalances) {
      negativeBalanceRest = negativeBalance.getBalance();
      do {
        if (negativeBalanceRest.multiply(BigDecimal.valueOf(-1)).compareTo(positiveBalanceRest) > 0) {
          compensationPayments.add(new CompensationPayment(
              negativeBalance.getUser(),
              positiveBalances.get(positiveBalanceCounter).getUser(),
              positiveBalanceRest
          ));
          negativeBalanceRest = negativeBalanceRest.add(positiveBalanceRest);
          positiveBalanceCounter++;
          positiveBalanceRest = positiveBalances.get(positiveBalanceCounter).getBalance();
        } else if (negativeBalanceRest.multiply(BigDecimal.valueOf(-1)).compareTo(positiveBalanceRest) < 0) {
          compensationPayments.add(new CompensationPayment(
              negativeBalance.getUser(),
              positiveBalances.get(positiveBalanceCounter).getUser(),
              negativeBalanceRest.multiply(BigDecimal.valueOf(-1))
          ));
          negativeBalanceRest = BigDecimal.ZERO;
          positiveBalanceRest = positiveBalanceRest.add(negativeBalanceRest);
        } else { // negativeBalanceRest * -1 == positiveBalanceRest
          compensationPayments.add(new CompensationPayment(
              negativeBalance.getUser(),
              positiveBalances.get(positiveBalanceCounter).getUser(),
             negativeBalanceRest.multiply(BigDecimal.valueOf(-1))
          ));
          negativeBalanceRest = BigDecimal.ZERO;
        }
      } while (negativeBalanceRest.compareTo(BigDecimal.ZERO) != 0);
    }

    // Finally close all open bills that where taken
    // into consideration by this checkout
    closeOpenBills(groupId);

    return new CheckoutReport(LocalDateTime.now(), compensationPayments);
  }
}
