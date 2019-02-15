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
  private BigDecimal totalAmount;

  @NotNull
  private Date dateTime;

  @NotNull
  private int parts;

  private List<SplitDTO> splits;

  public BillDTO() {
  }

  public BillDTO(@NotNull String description,
                 @NotNull BigDecimal totalAmount,
                 @NotNull Date dateTime,
                 @NotNull int parts,
                 List<SplitDTO> splits) {
    this.description = description;
    this.totalAmount = totalAmount;
    this.dateTime = dateTime;
    this.parts = parts;
    this.splits = splits;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public Date getDateTime() {
    return dateTime;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  public int getParts() {
    return parts;
  }

  public void setParts(int parts) {
    this.parts = parts;
  }

  public List<SplitDTO> getSplits() {
    return splits;
  }

  public void setSplits(List<SplitDTO> splits) {
    this.splits = splits;
  }
}
