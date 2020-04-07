package net.fosforito.partido.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fosforito.partido.model.role.Role;

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

  private String username;

  @NotNull
  @Column(unique = true)
  private String email;

  @JsonIgnore
  private String password;

  @JsonIgnore
  private Boolean active;

  @ManyToMany
  @JsonIgnore
  @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles;

  public User() {
  }

  public User(String username, String email, String password, Boolean active) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.active = active;
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

