package client.database.data;

import java.sql.Timestamp;

public class FredStorageData {
   private int characterId;

   private String name;

   private int worldId;

   private Timestamp timestamp;

   private int dayNotes;

   private Timestamp lastLogoutTime;

   public FredStorageData(int characterId, String name, int worldId, Timestamp timestamp, int dayNotes, Timestamp lastLogoutTime) {
      this.characterId = characterId;
      this.name = name;
      this.worldId = worldId;
      this.timestamp = timestamp;
      this.dayNotes = dayNotes;
      this.lastLogoutTime = lastLogoutTime;
   }

   public int getCharacterId() {
      return characterId;
   }

   public String getName() {
      return name;
   }

   public int getWorldId() {
      return worldId;
   }

   public Timestamp getTimestamp() {
      return timestamp;
   }

   public int getDayNotes() {
      return dayNotes;
   }

   public Timestamp getLastLogoutTime() {
      return lastLogoutTime;
   }

   /**
    * try (PreparedStatement ps = con.prepareStatement("SELECT * FROM fredstorage f LEFT JOIN (SELECT id, name, world, lastLogoutTime FROM characters) AS c ON c.id = f.cid")) {
    *             try (ResultSet rs = ps.executeQuery()) {
    *                long curTime = System.currentTimeMillis();
    *
    *                while (rs.next()) {
    *                   int cid = rs.getInt("cid");
    *                   int world = rs.getInt("world");
    *                   Timestamp ts = rs.getTimestamp("timestamp");
    *                   int daynotes = Math.min(dailyReminders.length - 1, rs.getInt("daynotes"));
    *
    *                   int elapsedDays = timestampElapsedDays(ts, curTime);
    *                   if (elapsedDays > 100) {
    *                      expiredCids.add(new CharacterWorldData(cid, world));
    *                   } else {
    *                      int notifDay = dailyReminders[daynotes];
    *
    *                      if (elapsedDays >= notifDay) {
    *                         do {
    *                            daynotes++;
    *                            notifDay = dailyReminders[daynotes];
    *                         } while (elapsedDays >= notifDay);
    *
    *                         Timestamp logoutTs = rs.getTimestamp("lastLogoutTime");
    *                         int inactivityDays = timestampElapsedDays(logoutTs, curTime);
    *
    *                         if (inactivityDays < 7 || daynotes >= dailyReminders.length - 1) {  // don't spam inactive players
    *                            String name = rs.getString("name");
    *                            notifCids.add(new CharacterNameNote(cid, name, daynotes));
    *                         }
    *                      }
    *                   }
    *                }
    *             }
    */
}
