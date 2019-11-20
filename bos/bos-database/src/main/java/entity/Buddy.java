package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "buddies")
public class Buddy implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer id;

   @Column(nullable = false)
   private Integer characterId;

   @Column(nullable = false)
   private Integer buddyId;

   @Column(nullable = false)
   private Integer pending = 0;

   private String buddyGroup = "0";

   public Buddy() {
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

   public Integer getBuddyId() {
      return buddyId;
   }

   public void setBuddyId(Integer buddyId) {
      this.buddyId = buddyId;
   }

   public Integer getPending() {
      return pending;
   }

   public void setPending(Integer pending) {
      this.pending = pending;
   }

   public String getBuddyGroup() {
      return buddyGroup;
   }

   public void setBuddyGroup(String group) {
      this.buddyGroup = group;
   }
}
