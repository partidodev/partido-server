package net.fosforito.partido.model.bill;

import net.fosforito.partido.model.split.SplitDTO;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class BillDTO {

  @NotNull
  private String description;

  @NotNull
  private BigDecimal amount;

  @NotNull
  private Date dateTime;

  @NotNull
  private Long group;

  @NotNull
  private Long creator;

  @NotNull
  private Long creditor;

  private List<SplitDTO> splits;

  public BillDTO() {
  }

  public BillDTO(@NotNull String description, @NotNull BigDecimal amount, @NotNull Date dateTime, @NotNull Long group, @NotNull Long creator, @NotNull Long creditor, List<SplitDTO> splits) {
    this.description = description;
    this.amount = amount;
    this.dateTime = dateTime;
    this.group = group;
    this.creator = creator;
    this.creditor = creditor;
    this.splits = splits;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public Date getDateTime() {
    return dateTime;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  public Long getGroup() {
    return group;
  }

  public void setGroup(Long group) {
    this.group = group;
  }

  public Long getCreator() {
    return creator;
  }

  public void setCreator(Long creator) {
    this.creator = creator;
  }

  public Long getCreditor() {
    return creditor;
  }

  public void setCreditor(Long creditor) {
    this.creditor = creditor;
  }

  public List<SplitDTO> getSplits() {
    return splits;
  }

  public void setSplits(List<SplitDTO> splits) {
    this.splits = splits;
  }
}
