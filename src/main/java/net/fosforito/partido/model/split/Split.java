package net.fosforito.partido.model.split;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import net.fosforito.partido.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * A Split defines how much a debtor owes to a creditor. The Amount can be negative,
 * in this case the debtor is not really a \&quot;debtor\&quot; because he will recieve money.
 * But for naming conventions the creditor is always the \&quot;base\&quot; of a bill and the
 * debtors give or receive money from or to this owner.
 **/
@Entity
public class Split {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @OneToOne
  @JoinColumn(
      name = "debtor_user_id",
      foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT)
  )
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @JsonIdentityReference(alwaysAsId = true)
  private User debtor;

  @NotNull
  private BigDecimal paid;

  @NotNull
  private BigDecimal partsOfBill;

  public Split() {
  }

  public Split(User debtor,
               BigDecimal paid,
               BigDecimal partsOfBill) {
    this.debtor = debtor;
    this.paid = paid;
    this.partsOfBill = partsOfBill;
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

  public BigDecimal getPartsOfBill() {
    return partsOfBill;
  }

  public void setPartsOfBill(BigDecimal partsOfBill) {
    this.partsOfBill = partsOfBill;
  }
}

