package entity.quest;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "questprogress")
public class QuestProgress implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer id;

   @Column(nullable = false)
   private Integer characterId;

   @Column(nullable = false)
   private Integer questStatusId;

   @Column(nullable = false)
   private Integer progressId;

   @Column(nullable = false)
   private String progress;

   public QuestProgress() {
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

   public Integer getQuestStatusId() {
      return questStatusId;
   }

   public void setQuestStatusId(Integer questStatusId) {
      this.questStatusId = questStatusId;
   }

   public Integer getProgressId() {
      return progressId;
   }

   public void setProgressId(Integer progressId) {
      this.progressId = progressId;
   }

   public String getProgress() {
      return progress;
   }

   public void setProgress(String progress) {
      this.progress = progress;
   }
}
