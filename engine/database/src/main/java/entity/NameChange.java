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
@Table(name = "namechanges", indexes = {
      @Index(name = "characterId", columnList = "characterId")
})
public class NameChange implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer id;

   @Column(nullable = false)
   private Integer characterId;

   @Column(nullable = false)
   private String old;

   @Column(nullable = false, name = "new")
   private String newName;

   @Column(nullable = false)
   private Timestamp requestTime;

   @Column
   private Timestamp completionTime;

   public NameChange() {
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

   public String getOld() {
      return old;
   }

   public void setOld(String old) {
      this.old = old;
   }

   public String getNewName() {
      return newName;
   }

   public void setNewName(String newName) {
      this.newName = newName;
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
