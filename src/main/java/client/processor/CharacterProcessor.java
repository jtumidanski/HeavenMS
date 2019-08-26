package client.processor;

import java.util.regex.Pattern;

import client.database.data.CharacterIdNameAccountId;
import client.database.provider.CharacterProvider;
import tools.DatabaseConnection;

public class CharacterProcessor {
   private static CharacterProcessor ourInstance = new CharacterProcessor();

   private static final String[] BLOCKED_NAMES = {"admin", "owner", "moderator", "intern", "donor", "administrator", "FREDRICK", "help", "helper", "alert", "notice", "maplestory", "fuck", "wizet", "fucking", "negro", "fuk", "fuc", "penis", "pussy", "asshole", "gay",
         "nigger", "homo", "suck", "cum", "shit", "shitty", "condom", "security", "official", "rape", "nigga", "sex", "tit", "boner", "orgy", "clit", "asshole", "fatass", "bitch", "support", "gamemaster", "cock", "gaay", "gm",
         "operate", "master", "sysop", "party", "GameMaster", "community", "message", "event", "test", "meso", "Scania", "yata", "AsiaSoft", "henesys"};

   public static CharacterProcessor getInstance() {
      return ourInstance;
   }

   private CharacterProcessor() {
   }

   public boolean canCreateChar(String name) {
      String lname = name.toLowerCase();
      for (String nameTest : BLOCKED_NAMES) {
         if (lname.contains(nameTest)) {
            return false;
         }
      }
      return getIdByName(name) < 0 && Pattern.compile("[a-zA-Z0-9]{3,12}").matcher(name).matches();
   }

   public int getIdByName(String name) {
      return DatabaseConnection.withConnectionResult(connection -> CharacterProvider.getInstance().getIdForName(connection, name)).orElse(-1);
   }

   public String getNameById(int id) {
      return DatabaseConnection.withConnectionResult(connection -> CharacterProvider.getInstance().getNameForId(connection, id)).orElse(null);
   }

   public CharacterIdNameAccountId getCharacterFromDatabase(String name) {
      return DatabaseConnection.withConnectionResultOpt(connection -> CharacterProvider.getInstance().getByName(connection, name)).orElse(null);
   }
}
