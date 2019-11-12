package entity.quest;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "questactions")
public class QuestAction implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer questActionId;

   @Column(nullable = false)
   private Integer questId;

   @Column(nullable = false)
   private Integer status;

   @Lob
   @Column(nullable = false)
   private byte[] data;

   public QuestAction() {
   }

   public Integer getQuestActionId() {
      return questActionId;
   }

   public void setQuestActionId(Integer questActionId) {
      this.questActionId = questActionId;
   }

   public Integer getQuestId() {
      return questId;
   }

   public void setQuestId(Integer questId) {
      this.questId = questId;
   }

   public Integer getStatus() {
      return status;
   }

   public void setStatus(Integer status) {
      this.status = status;
   }

   public byte[] getData() {
      return data;
   }

   public void setData(byte[] data) {
      this.data = data;
   }
}
