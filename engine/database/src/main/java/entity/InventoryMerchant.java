package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "inventorymerchant", indexes = {
      @Index(name = "inventoryItemId", columnList = "inventoryItemId")
})
public class InventoryMerchant implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer inventoryMerchantId;

   @Column(nullable = false)
   private Integer inventoryItemId;

   @Column
   private Integer characterId;

   @Column(nullable = false)
   private Short bundles;

   public InventoryMerchant() {
   }

   public Integer getInventoryMerchantId() {
      return inventoryMerchantId;
   }

   public void setInventoryMerchantId(Integer inventoryMerchantId) {
      this.inventoryMerchantId = inventoryMerchantId;
   }

   public Integer getInventoryItemId() {
      return inventoryItemId;
   }

   public void setInventoryItemId(Integer inventoryItemId) {
      this.inventoryItemId = inventoryItemId;
   }

   public Integer getCharacterId() {
      return characterId;
   }

   public void setCharacterId(Integer characterId) {
      this.characterId = characterId;
   }

   public Short getBundles() {
      return bundles;
   }

   public void setBundles(Short bundles) {
      this.bundles = bundles;
   }
}
