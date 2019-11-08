package entity.family;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "family_entitlement", indexes = {
      @Index(name = "characterId", columnList = "charid")
})
public class FamilyEntitlement implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer id;

   @Column(nullable = false, name = "charid")
   private Integer characterId;

   @Column(nullable = false)
   private Integer entitlementId;

   @Column(nullable = false)
   private Long timestamp;

   public FamilyEntitlement() {
      timestamp = 0L;
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getCharacterId() {
      return characterId;
   }

   public void setCharacterId(Integer characterId) {
      this.characterId = characterId;
   }

   public Integer getEntitlementId() {
      return entitlementId;
   }

   public void setEntitlementId(Integer entitlementId) {
      this.entitlementId = entitlementId;
   }

   public Long getTimestamp() {
      return timestamp;
   }

   public void setTimestamp(Long timestamp) {
      this.timestamp = timestamp;
   }
}
