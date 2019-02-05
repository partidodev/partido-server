package net.fosforito.partido.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fosforito.partido.model.role.Role;

import java.time.LocalDate;
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

  private String firstNames;

  private String lastNames;

  @NotNull
  @Column(unique = true)
  private String email;

  @JsonIgnore
  private String password;

  private LocalDate birthday;

  @JsonIgnore
  private Boolean active;

  @ManyToMany
  @JsonIgnore
  @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles;

  public User() {
  }

  public User(String firstNames, String lastNames, String email, String password, LocalDate birthday, Boolean active) {
    this.firstNames = firstNames;
    this.lastNames = lastNames;
    this.email = email;
    this.password = password;
    this.birthday = birthday;
    this.active = active;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }
}

