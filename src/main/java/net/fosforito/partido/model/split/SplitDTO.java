package net.fosforito.partido.model.split;

import java.math.BigDecimal;

public class SplitDTO {

  private Long debtor;

  private BigDecimal paid;

  private BigDecimal partsOfBill;

  public SplitDTO() {
  }

  public SplitDTO(Long debtor,
                  BigDecimal paid,
                  BigDecimal partsOfBill) {
    this.debtor = debtor;
    this.paid = paid;
    this.partsOfBill = partsOfBill;
  }

  public Long getDebtor() {
    return debtor;
  }

  public void setDebtor(Long debtor) {
    this.debtor = debtor;
  }

  public BigDecimal getPaid() {
    return paid;
  }

  public void setPaid(BigDecimal paid) {
    this.paid = paid;
  }

  public BigDecimal getPartsOfBill() {
    return partsOfBill;
  }

  public void setPartsOfBill(BigDecimal partsOfBill) {
    this.partsOfBill = partsOfBill;
  }
}
