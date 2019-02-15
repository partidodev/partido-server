package net.fosforito.partido.model.bill;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import net.fosforito.partido.model.group.Group;
import net.fosforito.partido.model.split.Split;
import net.fosforito.partido.model.user.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Bill implements Comparable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private String description;

  @NotNull
  private double totalAmount;

  @NotNull
  private Date billingDate;

  @NotNull
  private Date creationDate;

  @NotNull
  private int parts;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "group_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JsonIgnore
  private Group group;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "creator_user_id")
  @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
  @JsonIdentityReference(alwaysAsId=true)
  private User creator;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "bill_id", nullable = false)
  private List<Split> splits = new ArrayList<>();

  private boolean closed;

  public Bill() {
  }

  public Bill(@NotNull String description,
              @NotNull double totalAmount,
              @NotNull int parts,
              @NotNull Date billingDate,
              @NotNull Date creationDate,
              Group group,
              User creator,
              List<Split> splits) {
    this.description = description;
    this.totalAmount = totalAmount;
    this.parts = parts;
    this.billingDate = billingDate;
    this.creationDate = creationDate;
    this.group = group;
    this.creator = creator;
    this.splits = splits;
    this.closed = false;
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

  public double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public int getParts() {
    return parts;
  }

  public void setParts(int parts) {
    this.parts = parts;
  }

  public Date getBillingDate() {
    return billingDate;
  }

  public void setBillingDate(Date billingDate) {
    this.billingDate = billingDate;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
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

  public List<Split> getSplits() {
    return splits;
  }

  public void setSplits(List<Split> splits) {
    this.splits = splits;
  }

  public boolean isClosed() {
    return closed;
  }

  public void setClosed(boolean closed) {
    this.closed = closed;
  }

  @Override
  public int compareTo(Object o) {
    return creationDate.compareTo(((Bill)o).getCreationDate());
  }
}

