package net.fosforito.partido.model.group;

import net.fosforito.partido.model.checkout.CheckoutReport;
import net.fosforito.partido.model.group.GroupService;
import net.fosforito.partido.model.report.Balance;
import net.fosforito.partido.model.report.Report;
import net.fosforito.partido.model.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {

  @Mock
  GroupService groupService;

  @Test
  public void checkoutGroupCaseOne() {
    User userA = new User("A", "a@test.de", "12345678", new Date(), true, "");
    User userB = new User("B", "b@test.de", "12345678", new Date(), true, "");
    User userC = new User("C", "c@test.de", "12345678", new Date(), true, "");
    User userD = new User("D", "d@test.de", "12345678", new Date(), true, "");

    List<Balance> currentGroupBalances = new LinkedList<>();
    currentGroupBalances.add(new Balance(userA, BigDecimal.valueOf(150)));
    currentGroupBalances.add(new Balance(userB, BigDecimal.valueOf(50)));
    currentGroupBalances.add(new Balance(userC, BigDecimal.valueOf(-40)));
    currentGroupBalances.add(new Balance(userD, BigDecimal.valueOf(-160)));

    Mockito.when(groupService.createActualGroupReport(1L))
        .thenReturn(new Report(LocalDateTime.now(), currentGroupBalances));
    Mockito.when(groupService.checkoutGroup(1L)).thenCallRealMethod();

    CheckoutReport checkoutReport = groupService.checkoutGroup(1L);

    Assertions.assertEquals(3, checkoutReport.getCompensationPayments().size());

    Assertions.assertEquals("D", checkoutReport.getCompensationPayments().get(0).getFromUser().getUsername());
    Assertions.assertEquals("A", checkoutReport.getCompensationPayments().get(0).getToUser().getUsername());
    Assertions.assertEquals(BigDecimal.valueOf(150), checkoutReport.getCompensationPayments().get(0).getAmount());

    Assertions.assertEquals("D", checkoutReport.getCompensationPayments().get(1).getFromUser().getUsername());
    Assertions.assertEquals("B", checkoutReport.getCompensationPayments().get(1).getToUser().getUsername());
    Assertions.assertEquals(BigDecimal.valueOf(10), checkoutReport.getCompensationPayments().get(1).getAmount());

    Assertions.assertEquals("C", checkoutReport.getCompensationPayments().get(2).getFromUser().getUsername());
    Assertions.assertEquals("B", checkoutReport.getCompensationPayments().get(2).getToUser().getUsername());
    Assertions.assertEquals(BigDecimal.valueOf(40), checkoutReport.getCompensationPayments().get(2).getAmount());
  }

  @Test
  public void checkoutGroupCaseTwo() {
    User userA = new User("A", "a@test.de", "12345678", new Date(), true, "");
    User userB = new User("B", "b@test.de", "12345678", new Date(), true, "");
    User userC = new User("C", "c@test.de", "12345678", new Date(), true, "");
    User userD = new User("D", "d@test.de", "12345678", new Date(), true, "");
    User userE = new User("E", "e@test.de", "12345678", new Date(), true, "");

    List<Balance> currentGroupBalances = new LinkedList<>();
    currentGroupBalances.add(new Balance(userA, BigDecimal.valueOf(154.99)));
    currentGroupBalances.add(new Balance(userB, BigDecimal.valueOf(38.53)));
    currentGroupBalances.add(new Balance(userC, BigDecimal.valueOf(78.11)));
    currentGroupBalances.add(new Balance(userD, BigDecimal.valueOf(-270.63)));
    currentGroupBalances.add(new Balance(userE, BigDecimal.valueOf(-1)));

    Mockito.when(groupService.createActualGroupReport(1L))
        .thenReturn(new Report(LocalDateTime.now(), currentGroupBalances));
    Mockito.when(groupService.checkoutGroup(1L)).thenCallRealMethod();

    CheckoutReport checkoutReport = groupService.checkoutGroup(1L);

    Assertions.assertEquals(4, checkoutReport.getCompensationPayments().size());

    Assertions.assertEquals("D", checkoutReport.getCompensationPayments().get(0).getFromUser().getUsername());
    Assertions.assertEquals("A", checkoutReport.getCompensationPayments().get(0).getToUser().getUsername());
    Assertions.assertEquals(BigDecimal.valueOf(154.99), checkoutReport.getCompensationPayments().get(0).getAmount());

    Assertions.assertEquals("D", checkoutReport.getCompensationPayments().get(1).getFromUser().getUsername());
    Assertions.assertEquals("C", checkoutReport.getCompensationPayments().get(1).getToUser().getUsername());
    Assertions.assertEquals(BigDecimal.valueOf(78.11), checkoutReport.getCompensationPayments().get(1).getAmount());

    Assertions.assertEquals("D", checkoutReport.getCompensationPayments().get(2).getFromUser().getUsername());
    Assertions.assertEquals("B", checkoutReport.getCompensationPayments().get(2).getToUser().getUsername());
    Assertions.assertEquals(BigDecimal.valueOf(37.53), checkoutReport.getCompensationPayments().get(2).getAmount());

    Assertions.assertEquals("E", checkoutReport.getCompensationPayments().get(3).getFromUser().getUsername());
    Assertions.assertEquals("B", checkoutReport.getCompensationPayments().get(3).getToUser().getUsername());
    Assertions.assertEquals(BigDecimal.ONE, checkoutReport.getCompensationPayments().get(3).getAmount());
  }

  @Test
  public void checkoutGroupCaseThree() {
    User userA = new User("A", "a@test.de", "12345678", new Date(), true, "");
    User userB = new User("B", "b@test.de", "12345678", new Date(), true, "");

    List<Balance> currentGroupBalances = new LinkedList<>();
    currentGroupBalances.add(new Balance(userA, BigDecimal.valueOf(150)));
    currentGroupBalances.add(new Balance(userB, BigDecimal.valueOf(-150)));

    Mockito.when(groupService.createActualGroupReport(1L))
        .thenReturn(new Report(LocalDateTime.now(), currentGroupBalances));
    Mockito.when(groupService.checkoutGroup(1L)).thenCallRealMethod();

    CheckoutReport checkoutReport = groupService.checkoutGroup(1L);

    Assertions.assertEquals(1, checkoutReport.getCompensationPayments().size());
    Assertions.assertEquals("B", checkoutReport.getCompensationPayments().get(0).getFromUser().getUsername());
    Assertions.assertEquals("A", checkoutReport.getCompensationPayments().get(0).getToUser().getUsername());
    Assertions.assertEquals(BigDecimal.valueOf(150), checkoutReport.getCompensationPayments().get(0).getAmount());
  }
}
