package entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "hwidaccounts")
public class HwidAccount implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   private Integer accountId;

   @Id
   private String hwid;

   @Column(nullable = false)
   private Integer relevance;

   @Column(nullable = false)
   private Timestamp expiresAt;

   public HwidAccount() {
      expiresAt = new Timestamp(Calendar.getInstance().getTimeInMillis());
   }

   public Integer getAccountId() {
      return accountId;
   }

   public void setAccountId(Integer accountId) {
      this.accountId = accountId;
   }

   public String getHwid() {
      return hwid;
   }

   public void setHwid(String hwid) {
      this.hwid = hwid;
   }

   public Integer getRelevance() {
      return relevance;
   }

   public void setRelevance(Integer relevance) {
      this.relevance = relevance;
   }

   public Timestamp getExpiresAt() {
      return expiresAt;
   }

   public void setExpiresAt(Timestamp expiresAt) {
      this.expiresAt = expiresAt;
   }
}
