package entity.quest;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "queststatus")
public class QuestStatus implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer questStatusId;

   @Column(nullable = false)
   private Integer characterId;

   @Column(nullable = false)
   private Integer quest;

   @Column(nullable = false)
   private Integer status;

   @Column(nullable = false)
   private Integer time;

   @Column(nullable = false)
   private Long expires;

   @Column(nullable = false)
   private Integer forfeited;

   @Column(nullable = false)
   private Integer completed;

   @Column(nullable = false)
   private Integer info;

   public QuestStatus() {
   }

   public Integer getQuestStatusId() {
      return questStatusId;
   }

   public void setQuestStatusId(Integer questStatusId) {
      this.questStatusId = questStatusId;
   }

   public Integer getCharacterId() {
      return characterId;
   }

   public void setCharacterId(Integer characterId) {
      this.characterId = characterId;
   }

   public Integer getQuest() {
      return quest;
   }

   public void setQuest(Integer quest) {
      this.quest = quest;
   }

   public Integer getStatus() {
      return status;
   }

   public void setStatus(Integer status) {
      this.status = status;
   }

   public Integer getTime() {
      return time;
   }

   public void setTime(Integer time) {
      this.time = time;
   }

   public Long getExpires() {
      return expires;
   }

   public void setExpires(Long expires) {
      this.expires = expires;
   }

   public Integer getForfeited() {
      return forfeited;
   }

   public void setForfeited(Integer forfeited) {
      this.forfeited = forfeited;
   }

   public Integer getCompleted() {
      return completed;
   }

   public void setCompleted(Integer completed) {
      this.completed = completed;
   }

   public Integer getInfo() {
      return info;
   }

   public void setInfo(Integer info) {
      this.info = info;
   }
}
