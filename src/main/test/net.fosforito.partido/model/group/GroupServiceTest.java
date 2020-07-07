package net.fosforito.partido.model.group;

import net.fosforito.partido.model.checkout.CheckoutReport;
import net.fosforito.partido.model.report.Balance;
import net.fosforito.partido.model.report.Report;
import net.fosforito.partido.model.user.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
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
    currentGroupBalances.add(new Balance(userA, 150));
    currentGroupBalances.add(new Balance(userB, 50));
    currentGroupBalances.add(new Balance(userC, -40));
    currentGroupBalances.add(new Balance(userD, -160));

    Mockito.when(groupService.createActualGroupReport(1L))
        .thenReturn(new Report(LocalDateTime.now(), currentGroupBalances));
    Mockito.when(groupService.checkoutGroup(1L)).thenCallRealMethod();

    CheckoutReport checkoutReport = groupService.checkoutGroup(1L);

    Assert.assertEquals(3, checkoutReport.getCompensationPayments().size());

    Assert.assertEquals("D", checkoutReport.getCompensationPayments().get(0).getFromUser().getUsername());
    Assert.assertEquals("A", checkoutReport.getCompensationPayments().get(0).getToUser().getUsername());
    Assert.assertEquals(150, checkoutReport.getCompensationPayments().get(0).getAmount(), 0.0);

    Assert.assertEquals("D", checkoutReport.getCompensationPayments().get(1).getFromUser().getUsername());
    Assert.assertEquals("B", checkoutReport.getCompensationPayments().get(1).getToUser().getUsername());
    Assert.assertEquals(10, checkoutReport.getCompensationPayments().get(1).getAmount(), 0.0);

    Assert.assertEquals("C", checkoutReport.getCompensationPayments().get(2).getFromUser().getUsername());
    Assert.assertEquals("B", checkoutReport.getCompensationPayments().get(2).getToUser().getUsername());
    Assert.assertEquals(40, checkoutReport.getCompensationPayments().get(2).getAmount(), 0.0);
  }

  @Test
  public void checkoutGroupCaseTwo() {
    User userA = new User("A", "a@test.de", "12345678", new Date(), true, "");
    User userB = new User("B", "b@test.de", "12345678", new Date(), true, "");
    User userC = new User("C", "c@test.de", "12345678", new Date(), true, "");
    User userD = new User("D", "d@test.de", "12345678", new Date(), true, "");
    User userE = new User("E", "e@test.de", "12345678", new Date(), true, "");

    List<Balance> currentGroupBalances = new LinkedList<>();
    currentGroupBalances.add(new Balance(userA, 154.99));
    currentGroupBalances.add(new Balance(userB, 38.53));
    currentGroupBalances.add(new Balance(userC, 78.11));
    currentGroupBalances.add(new Balance(userD, -270.63));
    currentGroupBalances.add(new Balance(userE, -1));

    Mockito.when(groupService.createActualGroupReport(1L))
        .thenReturn(new Report(LocalDateTime.now(), currentGroupBalances));
    Mockito.when(groupService.checkoutGroup(1L)).thenCallRealMethod();

    CheckoutReport checkoutReport = groupService.checkoutGroup(1L);

    Assert.assertEquals(4, checkoutReport.getCompensationPayments().size());

    Assert.assertEquals("D", checkoutReport.getCompensationPayments().get(0).getFromUser().getUsername());
    Assert.assertEquals("A", checkoutReport.getCompensationPayments().get(0).getToUser().getUsername());
    Assert.assertEquals(154.99, checkoutReport.getCompensationPayments().get(0).getAmount(), 0.01);

    Assert.assertEquals("D", checkoutReport.getCompensationPayments().get(1).getFromUser().getUsername());
    Assert.assertEquals("C", checkoutReport.getCompensationPayments().get(1).getToUser().getUsername());
    Assert.assertEquals(78.11, checkoutReport.getCompensationPayments().get(1).getAmount(), 0.01);

    Assert.assertEquals("D", checkoutReport.getCompensationPayments().get(2).getFromUser().getUsername());
    Assert.assertEquals("B", checkoutReport.getCompensationPayments().get(2).getToUser().getUsername());
    Assert.assertEquals(37.53, checkoutReport.getCompensationPayments().get(2).getAmount(), 0.01);

    Assert.assertEquals("E", checkoutReport.getCompensationPayments().get(3).getFromUser().getUsername());
    Assert.assertEquals("B", checkoutReport.getCompensationPayments().get(3).getToUser().getUsername());
    Assert.assertEquals(1, checkoutReport.getCompensationPayments().get(3).getAmount(), 0.01);
  }
}
