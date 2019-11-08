package entity.duey;


import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "dueyitems", indexes = {
      @Index(name = "inventoryItemId", columnList = "inventoryItemId"),
      @Index(name = "packageId", columnList = "packageId")
})
public class DueyItem implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer id;

   @Column(nullable = false)
   private Integer packageId = 0;

   @Column(nullable = false)
   private Integer inventoryItemId;

   public DueyItem() {
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getPackageId() {
      return packageId;
   }

   public void setPackageId(Integer packageId) {
      this.packageId = packageId;
   }

   public Integer getInventoryItemId() {
      return inventoryItemId;
   }

   public void setInventoryItemId(Integer inventoryItemId) {
      this.inventoryItemId = inventoryItemId;
   }
}
