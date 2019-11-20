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
@Table(name = "worldtransfers", indexes = {
      @Index(name = "characterId", columnList = "characterId")
})
public class WorldTransfer implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer id;

   @Column(nullable = false)
   private Integer characterId;

   @Column(nullable = false)
   private Integer fromWorld;

   @Column(nullable = false)
   private Integer toWorld;

   @Column(nullable = false)
   private Timestamp requestTime;

   @Column
   private Timestamp completionTime;

   public WorldTransfer() {
      requestTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
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

   public Integer getFromWorld() {
      return fromWorld;
   }

   public void setFromWorld(Integer from) {
      this.fromWorld = from;
   }

   public Integer getToWorld() {
      return toWorld;
   }

   public void setToWorld(Integer to) {
      this.toWorld = to;
   }

   public Timestamp getRequestTime() {
      return requestTime;
   }

   public void setRequestTime(Timestamp requestTime) {
      this.requestTime = requestTime;
   }

   public Timestamp getCompletionTime() {
      return completionTime;
   }

   public void setCompletionTime(Timestamp completionTime) {
      this.completionTime = completionTime;
   }
}
