package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "specialcashitems")
public class SpecialCashItem implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer id;

   @Column(nullable = false)
   private Integer sn;

   @Column(nullable = false)
   private Integer modifier;

   @Column(nullable = false)
   private Integer info;

   public SpecialCashItem() {
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getSn() {
      return sn;
   }

   public void setSn(Integer sn) {
      this.sn = sn;
   }

   public Integer getModifier() {
      return modifier;
   }

   public void setModifier(Integer modifier) {
      this.modifier = modifier;
   }

   public Integer getInfo() {
      return info;
   }

   public void setInfo(Integer info) {
      this.info = info;
   }
}
