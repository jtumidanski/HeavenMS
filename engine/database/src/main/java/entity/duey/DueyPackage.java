package entity.duey;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dueypackages")
public class DueyPackage implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer packageId;

   @Column(nullable = false)
   private Integer receiverId;

   @Column(nullable = false)
   private String senderName;

   @Column(nullable = false)
   private Integer mesos;

   @Column(nullable = false)
   private Timestamp timestamp;

   @Column
   private String message;

   @Column(nullable = false)
   private Integer checked;

   @Column(nullable = false)
   private Integer type;

   public DueyPackage() {
      timestamp = new Timestamp(0);
   }

   public Integer getPackageId() {
      return packageId;
   }

   public void setPackageId(Integer packageId) {
      this.packageId = packageId;
   }

   public Integer getReceiverId() {
      return receiverId;
   }

   public void setReceiverId(Integer receiverId) {
      this.receiverId = receiverId;
   }

   public String getSenderName() {
      return senderName;
   }

   public void setSenderName(String senderName) {
      this.senderName = senderName;
   }

   public Integer getMesos() {
      return mesos;
   }

   public void setMesos(Integer mesos) {
      this.mesos = mesos;
   }

   public Timestamp getTimestamp() {
      return timestamp;
   }

   public void setTimestamp(Timestamp timestamp) {
      this.timestamp = timestamp;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public Integer getChecked() {
      return checked;
   }

   public void setChecked(Integer checked) {
      this.checked = checked;
   }

   public Integer getType() {
      return type;
   }

   public void setType(Integer type) {
      this.type = type;
   }
}
