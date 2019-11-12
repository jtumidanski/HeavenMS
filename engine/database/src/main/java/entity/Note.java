package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "notes")
public class Note implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer id;

   @Column(nullable = false)
   private String noteTo;

   @Column(nullable = false)
   private String noteFrom;

   @Column(nullable = false)
   private String message;

   @Column(nullable = false)
   private Long timestamp;

   @Column(nullable = false)
   private Integer fame;

   @Column(nullable = false)
   private Integer deleted;

   public Note() {
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public String getNoteTo() {
      return noteTo;
   }

   public void setNoteTo(String to) {
      this.noteTo = to;
   }

   public String getNoteFrom() {
      return noteFrom;
   }

   public void setNoteFrom(String from) {
      this.noteFrom = from;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public Long getTimestamp() {
      return timestamp;
   }

   public void setTimestamp(Long timestamp) {
      this.timestamp = timestamp;
   }

   public Integer getFame() {
      return fame;
   }

   public void setFame(Integer fame) {
      this.fame = fame;
   }

   public Integer getDeleted() {
      return deleted;
   }

   public void setDeleted(Integer deleted) {
      this.deleted = deleted;
   }
}
