package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "playernpcs_equip")
public class PlayerNpcEquip implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer id;

   @Column(nullable = false)
   private Integer npcId;

   @Column(nullable = false)
   private Integer equipId;

   @Column(nullable = false)
   private Integer type;

   @Column(nullable = false, name = "equippos")
   private Integer equipPosition;

   public PlayerNpcEquip() {
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getNpcId() {
      return npcId;
   }

   public void setNpcId(Integer npcId) {
      this.npcId = npcId;
   }

   public Integer getEquipId() {
      return equipId;
   }

   public void setEquipId(Integer equipId) {
      this.equipId = equipId;
   }

   public Integer getType() {
      return type;
   }

   public void setType(Integer type) {
      this.type = type;
   }

   public Integer getEquipPosition() {
      return equipPosition;
   }

   public void setEquipPosition(Integer equipPosition) {
      this.equipPosition = equipPosition;
   }
}
