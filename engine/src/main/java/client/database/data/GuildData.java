package client.database.data;

public class GuildData {
   private String name;

   private int gp;

   private int logo;

   private int logoColor;

   private int logoBackground;

   private int logoBackgroundColor;

   private int capacity;

   private String[] rankTitles;

   private int leaderId;

   private String notice;

   private int signature;

   private int allianceId;

   public GuildData(String name, int gp, int logo, int logoColor, int logoBackground, int logoBackgroundColor) {
      this.name = name;
      this.gp = gp;
      this.logo = logo;
      this.logoColor = logoColor;
      this.logoBackground = logoBackground;
      this.logoBackgroundColor = logoBackgroundColor;
   }

   public GuildData(String name, int gp, int logo, int logoColor, int logoBackground, int logoBackgroundColor, int capacity, String[] rankTitles, int leaderId, String notice, int signature, int allianceId) {
      this.name = name;
      this.gp = gp;
      this.logo = logo;
      this.logoColor = logoColor;
      this.logoBackground = logoBackground;
      this.logoBackgroundColor = logoBackgroundColor;
      this.capacity = capacity;
      this.rankTitles = rankTitles;
      this.leaderId = leaderId;
      this.notice = notice;
      this.signature = signature;
      this.allianceId = allianceId;
   }

   public String getName() {
      return name;
   }

   public int getGp() {
      return gp;
   }

   public int getLogo() {
      return logo;
   }

   public int getLogoColor() {
      return logoColor;
   }

   public int getLogoBackground() {
      return logoBackground;
   }

   public int getLogoBackgroundColor() {
      return logoBackgroundColor;
   }

   public int getCapacity() {
      return capacity;
   }

   public String[] getRankTitles() {
      return rankTitles;
   }

   public int getLeaderId() {
      return leaderId;
   }

   public String getNotice() {
      return notice;
   }

   public int getSignature() {
      return signature;
   }

   public int getAllianceId() {
      return allianceId;
   }
}
