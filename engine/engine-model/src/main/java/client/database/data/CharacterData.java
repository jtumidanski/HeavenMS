package client.database.data;

import java.util.Date;

public record CharacterData(int accountId, int id, String name, int gender, int skinColor, int face, int hair,
                            int level, int job, int str, int dex, int intelligence, int luk, int hp, int maxHp, int mp,
                            int maxMp, int ap, String[] sp, int exp, int fame, int gachaponExp, int map, int spawnPoint,
                            int gm, int world, int rank, int rankMove, int jobRank, int jobRankMove, int questFame,
                            int hpMpUsed, boolean merchant, int meso, int merchantMeso, boolean finishedDojoTutorial,
                            int vanquisherKills, int vanquisherStage, GameData omok, GameData matchCard,
                            long jailExpire, int mountExp, int mountLevel, int mountTiredness, int guildId,
                            int guildRank, int allianceRank, int familyId, int monsterBookCover, int ariantPoints,
                            int dojoPoints, int lastDojoStage, String dataString, Date lastExpGainTime,
                            boolean partyInvite, int equipSlotLimit, int useSlotLimit, int setupSlotLimit,
                            int etcSlotLimit, int partnerId, int marriageItemId, int partyId, int messengerId,
                            int messengerPosition) {

   public CharacterData(int world, String name, int level) {
      this(0, 0, name, 0, 0, 0, 0, level, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, new String[0], 0, 0, 0, 0, 0,
            0, world, 0, 0, 0, 0, 0, 0, false, 0,
            0, false, 0, 0, null, null, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, "", null, false, 0, 0,
            0, 0, 0, 0, 0, 0, 0);
   }

   public CharacterData(String name, int level) {
      this(0, 0, name, 0, 0, 0, 0, level, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, new String[0], 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, false,
            0, 0, false, 0, 0, null, null,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, "", null, false,
            0, 0, 0, 0, 0, 0, 0, 0,
            0);
   }
}
