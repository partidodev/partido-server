package net.fosforito.partido.model.report;

import java.time.LocalDateTime;
import java.util.List;

public class Report {

  private LocalDateTime timestamp;

  private List<Balance> balances;

  public Report() {
  }

  public Report(LocalDateTime timestamp, List<Balance> balances) {
    this.timestamp = timestamp;
    this.balances = balances;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public List<Balance> getBalances() {
    return balances;
  }

  public void setBalances(List<Balance> balances) {
    this.balances = balances;
  }
}
