package client.database.utility;

import client.database.GameType;
import client.database.data.CharacterData;
import client.database.data.GameData;
import entity.Character;
import transformer.SqlTransformer;

public class CharacterFromResultSetTransformer implements SqlTransformer<CharacterData, Character> {
   @Override
   public CharacterData transform(Character resultSet) {
      return new CharacterData(
            resultSet.getAccountId(),
            resultSet.getId(),
            resultSet.getName(),
            resultSet.getGender(),
            resultSet.getSkinColor(),
            resultSet.getFace(),
            resultSet.getHair(),
            resultSet.getLevel(),
            resultSet.getJob(),
            resultSet.getStr(),
            resultSet.getDex(),
            resultSet.getIntelligence(),
            resultSet.getLuk(),
            resultSet.getHp(),
            resultSet.getMaxHp(),
            resultSet.getMp(),
            resultSet.getMaxMp(),
            resultSet.getAp(),
            resultSet.getSp().split(","),
            resultSet.getExp(),
            resultSet.getFame(),
            resultSet.getGachaponExp(),
            resultSet.getMap(),
            resultSet.getSpawnPoint(),
            resultSet.getGm(),
            resultSet.getWorld(),
            resultSet.getRank(),
            resultSet.getRankMove(),
            resultSet.getJobRank(),
            resultSet.getJobRankMove(),
            resultSet.getFquest(),
            resultSet.getHpMpUsed(),
            resultSet.getHasMerchant() == 1,
            resultSet.getMeso(),
            resultSet.getMerchantMesos(),
            resultSet.getFinishedDojoTutorial() == 1,
            resultSet.getVanquisherKills(),
            resultSet.getVanquisherStage(),
            new GameData(GameType.OMOK, resultSet.getOmokWins(), resultSet.getOmokLosses(), resultSet.getOmokTies()),
            new GameData(GameType.MATCH, resultSet.getMatchCardWins(), resultSet.getMatchCardLosses(), resultSet.getMatchCardTies()),
            resultSet.getJailExpire(),
            resultSet.getMountExp(),
            resultSet.getMountLevel(),
            resultSet.getMountTiredness(),
            resultSet.getGuildId(),
            resultSet.getGuildRank(),
            resultSet.getAllianceRank(),
            resultSet.getFamilyId(),
            resultSet.getMonsterBookCover(),
            resultSet.getAriantPoints(),
            resultSet.getDojoPoints(),
            resultSet.getLastDojoStage(),
            resultSet.getDataString(),
            resultSet.getLastExpGainTime(),
            resultSet.getPartySearch() == 1,
            resultSet.getEquipSlots(),
            resultSet.getUseSlots(),
            resultSet.getSetupSlots(),
            resultSet.getEtcSlots(),
            resultSet.getPartnerId(),
            resultSet.getMarriageItemId(),
            resultSet.getParty(),
            resultSet.getMessengerId(),
            resultSet.getMessengerPosition());
   }
}
