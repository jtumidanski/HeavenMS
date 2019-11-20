package entity.bbs;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bbs_threads")
public class BBSThread implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer threadId;

   @Column(nullable = false)
   private Integer posterId;

   @Column(nullable = false, length = 26)
   private String name;

   @Column(nullable = false)
   private Long timestamp;

   @Column(nullable = false)
   private Integer icon;

   @Column(nullable = false)
   private Integer replyCount = 0;

   @Column(nullable = false)
   private String startPost;

   @Column(nullable = false)
   private Integer guildId;

   @Column(nullable = false)
   private Integer localThreadId;

   public BBSThread() {
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

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Long getTimestamp() {
      return timestamp;
   }

   public void setTimestamp(Long timestamp) {
      this.timestamp = timestamp;
   }

   public Integer getIcon() {
      return icon;
   }

   public void setIcon(Integer icon) {
      this.icon = icon;
   }

   public Integer getReplyCount() {
      return replyCount;
   }

   public void setReplyCount(Integer replyCount) {
      this.replyCount = replyCount;
   }

   public String getStartPost() {
      return startPost;
   }

   public void setStartPost(String startPost) {
      this.startPost = startPost;
   }

   public Integer getGuildId() {
      return guildId;
   }

   public void setGuildId(Integer guildId) {
      this.guildId = guildId;
   }

   public Integer getLocalThreadId() {
      return localThreadId;
   }

   public void setLocalThreadId(Integer localThreadId) {
      this.localThreadId = localThreadId;
   }
}
