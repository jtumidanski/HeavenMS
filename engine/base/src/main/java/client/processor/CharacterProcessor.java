package client.processor;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import client.KeyBinding;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleDisease;
import client.MapleFamily;
import client.MapleJob;
import client.MapleMount;
import client.MapleQuestStatus;
import client.MapleSkinColor;
import client.Ring;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import client.creator.CharacterFactoryRecipe;
import client.database.administrator.AreaInfoAdministrator;
import client.database.administrator.BbsThreadAdministrator;
import client.database.administrator.BuddyAdministrator;
import client.database.administrator.CharacterAdministrator;
import client.database.administrator.CoolDownAdministrator;
import client.database.administrator.EventStatAdministrator;
import client.database.administrator.FameLogAdministrator;
import client.database.administrator.InventoryEquipmentAdministrator;
import client.database.administrator.InventoryItemAdministrator;
import client.database.administrator.KeyMapAdministrator;
import client.database.administrator.MedalMapAdministrator;
import client.database.administrator.MonsterBookAdministrator;
import client.database.administrator.MtsCartAdministrator;
import client.database.administrator.MtsItemAdministrator;
import client.database.administrator.PetAdministrator;
import client.database.administrator.PlayerDiseaseAdministrator;
import client.database.administrator.QuestProgressAdministrator;
import client.database.administrator.QuestStatusAdministrator;
import client.database.administrator.RingAdministrator;
import client.database.administrator.SavedLocationAdministrator;
import client.database.administrator.ServerQueueAdministrator;
import client.database.administrator.SkillAdministrator;
import client.database.administrator.SkillMacroAdministrator;
import client.database.administrator.TeleportRockLocationAdministrator;
import client.database.administrator.WishListAdministrator;
import client.database.data.CharacterData;
import client.database.data.CharacterGuildFamilyData;
import client.database.data.CharacterIdNameAccountId;
import client.database.provider.AccountProvider;
import client.database.provider.AreaInfoProvider;
import client.database.provider.BuddyProvider;
import client.database.provider.CharacterProvider;
import client.database.provider.CoolDownProvider;
import client.database.provider.EventStatProvider;
import client.database.provider.FameLogProvider;
import client.database.provider.InventoryEquipmentProvider;
import client.database.provider.InventoryItemProvider;
import client.database.provider.KeyMapProvider;
import client.database.provider.MedalMapProvider;
import client.database.provider.PetIgnoreProvider;
import client.database.provider.PlayerDiseaseProvider;
import client.database.provider.QuestProgressProvider;
import client.database.provider.QuestStatusProvider;
import client.database.provider.SavedLocationProvider;
import client.database.provider.SkillMacroProvider;
import client.database.provider.SkillProvider;
import client.database.provider.TeleportRockProvider;
import client.database.provider.WorldTransferProvider;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleCashIdGenerator;
import constants.GameConstants;
import constants.ItemConstants;
import constants.ServerConstants;
import net.server.Server;
import net.server.SkillMacro;
import net.server.guild.MapleGuildCharacter;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import net.server.world.World;
import server.events.RescueGaga;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.MapleMapManager;
import server.maps.MaplePortal;
import server.maps.SavedLocation;
import server.maps.SavedLocationType;
import server.quest.MapleQuest;
import tools.DatabaseConnection;
import tools.Pair;

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
      return DatabaseConnection.getInstance().withConnectionResult(connection -> CharacterProvider.getInstance().getIdForName(connection, name)).orElse(-1);
   }

   public String getNameById(int id) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> CharacterProvider.getInstance().getNameForId(connection, id)).orElse(null);
   }

   public CharacterIdNameAccountId getCharacterFromDatabase(String name) {
      return DatabaseConnection.getInstance().withConnectionResultOpt(connection -> CharacterProvider.getInstance().getByName(connection, name)).orElse(null);
   }

   public boolean doWorldTransfer(Connection con, int characterId, int oldWorld, int newWorld, int worldTransferId) {
      CharacterAdministrator.getInstance().performWorldTransfer(con, characterId, oldWorld, newWorld, worldTransferId);
      return true;
   }

   public String checkWorldTransferEligibility(Connection con, int characterId, int oldWorld, int newWorld) {
      if (!ServerConstants.ALLOW_CASHSHOP_WORLD_TRANSFER) {
         return "World transfers disabled.";
      }

      int accountId = -1;

      Optional<CharacterData> characterDataResult = CharacterProvider.getInstance().getById(con, characterId);
      if (characterDataResult.isEmpty()) {
         return "Character does not exist.";
      }

      CharacterData characterData = characterDataResult.get();

      accountId = characterData.accountId();
      if (characterData.level() < 20) {
         return "Character is under level 20.";
      }
      if (characterData.familyId() != -1) {
         return "Character is in family.";
      }
      if (characterData.partnerId() != 0) {
         return "Character is married.";
      }
      if (characterData.guildId() != 0 && characterData.guildRank() < 2) {
         return "Character is the leader of a guild.";
      }

      Calendar tempBan = AccountProvider.getInstance().getTempBanCalendar(con, accountId);
      if (tempBan != null) {
         return "Account has been banned.";
      }

      int charCountInNewWorld = CharacterProvider.getInstance().getCharactersInWorld(con, accountId, newWorld);
      if (charCountInNewWorld >= 3) {
         return "Too many characters on destination world.";
      }

      return null;
   }

   public boolean deleteCharFromDB(MapleCharacter player, int senderAccId) {
      int cid = player.getId();
      if (!Server.getInstance().haveCharacterEntry(senderAccId, cid)) {    // thanks zera (EpiphanyMS) for pointing a critical exploit with non-authored character deletion request
         return false;
      }

      DatabaseConnection.getInstance().withConnection(connection -> {
         int world = CharacterProvider.getInstance().getWorldId(connection, cid);

         BuddyProvider.getInstance().getBuddies(connection, cid).stream()
               .map(buddyId -> Server.getInstance().getWorld(world).getPlayerStorage().getCharacterById(buddyId))
               .flatMap(Optional::stream)
               .forEach(buddy -> buddy.deleteBuddy(cid));

         BuddyAdministrator.getInstance().deleteForCharacter(connection, cid);
         BbsThreadAdministrator.getInstance().deleteThreadsFromCharacter(connection, cid);
         CharacterProvider.getInstance().getGuildDataForCharacter(connection, cid, senderAccId).ifPresent(result -> Server.getInstance().deleteGuildCharacter(
               new MapleGuildCharacter(player, cid, 0, result.name(), (byte) -1, (byte) -1, 0, result.guildRank(), result.guildId(), false, result.allianceRank())));
         WishListAdministrator.getInstance().deleteForCharacter(connection, cid);
         CoolDownAdministrator.getInstance().deleteForCharacter(connection, cid);
         PlayerDiseaseAdministrator.getInstance().deleteForCharacter(connection, cid);
         AreaInfoAdministrator.getInstance().deleteForCharacter(connection, cid);
         MonsterBookAdministrator.getInstance().deleteForCharacter(connection, cid);
         CharacterAdministrator.getInstance().deleteCharacter(connection, cid);
         FameLogAdministrator.getInstance().deleteForCharacter(connection, cid);
         cleanupInventoryEquipment(connection, cid);
         deleteQuestProgressWhereCharacterId(connection, cid);
         FredrickProcessor.removeFredrickLog(cid);
         MtsItemAdministrator.getInstance().deleteForCharacter(connection, cid);
         MtsCartAdministrator.getInstance().deleteForCharacter(connection, cid);
         InventoryItemAdministrator.getInstance().deleteForCharacter(connection, cid);
         KeyMapAdministrator.getInstance().deleteForCharacter(connection, cid);
         SavedLocationAdministrator.getInstance().deleteForCharacter(connection, cid);
         TeleportRockLocationAdministrator.getInstance().deleteForCharacter(connection, cid);
         SkillMacroAdministrator.getInstance().deleteForCharacter(connection, cid);
         SkillAdministrator.getInstance().deleteForCharacter(connection, cid);
         EventStatAdministrator.getInstance().deleteForCharacter(connection, cid);
         ServerQueueAdministrator.getInstance().deleteForCharacter(connection, cid);
      });
      Server.getInstance().deleteCharacterEntry(senderAccId, cid);
      return true;
   }

   private void cleanupInventoryEquipment(Connection connection, int cid) {
      InventoryItemProvider.getInstance().get(connection, cid).forEach(pair -> {
         InventoryEquipmentProvider.getInstance().getRings(connection, pair.getLeft()).stream()
               .filter(ringId -> ringId > 1)
               .forEach(ringId -> {
                  RingAdministrator.getInstance().deleteRing(connection, ringId);
                  MapleCashIdGenerator.getInstance().freeCashId(ringId);
               });
         InventoryEquipmentAdministrator.getInstance().deleteById(connection, pair.getLeft());
         if (pair.getRight() > -1) {
            PetAdministrator.getInstance().deletePet(connection, pair.getRight());
            MapleCashIdGenerator.getInstance().freeCashId(pair.getRight());
         }
      });
   }

   public void deleteQuestProgressWhereCharacterId(Connection con, int cid) {
      MedalMapAdministrator.getInstance().deleteForCharacter(con, cid);
      QuestProgressAdministrator.getInstance().deleteForCharacter(con, cid);
      QuestStatusAdministrator.getInstance().deleteForCharacter(con, cid);
   }

   private void loadPlayerDiseases(Connection connection, CharacterData characterData, MapleCharacter mapleCharacter) {
      Map<MapleDisease, Pair<Long, MobSkill>> loadedDiseases = new LinkedHashMap<>();
      PlayerDiseaseProvider.getInstance().getForCharacter(connection, characterData.id()).forEach(playerDiseaseData -> {
         final MapleDisease disease = MapleDisease.ordinal(playerDiseaseData.disease());
         if (disease != MapleDisease.NULL) {
            MobSkill ms = MobSkillFactory.getMobSkill(playerDiseaseData.mobSkillId(), playerDiseaseData.mobSkillLevel());
            if (ms != null) {
               loadedDiseases.put(disease, new Pair<>((long) playerDiseaseData.length(), ms));
            }
         }
      });
      PlayerDiseaseAdministrator.getInstance().deleteForCharacter(connection, characterData.id());
      if (!loadedDiseases.isEmpty()) {
         Server.getInstance().getPlayerBuffStorage().addDiseasesToStorage(mapleCharacter.getId(), loadedDiseases);
      }
   }

   private void loadCoolDowns(Connection connection, CharacterData characterData, MapleCharacter mapleCharacter) {
      long curTime = Server.getInstance().getCurrentTime();
      CoolDownProvider.getInstance().getForCharacter(connection, characterData.id()).forEach(coolDownData -> {
         if (coolDownData.skillId() == 5221999 || (coolDownData.length() + coolDownData.startTime() >= curTime)) {
            mapleCharacter.giveCoolDowns(coolDownData.skillId(), coolDownData.startTime(), coolDownData.length());
         }
      });
      CoolDownAdministrator.getInstance().deleteForCharacter(connection, characterData.id());
   }

   private void loadSkills(Connection connection, CharacterData characterData, MapleCharacter mapleCharacter) {
      SkillProvider.getInstance().getSkills(connection, characterData.id()).forEach(skillData -> {
         Optional<Skill> skill = SkillFactory.getSkill(skillData.skillId());
         if (skill.isPresent()) {
            SkillEntry skillEntry = new SkillEntry(skillData.skillLevel(), skillData.masterLevel(), skillData.expiration());
            mapleCharacter.addSkill((skill.get()), skillEntry);
         }
      });
   }

   private void loadQuests(Connection connection, CharacterData characterData, MapleCharacter mapleCharacter) {
      Map<Integer, MapleQuestStatus> loadedQuestStatus = new LinkedHashMap<>();
      QuestStatusProvider.getInstance().getQuestData(connection, characterData.id()).forEach(questData -> {
         MapleQuest q = MapleQuest.getInstance(questData.questId());
         MapleQuestStatus status = new MapleQuestStatus(q, MapleQuestStatus.Status.getById(questData.status()));
         if (questData.time() > -1) {
            status.setCompletionTime(questData.time() * 1000);
         }

         if (questData.expires() > 0) {
            status.setExpirationTime(questData.expires());
         }

         status.setForfeited(questData.forfeited());
         status.setCompleted(questData.completed());
         mapleCharacter.addQuest(q.getId(), status);
         loadedQuestStatus.put(questData.questStatusId(), status);
      });
      QuestProgressProvider.getInstance().getProgress(connection, characterData.id()).forEach(questProgress -> {
         MapleQuestStatus status = loadedQuestStatus.get(questProgress.questStatusId());
         if (status != null) {
            status.setProgress(questProgress.progressId(), questProgress.progress());
         }
      });
      MedalMapProvider.getInstance().get(connection, characterData.id()).forEach(medalMap -> {
         MapleQuestStatus status = loadedQuestStatus.get(medalMap.getLeft());
         if (status != null) {
            status.addMedalMap(medalMap.getRight());
         }
      });
      loadedQuestStatus.clear();
   }

   private void loadTeleportLocations(Connection connection, CharacterData data, MapleCharacter mapleCharacter) {
      List<Pair<Integer, Integer>> locations = TeleportRockProvider.getInstance().getTeleportLocations(connection, data.id());
      int v = 0, r = 0;
      for (Pair<Integer, Integer> location : locations) {
         if (location.getRight() == 1) {
            mapleCharacter.addVipTeleportRockMap(location.getLeft());
            v++;
         } else {
            mapleCharacter.addTeleportRockMap(location.getLeft());
            r++;
         }
      }
      while (v < 10) {
         mapleCharacter.addVipTeleportRockMap(999999999);
         v++;
      }
      while (r < 5) {
         mapleCharacter.addTeleportRockMap(999999999);
         r++;
      }
   }

   private void loadMessengerData(CharacterData data, MapleCharacter mapleCharacter, World world) {
      if (data.messengerId() > 0 && data.messengerPosition() < 4 && data.messengerPosition() > -1) {
         world.getMessenger(data.messengerId()).ifPresent(messenger -> mapleCharacter.setMessenger(messenger, data.messengerPosition()));
      }
   }

   private void loadPartyData(CharacterData data, MapleCharacter mapleCharacter, World world) {
      MapleParty party = world.getParty(data.partyId());
      if (party != null) {
         //TODO this seems like a bug
         mapleCharacter.setMPC(party.getMemberById(data.id()));
         if (mapleCharacter.getMPC() != null) {
            mapleCharacter.setMPC(new MaplePartyCharacter(mapleCharacter));
            mapleCharacter.setParty(party);
         }
      }
   }

   private void loadMapData(MapleClient client, CharacterData data, MapleCharacter mapleCharacter) {
      MapleMapManager mapManager = client.getChannelServer().getMapFactory();
      mapleCharacter.setMap(mapManager.getMap(data.map()));
      if (mapleCharacter.getMap() == null) {
         mapleCharacter.setMap(mapManager.getMap(100000000));
      }

      MaplePortal portal = mapleCharacter.getMap().getPortal(data.spawnPoint());
      if (portal == null) {
         portal = mapleCharacter.getMap().getPortal(0);
         mapleCharacter.setInitialSpawnPoint(0);
      }
      mapleCharacter.setPosition(portal.getPosition());
   }

   private void loadPetIgnores(Connection connection, CharacterData data, MapleCharacter mapleCharacter) {
      InventoryItemProvider.getInstance().getPetsForCharacter(connection, data.id()).forEach(petId -> {
         mapleCharacter.resetExcluded(petId);
         PetIgnoreProvider.getInstance().getIgnoresForPet(connection, petId).forEach(itemId -> mapleCharacter.addExcluded(petId, itemId));
      });
      mapleCharacter.commitExcludedItems();
   }

   private void correctMarriageDatabaseData(CharacterData data, MapleCharacter mapleCharacter, World world) {
      if (data.marriageItemId() > 0 && data.partnerId() <= 0) {
         mapleCharacter.setMarriageItemId(-1);
      } else if (data.partnerId() > 0 && world.getRelationshipId(data.id()) <= 0) {
         mapleCharacter.setMarriageItemId(-1);
         mapleCharacter.setPartnerId(-1);
      }
   }

   private void loadInventory(int characterId, boolean channelserver, MapleCharacter mapleCharacter) {
      short sandboxCheck = 0x0;
      for (Pair<Item, MapleInventoryType> item : ItemFactory.INVENTORY.loadItems(characterId, !channelserver)) {
         sandboxCheck |= item.getLeft().flag();

         mapleCharacter.getInventory(item.getRight()).addItemFromDB(item.getLeft());
         Item itemz = item.getLeft();
         if (itemz.petId() > -1) {
            if (itemz.pet().isDefined() && itemz.pet().get().summoned()) {
               mapleCharacter.addPet(itemz.pet().get());
            }
            continue;
         }

         MapleInventoryType mit = item.getRight();
         if (mit.equals(MapleInventoryType.EQUIP) || mit.equals(MapleInventoryType.EQUIPPED)) {
            Equip equip = (Equip) item.getLeft();
            if (equip.ringId() > -1) {
               Ring ring = MapleRingProcessor.getInstance().loadFromDb(equip.ringId());
               if (item.getRight().equals(MapleInventoryType.EQUIPPED)) {
                  ring.equip();
               }

               mapleCharacter.addPlayerRing(ring);
            }
         }
      }
      if ((sandboxCheck & ItemConstants.SANDBOX) == ItemConstants.SANDBOX) {
         mapleCharacter.setHasSandboxItem();
      }
   }

   public MapleCharacter loadCharFromDB(int characterId, MapleClient client, boolean channelserver) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> {
         Optional<CharacterData> characterDataOptional = CharacterProvider.getInstance().getById(connection, characterId);
         if (characterDataOptional.isEmpty()) {
            return null;
         }
         CharacterData characterData = characterDataOptional.get();

         MapleCharacter mapleCharacter = loadFromCharacterData(characterData);
         mapleCharacter.setClient(client);
         mapleCharacter.setMGC(new MapleGuildCharacter(mapleCharacter));
         CharacterProcessor.getInstance().loadInventory(characterData.id(), channelserver, mapleCharacter);
         World world = Server.getInstance().getWorld(characterData.world());
         CharacterProcessor.getInstance().correctMarriageDatabaseData(characterData, mapleCharacter, world);
         NewYearCardProcessor.getInstance().loadPlayerNewYearCards(mapleCharacter);
         CharacterProcessor.getInstance().loadPetIgnores(connection, characterData, mapleCharacter);

         if (channelserver) {
            CharacterProcessor.getInstance().loadMapData(client, characterData, mapleCharacter);
            CharacterProcessor.getInstance().loadPartyData(characterData, mapleCharacter, world);
            CharacterProcessor.getInstance().loadMessengerData(characterData, mapleCharacter, world);

            mapleCharacter.setLoggedIn();
         }

         CharacterProcessor.getInstance().loadTeleportLocations(connection, characterData, mapleCharacter);

         AccountProvider.getInstance().getAccountDataById(connection, characterData.accountId()).ifPresent(accountData -> {
            MapleClient retClient = mapleCharacter.getClient();
            retClient.setAccountName(accountData.name());
            retClient.setCharacterSlots(accountData.characterSlots());
            retClient.setLanguage(accountData.language());
         });

         AreaInfoProvider.getInstance().getAreaInfo(connection, characterData.id())
               .forEach(areaInfo -> mapleCharacter.getAreaInfos().put(areaInfo.getLeft(), areaInfo.getRight()));

         EventStatProvider.getInstance().getInfo(connection, characterData.id()).stream()
               .filter(info -> info.getLeft().contentEquals("rescueGaga"))
               .forEach(info -> mapleCharacter.getEvents().put(info.getLeft(), new RescueGaga(info.getRight())));

         mapleCharacter.initCashShop();
         mapleCharacter.initAutoBanManager();

         CharacterProvider.getInstance().getHighestLevelOtherCharacterData(connection, characterData.accountId(), characterData.id())
               .ifPresent(otherCharacterData -> mapleCharacter.setLinkedCharacterInformation(otherCharacterData.name(), otherCharacterData.level()));

         if (channelserver) {
            CharacterProcessor.getInstance().loadQuests(connection, characterData, mapleCharacter);
            CharacterProcessor.getInstance().loadSkills(connection, characterData, mapleCharacter);
            CharacterProcessor.getInstance().loadCoolDowns(connection, characterData, mapleCharacter);
            CharacterProcessor.getInstance().loadPlayerDiseases(connection, characterData, mapleCharacter);

            SkillMacroProvider.getInstance().getForCharacter(connection, characterData.id()).forEach(skillMacroData -> {
               int position = skillMacroData.position();
               SkillMacro macro = new SkillMacro(skillMacroData.name(), skillMacroData.shout(), skillMacroData.skill1Id(), skillMacroData.skill2Id(),
                     skillMacroData.skill3Id(),
                     position);
               mapleCharacter.updateMacros(position, macro);
            });

            KeyMapProvider.getInstance().getForCharacter(connection, characterData.id())
                  .forEach(keyMapData -> mapleCharacter.getKeymap().put(keyMapData.key(), new KeyBinding(keyMapData.theType(), keyMapData.action())));

            SavedLocationProvider.getInstance().getForCharacter(connection, characterData.id())
                  .forEach(savedLocationData -> mapleCharacter.updateSavedLocation(SavedLocationType.valueOf(savedLocationData.locationType()).ordinal(), new SavedLocation(savedLocationData.mapId(), savedLocationData.portalId())));

            FameLogProvider.getInstance().getForCharacter(connection, characterData.id()).forEach(fameLogData -> mapleCharacter.giveFame(fameLogData.getLeft(), fameLogData.getRight().getTime()));

            BuddyListProcessor.getInstance().loadFromDb(characterData.id(), mapleCharacter.getBuddylist());
            mapleCharacter.setStorage(world.getAccountStorage(characterData.accountId()));

            mapleCharacter.reapplyLocalStats();
            mapleCharacter.changeHpMp(mapleCharacter.getHp(), mapleCharacter.getMp(), true);

            int mountid = mapleCharacter.getJobType() * 10000000 + 1004;

            MapleMount mapleMount;
            if (mapleCharacter.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -18) != null) {
               mapleMount = new MapleMount(mapleCharacter.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -18).id(), mountid);
            } else {
               mapleMount = new MapleMount(0, mountid);
            }
            mapleMount.exp_$eq(characterData.mountExp());
            mapleMount.level_$eq(characterData.mountLevel());
            mapleMount.tiredness_$eq(characterData.mountTiredness());
            mapleMount.active_$eq(false);
            mapleCharacter.setMount(mapleMount);
         }
         return mapleCharacter;
      }).orElse(null);
   }

   private MapleCharacter loadFromCharacterData(CharacterData characterData) {
      MapleCharacter mapleCharacter = new MapleCharacter(characterData.id(), characterData.accountId(),
            characterData.str(), characterData.dex(), characterData.intelligence(),
            characterData.luk(), characterData.hp(), characterData.mp(), characterData.meso());
      mapleCharacter.setName(characterData.name());
      mapleCharacter.setLevel(characterData.level());
      mapleCharacter.setFame(characterData.fame());
      mapleCharacter.setQuestFame(characterData.questFame());
      mapleCharacter.setExp(characterData.exp());
      mapleCharacter.setGachaExp(characterData.gachaponExp());
      mapleCharacter.setMaxHp(characterData.maxHp());
      mapleCharacter.setMaxMp(characterData.maxMp());
      mapleCharacter.setHpMpApUsed(characterData.hpMpUsed());
      mapleCharacter.setHasMerchantNoUpdate(characterData.merchant());
      mapleCharacter.setRemainingAp(characterData.ap());
      mapleCharacter.loadCharSkillPoints(characterData.sp());
      mapleCharacter.setMerchantMesoNoUpdate(characterData.merchantMeso());
      mapleCharacter.setGM(characterData.gm());
      mapleCharacter.setSkinColor(MapleSkinColor.getById(characterData.skinColor()));
      mapleCharacter.setGender(characterData.gender());
      mapleCharacter.setJob(MapleJob.getById(characterData.job()));
      mapleCharacter.setFinishedDojoTutorial(characterData.finishedDojoTutorial());
      mapleCharacter.setVanquisherKills(characterData.vanquisherKills());
      mapleCharacter.setOmok(characterData.omok());
      mapleCharacter.setMatchCard(characterData.matchCard());
      mapleCharacter.setHair(characterData.hair());
      mapleCharacter.setFace(characterData.face());
      mapleCharacter.setMapId(characterData.map());
      mapleCharacter.setJailExpiration(characterData.jailExpire());
      mapleCharacter.setInitialSpawnPoint(characterData.spawnPoint());
      mapleCharacter.setWorld(characterData.world());
      mapleCharacter.setRankAndMove(characterData.rank(), characterData.rankMove());
      mapleCharacter.setJobRankAndMove(characterData.jobRank(), characterData.jobRankMove());
      mapleCharacter.setGuildId(characterData.guildId());
      mapleCharacter.setGuildRank(characterData.guildRank());
      mapleCharacter.setAllianceRank(characterData.allianceRank());
      mapleCharacter.setFamilyId(characterData.familyId());
      mapleCharacter.setMonsterBookCover(characterData.monsterBookCover());
      mapleCharacter.initMonsterBook();
      mapleCharacter.setVanquisherStage(characterData.vanquisherStage());
      mapleCharacter.setAriantPoints(characterData.ariantPoints());
      mapleCharacter.setDojoPoints(characterData.dojoPoints());
      mapleCharacter.setDojoStage(characterData.lastDojoStage());
      mapleCharacter.setDataString(characterData.dataString());
      mapleCharacter.initBuddyList(characterData.buddyCapacity());
      mapleCharacter.setLastExpGainTime(characterData.lastExpGainTime().getTime());
      mapleCharacter.setCanRecvPartySearchInvite(characterData.partyInvite());
      mapleCharacter.getInventory(MapleInventoryType.EQUIP).setSlotLimit(characterData.equipSlotLimit());
      mapleCharacter.getInventory(MapleInventoryType.USE).setSlotLimit(characterData.useSlotLimit());
      mapleCharacter.getInventory(MapleInventoryType.SETUP).setSlotLimit(characterData.setupSlotLimit());
      mapleCharacter.getInventory(MapleInventoryType.ETC).setSlotLimit(characterData.etcSlotLimit());
      mapleCharacter.setPartnerId(characterData.partnerId());
      mapleCharacter.setMarriageItemId(characterData.marriageItemId());
      return mapleCharacter;
   }

   public MapleCharacter loadCharacterEntryFromDB(CharacterData characterData, List<Item> equipped) {
      MapleCharacter mapleCharacter = CharacterProcessor.getInstance().loadFromCharacterData(characterData);
      if (equipped != null) {  // players can have no equipped items at all, ofc
         MapleInventory inv = mapleCharacter.getInventory(MapleInventoryType.EQUIPPED);
         for (Item item : equipped) {
            inv.addItemFromDB(item);
         }
      }
      return mapleCharacter;
   }

   public MapleCharacter getDefault(MapleClient c) {
      MapleCharacter ret = new MapleCharacter(-1, c.getAccID(), 12, 5, 4, 4, 50, 5, 0);
      ret.setClient(c);
      ret.setGM(0);
      ret.setMaxHp(50);
      ret.setMaxMp(5);
      ret.setMap(null);
      ret.setJob(MapleJob.BEGINNER);
      ret.setLevel(1);
      ret.initBuddyList(20);
      ret.setMount(null);
      ret.getInventory(MapleInventoryType.EQUIP).setSlotLimit(24);
      ret.getInventory(MapleInventoryType.USE).setSlotLimit(24);
      ret.getInventory(MapleInventoryType.SETUP).setSlotLimit(24);
      ret.getInventory(MapleInventoryType.ETC).setSlotLimit(24);

      // Select a keybinding method
      int[] selectedKey;
      int[] selectedType;
      int[] selectedAction;

      if (ServerConstants.USE_CUSTOM_KEYSET) {
         selectedKey = GameConstants.getCustomKey(true);
         selectedType = GameConstants.getCustomType(true);
         selectedAction = GameConstants.getCustomAction(true);
      } else {
         selectedKey = GameConstants.getCustomKey(false);
         selectedType = GameConstants.getCustomType(false);
         selectedAction = GameConstants.getCustomAction(false);
      }

      for (int i = 0; i < selectedKey.length; i++) {
         ret.getKeymap().put(selectedKey[i], new KeyBinding(selectedType[i], selectedAction[i]));
      }

      //to fix the map 0 lol
      for (int i = 0; i < 5; i++) {
         ret.addTeleportRockMap(999999999);
      }
      for (int i = 0; i < 10; i++) {
         ret.addVipTeleportRockMap(999999999);
      }

      return ret;
   }


   public final boolean insertNewChar(MapleCharacter character, CharacterFactoryRecipe recipe) {
      character.init(recipe.getStr(), recipe.getDex(), recipe.getInt(), recipe.getLuk(), recipe.getMaxHp(), recipe.getMaxMp(), recipe.getMeso());
      character.setMaxHp(recipe.getMaxHp());
      character.setMaxMp(recipe.getMaxMp());
      character.setLevel(recipe.getLevel());
      character.setRemainingAp(recipe.getRemainingAp());
      character.setRemainingSp(GameConstants.getSkillBook(character.getJob().getId()), recipe.getRemainingSp());
      character.setMapId(recipe.getMap());

      List<Pair<Skill, Integer>> startingSkills = recipe.getStartingSkillLevel();
      for (Pair<Skill, Integer> skEntry : startingSkills) {
         Skill skill = skEntry.getLeft();
         character.changeSkillLevel(skill, skEntry.getRight().byteValue(), skill.getMaxLevel(), -1);
      }

      List<Pair<Item, MapleInventoryType>> itemsWithType = recipe.getStartingItems();
      for (Pair<Item, MapleInventoryType> itEntry : itemsWithType) {
         character.getInventory(itEntry.getRight()).addItem(itEntry.getLeft());
      }

      character.getEvents().put("rescueGaga", new RescueGaga(0));


      DatabaseConnection.getInstance().withExplicitCommitConnection(connection -> {
         int key = CharacterAdministrator.getInstance().create(connection, character.getStr(), character.getDex(), character.getLuk(), character.getInt(), character.gmLevel(), character.getSkinColor().getId(),
               character.getGender(), character.getJob().getId(), character.getHair(), character.getFace(), character.getMapId(), Math.abs(character.getMeso()), character.getAccountID(), character.getName(), character.getWorld(), character.getHp(), character.getMp(),
               character.getMaxHp(), character.getMaxMp(), character.getLevel(), character.getRemainingAp(), character.getRemainingSps());
         character.setId(key);

         // Select a keybinding method
         int[] selectedKey;
         int[] selectedType;
         int[] selectedAction;

         if (ServerConstants.USE_CUSTOM_KEYSET) {
            selectedKey = GameConstants.getCustomKey(true);
            selectedType = GameConstants.getCustomType(true);
            selectedAction = GameConstants.getCustomAction(true);
         } else {
            selectedKey = GameConstants.getCustomKey(false);
            selectedType = GameConstants.getCustomType(false);
            selectedAction = GameConstants.getCustomAction(false);
         }


         for (int i = 0; i < selectedKey.length; i++) {
            KeyMapAdministrator.getInstance().create(connection, character.getId(), selectedKey[i], selectedType[i], selectedAction[i]);
         }

         List<Pair<Item, MapleInventoryType>> itemsByType = new ArrayList<>();

         Arrays.stream(MapleInventoryType.values())
               .map(character::getInventory)
               .forEach(inventory -> inventory.list()
                     .forEach(item -> itemsByType.add(new Pair<>(item, inventory.getType()))));

         ItemFactory.INVENTORY.saveItems(itemsByType, character.getId(), connection);

         if (!character.getSkills().isEmpty()) {
            SkillAdministrator.getInstance().create(connection, character.getId(), character.getSkills().entrySet());
         }

         connection.commit();
      });
      return true;
   }

   public Optional<Byte> canDeleteCharacter(int characterId) {
      return DatabaseConnection.getInstance().withConnectionResultOpt(connection -> {
         Optional<CharacterGuildFamilyData> guildFamilyData = CharacterProvider.getInstance().getGuildFamilyInformation(connection, characterId);
         if (guildFamilyData.isEmpty()) {
            return Optional.of((byte) 0x09);
         }
         if (guildFamilyData.get().guildId() != 0 && guildFamilyData.get().guildRank() <= 1) {
            return Optional.of((byte) 0x16);
         } else if (guildFamilyData.get().familyId() != -1) {
            MapleFamily family = Server.getInstance().getWorld(guildFamilyData.get().world()).getFamily(guildFamilyData.get().familyId());
            if (family != null && family.getTotalMembers() > 1) {
               return Optional.of((byte) 0x1D);
            }
         }
         int pendingWorldTransfers = WorldTransferProvider.getInstance().countOutstandingWorldTransfers(connection, characterId);
         if (pendingWorldTransfers > 0) {
            return Optional.of((byte) 0x1A);
         }
         return Optional.empty();
      });
   }
}
