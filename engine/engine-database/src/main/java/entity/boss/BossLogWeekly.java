package entity.boss;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bosslog_weekly")
public class BossLogWeekly implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer id;

   @Column(nullable = false)
   private Integer characterId;

   @Enumerated(EnumType.STRING)
   @Column(nullable = false)
   private BossType bossType;

   @Column(nullable = false)
   private Timestamp attemptTime;

   public BossLogWeekly() {
      attemptTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
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

   public BossType getBossType() {
      return bossType;
   }

   public void setBossType(BossType bossType) {
      this.bossType = bossType;
   }

   public Timestamp getAttemptTime() {
      return attemptTime;
   }

   public void setAttemptTime(Timestamp attemptTime) {
      this.attemptTime = attemptTime;
   }
}