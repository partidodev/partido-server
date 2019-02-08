package net.fosforito.partido.model.split;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import net.fosforito.partido.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * A Split defines how much a debtor owes to a creditor. The Amount can be negative,
 * in this case the debtor is not really a \&quot;debtor\&quot; because he will recieve money.
 * But for naming conventions the creditor is always the \&quot;base\&quot; of a bill and the
 * debtors give or recieve money from or to this owner.
 **/
@Entity
public class Split {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "debtor_user_id")
  @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
  @JsonIdentityReference(alwaysAsId=true)
  private User debtor;

  @NotNull
  private BigDecimal paid;

  @NotNull
  private BigDecimal borrows;

  @NotNull
  private int partsOfBill;

  @NotNull
  private boolean main;

  public Split() {
  }

  public Split(User debtor,
               @NotNull BigDecimal paid,
               @NotNull BigDecimal borrows,
               @NotNull int partsOfBill,
               @NotNull boolean main) {
    this.debtor = debtor;
    this.paid = paid;
    this.borrows = borrows;
    this.partsOfBill = partsOfBill;
    this.main = main;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getDebtor() {
    return debtor;
  }

  public void setDebtor(User debtor) {
    this.debtor = debtor;
  }

  public BigDecimal getPaid() {
    return paid;
  }

  public void setPaid(BigDecimal paid) {
    this.paid = paid;
  }

  public BigDecimal getBorrows() {
    return borrows;
  }

  public void setBorrows(BigDecimal borrows) {
    this.borrows = borrows;
  }

  public int getPartsOfBill() {
    return partsOfBill;
  }

  public void setPartsOfBill(int partsOfBill) {
    this.partsOfBill = partsOfBill;
  }

  public boolean isMain() {
    return main;
  }

  public void setMain(boolean main) {
    this.main = main;
  }
}

