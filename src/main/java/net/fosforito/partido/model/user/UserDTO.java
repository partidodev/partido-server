package net.fosforito.partido.model.user;

import javax.validation.constraints.NotNull;

public class UserDTO {

  @NotNull
  private String username;

  @NotNull
  private String email;

  @NotNull
  private String password;

  private String newPassword;

  public UserDTO() {
  }

  public UserDTO(@NotNull String username, @NotNull String email, @NotNull String password, String newPassword) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.newPassword = newPassword;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
}
