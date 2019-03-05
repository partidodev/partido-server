package net.fosforito.partido.model.group;

import javax.validation.constraints.NotNull;

public class GroupDTO {

  @NotNull
  private String name;

  private String status;

  @NotNull
  private String currency;

  public GroupDTO() {
  }

  public GroupDTO(String name, String status, String currency) {
    this.name = name;
    this.status = status;
    this.currency = currency;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }
}
