package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "guilds", indexes = {
      @Index(name = "guild", columnList = "guildId,name")
})
public class Guild implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer guildId;

   @Column(nullable = false)
   private Integer leader;

   @Column(nullable = false)
   private Integer gp;

   @Column
   private Integer logo;

   @Column(nullable = false)
   private Integer logoColor;

   @Column(nullable = false)
   private String name;

   @Column(nullable = false)
   private String rank1Title = "Master";

   @Column(nullable = false)
   private String rank2Title = "Jr. Master";

   @Column(nullable = false)
   private String rank3Title = "Member";

   @Column(nullable = false)
   private String rank4Title = "Member";

   @Column(nullable = false)
   private String rank5Title = "Member";

   @Column(nullable = false)
   private Integer capacity;

   @Column(nullable = false, name = "logoBG")
   private Integer logoBackground;

   @Column(nullable = false, name = "logoBGColor")
   private Integer logoBackgroundColor;

   @Column
   private String notice;

   @Column(nullable = false)
   private Integer signature;

   @Column(nullable = false)
   private Integer allianceId;

   public Guild() {
   }

   public Integer getGuildId() {
      return guildId;
   }

   public void setGuildId(Integer guildId) {
      this.guildId = guildId;
   }

   public Integer getLeader() {
      return leader;
   }

   public void setLeader(Integer leader) {
      this.leader = leader;
   }

   public Integer getGp() {
      return gp;
   }

   public void setGp(Integer gp) {
      this.gp = gp;
   }

   public Integer getLogo() {
      return logo;
   }

   public void setLogo(Integer logo) {
      this.logo = logo;
   }

   public Integer getLogoColor() {
      return logoColor;
   }

   public void setLogoColor(Integer logoColor) {
      this.logoColor = logoColor;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getRank1Title() {
      return rank1Title;
   }

   public void setRank1Title(String rank1Title) {
      this.rank1Title = rank1Title;
   }

   public String getRank2Title() {
      return rank2Title;
   }

   public void setRank2Title(String rank2Title) {
      this.rank2Title = rank2Title;
   }

   public String getRank3Title() {
      return rank3Title;
   }

   public void setRank3Title(String rank3Title) {
      this.rank3Title = rank3Title;
   }

   public String getRank4Title() {
      return rank4Title;
   }

   public void setRank4Title(String rank4Title) {
      this.rank4Title = rank4Title;
   }

   public String getRank5Title() {
      return rank5Title;
   }

   public void setRank5Title(String rank5Title) {
      this.rank5Title = rank5Title;
   }

   public Integer getCapacity() {
      return capacity;
   }

   public void setCapacity(Integer capacity) {
      this.capacity = capacity;
   }

   public Integer getLogoBackground() {
      return logoBackground;
   }

   public void setLogoBackground(Integer logoBackground) {
      this.logoBackground = logoBackground;
   }

   public Integer getLogoBackgroundColor() {
      return logoBackgroundColor;
   }

   public void setLogoBackgroundColor(Integer logoBackgroundColor) {
      this.logoBackgroundColor = logoBackgroundColor;
   }

   public String getNotice() {
      return notice;
   }

   public void setNotice(String notice) {
      this.notice = notice;
   }

   public Integer getSignature() {
      return signature;
   }

   public void setSignature(Integer signature) {
      this.signature = signature;
   }

   public Integer getAllianceId() {
      return allianceId;
   }

   public void setAllianceId(Integer allianceId) {
      this.allianceId = allianceId;
   }
}
