package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "allianceguilds")
public class AllianceGuild implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer id;

   @Column(nullable = false)
   private Integer allianceId = -1;

   @Column(nullable = false)
   private Integer guildId = -1;

   public AllianceGuild() {
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getAllianceId() {
      return allianceId;
   }

   public void setAllianceId(Integer allianceId) {
      this.allianceId = allianceId;
   }

   public Integer getGuildId() {
      return guildId;
   }

   public void setGuildId(Integer guildId) {
      this.guildId = guildId;
   }
}
