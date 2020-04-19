package net.fosforito.partido.model.split;

import javax.validation.constraints.NotNull;

public class SplitDTO {

  private Long debtor;

  @NotNull
  private double paid;

  @NotNull
  private int partsOfBill;

  public SplitDTO() {
  }

  public SplitDTO(Long debtor,
                  @NotNull double paid,
                  @NotNull int partsOfBill) {
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

  public double getPaid() {
    return paid;
  }

  public void setPaid(double paid) {
    this.paid = paid;
  }

  public int getPartsOfBill() {
    return partsOfBill;
  }

  public void setPartsOfBill(int partsOfBill) {
    this.partsOfBill = partsOfBill;
  }
}
