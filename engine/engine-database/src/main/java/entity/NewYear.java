package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "newyear")
public class NewYear implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer id;

   @Column(nullable = false)
   private Integer senderId;

   @Column(nullable = false)
   private String senderName;

   @Column(nullable = false)
   private Integer receiverId;

   @Column(nullable = false)
   private String receiverName;

   @Column(nullable = false)
   private String message;

   @Column(nullable = false)
   private Integer senderDiscard;

   @Column(nullable = false)
   private Integer receiverDiscard;

   @Column(nullable = false)
   private Integer received;

   @Column(nullable = false)
   private Long timeSent;

   @Column(nullable = false)
   private Long timerReceived;

   public NewYear() {
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getSenderId() {
      return senderId;
   }

   public void setSenderId(Integer senderId) {
      this.senderId = senderId;
   }

   public String getSenderName() {
      return senderName;
   }

   public void setSenderName(String senderName) {
      this.senderName = senderName;
   }

   public Integer getReceiverId() {
      return receiverId;
   }

   public void setReceiverId(Integer receiverId) {
      this.receiverId = receiverId;
   }

   public String getReceiverName() {
      return receiverName;
   }

   public void setReceiverName(String receiverName) {
      this.receiverName = receiverName;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public Integer getSenderDiscard() {
      return senderDiscard;
   }

   public void setSenderDiscard(Integer senderDiscard) {
      this.senderDiscard = senderDiscard;
   }

   public Integer getReceiverDiscard() {
      return receiverDiscard;
   }

   public void setReceiverDiscard(Integer receiverDiscard) {
      this.receiverDiscard = receiverDiscard;
   }

   public Integer getReceived() {
      return received;
   }

   public void setReceived(Integer received) {
      this.received = received;
   }

   public Long getTimeSent() {
      return timeSent;
   }

   public void setTimeSent(Long timeSent) {
      this.timeSent = timeSent;
   }

   public Long getTimerReceived() {
      return timerReceived;
   }

   public void setTimerReceived(Long timerReceived) {
      this.timerReceived = timerReceived;
   }
}
