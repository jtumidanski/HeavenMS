package entity.maker;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "makercreatedata")
public class MakerCreateData implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   private Integer id;

   @Id
   private Integer itemId;

   @Column(nullable = false, name = "req_level")
   private Integer requiredLevel;

   @Column(nullable = false, name = "req_maker_level")
   private Integer requiredMakerLevel;

   @Column(nullable = false, name = "req_meso")
   private Integer requiredMeso;

   @Column(nullable = false, name = "req_item")
   private Integer requiredItem;

   @Column(nullable = false, name = "req_equip")
   private Integer requiredEquip;

   @Column(nullable = false)
   private Integer catalyst;

   @Column(nullable = false)
   private Integer quantity;

   @Column(nullable = false)
   private Integer tuc;

   public MakerCreateData() {
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getItemId() {
      return itemId;
   }

   public void setItemId(Integer itemId) {
      this.itemId = itemId;
   }

   public Integer getRequiredLevel() {
      return requiredLevel;
   }

   public void setRequiredLevel(Integer requiredLevel) {
      this.requiredLevel = requiredLevel;
   }

   public Integer getRequiredMakerLevel() {
      return requiredMakerLevel;
   }

   public void setRequiredMakerLevel(Integer requiredMakerLevel) {
      this.requiredMakerLevel = requiredMakerLevel;
   }

   public Integer getRequiredMeso() {
      return requiredMeso;
   }

   public void setRequiredMeso(Integer requiredMeso) {
      this.requiredMeso = requiredMeso;
   }

   public Integer getRequiredItem() {
      return requiredItem;
   }

   public void setRequiredItem(Integer requiredItem) {
      this.requiredItem = requiredItem;
   }

   public Integer getRequiredEquip() {
      return requiredEquip;
   }

   public void setRequiredEquip(Integer requiredEquip) {
      this.requiredEquip = requiredEquip;
   }

   public Integer getCatalyst() {
      return catalyst;
   }

   public void setCatalyst(Integer catalyst) {
      this.catalyst = catalyst;
   }

   public Integer getQuantity() {
      return quantity;
   }

   public void setQuantity(Integer quantity) {
      this.quantity = quantity;
   }

   public Integer getTuc() {
      return tuc;
   }

   public void setTuc(Integer tuc) {
      this.tuc = tuc;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }
      MakerCreateData that = (MakerCreateData) o;
      return id.equals(that.id) &&
            itemId.equals(that.itemId);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, itemId);
   }
}
