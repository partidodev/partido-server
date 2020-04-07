package net.fosforito.partido.model.group;

import javax.validation.constraints.NotNull;

public class GroupDTO {

  @NotNull
  private String name;

  private String status;

  @NotNull
  private String currency;

  private boolean joinModeActive;

  private String joinKey;

  public GroupDTO() {
  }

  public GroupDTO(String name, String status, String currency, boolean joinModeActive, String joinKey) {
    this.name = name;
    this.status = status;
    this.currency = currency;
    this.joinModeActive = joinModeActive;
    this.joinKey = joinKey;
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

  public boolean isJoinModeActive() {
    return joinModeActive;
  }

  public void setJoinModeActive(boolean joinModeActive) {
    this.joinModeActive = joinModeActive;
  }

  public String getJoinKey() {
    return joinKey;
  }

  public void setJoinKey(String joinKey) {
    this.joinKey = joinKey;
  }
}
