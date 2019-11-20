package entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "famelog", indexes = {
      @Index(name = "characterId", columnList = "characterId")
})
public class FameLog implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer fameLogId;

   @Column(nullable = false)
   private Integer characterId;

   @Column(nullable = false)
   private Integer characterIdTo;

   @Column(nullable = false)
   private Timestamp createDate;

   public FameLog() {
      createDate = new Timestamp(Calendar.getInstance().getTimeInMillis());
   }

   public Integer getFameLogId() {
      return fameLogId;
   }

   public void setFameLogId(Integer fameLogId) {
      this.fameLogId = fameLogId;
   }

   public Integer getCharacterId() {
      return characterId;
   }

   public void setCharacterId(Integer characterId) {
      this.characterId = characterId;
   }

   public Integer getCharacterIdTo() {
      return characterIdTo;
   }

   public void setCharacterIdTo(Integer characterIdTo) {
      this.characterIdTo = characterIdTo;
   }

   public Timestamp getCreateDate() {
      return createDate;
   }

   public void setCreateDate(Timestamp when) {
      this.createDate = when;
   }
}
