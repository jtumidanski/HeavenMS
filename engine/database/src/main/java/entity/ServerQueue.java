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
@Table(name = "server_queue")
public class ServerQueue implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer id;

   @Column(nullable = false)
   private Integer accountId;

   @Column(nullable = false)
   private Integer characterId;

   @Column(nullable = false)
   private Integer type;

   @Column(nullable = false)
   private Integer value;

   @Column(nullable = false)
   private String message;

   @Column(nullable = false)
   private Timestamp createTime;

   public ServerQueue() {
      createTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getAccountId() {
      return accountId;
   }

   public void setAccountId(Integer accountId) {
      this.accountId = accountId;
   }

   public Integer getCharacterId() {
      return characterId;
   }

   public void setCharacterId(Integer characterId) {
      this.characterId = characterId;
   }

   public Integer getType() {
      return type;
   }

   public void setType(Integer type) {
      this.type = type;
   }

   public Integer getValue() {
      return value;
   }

   public void setValue(Integer value) {
      this.value = value;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public Timestamp getCreateTime() {
      return createTime;
   }

   public void setCreateTime(Timestamp createTime) {
      this.createTime = createTime;
   }
}
