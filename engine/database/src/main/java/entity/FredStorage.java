package entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "fredstorage", indexes = {
      @Index(name = "characterId", columnList = "cid", unique = true)
})
public class FredStorage implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer id;

   @Column(nullable = false, name = "cid")
   private Integer characterId;

   @Column(nullable = false)
   private Integer dayNotes;

   @Column(nullable = false)
   private Timestamp timestamp;

   public FredStorage() {
      timestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
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

   public Integer getDayNotes() {
      return dayNotes;
   }

   public void setDayNotes(Integer dayNotes) {
      this.dayNotes = dayNotes;
   }

   public Timestamp getTimestamp() {
      return timestamp;
   }

   public void setTimestamp(Timestamp timestamp) {
      this.timestamp = timestamp;
   }
}
