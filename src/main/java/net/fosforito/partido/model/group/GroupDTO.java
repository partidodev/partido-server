package net.fosforito.partido.model.group;

import javax.validation.constraints.NotNull;

public class GroupDTO {

  @NotNull
  private String name;

  private String status;

  @NotNull
  private Long founder;

  public GroupDTO() {
  }

  public GroupDTO(String name, String status, Long founder) {
    this.name = name;
    this.status = status;
    this.founder = founder;
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

  public Long getFounder() {
    return founder;
  }

  public void setFounder(Long founder) {
    this.founder = founder;
  }
}
