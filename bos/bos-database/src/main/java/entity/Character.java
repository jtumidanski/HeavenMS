package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "characters", indexes = {
      @Index(name = "accountId", columnList = "accountId")
})
public class Character implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   private Integer id;

   @Column(nullable = false)
   private Integer accountId;

   @Column(nullable = false)
   private Integer buddyCapacity = 25;

   public Character() {
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getAccountId() {
      return accountId;
   }

   public void setAccountId(Integer accountId) {
      this.accountId = accountId;
   }

   public Integer getBuddyCapacity() {
      return buddyCapacity;
   }

   public void setBuddyCapacity(Integer buddyCapacity) {
      this.buddyCapacity = buddyCapacity;
   }
}
