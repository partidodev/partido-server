package net.fosforito.partido.model.split;

import java.math.BigDecimal;

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
  private User debtor;

  @NotNull
  private BigDecimal amount;

  public Split() {
  }

  public Split(User debtor, @NotNull BigDecimal amount) {
    this.debtor = debtor;
    this.amount = amount;
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

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
}

