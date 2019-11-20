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
@Table(name = "inventoryitems", indexes = {
      @Index(name = "charId", columnList = "characterId")
})
public class InventoryItem implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer inventoryItemId;

   @Column(nullable = false)
   private Integer type;

   @Column(nullable = false)
   private Integer characterId;

   @Column(nullable = false)
   private Integer accountId;

   @Column(nullable = false)
   private Integer itemId;

   @Column(nullable = false)
   private Integer inventoryType = 0;

   @Column(nullable = false)
   private Integer position;

   @Column(nullable = false)
   private Integer quantity;

   @Column(nullable = false)
   private String owner;

   @Column(nullable = false)
   private Integer petId = -1;

   @Column(nullable = false)
   private Integer flag;

   @Column(nullable = false)
   private Long expiration = -1L;

   @Column(nullable = false)
   private String giftFrom;

   public InventoryItem() {
   }

   public Integer getInventoryItemId() {
      return inventoryItemId;
   }

   public void setInventoryItemId(Integer inventoryItemId) {
      this.inventoryItemId = inventoryItemId;
   }

   public Integer getType() {
      return type;
   }

   public void setType(Integer type) {
      this.type = type;
   }

   public Integer getCharacterId() {
      return characterId;
   }

   public void setCharacterId(Integer characterId) {
      this.characterId = characterId;
   }

   public Integer getAccountId() {
      return accountId;
   }

   public void setAccountId(Integer accountId) {
      this.accountId = accountId;
   }

   public Integer getItemId() {
      return itemId;
   }

   public void setItemId(Integer itemId) {
      this.itemId = itemId;
   }

   public Integer getInventoryType() {
      return inventoryType;
   }

   public void setInventoryType(Integer inventoryType) {
      this.inventoryType = inventoryType;
   }

   public Integer getPosition() {
      return position;
   }

   public void setPosition(Integer position) {
      this.position = position;
   }

   public Integer getQuantity() {
      return quantity;
   }

   public void setQuantity(Integer quantity) {
      this.quantity = quantity;
   }

   public String getOwner() {
      return owner;
   }

   public void setOwner(String owner) {
      this.owner = owner;
   }

   public Integer getPetId() {
      return petId;
   }

   public void setPetId(Integer petId) {
      this.petId = petId;
   }

   public Integer getFlag() {
      return flag;
   }

   public void setFlag(Integer flag) {
      this.flag = flag;
   }

   public Long getExpiration() {
      return expiration;
   }

   public void setExpiration(Long expiration) {
      this.expiration = expiration;
   }

   public String getGiftFrom() {
      return giftFrom;
   }

   public void setGiftFrom(String giftFrom) {
      this.giftFrom = giftFrom;
   }
}
