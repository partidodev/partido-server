package net.fosforito.partido.model.user;

import java.time.LocalDate;

public class UserDTO {

  private String firstNames;

  private String lastNames;

  private String email;

  private String password;

  private LocalDate birthday;

  public UserDTO() {
  }

  public UserDTO(String firstNames, String lastNames, String email, String password, LocalDate birthday) {
    this.firstNames = firstNames;
    this.lastNames = lastNames;
    this.email = email;
    this.password = password;
    this.birthday = birthday;
  }

  public String getFirstNames() {
    return firstNames;
  }

  public void setFirstNames(String firstNames) {
    this.firstNames = firstNames;
  }

  public String getLastNames() {
    return lastNames;
  }

  public void setLastNames(String lastNames) {
    this.lastNames = lastNames;
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
