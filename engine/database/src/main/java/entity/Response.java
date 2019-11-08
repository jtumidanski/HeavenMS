package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "responses")
public class Response implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer id;

   @Column(nullable = false)
   private String chat;

   @Column(nullable = false)
   private String response;

   public Response() {
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public String getChat() {
      return chat;
   }

   public void setChat(String chat) {
      this.chat = chat;
   }

   public String getResponse() {
      return response;
   }

   public void setResponse(String response) {
      this.response = response;
   }
}
