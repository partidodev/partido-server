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
  private String category;

  @NotNull
  private BigDecimal totalAmount;

  @NotNull
  private Date billingDate;

  @NotNull
  private BigDecimal parts;

  private List<SplitDTO> splits;

  public BillDTO() {
  }

  public BillDTO(@NotNull String description,
                 @NotNull String category,
                 @NotNull BigDecimal totalAmount,
                 @NotNull Date billingDate,
                 @NotNull BigDecimal parts,
                 List<SplitDTO> splits) {
    this.description = description;
    this.category = category;
    this.totalAmount = totalAmount;
    this.billingDate = billingDate;
    this.parts = parts;
    this.splits = splits;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public Date getBillingDate() {
    return billingDate;
  }

  public void setBillingDate(Date billingDate) {
    this.billingDate = billingDate;
  }

  public BigDecimal getParts() {
    return parts;
  }

  public void setParts(BigDecimal parts) {
    this.parts = parts;
  }

  public List<SplitDTO> getSplits() {
    return splits;
  }

  public void setSplits(List<SplitDTO> splits) {
    this.splits = splits;
  }
}
