package net.fosforito.partido.model.report;

import net.fosforito.partido.model.user.User;

import java.math.BigDecimal;

public class Balance {

  private User user;

  private BigDecimal balance;

  public Balance() {
  }

  public Balance(User user, BigDecimal balance) {
    this.user = user;
    this.balance = balance;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }
}
