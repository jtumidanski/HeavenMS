package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "storages")
public class Storage implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer storageId;

   @Column(nullable = false)
   private Integer accountId;

   @Column(nullable = false)
   private Integer world;

   @Column(nullable = false)
   private Integer slots;

   @Column(nullable = false)
   private Integer meso;

   public Storage() {
   }

   public Integer getStorageId() {
      return storageId;
   }

   public void setStorageId(Integer storageId) {
      this.storageId = storageId;
   }

   public Integer getAccountId() {
      return accountId;
   }

   public void setAccountId(Integer accountId) {
      this.accountId = accountId;
   }

   public Integer getWorld() {
      return world;
   }

   public void setWorld(Integer world) {
      this.world = world;
   }

   public Integer getSlots() {
      return slots;
   }

   public void setSlots(Integer slots) {
      this.slots = slots;
   }

   public Integer getMeso() {
      return meso;
   }

   public void setMeso(Integer meso) {
      this.meso = meso;
   }
}
