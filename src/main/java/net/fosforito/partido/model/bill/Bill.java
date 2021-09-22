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
public class Bill implements Comparable<Bill> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private String description;

  @NotNull
  private String category;

  private BigDecimal totalAmount;

  @NotNull
  private Date billingDate;

  @NotNull
  private Date creationDate;

  @NotNull
  private BigDecimal parts;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "group_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JsonIgnore
  private Group group;

  @OneToOne
  @JoinColumn(
      name = "creator_user_id",
      foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT)
  )
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
              @NotNull String category,
              BigDecimal totalAmount,
              @NotNull BigDecimal parts,
              @NotNull Date billingDate,
              @NotNull Date creationDate,
              Group group,
              User creator,
              List<Split> splits) {
    this.description = description;
    this.category = category;
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

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public BigDecimal getParts() {
    return parts;
  }

  public void setParts(BigDecimal parts) {
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
    this.splits.clear();
    this.splits.addAll(splits);
  }

  public boolean isClosed() {
    return closed;
  }

  public void setClosed(boolean closed) {
    this.closed = closed;
  }

  @Override
  public int compareTo(Bill bill) {
    return creationDate.compareTo((bill).getCreationDate());
  }
}

