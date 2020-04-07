package net.fosforito.partido.model.group;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import net.fosforito.partido.model.user.User;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * A representation of a Group.
 **/
@Entity
public class Group {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private String name;

  private String status;

  @NotNull
  private String currency;

  @NotNull
  private boolean joinModeActive;

  private String joinKey;

  @ManyToOne
  @NotNull
  @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
  @JsonIdentityReference(alwaysAsId=true)
  private User founder;

  @ManyToMany(fetch = FetchType.LAZY,
      cascade = {
          CascadeType.PERSIST,
          CascadeType.MERGE
      })
  @JoinTable(name = "groups_users",
      joinColumns = { @JoinColumn(name = "group_id") },
      inverseJoinColumns = { @JoinColumn(name = "user_id") })
  @JsonIgnoreProperties({ "email", "birthday" })
  private List<User> users = new ArrayList<>();

  public Group() {
  }

  public Group(
          @NotNull String name,
          String status,
          @NotNull String currency,
          @NotNull boolean joinModeActive,
          String joinKey,
          @NotNull User founder, List<User> users
  ) {
    this.name = name;
    this.status = status;
    this.currency = currency;
    this.joinModeActive = joinModeActive;
    this.joinKey = joinKey;
    this.founder = founder;
    this.users = users;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public User getFounder() {
    return founder;
  }

  public void setFounder(User founder) {
    this.founder = founder;
  }

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }
}
