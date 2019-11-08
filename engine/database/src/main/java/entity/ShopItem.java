package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "shopitems")
public class ShopItem implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer shopItemId;

   @Column(nullable = false)
   private Integer shopId;

   @Column(nullable = false)
   private Integer itemId;

   @Column(nullable = false)
   private Integer price;

   @Column(nullable = false)
   private Integer pitch;

   @Column(nullable = false)
   private Integer position;

   public ShopItem() {
   }

   public Integer getShopItemId() {
      return shopItemId;
   }

   public void setShopItemId(Integer shopItemId) {
      this.shopItemId = shopItemId;
   }

   public Integer getShopId() {
      return shopId;
   }

   public void setShopId(Integer shopId) {
      this.shopId = shopId;
   }

   public Integer getItemId() {
      return itemId;
   }

   public void setItemId(Integer itemId) {
      this.itemId = itemId;
   }

   public Integer getPrice() {
      return price;
   }

   public void setPrice(Integer price) {
      this.price = price;
   }

   public Integer getPitch() {
      return pitch;
   }

   public void setPitch(Integer pitch) {
      this.pitch = pitch;
   }

   public Integer getPosition() {
      return position;
   }

   public void setPosition(Integer position) {
      this.position = position;
   }
}
