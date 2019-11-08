package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "trocklocations")
public class TransferRockLocation implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   @Column(name = "trockid")
   private Integer transferRockId;

   @Column(nullable = false)
   private Integer characterId;

   @Column(nullable = false)
   private Integer mapId;

   @Column(nullable = false)
   private Integer vip;

   public TransferRockLocation() {
   }

   public Integer getTransferRockId() {
      return transferRockId;
   }

   public void setTransferRockId(Integer transferRockId) {
      this.transferRockId = transferRockId;
   }

   public Integer getCharacterId() {
      return characterId;
   }

   public void setCharacterId(Integer characterId) {
      this.characterId = characterId;
   }

   public Integer getMapId() {
      return mapId;
   }

   public void setMapId(Integer mapId) {
      this.mapId = mapId;
   }

   public Integer getVip() {
      return vip;
   }

   public void setVip(Integer vip) {
      this.vip = vip;
   }
}
