package client.processor;

import java.sql.Connection;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleDisease;
import client.MapleJob;
import client.MapleKeyBinding;
import client.MapleMount;
import client.MapleQuestStatus;
import client.MapleRing;
import client.MapleSkinColor;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import client.SkillMacro;
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
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.manipulator.MapleCashIdGenerator;
import client.newyear.NewYearCardRecord;
import constants.GameConstants;
import constants.ItemConstants;
import constants.ServerConstants;
import net.server.Server;
import net.server.guild.MapleGuildCharacter;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import net.server.world.World;
import server.MaplePortal;
import server.MapleStorage;
import server.events.RescueGaga;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.MapleMapManager;
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

      accountId = characterData.getAccountId();
      if (characterData.getLevel() < 20) {
         return "Character is under level 20.";
      }
      if (characterData.getFamilyId() != -1) {
         return "Character is in family.";
      }
      if (characterData.getPartnerId() != 0) {
         return "Character is married.";
      }
      if (characterData.getGuildId() != 0 && characterData.getGuildRank() < 2) {
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
               new MapleGuildCharacter(player, cid, 0, result.getName(), (byte) -1, (byte) -1, 0, result.getGuildRank(), result.getGuildId(), false, result.getAllianceRank())));
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
      PlayerDiseaseProvider.getInstance().getForCharacter(connection, characterData.getId()).forEach(playerDiseaseData -> {
         final MapleDisease disease = MapleDisease.ordinal(playerDiseaseData.getDisease());
         if (disease != MapleDisease.NULL) {
            MobSkill ms = MobSkillFactory.getMobSkill(playerDiseaseData.getMobSkillId(), playerDiseaseData.getMobSkillLevel());
            if (ms != null) {
               loadedDiseases.put(disease, new Pair<>((long) playerDiseaseData.getLength(), ms));
            }
         }
      });
      PlayerDiseaseAdministrator.getInstance().deleteForCharacter(connection, characterData.getId());
      if (!loadedDiseases.isEmpty()) {
         Server.getInstance().getPlayerBuffStorage().addDiseasesToStorage(mapleCharacter.getId(), loadedDiseases);
      }
   }

   private void loadCoolDowns(Connection connection, CharacterData characterData, MapleCharacter mapleCharacter) {
      long curTime = Server.getInstance().getCurrentTime();
      CoolDownProvider.getInstance().getForCharacter(connection, characterData.getId()).forEach(coolDownData -> {
         if (coolDownData.getSkillId() == 5221999 || (coolDownData.getLength() + coolDownData.getStartTime() >= curTime)) {
            mapleCharacter.giveCoolDowns(coolDownData.getSkillId(), coolDownData.getStartTime(), coolDownData.getLength());
         }
      });
      CoolDownAdministrator.getInstance().deleteForCharacter(connection, characterData.getId());
   }

   private void loadSkills(Connection connection, CharacterData characterData, MapleCharacter mapleCharacter) {
      SkillProvider.getInstance().getSkills(connection, characterData.getId()).forEach(skillData -> {
         Optional<Skill> skill = SkillFactory.getSkill(skillData.getSkillId());
         if (skill.isPresent()) {
            SkillEntry skillEntry = new SkillEntry(skillData.getSkillLevel(), skillData.getMasterLevel(), skillData.getExpiration());
            mapleCharacter.addSkill((skill.get()), skillEntry);
         }
      });
   }

   private void loadQuests(Connection connection, CharacterData characterData, MapleCharacter mapleCharacter) {
      Map<Integer, MapleQuestStatus> loadedQuestStatus = new LinkedHashMap<>();
      QuestStatusProvider.getInstance().getQuestData(connection, characterData.getId()).forEach(questData -> {
         MapleQuest q = MapleQuest.getInstance(questData.getQuestId());
         MapleQuestStatus status = new MapleQuestStatus(q, MapleQuestStatus.Status.getById(questData.getStatus()));
         if (questData.getTime() > -1) {
            status.setCompletionTime(questData.getTime() * 1000);
         }

         if (questData.getExpires() > 0) {
            status.setExpirationTime(questData.getExpires());
         }

         status.setForfeited(questData.getForfeited());
         status.setCompleted(questData.getCompleted());
         mapleCharacter.addQuest(q.getId(), status);
         loadedQuestStatus.put(questData.getQuestStatusId(), status);
      });
      QuestProgressProvider.getInstance().getProgress(connection, characterData.getId()).forEach(questProgress -> {
         MapleQuestStatus status = loadedQuestStatus.get(questProgress.getQuestStatusId());
         if (status != null) {
            status.setProgress(questProgress.getProgressId(), questProgress.getProgress());
         }
      });
      MedalMapProvider.getInstance().get(connection, characterData.getId()).forEach(medalMap -> {
         MapleQuestStatus status = loadedQuestStatus.get(medalMap.getLeft());
         if (status != null) {
            status.addMedalMap(medalMap.getRight());
         }
      });
      loadedQuestStatus.clear();
   }

   private void loadTeleportLocations(Connection connection, CharacterData data, MapleCharacter mapleCharacter) {
      List<Pair<Integer, Integer>> locations = TeleportRockProvider.getInstance().getTeleportLocations(connection, data.getId());
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
      if (data.getMessengerId() > 0 && data.getMessengerPosition() < 4 && data.getMessengerPosition() > -1) {
         world.getMessenger(data.getMessengerId()).ifPresent(messenger -> mapleCharacter.setMessenger(messenger, data.getMessengerPosition()));
      }
   }

   private void loadPartyData(CharacterData data, MapleCharacter mapleCharacter, World world) {
      MapleParty party = world.getParty(data.getPartyId());
      if (party != null) {
         //TODO this seems like a bug
         mapleCharacter.setMPC(party.getMemberById(data.getId()));
         if (mapleCharacter.getMPC() != null) {
            mapleCharacter.setMPC(new MaplePartyCharacter(mapleCharacter));
            mapleCharacter.setParty(party);
         }
      }
   }

   private void loadMapData(MapleClient client, CharacterData data, MapleCharacter mapleCharacter) {
      MapleMapManager mapManager = client.getChannelServer().getMapFactory();
      mapleCharacter.setMap(mapManager.getMap(data.getMap()));
      if (mapleCharacter.getMap() == null) {
         mapleCharacter.setMap(mapManager.getMap(100000000));
      }

      MaplePortal portal = mapleCharacter.getMap().getPortal(data.getSpawnPoint());
      if (portal == null) {
         portal = mapleCharacter.getMap().getPortal(0);
         mapleCharacter.setInitialSpawnPoint(0);
      }
      mapleCharacter.setPosition(portal.getPosition());
   }

   private void loadPetIgnores(Connection connection, CharacterData data, MapleCharacter mapleCharacter) {
      InventoryItemProvider.getInstance().getPetsForCharacter(connection, data.getId()).forEach(petId -> {
         mapleCharacter.resetExcluded(petId);
         PetIgnoreProvider.getInstance().getIgnoresForPet(connection, petId).forEach(itemId -> mapleCharacter.addExcluded(petId, itemId));
      });
      mapleCharacter.commitExcludedItems();
   }

   private void correctMarriageDatabaseData(CharacterData data, MapleCharacter mapleCharacter, World world) {
      if (data.getMarriageItemId() > 0 && data.getPartnerId() <= 0) {
         mapleCharacter.setMarriageItemId(-1);
      } else if (data.getPartnerId() > 0 && world.getRelationshipId(data.getId()) <= 0) {
         mapleCharacter.setMarriageItemId(-1);
         mapleCharacter.setPartnerId(-1);
      }
   }

   private void loadInventory(int characterId, boolean channelserver, MapleCharacter mapleCharacter) {
      short sandboxCheck = 0x0;
      for (Pair<Item, MapleInventoryType> item : ItemFactory.INVENTORY.loadItems(characterId, !channelserver)) {
         sandboxCheck |= item.getLeft().getFlag();

         mapleCharacter.getInventory(item.getRight()).addItemFromDB(item.getLeft());
         Item itemz = item.getLeft();
         if (itemz.getPetId() > -1) {
            MaplePet pet = itemz.getPet();
            if (pet != null && pet.isSummoned()) {
               mapleCharacter.addPet(pet);
            }
            continue;
         }

         MapleInventoryType mit = item.getRight();
         if (mit.equals(MapleInventoryType.EQUIP) || mit.equals(MapleInventoryType.EQUIPPED)) {
            Equip equip = (Equip) item.getLeft();
            if (equip.getRingId() > -1) {
               MapleRing ring = MapleRingProcessor.getInstance().loadFromDb(equip.getRingId());
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
         CharacterProcessor.getInstance().loadInventory(characterData.getId(), channelserver, mapleCharacter);
         World world = Server.getInstance().getWorld(characterData.getWorld());
         CharacterProcessor.getInstance().correctMarriageDatabaseData(characterData, mapleCharacter, world);
         NewYearCardRecord.loadPlayerNewYearCards(mapleCharacter);
         CharacterProcessor.getInstance().loadPetIgnores(connection, characterData, mapleCharacter);

         if (channelserver) {
            CharacterProcessor.getInstance().loadMapData(client, characterData, mapleCharacter);
            CharacterProcessor.getInstance().loadPartyData(characterData, mapleCharacter, world);
            CharacterProcessor.getInstance().loadMessengerData(characterData, mapleCharacter, world);

            mapleCharacter.setLoggedIn();
         }

         CharacterProcessor.getInstance().loadTeleportLocations(connection, characterData, mapleCharacter);

         AccountProvider.getInstance().getAccountDataById(connection, characterData.getAccountId()).ifPresent(accountData -> {
            MapleClient retClient = mapleCharacter.getClient();
            retClient.setAccountName(accountData.getName());
            retClient.setCharacterSlots(accountData.getCharacterSlots());
            retClient.setLanguage(accountData.getLanguage());
         });

         AreaInfoProvider.getInstance().getAreaInfo(connection, characterData.getId())
               .forEach(areaInfo -> mapleCharacter.getAreaInfos().put(areaInfo.getLeft(), areaInfo.getRight()));

         EventStatProvider.getInstance().getInfo(connection, characterData.getId()).stream()
               .filter(info -> info.getLeft().contentEquals("rescueGaga"))
               .forEach(info -> mapleCharacter.getEvents().put(info.getLeft(), new RescueGaga(info.getRight())));

         mapleCharacter.initCashShop();
         mapleCharacter.initAutoBanManager();

         CharacterProvider.getInstance().getHighestLevelOtherCharacterData(connection, characterData.getAccountId(), characterData.getId())
               .ifPresent(otherCharacterData -> mapleCharacter.setLinkedCharacterInformation(otherCharacterData.getName(), otherCharacterData.getLevel()));

         if (channelserver) {
            CharacterProcessor.getInstance().loadQuests(connection, characterData, mapleCharacter);
            CharacterProcessor.getInstance().loadSkills(connection, characterData, mapleCharacter);
            CharacterProcessor.getInstance().loadCoolDowns(connection, characterData, mapleCharacter);
            CharacterProcessor.getInstance().loadPlayerDiseases(connection, characterData, mapleCharacter);

            SkillMacroProvider.getInstance().getForCharacter(connection, characterData.getId()).forEach(skillMacroData -> {
               int position = skillMacroData.getPosition();
               SkillMacro macro = new SkillMacro(skillMacroData.getSkill1Id(), skillMacroData.getSkill2Id(),
                     skillMacroData.getSkill3Id(), skillMacroData.getName(), skillMacroData.getShout(),
                     position);
               mapleCharacter.updateMacros(position, macro);
            });

            KeyMapProvider.getInstance().getForCharacter(connection, characterData.getId())
                  .forEach(keyMapData -> mapleCharacter.getKeymap().put(keyMapData.getKey(), new MapleKeyBinding(keyMapData.getType(), keyMapData.getAction())));

            SavedLocationProvider.getInstance().getForCharacter(connection, characterData.getId())
                  .forEach(savedLocationData -> mapleCharacter.updateSavedLocation(SavedLocationType.valueOf(savedLocationData.getLocationType()).ordinal(), new SavedLocation(savedLocationData.getMapId(), savedLocationData.getPortalId())));

            FameLogProvider.getInstance().getForCharacter(connection, characterData.getId()).forEach(fameLogData -> mapleCharacter.giveFame(fameLogData.getLeft(), fameLogData.getRight().getTime()));

            BuddyListProcessor.getInstance().loadFromDb(characterData.getId(), mapleCharacter.getBuddylist());
            mapleCharacter.setStorage(MapleStorage.loadOrCreateFromDB(characterData.getAccountId(), characterData.getWorld()));

            mapleCharacter.reapplyLocalStats();
            mapleCharacter.changeHpMp(mapleCharacter.getHp(), mapleCharacter.getMp(), true);

            int mountid = mapleCharacter.getJobType() * 10000000 + 1004;

            MapleMount mapleMount;
            if (mapleCharacter.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -18) != null) {
               mapleMount = new MapleMount(mapleCharacter, mapleCharacter.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -18).getItemId(), mountid);
            } else {
               mapleMount = new MapleMount(mapleCharacter, 0, mountid);
            }
            mapleMount.setExp(characterData.getMountExp());
            mapleMount.setLevel(characterData.getMountLevel());
            mapleMount.setTiredness(characterData.getMountTiredness());
            mapleMount.setActive(false);
            mapleCharacter.setMount(mapleMount);
         }
         return mapleCharacter;
      }).orElse(null);
   }

   private MapleCharacter loadFromCharacterData(CharacterData characterData) {
      MapleCharacter mapleCharacter = new MapleCharacter(characterData.getId(), characterData.getAccountId(),
            characterData.getStr(), characterData.getDex(), characterData.getIntelligence(),
            characterData.getLuk(), characterData.getHp(), characterData.getMp(), characterData.getMeso());
      mapleCharacter.setName(characterData.getName());
      mapleCharacter.setLevel(characterData.getLevel());
      mapleCharacter.setFame(characterData.getFame());
      mapleCharacter.setQuestFame(characterData.getQuestFame());
      mapleCharacter.setExp(characterData.getExp());
      mapleCharacter.setGachaExp(characterData.getGachaponExp());
      mapleCharacter.setMaxHp(characterData.getMaxhp());
      mapleCharacter.setMaxMp(characterData.getMaxmp());
      mapleCharacter.setHpMpApUsed(characterData.getHpMpUsed());
      mapleCharacter.setHasMerchantNoUpdate(characterData.hasMerchant());
      mapleCharacter.setRemainingAp(characterData.getAp());
      mapleCharacter.loadCharSkillPoints(characterData.getSp());
      mapleCharacter.setMerchantMesoNoUpdate(characterData.getMerchantMeso());
      mapleCharacter.setGM(characterData.getGm());
      mapleCharacter.setSkinColor(MapleSkinColor.getById(characterData.getSkinColor()));
      mapleCharacter.setGender(characterData.getGender());
      mapleCharacter.setJob(MapleJob.getById(characterData.getJob()));
      mapleCharacter.setFinishedDojoTutorial(characterData.hasFinishedDojoTutorial());
      mapleCharacter.setVanquisherKills(characterData.getVanquisherKills());
      mapleCharacter.setOmok(characterData.getOmok());
      mapleCharacter.setMatchCard(characterData.getMatchCard());
      mapleCharacter.setHair(characterData.getHair());
      mapleCharacter.setFace(characterData.getFace());
      mapleCharacter.setMapId(characterData.getMap());
      mapleCharacter.setJailExpiration(characterData.getJailExpire());
      mapleCharacter.setInitialSpawnPoint(characterData.getSpawnPoint());
      mapleCharacter.setWorld(characterData.getWorld());
      mapleCharacter.setRankAndMove(characterData.getRank(), characterData.getRankMove());
      mapleCharacter.setJobRankAndMove(characterData.getJobRank(), characterData.getJobRankMove());
      mapleCharacter.setGuildId(characterData.getGuildId());
      mapleCharacter.setGuildRank(characterData.getGuildRank());
      mapleCharacter.setAllianceRank(characterData.getAllianceRank());
      mapleCharacter.setFamilyId(characterData.getFamilyId());
      mapleCharacter.setMonsterBookCover(characterData.getMonsterBookCover());
      mapleCharacter.initMonsterBook();
      mapleCharacter.setVanquisherStage(characterData.getVanquisherStage());
      mapleCharacter.setAriantPoints(characterData.getAriantPoints());
      mapleCharacter.setDojoPoints(characterData.getDojoPoints());
      mapleCharacter.setDojoStage(characterData.getLastDojoStage());
      mapleCharacter.setDataString(characterData.getDataString());
      mapleCharacter.initBuddyList(characterData.getBuddyCapacity());
      mapleCharacter.setLastExpGainTime(characterData.getLastExpGainTime().getTime());
      mapleCharacter.setCanRecvPartySearchInvite(characterData.hasPartyInvite());
      mapleCharacter.getInventory(MapleInventoryType.EQUIP).setSlotLimit(characterData.getEquipSlotLimit());
      mapleCharacter.getInventory(MapleInventoryType.USE).setSlotLimit(characterData.getUseSlotLimit());
      mapleCharacter.getInventory(MapleInventoryType.SETUP).setSlotLimit(characterData.getSetupSlotLimit());
      mapleCharacter.getInventory(MapleInventoryType.ETC).setSlotLimit(characterData.getEtcSlotLimit());
      mapleCharacter.setPartnerId(characterData.getPartnerId());
      mapleCharacter.setMarriageItemId(characterData.getMarriageItemId());
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
         ret.getKeymap().put(selectedKey[i], new MapleKeyBinding(selectedType[i], selectedAction[i]));
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
}
