package client.database.data;

public record GuildData(String name, Integer gp, Integer logo, Integer logoColor, Integer logoBackground,
                        Integer logoBackgroundColor, Integer capacity, String[] rankTitles, Integer leaderId,
                        String notice, Integer signature, Integer allianceId) {
   public GuildData(String name, Integer gp, Integer logo, Integer logoColor, Integer logoBackground,
                    Integer logoBackgroundColor, Integer capacity, String rank1Title, String rank2Title,
                    String rank3Title, String rank4Title, String rank5Title, Integer leaderId, String notice,
                    Integer signature, Integer allianceId) {
      this(name, gp, logo, logoColor, logoBackground, logoBackgroundColor, capacity, new String[]{rank1Title,
            rank2Title, rank3Title, rank4Title, rank5Title}, leaderId, notice, signature, allianceId);
   }

   public GuildData(String name, Integer gp, Integer logo, Integer logoColor, Integer logoBackground,
                    Integer logoBackgroundColor) {
      this(name, gp, logo, logoColor, logoBackground, logoBackgroundColor, 0, new String[0], 0, "", 0, 0);
   }
}
