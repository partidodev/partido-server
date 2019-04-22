package net.fosforito.partido.model.user;

import java.time.LocalDate;

public class UserDTO {

  private String username;

  private String email;

  private String password;

  private LocalDate birthday;

  public UserDTO() {
  }

  public UserDTO(String username, String email, String password, LocalDate birthday) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.birthday = birthday;
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

  public LocalDate getBirthday() {
    return birthday;
  }

  public void setBirthday(LocalDate birthday) {
    this.birthday = birthday;
  }
}
