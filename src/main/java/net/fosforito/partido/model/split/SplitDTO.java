package net.fosforito.partido.model.split;

import java.math.BigDecimal;

public class SplitDTO {

  private Long debtor;

  private BigDecimal amount;

  public SplitDTO() {
  }

  public SplitDTO(Long debtor, BigDecimal amount) {
    this.debtor = debtor;
    this.amount = amount;
  }

  public Long getDebtor() {
    return debtor;
  }

  public void setDebtor(Long debtor) {
    this.debtor = debtor;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
}
