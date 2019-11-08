package client.database.administrator;

import java.sql.Timestamp;
import java.util.List;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import client.database.AbstractQueryExecutor;
import client.database.provider.CharacterProvider;
import entity.Character;
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

   protected void update(EntityManager entityManager, int id, Consumer<Character> consumer) {
      super.update(entityManager, Character.class, id, consumer);
   }

   public void setMerchant(EntityManager entityManager, int characterId, boolean hasMerchant) {
      update(entityManager, characterId, character -> character.setHasMerchant(hasMerchant ? 1 : 0));
   }

   public void setMerchantMesos(EntityManager entityManager, int characterId, int total) {
      update(entityManager, characterId, character -> character.setMerchantMesos(total));
   }

   public void setMerchantMesosBatch(EntityManager entityManager, List<Pair<Integer, Integer>> data) {
      entityManager.getTransaction().begin();
      data.forEach(dataPoint -> {
         Query query = entityManager.createQuery("UPDATE Character SET merchantMesos = :merchantMesos WHERE id = :id");
         query.setParameter("merchantMesos", dataPoint.getRight());
         query.setParameter("id", dataPoint.getLeft());
         query.executeUpdate();
      });
      entityManager.getTransaction().commit();
   }

   public void setName(EntityManager entityManager, int characterId, String name) {
      update(entityManager, characterId, character -> character.setName(name));
   }

   public void logCharacterOut(EntityManager entityManager, int characterId) {
      update(entityManager, characterId, character -> character.setLastLogoutTime(new Timestamp(System.currentTimeMillis())));
   }

   public void setReborns(EntityManager entityManager, int characterId, int value) {
      update(entityManager, characterId, character -> character.setReborns(value));
   }

   public void deleteCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM Character WHERE id = :id");
      query.setParameter("id", characterId);
      execute(entityManager, query);
   }

   public void removeAllCharactersFromGuild(EntityManager entityManager, int guildId) {
      Query query = entityManager.createQuery("UPDATE Character SET guildId = 0, guildRank = 5 WHERE guildId = :guildId");
      query.setParameter("guildId", guildId);
      execute(entityManager, query);
   }

   public void updateGuild(EntityManager entityManager, int characterId, int guildId) {
      update(entityManager, characterId, character -> character.setGuildId(guildId));
   }

   public void updateAllianceRank(EntityManager entityManager, int guildId, int allianceRank) {
      Query query = entityManager.createQuery("UPDATE Character SET allianceRank = :allianceRank WHERE guildId = :guildId");
      query.setParameter("allianceRank", allianceRank);
      query.setParameter("guildId", guildId);
      execute(entityManager, query);
   }

   public void updateGuildStatus(EntityManager entityManager, int characterId, int guildId, int rank, int allianceRank) {
      update(entityManager, characterId, character -> {
         character.setGuildId(guildId);
         character.setGuildRank(rank);
         character.setAllianceRank(allianceRank);
      });
   }

   public void updateGuildStatus(EntityManager entityManager, int characterId, int guildId, int rank) {
      update(entityManager, characterId, character -> {
         character.setGuildId(guildId);
         character.setGuildRank(rank);
      });
   }

   public void removeAllMerchants(EntityManager entityManager) {
      Query query = entityManager.createQuery("UPDATE Character SET hasMerchant = 0");
      execute(entityManager, query);
   }

   public void eraseEngagement(EntityManager entityManager, int characterId) {
      update(entityManager, characterId, character -> {
         character.setMarriageItemId(-1);
         character.setPartnerId(-1);
      });
   }

   public void resetAllJobRankMove(EntityManager entityManager) {
      Query query = entityManager.createQuery("UPDATE Character SET jobRankMove = 0");
      execute(entityManager, query);
   }

   public void resetAllRankMove(EntityManager entityManager) {
      Query query = entityManager.createQuery("UPDATE Character SET rankMove = 0");
      execute(entityManager, query);
   }

   public void updateJobRank(EntityManager entityManager, int characterId, int rank, int rankMove) {
      update(entityManager, characterId, character -> {
         character.setJobRank(rank);
         character.setJobRankMove(rankMove);
      });
   }

   public void updateRank(EntityManager entityManager, int characterId, int rank, int rankMove) {
      update(entityManager, characterId, character -> {
         character.setRank(rank);
         character.setRankMove(rankMove);
      });
   }

   public int create(EntityManager entityManager, int str, int dex, int luk, int int_, int gmLevel, int skinColor,
                     int gender, int jobId, int hair, int face, int mapId, int meso, int accountId, String name,
                     int worldId, int hp, int mp, int maxHp, int maxMp, int level, int ap, int[] sp) {
      Character character = new Character();
      character.setStr(str);
      character.setDex(dex);
      character.setLuk(luk);
      character.setIntelligence(int_);
      character.setGm(gmLevel);
      character.setSkinColor(skinColor);
      character.setGender(gender);
      character.setJob(jobId);
      character.setHair(hair);
      character.setFace(face);
      character.setMap(mapId);
      character.setMeso(meso);
      character.setSpawnPoint(0);
      character.setAccountId(accountId);
      character.setName(name);
      character.setWorld(worldId);
      character.setHp(hp);
      character.setMp(mp);
      character.setMaxHp(maxHp);
      character.setMaxMp(maxMp);
      character.setLevel(level);
      character.setAp(ap);

      StringBuilder sps = new StringBuilder();
      for (int value : sp) {
         sps.append(value);
         sps.append(",");
      }
      String spString = sps.toString();
      character.setSp(spString.substring(0, spString.length() - 1));

      insert(entityManager, character);
      return character.getId();
   }

   public void update(EntityManager entityManager, int characterId, int level, int fame, int str, int dex, int luk, int int_,
                      int exp, int gachaponExp, int hp, int mp, int maxHp, int maxMp, String sp, int ap, int gmLevel,
                      int skinColor, int gender, int jobId, int hair, int face, int mapId, int meso, int hpMpApUsed,
                      int spawnPoint, int partyId, int buddyCapacity, int messengerId, int messengerPosition,
                      int mountLevel, int mountExp, int mountTiredness, int equipSlots, int useSlots, int setupSlots,
                      int etcSlots, int monsterBookCover, int vanquisherStage, int dojoPoints, int lastDojoStage,
                      int finishedDojoTutorial, int vanquisherKills, int matchCardWins, int matchCardLosses,
                      int matchCardTies, int omokWins, int omokLosses, int omokTies, String dataString, int questFame,
                      long jailExpiration, int partnerId, int marriageItemId, long lastExpGainTime, int ariantPoints,
                      boolean partySearchInvite) {
      update(entityManager, characterId, character -> {
         character.setLevel(level);
         character.setFame(fame);
         character.setStr(str);
         character.setDex(dex);
         character.setLuk(luk);
         character.setIntelligence(int_);
         character.setExp(exp);
         character.setGachaponExp(gachaponExp);
         character.setHp(hp);
         character.setMp(mp);
         character.setMaxHp(maxHp);
         character.setMaxMp(maxMp);
         character.setSp(sp);
         character.setAp(ap);
         character.setGm(gmLevel);
         character.setSkinColor(skinColor);
         character.setGender(gender);
         character.setJob(jobId);
         character.setHair(hair);
         character.setFace(face);
         character.setMap(mapId);
         character.setMeso(meso);
         character.setHpMpUsed(hpMpApUsed);
         character.setSpawnPoint(spawnPoint);
         character.setParty(partyId);
         character.setBuddyCapacity(buddyCapacity);
         character.setMessengerId(messengerId);
         character.setMessengerPosition(messengerPosition);
         character.setMountLevel(mountLevel);
         character.setMountExp(mountExp);
         character.setMountTiredness(mountTiredness);
         character.setEquipSlots(equipSlots);
         character.setUseSlots(useSlots);
         character.setSetupSlots(setupSlots);
         character.setEtcSlots(etcSlots);
         character.setMonsterBookCover(monsterBookCover);
         character.setVanquisherStage(vanquisherStage);
         character.setDojoPoints(dojoPoints);
         character.setLastDojoStage(lastDojoStage);
         character.setFinishedDojoTutorial(finishedDojoTutorial);
         character.setVanquisherKills(vanquisherKills);
         character.setMatchCardWins(matchCardWins);
         character.setMatchCardLosses(matchCardLosses);
         character.setMatchCardTies(matchCardTies);
         character.setOmokWins(omokWins);
         character.setOmokLosses(omokLosses);
         character.setOmokTies(omokTies);
         character.setDataString(dataString);
         character.setFquest(questFame);
         character.setJailExpire(jailExpiration);
         character.setPartnerId(partnerId);
         character.setMarriageItemId(marriageItemId);
         character.setLastExpGainTime(new Timestamp(lastExpGainTime));
         character.setAriantPoints(ariantPoints);
         character.setPartySearch(partySearchInvite ? 1 : 0);
      });
   }

   public void performNameChange(EntityManager entityManager, int characterId, String oldName, String newName, int nameChangeId) {
      setName(entityManager, characterId, newName);
      RingAdministrator.getInstance().updatePartnerName(entityManager, newName, oldName);
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
         NameChangeAdministrator.getInstance().markCompleted(entityManager, nameChangeId);
      }
   }

   public void moveWorld(EntityManager entityManager, int characterId, int worldId, int oldMesoAmount) {
      update(entityManager, characterId, character -> {
         character.setWorld(worldId);
         character.setMeso(Math.min(oldMesoAmount, 1000000));
         character.setGuildId(0);
         character.setGuildRank(5);
      });
   }

   public void performWorldTransfer(EntityManager entityManager, int characterId, int oldWorld, int newWorld, int worldTransferId) {
      int mesos = CharacterProvider.getInstance().getMesosForCharacter(entityManager, characterId);
      moveWorld(entityManager, characterId, newWorld, mesos);
      BuddyAdministrator.getInstance().deleteForCharacterOrBuddyId(entityManager, characterId);
      WorldTransferAdministrator.getInstance().markComplete(entityManager, worldTransferId);
   }

   public void setFamilyId(EntityManager entityManager, int characterId, int familyId) {
      update(entityManager, characterId, character -> character.setFamilyId(familyId));
   }
}
