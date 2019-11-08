package entity.bbs;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bbs_replies")
public class BBSReply implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer replyId;

   @Column(nullable = false)
   private Integer threadId;

   @Column(nullable = false)
   private Integer posterId;

   @Column(nullable = false)
   private Long timestamp;

   @Column(nullable = false, length = 26)
   private String content;

   public BBSReply() {
   }

   public Integer getReplyId() {
      return replyId;
   }

   public void setReplyId(Integer replyId) {
      this.replyId = replyId;
   }

   public Integer getThreadId() {
      return threadId;
   }

   public void setThreadId(Integer threadId) {
      this.threadId = threadId;
   }

   public Integer getPosterId() {
      return posterId;
   }

   public void setPosterId(Integer posterId) {
      this.posterId = posterId;
   }

   public Long getTimestamp() {
      return timestamp;
   }

   public void setTimestamp(Long timestamp) {
      this.timestamp = timestamp;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }
}
