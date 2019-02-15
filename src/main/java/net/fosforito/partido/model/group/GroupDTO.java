package net.fosforito.partido.model.group;

import javax.validation.constraints.NotNull;

public class GroupDTO {

  @NotNull
  private String name;

  private String status;

  public GroupDTO() {
  }

  public GroupDTO(String name, String status) {
    this.name = name;
    this.status = status;
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
}
