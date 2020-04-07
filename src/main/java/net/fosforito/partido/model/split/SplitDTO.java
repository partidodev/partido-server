package net.fosforito.partido.model.split;

public class SplitDTO {

  private Long debtor;

  private double paid;

  private int partsOfBill;

  public SplitDTO() {
  }

  public SplitDTO(Long debtor,
                  double paid,
                  int partsOfBill) {
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
