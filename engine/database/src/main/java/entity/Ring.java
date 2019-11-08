package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rings")
public class Ring implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer id;

   @Column(nullable = false)
   private Integer partnerRingId;

   @Column(nullable = false)
   private Integer partnerCharacterId;

   @Column(nullable = false)
   private Integer itemId;

   @Column(nullable = false)
   private String partnerName;

   public Ring() {
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getPartnerRingId() {
      return partnerRingId;
   }

   public void setPartnerRingId(Integer partnerRingId) {
      this.partnerRingId = partnerRingId;
   }

   public Integer getPartnerCharacterId() {
      return partnerCharacterId;
   }

   public void setPartnerCharacterId(Integer partnerCharacterId) {
      this.partnerCharacterId = partnerCharacterId;
   }

   public Integer getItemId() {
      return itemId;
   }

   public void setItemId(Integer itemId) {
      this.itemId = itemId;
   }

   public String getPartnerName() {
      return partnerName;
   }

   public void setPartnerName(String partnerName) {
      this.partnerName = partnerName;
   }
}
