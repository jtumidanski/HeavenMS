package client.database.administrator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.provider.CharacterProvider;
import tools.FilePrinter;
import tools.Pair;

public class CharacterAdministrator extends AbstractQueryExecutor {
   private static CharacterAdministrator instance;

   public static CharacterAdministrator getInstance() {
      if (instance == null) {
         instance = new CharacterAdministrator();
      }
      return instance;
   }

   private CharacterAdministrator() {
   }

   public void setMerchant(Connection connection, int characterId, boolean hasMerchant) {
      String sql = "UPDATE characters SET HasMerchant = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, hasMerchant ? 1 : 0);
         ps.setInt(2, characterId);
      });
   }

   public void setMerchantMesos(Connection connection, int characterId, int total) {
      String sql = "UPDATE characters SET MerchantMesos = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, total);
         ps.setInt(2, characterId);
      });
   }

   public void setMerchantMesosBatch(Connection connection, List<Pair<Integer, Integer>> data) {
      String sql = "UPDATE characters SET MerchantMesos = ? WHERE id = ?";
      batch(connection, sql, (ps, dataPoint) -> {
         ps.setInt(1, dataPoint.getRight());
         ps.setInt(2, dataPoint.getLeft());
      }, data);
   }

   public void setName(Connection connection, int characterId, String name) {
      String sql = "UPDATE `characters` SET `name` = ? WHERE `id` = ?";
      execute(connection, sql, ps -> {
         ps.setString(1, name);
         ps.setInt(2, characterId);
      });
   }

   public void logCharacterOut(Connection connection, int characterId) {
      String sql = "UPDATE characters SET lastLogoutTime=? WHERE id=?";
      execute(connection, sql, ps -> {
         ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
         ps.setInt(2, characterId);
      });
   }

   public void setReborns(Connection connection, int characterId, int value) {
      String sql = "UPDATE characters SET reborns=? WHERE id=?;";
      execute(connection, sql, ps -> {
         ps.setInt(1, value);
         ps.setInt(2, characterId);
      });
   }

   public void deleteCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM characters WHERE id = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void removeAllCharactersFromGuild(Connection connection, int guildId) {
      String sql = "UPDATE characters SET guildid = 0, guildrank = 5 WHERE guildid = ?";
      execute(connection, sql, ps -> ps.setInt(1, guildId));
   }

   public void updateGuild(Connection connection, int characterId, int guildId) {
      String sql = "UPDATE characters SET guildid = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, guildId);
         ps.setInt(2, characterId);
      });
   }

   public void updateAllianceRank(Connection connection, int guildId, int allianceId) {
      String sql = "UPDATE characters SET allianceRank = ? WHERE guildid = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, allianceId);
         ps.setInt(2, guildId);
      });
   }

   public void updateGuildStatus(Connection connection, int characterId, int guildId, int rank, int allianceRank) {
      String sql = "UPDATE characters SET guildid = ?, guildrank = ?, allianceRank = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, guildId);
         ps.setInt(2, rank);
         ps.setInt(3, allianceRank);
         ps.setInt(4, characterId);
      });
   }

   public void updateGuildStatus(Connection connection, int characterId, int guildId, int rank) {
      String sql = "UPDATE characters SET guildid = ?, guildrank = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, guildId);
         ps.setInt(2, rank);
         ps.setInt(4, characterId);
      });
   }

   public void removeAllMerchants(Connection connection) {
      String sql = "UPDATE characters SET HasMerchant = 0";
      executeNoParam(connection, sql);
   }

   public void eraseEngagement(Connection connection, int characterId) {
      String sql = "UPDATE characters SET marriageItemId=-1, partnerId=-1 WHERE id=?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void resetAllJobRankMove(Connection connection) {
      String sql = "UPDATE characters SET jobRankMove = 0";
      executeNoParam(connection, sql);
   }

   public void resetAllRankMove(Connection connection) {
      String sql = "UPDATE characters SET rankMove = 0";
      executeNoParam(connection, sql);
   }

   public void updateJobRank(Connection connection, int characterId, int rank, int rankMove) {
      String sql = "UPDATE characters SET jobRank = ?, jobRankMove = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, rank);
         ps.setInt(2, rankMove);
         ps.setInt(3, characterId);
      });
   }

   public void updateRank(Connection connection, int characterId, int rank, int rankMove) {
      String sql = "UPDATE characters SET rank = ?, rankMove = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, rank);
         ps.setInt(2, rankMove);
         ps.setInt(3, characterId);
      });
   }

   public int create(Connection connection, int str, int dex, int luk, int int_, int gmLevel, int skinColor,
                     int gender, int jobId, int hair, int face, int mapId, int meso, int accountId, String name,
                     int worldId, int hp, int mp, int maxHp, int maxMp, int level, int ap, int[] sp) {
      String sql = "INSERT INTO characters (str, dex, luk, `int`, gm, skincolor, gender, job, hair, face, map, meso, spawnpoint, accountid, name, world, hp, mp, maxhp, maxmp, level, ap, sp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
      return insertAndReturnKey(connection, sql, ps -> {
         ps.setInt(1, str);
         ps.setInt(2, dex);
         ps.setInt(3, luk);
         ps.setInt(4, int_);
         ps.setInt(5, gmLevel);
         ps.setInt(6, skinColor);
         ps.setInt(7, gender);
         ps.setInt(8, jobId);
         ps.setInt(9, hair);
         ps.setInt(10, face);
         ps.setInt(11, mapId);
         ps.setInt(12, meso);
         ps.setInt(13, 0);
         ps.setInt(14, accountId);
         ps.setString(15, name);
         ps.setInt(16, worldId);
         ps.setInt(17, hp);
         ps.setInt(18, mp);
         ps.setInt(19, maxHp);
         ps.setInt(20, maxMp);
         ps.setInt(21, level);
         ps.setInt(22, ap);

         StringBuilder sps = new StringBuilder();
         for (int value : sp) {
            sps.append(value);
            sps.append(",");
         }
         String spString = sps.toString();
         ps.setString(23, spString.substring(0, spString.length() - 1));
      });
   }

   public void update(Connection connection, int characterId, int level, int fame, int str, int dex, int luk, int int_,
                      int exp, int gachaponExp, int hp, int mp, int maxHp, int maxMp, String sp, int ap, int gmLevel,
                      int skinColor, int gender, int jobId, int hair, int face, int mapId, int meso, int hpMpApUsed,
                      int spawnPoint, int partyId, int buddyCapacity, int messengerId, int messengerPosition,
                      int mountLevel, int mountExp, int mountTiredness, int equipSlots, int useSlots, int setupSlots,
                      int etcSlots, int monsterBookCover, int vanquisherStage, int dojoPoints, int lastDojoStage,
                      int finishedDojoTutorial, int vanquisherKills, int matchCardWins, int matchCardLosses,
                      int matchCardTies, int omokWins, int omokLosses, int omokTies, String dataString, int questFame,
                      long jailExpiration, int partnerId, int marriageItemId, long lastExpGainTime, int ariantPoints,
                      boolean partySearchInvite) {
      String sql = "UPDATE characters SET level = ?, fame = ?, str = ?, dex = ?, luk = ?, `int` = ?, exp = ?, " +
            "gachaexp = ?, hp = ?, mp = ?, maxhp = ?, maxmp = ?, sp = ?, ap = ?, gm = ?, skincolor = ?, " +
            "gender = ?, job = ?, hair = ?, face = ?, map = ?, meso = ?, hpMpUsed = ?, spawnpoint = ?, party = ?, " +
            "buddyCapacity = ?, messengerid = ?, messengerposition = ?, mountlevel = ?, mountexp = ?, " +
            "mounttiredness= ?, equipslots = ?, useslots = ?, setupslots = ?, etcslots = ?,  monsterbookcover = ?, " +
            "vanquisherStage = ?, dojoPoints = ?, lastDojoStage = ?, finishedDojoTutorial = ?, vanquisherKills = ?, " +
            "matchcardwins = ?, matchcardlosses = ?, matchcardties = ?, omokwins = ?, omoklosses = ?, omokties = ?, " +
            "dataString = ?, fquest = ?, jailexpire = ?, partnerId = ?, marriageItemId = ?, lastExpGainTime = ?, " +
            "ariantPoints = ?, partySearch = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, level);
         ps.setInt(2, fame);
         ps.setInt(3, str);
         ps.setInt(4, dex);
         ps.setInt(5, luk);
         ps.setInt(6, int_);
         ps.setInt(7, exp);
         ps.setInt(8, gachaponExp);
         ps.setInt(9, hp);
         ps.setInt(10, mp);
         ps.setInt(11, maxHp);
         ps.setInt(12, maxMp);
         ps.setString(13, sp);
         ps.setInt(14, ap);
         ps.setInt(15, gmLevel);
         ps.setInt(16, skinColor);
         ps.setInt(17, gender);
         ps.setInt(18, jobId);
         ps.setInt(19, hair);
         ps.setInt(20, face);
         ps.setInt(21, mapId);
         ps.setInt(22, meso);
         ps.setInt(23, hpMpApUsed);
         ps.setInt(24, spawnPoint);
         ps.setInt(25, partyId);
         ps.setInt(26, buddyCapacity);
         ps.setInt(27, messengerId);
         ps.setInt(28, messengerPosition);
         ps.setInt(29, mountLevel);
         ps.setInt(30, mountExp);
         ps.setInt(31, mountTiredness);
         ps.setInt(32, equipSlots);
         ps.setInt(33, useSlots);
         ps.setInt(34, setupSlots);
         ps.setInt(35, etcSlots);
         ps.setInt(36, monsterBookCover);
         ps.setInt(37, vanquisherStage);
         ps.setInt(38, dojoPoints);
         ps.setInt(39, lastDojoStage);
         ps.setInt(40, finishedDojoTutorial);
         ps.setInt(41, vanquisherKills);
         ps.setInt(42, matchCardWins);
         ps.setInt(43, matchCardLosses);
         ps.setInt(44, matchCardTies);
         ps.setInt(45, omokWins);
         ps.setInt(46, omokLosses);
         ps.setInt(47, omokTies);
         ps.setString(48, dataString);
         ps.setInt(49, questFame);
         ps.setLong(50, jailExpiration);
         ps.setInt(51, partnerId);
         ps.setInt(52, marriageItemId);
         ps.setTimestamp(53, new Timestamp(lastExpGainTime));
         ps.setInt(54, ariantPoints);
         ps.setBoolean(55, partySearchInvite);
         ps.setInt(56, characterId);
      });
   }

   public void updateName(Connection connection, int characterId, String newName) {
      String sql = "UPDATE characters SET name = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setString(1, newName);
         ps.setInt(2, characterId);
      });
   }

   public void performNameChange(Connection connection, int characterId, String oldName, String newName, int nameChangeId) {
      updateName(connection, characterId, newName);
      RingAdministrator.getInstance().updatePartnerName(connection, newName, oldName);
        /*try (PreparedStatement ps = con.prepareStatement("UPDATE playernpcs SET name = ? WHERE name = ?")) {
            ps.setString(1, newName);
            ps.setString(2, oldName);
            ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
            FilePrinter.printError(FilePrinter.CHANGE_CHARACTER_NAME, e, "Character ID : " + characterId);
            return false;
        }
        try (PreparedStatement ps = con.prepareStatement("UPDATE gifts SET `from` = ? WHERE `from` = ?")) {
            ps.setString(1, newName);
            ps.setString(2, oldName);
            ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
            FilePrinter.printError(FilePrinter.CHANGE_CHARACTER_NAME, e, "Character ID : " + characterId);
            return false;
        }
        try (PreparedStatement ps = con.prepareStatement("UPDATE dueypackages SET SenderName = ? WHERE SenderName = ?")) {
            ps.setString(1, newName);
            ps.setString(2, oldName);
            ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
            FilePrinter.printError(FilePrinter.CHANGE_CHARACTER_NAME, e, "Character ID : " + characterId);
            return false;
        }
        try (PreparedStatement ps = con.prepareStatement("UPDATE dueypackages SET SenderName = ? WHERE SenderName = ?")) {
            ps.setString(1, newName);
            ps.setString(2, oldName);
            ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
            FilePrinter.printError(FilePrinter.CHANGE_CHARACTER_NAME, e, "Character ID : " + characterId);
            return false;
        }
        try (PreparedStatement ps = con.prepareStatement("UPDATE inventoryitems SET owner = ? WHERE owner = ?")) { //GMS doesn't do this
            ps.setString(1, newName);
            ps.setString(2, oldName);
            ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
            FilePrinter.printError(FilePrinter.CHANGE_CHARACTER_NAME, e, "Character ID : " + characterId);
            return false;
        }
        try (PreparedStatement ps = con.prepareStatement("UPDATE mts_items SET owner = ? WHERE owner = ?")) { //GMS doesn't do this
            ps.setString(1, newName);
            ps.setString(2, oldName);
            ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
            FilePrinter.printError(FilePrinter.CHANGE_CHARACTER_NAME, e, "Character ID : " + characterId);
            return false;
        }
        try (PreparedStatement ps = con.prepareStatement("UPDATE newyear SET sendername = ? WHERE sendername = ?")) {
            ps.setString(1, newName);
            ps.setString(2, oldName);
            ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
            FilePrinter.printError(FilePrinter.CHANGE_CHARACTER_NAME, e, "Character ID : " + characterId);
            return false;
        }
        try (PreparedStatement ps = con.prepareStatement("UPDATE newyear SET receivername = ? WHERE receivername = ?")) {
            ps.setString(1, newName);
            ps.setString(2, oldName);
            ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
            FilePrinter.printError(FilePrinter.CHANGE_CHARACTER_NAME, e, "Character ID : " + characterId);
            return false;
        }
        try (PreparedStatement ps = con.prepareStatement("UPDATE notes SET `to` = ? WHERE `to` = ?")) {
            ps.setString(1, newName);
            ps.setString(2, oldName);
            ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
            FilePrinter.printError(FilePrinter.CHANGE_CHARACTER_NAME, e, "Character ID : " + characterId);
            return false;
        }
        try (PreparedStatement ps = con.prepareStatement("UPDATE notes SET `from` = ? WHERE `from` = ?")) {
            ps.setString(1, newName);
            ps.setString(2, oldName);
            ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
            FilePrinter.printError(FilePrinter.CHANGE_CHARACTER_NAME, e, "Character ID : " + characterId);
            return false;
        }
        try (PreparedStatement ps = con.prepareStatement("UPDATE nxcode SET retriever = ? WHERE retriever = ?")) {
            ps.setString(1, newName);
            ps.setString(2, oldName);
            ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
            FilePrinter.printError(FilePrinter.CHANGE_CHARACTER_NAME, e, "Character ID : " + characterId);
            return false;
        }*/
      if (nameChangeId != -1) {
         NameChangeAdministrator.getInstance().markCompleted(connection, nameChangeId);
      }
   }

   public void moveWorld(Connection connection, int characterId, int worldId, int oldMesoAmount) {
      String sql = "UPDATE characters SET world = ?, meso = ?, guildid = ?, guildrank = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, worldId);
         ps.setInt(2, Math.min(oldMesoAmount, 1000000)); //might want a limit in ServerConstants for this
         ps.setInt(3, 0);
         ps.setInt(4, 5);
         ps.setInt(5, characterId);
      });
   }

   public void performWorldTransfer(Connection connection, int characterId, int oldWorld, int newWorld, int worldTransferId) {
      int mesos = CharacterProvider.getInstance().getMesosForCharacter(connection, characterId);
      moveWorld(connection, characterId, newWorld, mesos);
      BuddyAdministrator.getInstance().deleteForCharacterOrBuddyId(connection, characterId);
      WorldTransferAdministrator.getInstance().markComplete(connection, worldTransferId);
   }

   public void setFamilyId(Connection connection, int characterId, int familyId) {
      String sql = "UPDATE characters SET familyid = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, familyId);
         ps.setInt(2, characterId);
      });
   }
}
