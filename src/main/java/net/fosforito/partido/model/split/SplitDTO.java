package net.fosforito.partido.model.split;

import java.math.BigDecimal;

public class SplitDTO {

  private Long debtor;

  private BigDecimal paid;

  private BigDecimal borrows;

  private int partsOfBill;

  private boolean main;

  public SplitDTO() {
  }

  public SplitDTO(Long debtor,
                  BigDecimal paid,
                  BigDecimal borrows,
                  int partsOfBill,
                  boolean main) {
    this.debtor = debtor;
    this.paid = paid;
    this.borrows = borrows;
    this.partsOfBill = partsOfBill;
    this.main = main;
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

  public BigDecimal getBorrows() {
    return borrows;
  }

  public void setBorrows(BigDecimal borrows) {
    this.borrows = borrows;
  }

  public int getPartsOfBill() {
    return partsOfBill;
  }

  public void setPartsOfBill(int partsOfBill) {
    this.partsOfBill = partsOfBill;
  }

  public boolean isMain() {
    return main;
  }

  public void setMain(boolean main) {
    this.main = main;
  }
}
