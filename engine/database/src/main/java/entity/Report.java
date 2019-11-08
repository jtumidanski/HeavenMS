package entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "reports")
public class Report implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer id;

   @Column(nullable = false)
   private Timestamp reportTime;

   @Column(nullable = false)
   private Integer reporterId;

   @Column(nullable = false)
   private Integer victimId;

   @Column(nullable = false)
   private Integer reason;

   @Column(nullable = false)
   private String chatLog;

   @Column(nullable = false)
   private String description;

   public Report() {
      reportTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Timestamp getReportTime() {
      return reportTime;
   }

   public void setReportTime(Timestamp reportTime) {
      this.reportTime = reportTime;
   }

   public Integer getReporterId() {
      return reporterId;
   }

   public void setReporterId(Integer reporterId) {
      this.reporterId = reporterId;
   }

   public Integer getVictimId() {
      return victimId;
   }

   public void setVictimId(Integer victimId) {
      this.victimId = victimId;
   }

   public Integer getReason() {
      return reason;
   }

   public void setReason(Integer reason) {
      this.reason = reason;
   }

   public String getChatLog() {
      return chatLog;
   }

   public void setChatLog(String chatLog) {
      this.chatLog = chatLog;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }
}
