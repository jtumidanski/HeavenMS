package net.server.guild;

public class MapleGuildSummary {
   private String name;
   private short logoBG;
   private byte logoBGColor;
   private short logo;
   private byte logoColor;
   private int allianceId;

   public MapleGuildSummary(MapleGuild g) {
      this.name = g.getName();
      this.logoBG = (short) g.getLogoBG();
      this.logoBGColor = (byte) g.getLogoBGColor();
      this.logo = (short) g.getLogo();
      this.logoColor = (byte) g.getLogoColor();
      this.allianceId = g.getAllianceId();
   }

   public String getName() {
      return name;
   }

   public short getLogoBG() {
      return logoBG;
   }

   public byte getLogoBGColor() {
      return logoBGColor;
   }

   public short getLogo() {
      return logo;
   }

   public byte getLogoColor() {
      return logoColor;
   }

   public int getAllianceId() {
      return allianceId;
   }
}
