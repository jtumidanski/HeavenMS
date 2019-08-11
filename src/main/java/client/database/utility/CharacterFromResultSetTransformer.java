package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.CharacterData;
import client.database.data.GameData;

public class CharacterFromResultSetTransformer implements SqlTransformer<CharacterData, ResultSet> {
   @Override
   public CharacterData transform(ResultSet resultSet) throws SQLException {
      CharacterData mapleCharacter = new CharacterData();

      mapleCharacter.setAccountId(resultSet.getInt("accountid"));
      mapleCharacter.setId(resultSet.getInt("id"));
      mapleCharacter.setName(resultSet.getString("name"));
      mapleCharacter.setGender(resultSet.getInt("gender"));
      mapleCharacter.setSkinColor(resultSet.getInt("skincolor"));
      mapleCharacter.setFace(resultSet.getInt("face"));
      mapleCharacter.setHair(resultSet.getInt("hair"));
      mapleCharacter.setLevel(resultSet.getInt("level"));
      mapleCharacter.setJob(resultSet.getInt("job"));
      mapleCharacter.setStr(resultSet.getInt("str"));
      mapleCharacter.setDex(resultSet.getInt("dex"));
      mapleCharacter.setIntelligence(resultSet.getInt("int"));
      mapleCharacter.setLuk(resultSet.getInt("luk"));
      mapleCharacter.setHp(resultSet.getInt("hp"));
      mapleCharacter.setMaxhp(resultSet.getInt("maxhp"));
      mapleCharacter.setMp(resultSet.getInt("mp"));
      mapleCharacter.setMaxmp(resultSet.getInt("maxmp"));
      mapleCharacter.setAp(resultSet.getInt("ap"));
      mapleCharacter.setSp(resultSet.getString("sp").split(","));
      mapleCharacter.setExp(resultSet.getInt("exp"));
      mapleCharacter.setFame(resultSet.getInt("fame"));
      mapleCharacter.setGachaponExp(resultSet.getInt("gachaexp"));
      mapleCharacter.setMap(resultSet.getInt("map"));
      mapleCharacter.setSpawnPoint(resultSet.getInt("spawnpoint"));
      mapleCharacter.setGm(resultSet.getInt("gm"));
      mapleCharacter.setWorld(resultSet.getByte("world"));
      mapleCharacter.setRank(resultSet.getInt("rank"));
      mapleCharacter.setRankMove(resultSet.getInt("rankMove"));
      mapleCharacter.setJobRank(resultSet.getInt("jobRank"));
      mapleCharacter.setJobRankMove(resultSet.getInt("jobRankMove"));
      mapleCharacter.setQuestFame(resultSet.getInt("fquest"));
      mapleCharacter.setHpMpUsed(resultSet.getInt("hpMpUsed"));
      mapleCharacter.setHasMerchant(resultSet.getInt("HasMerchant") == 1);
      mapleCharacter.setMeso(resultSet.getInt("meso"));
      mapleCharacter.setMerchantMeso(resultSet.getInt("MerchantMesos"));
      mapleCharacter.setFinishedDojoTutorial(resultSet.getInt("finishedDojoTutorial") == 1);
      mapleCharacter.setVanquisherKills(resultSet.getInt("vanquisherKills"));
      mapleCharacter.setOmok(new GameData(GameData.Type.OMOK, resultSet.getInt("omokwins"), resultSet.getInt("omoklosses"), resultSet.getInt("omokties")));
      mapleCharacter.setMatchCard(new GameData(GameData.Type.MATCH, resultSet.getInt("matchcardwins"), resultSet.getInt("matchcardlosses"), resultSet.getInt("matchcardties")));
      mapleCharacter.setJailExpire(resultSet.getLong("jailexpire"));
      mapleCharacter.setMountExp(resultSet.getInt("mountexp"));
      mapleCharacter.setMountLevel(resultSet.getInt("mountlevel"));
      mapleCharacter.setMountTiredness(resultSet.getInt("mounttiredness"));
      mapleCharacter.setGuildId(resultSet.getInt("guildid"));
      mapleCharacter.setGuildRank(resultSet.getInt("guildrank"));
      mapleCharacter.setAllianceRank(resultSet.getInt("allianceRank"));
      mapleCharacter.setFamilyId(resultSet.getInt("familyId"));
      mapleCharacter.setMonsterBookCover(resultSet.getInt("monsterbookcover"));
      mapleCharacter.setVanquisherStage(resultSet.getInt("vanquisherStage"));
      mapleCharacter.setAriantPoints(resultSet.getInt("ariantPoints"));
      mapleCharacter.setDojoPoints(resultSet.getInt("dojoPoints"));
      mapleCharacter.setLastDojoStage(resultSet.getInt("lastDojoStage"));
      mapleCharacter.setDataString(resultSet.getString("dataString"));
      mapleCharacter.setBuddyCapacity(resultSet.getInt("buddyCapacity"));
      mapleCharacter.setLastExpGainTime(resultSet.getTimestamp("lastExpGainTime"));
      mapleCharacter.setPartyInvite(resultSet.getBoolean("partySearch"));
      mapleCharacter.setEquipSlotLimit(resultSet.getByte("equipslots"));
      mapleCharacter.setUseSlotLimit(resultSet.getByte("useslots"));
      mapleCharacter.setSetupSlotLimit(resultSet.getByte("setupslots"));
      mapleCharacter.setEtcSlotLimit(resultSet.getByte("etcslots"));
      mapleCharacter.setPartnerId(resultSet.getInt("partnerId"));
      mapleCharacter.setMarriageItemId(resultSet.getInt("marriageItemId"));
      mapleCharacter.setPartyId(resultSet.getInt("partyId"));
      mapleCharacter.setMessengerId(resultSet.getInt("messengerid"));
      mapleCharacter.setMessengerPosition(resultSet.getInt("messengerposition"));

      return mapleCharacter;
   }
}
