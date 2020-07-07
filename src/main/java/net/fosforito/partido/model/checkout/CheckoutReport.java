package net.fosforito.partido.model.checkout;

import java.time.LocalDateTime;
import java.util.List;

public class CheckoutReport {

  private LocalDateTime timestamp;

  private List<CompensationPayment> compensationPayments;

  public CheckoutReport() {
  }

  public CheckoutReport(LocalDateTime timestamp, List<CompensationPayment> compensationPayments) {
    this.timestamp = timestamp;
    this.compensationPayments = compensationPayments;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public List<CompensationPayment> getCompensationPayments() {
    return compensationPayments;
  }

  public void setCompensationPayments(List<CompensationPayment> compensationPayments) {
    this.compensationPayments = compensationPayments;
  }
}
