package entity.nx;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "nxcode_items")
public class NxCodeItem implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer id;

   @Column(nullable = false)
   private Integer codeId;

   @Column(nullable = false)
   private Integer type = 5;

   @Column(nullable = false)
   private Integer item = 4000000;

   @Column(nullable = false)
   private Integer quantity = 1;

   public NxCodeItem() {
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getCodeId() {
      return codeId;
   }

   public void setCodeId(Integer codeId) {
      this.codeId = codeId;
   }

   public Integer getType() {
      return type;
   }

   public void setType(Integer type) {
      this.type = type;
   }

   public Integer getItem() {
      return item;
   }

   public void setItem(Integer item) {
      this.item = item;
   }

   public Integer getQuantity() {
      return quantity;
   }

   public void setQuantity(Integer quantity) {
      this.quantity = quantity;
   }
}
