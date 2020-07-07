package net.fosforito.partido.model.report;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import net.fosforito.partido.model.user.User;

import java.math.BigDecimal;

public class Balance implements Comparable<Balance> {

  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @JsonIdentityReference(alwaysAsId = true)
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

  @Override
  public int compareTo(Balance o) {
    return this.getBalance().compareTo(o.getBalance());
  }
}
