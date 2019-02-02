package net.fosforito.partido.model.report;

import java.time.LocalDate;
import java.util.List;

public class Report {

  private LocalDate timestamp;

  private List<Balance> balances;

  public Report() {
  }

  public Report(LocalDate timestamp, List<Balance> balances) {
    this.timestamp = timestamp;
    this.balances = balances;
  }

  public LocalDate getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDate timestamp) {
    this.timestamp = timestamp;
  }

  public List<Balance> getBalances() {
    return balances;
  }

  public void setBalances(List<Balance> balances) {
    this.balances = balances;
  }
}
