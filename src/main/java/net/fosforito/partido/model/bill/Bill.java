package net.fosforito.partido.model.bill;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fosforito.partido.model.group.Group;
import net.fosforito.partido.model.split.Split;
import net.fosforito.partido.model.user.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Bill {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private String description;

  @NotNull
  private BigDecimal amount;

  @NotNull
  private Date dateTime;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "group_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JsonIgnore
  private Group group;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "creator_user_id")
  private User creator;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "creditor_user_id")
  private User creditor;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "bill_id", nullable = false)
  private List<Split> splits = new ArrayList<>();

  public Bill() {
  }

  public Bill(@NotNull String description, @NotNull BigDecimal amount, @NotNull Date dateTime, Group group, User creator, User creditor, List<Split> splits) {
    this.description = description;
    this.amount = amount;
    this.dateTime = dateTime;
    this.group = group;
    this.creator = creator;
    this.creditor = creditor;
    this.splits = splits;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public Date getDateTime() {
    return dateTime;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  public Group getGroup() {
    return group;
  }

  public void setGroup(Group group) {
    this.group = group;
  }

  public User getCreator() {
    return creator;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public User getCreditor() {
    return creditor;
  }

  public void setCreditor(User creditor) {
    this.creditor = creditor;
  }

  public List<Split> getSplits() {
    return splits;
  }

  public void setSplits(List<Split> splits) {
    this.splits = splits;
  }
}

