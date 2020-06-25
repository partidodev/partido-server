package net.fosforito.partido.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fosforito.partido.model.role.Role;

import java.util.Date;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * A representation of an User.
 **/
@Entity
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private String username;

  @NotNull
  @Column(unique = true)
  private String email;

  @NotNull
  @JsonIgnore
  private String password;

  @NotNull
  @JsonIgnore
  private Date registrationDate;

  @JsonIgnore
  private boolean emailVerified;

  @JsonIgnore
  private String emailVerificationCode;

  @ManyToMany
  @JsonIgnore
  private Set<Role> roles;

  public User() {
  }

  public User(String username, String email, String password, Date registrationDate, boolean emailVerified, String emailVerificationCode) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.registrationDate = registrationDate;
    this.emailVerified = emailVerified;
    this.emailVerificationCode = emailVerificationCode;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public Date getRegistrationDate() {
    return registrationDate;
  }

  public void setRegistrationDate(Date registrationDate) {
    this.registrationDate = registrationDate;
  }

  public boolean isEmailVerified() {
    return emailVerified;
  }

  public void setEmailVerified(boolean emailVerified) {
    this.emailVerified = emailVerified;
  }

  public String getEmailVerificationCode() {
    return emailVerificationCode;
  }

  public void setEmailVerificationCode(String emailVerificationCode) {
    this.emailVerificationCode = emailVerificationCode;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }
}

