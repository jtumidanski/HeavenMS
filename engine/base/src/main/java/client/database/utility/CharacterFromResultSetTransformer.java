package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.GameType;
import client.database.data.CharacterData;
import client.database.data.GameData;

public class CharacterFromResultSetTransformer implements SqlTransformer<CharacterData, ResultSet> {
   @Override
   public CharacterData transform(ResultSet resultSet) throws SQLException {
      CharacterData mapleCharacter = new CharacterData(
            resultSet.getInt("accountid"),
            resultSet.getInt("id"),
            resultSet.getString("name"),
            resultSet.getInt("gender"),
            resultSet.getInt("skincolor"),
            resultSet.getInt("face"),
            resultSet.getInt("hair"),
            resultSet.getInt("level"),
            resultSet.getInt("job"),
            resultSet.getInt("str"),
            resultSet.getInt("dex"),
            resultSet.getInt("int"),
            resultSet.getInt("luk"),
            resultSet.getInt("hp"),
            resultSet.getInt("maxhp"),
            resultSet.getInt("mp"),
            resultSet.getInt("maxmp"),
            resultSet.getInt("ap"),
            resultSet.getString("sp").split(","),
            resultSet.getInt("exp"),
            resultSet.getInt("fame"),
            resultSet.getInt("gachaexp"),
            resultSet.getInt("map"),
            resultSet.getInt("spawnpoint"),
            resultSet.getInt("gm"),
            resultSet.getByte("world"),
            resultSet.getInt("rank"),
            resultSet.getInt("rankMove"),
            resultSet.getInt("jobRank"),
            resultSet.getInt("jobRankMove"),
            resultSet.getInt("fquest"),
            resultSet.getInt("hpMpUsed"),
            resultSet.getInt("HasMerchant") == 1,
            resultSet.getInt("meso"),
            resultSet.getInt("MerchantMesos"),
            resultSet.getInt("finishedDojoTutorial") == 1,
            resultSet.getInt("vanquisherKills"),
            resultSet.getInt("vanquisherStage"),
            new GameData(GameType.OMOK, resultSet.getInt("omokwins"), resultSet.getInt("omoklosses"), resultSet.getInt("omokties")),
            new GameData(GameType.MATCH, resultSet.getInt("matchcardwins"), resultSet.getInt("matchcardlosses"), resultSet.getInt("matchcardties")),
            resultSet.getLong("jailexpire"),
            resultSet.getInt("mountexp"),
            resultSet.getInt("mountlevel"),
            resultSet.getInt("mounttiredness"),
            resultSet.getInt("guildid"),
            resultSet.getInt("guildrank"),
            resultSet.getInt("allianceRank"),
            resultSet.getInt("familyId"),
            resultSet.getInt("monsterbookcover"),
            resultSet.getInt("ariantPoints"),
            resultSet.getInt("dojoPoints"),
            resultSet.getInt("lastDojoStage"),
            resultSet.getString("dataString"),
            resultSet.getInt("buddyCapacity"),
            resultSet.getTimestamp("lastExpGainTime"),
            resultSet.getBoolean("partySearch"),
            resultSet.getByte("equipslots"),
            resultSet.getByte("useslots"),
            resultSet.getByte("setupslots"),
            resultSet.getByte("etcslots"),
            resultSet.getInt("partnerId"),
            resultSet.getInt("marriageItemId"),
            resultSet.getInt("party"),
            resultSet.getInt("messengerid"),
            resultSet.getInt("messengerposition"));
      return mapleCharacter;
   }
}
