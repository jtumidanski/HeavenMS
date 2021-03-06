package client.processor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;

import client.KeyBinding;
import client.MapleAbnormalStatus;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleFamily;
import client.MapleMount;
import client.MapleSkinColor;
import client.Ring;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import client.database.data.CharacterData;
import client.database.data.CharacterGuildFamilyData;
import client.database.data.CharacterIdNameAccountId;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MapleInventory;
import client.inventory.manipulator.MapleCashIdGenerator;
import client.keybind.MapleQuickSlotBinding;
import client.processor.npc.FredrickProcessor;
import com.ms.shared.rest.RestService;
import com.ms.shared.rest.UriBuilder;
import config.YamlConfig;
import constants.ItemConstants;
import constants.MapleInventoryType;
import constants.MapleJob;
import constants.game.GameConstants;
import database.DatabaseConnection;
import database.administrator.AreaInfoAdministrator;
import database.administrator.BbsThreadAdministrator;
import database.administrator.CharacterAdministrator;
import database.administrator.CoolDownAdministrator;
import database.administrator.EventStatAdministrator;
import database.administrator.FameLogAdministrator;
import database.administrator.FamilyCharacterAdministrator;
import database.administrator.InventoryEquipmentAdministrator;
import database.administrator.InventoryItemAdministrator;
import database.administrator.KeyMapAdministrator;
import database.administrator.MonsterBookAdministrator;
import database.administrator.MtsCartAdministrator;
import database.administrator.MtsItemAdministrator;
import database.administrator.PetAdministrator;
import database.administrator.PlayerDiseaseAdministrator;
import database.administrator.QuickSlotKeyMapAdministrator;
import database.administrator.RingAdministrator;
import database.administrator.SavedLocationAdministrator;
import database.administrator.ServerQueueAdministrator;
import database.administrator.SkillAdministrator;
import database.administrator.SkillMacroAdministrator;
import database.administrator.TeleportRockLocationAdministrator;
import database.provider.AccountProvider;
import database.provider.AreaInfoProvider;
import database.provider.CharacterProvider;
import database.provider.CoolDownProvider;
import database.provider.EventStatProvider;
import database.provider.FameLogProvider;
import database.provider.InventoryEquipmentProvider;
import database.provider.InventoryItemProvider;
import database.provider.KeyMapProvider;
import database.provider.PetIgnoreProvider;
import database.provider.PlayerDiseaseProvider;
import database.provider.QuickSlotKeyMapProvider;
import database.provider.SavedLocationProvider;
import database.provider.SkillMacroProvider;
import database.provider.SkillProvider;
import database.provider.TeleportRockProvider;
import database.provider.WorldTransferProvider;
import net.server.Server;
import net.server.SkillMacro;
import net.server.guild.MapleGuildCharacter;
import net.server.processor.MapleGuildProcessor;
import net.server.world.MaplePartyCharacter;
import net.server.world.World;
import server.events.RescueGaga;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.MapleMapManager;
import server.maps.MaplePortal;
import server.maps.SavedLocation;
import server.maps.SavedLocationType;
import tools.LongTool;
import tools.Pair;

public class CharacterProcessor {
   private static CharacterProcessor ourInstance = new CharacterProcessor();

   private static final String[] BLOCKED_NAMES =
         {"admin", "owner", "moderator", "intern", "donor", "administrator", "FREDRICK", "help", "helper", "alert", "notice", "maplestory", "fuck", "wizet", "fucking", "negro", "fuk", "fuc", "penis", "pussy", "asshole", "gay",
               "nigger", "homo", "suck", "cum", "shit", "shitty", "condom", "security", "official", "rape", "nigga", "sex", "tit", "boner", "orgy", "clit", "asshole", "fatass", "bitch", "support", "gamemaster", "cock", "gaay", "gm",
               "operate", "master", "sysop", "party", "GameMaster", "community", "message", "event", "test", "meso", "Scania", "yata", "AsiaSoft", "henesys"};

   public static CharacterProcessor getInstance() {
      return ourInstance;
   }

   private CharacterProcessor() {
   }

   public boolean canCreateChar(String name) {
      String lowerCaseName = name.toLowerCase();
      for (String nameTest : BLOCKED_NAMES) {
         if (lowerCaseName.contains(nameTest)) {
            return false;
         }
      }
      return getIdByName(name) < 0 && Pattern.compile("[a-zA-Z0-9]{3,12}").matcher(name).matches();
   }

   public int getIdByName(String name) {
      return DatabaseConnection.getInstance()
            .withConnectionResult(connection -> CharacterProvider.getInstance().getIdForName(connection, name)).orElse(-1);
   }

   public String getNameById(int id) {
      return DatabaseConnection.getInstance()
            .withConnectionResult(connection -> CharacterProvider.getInstance().getNameForId(connection, id)).orElse(null);
   }

   public CharacterIdNameAccountId getCharacterFromDatabase(String name) {
      return DatabaseConnection.getInstance()
            .withConnectionResult(connection -> CharacterProvider.getInstance().getByName(connection, name).orElse(null))
            .orElse(null);
   }

   public boolean doWorldTransfer(EntityManager entityManager, int characterId, int oldWorld, int newWorld, int worldTransferId) {
      CharacterAdministrator.getInstance().performWorldTransfer(entityManager, characterId, oldWorld, newWorld, worldTransferId);
      return true;
   }

   public String checkWorldTransferEligibility(EntityManager entityManager, int characterId, int oldWorld, int newWorld) {
      if (!YamlConfig.config.server.ALLOW_CASHSHOP_WORLD_TRANSFER) {
         return "World transfers disabled.";
      }

      Optional<CharacterData> characterDataResult = CharacterProvider.getInstance().getById(entityManager, characterId);
      if (characterDataResult.isEmpty()) {
         return "Character does not exist.";
      }

      CharacterData characterData = characterDataResult.get();

      int accountId = characterData.accountId();
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

      Calendar tempBan = AccountProvider.getInstance().getTempBanCalendar(entityManager, accountId);
      if (tempBan != null) {
         return "Account has been banned.";
      }

      int charCountInNewWorld = CharacterProvider.getInstance().getCharactersInWorld(entityManager, accountId, newWorld);
      if (charCountInNewWorld >= 3) {
         return "Too many characters on destination world.";
      }

      return null;
   }

   public boolean deleteCharFromDB(MapleCharacter player, int senderAccId) {
      int cid = player.getId();
      if (!Server.getInstance().haveCharacterEntry(senderAccId, cid)) {
         return false;
      }

      DatabaseConnection.getInstance().withConnection(entityManager -> {
         BuddyListProcessor.getInstance().deleteCharacter(player.getWorld(), cid);

         BbsThreadAdministrator.getInstance().deleteThreadsFromCharacter(entityManager, cid);
         CharacterProvider.getInstance().getGuildDataForCharacter(entityManager, cid, senderAccId)
               .ifPresent(result -> MapleGuildProcessor.getInstance().removeGuildCharacter(
                     new MapleGuildCharacter(player, cid, 0, result.name(), (byte) -1, (byte) -1, 0, result.guildRank(),
                           result.guildId(), false, result.allianceRank())));
         CashShopProcessor.getInstance().deleteWishListForCharacter(cid);
         CoolDownAdministrator.getInstance().deleteForCharacter(entityManager, cid);
         PlayerDiseaseAdministrator.getInstance().deleteForCharacter(entityManager, cid);
         AreaInfoAdministrator.getInstance().deleteForCharacter(entityManager, cid);
         MonsterBookAdministrator.getInstance().deleteForCharacter(entityManager, cid);
         CharacterAdministrator.getInstance().deleteCharacter(entityManager, cid);
         FamilyCharacterAdministrator.getInstance().deleteForCharacter(entityManager, cid);
         FameLogAdministrator.getInstance().deleteForCharacter(entityManager, cid);
         cleanupInventoryEquipment(entityManager, cid);

         UriBuilder.service(RestService.QUEST).path("characters").path(cid).path("quests").getRestClient().delete();

         FredrickProcessor.removeFredrickLog(cid);
         MtsItemAdministrator.getInstance().deleteForCharacter(entityManager, cid);
         MtsCartAdministrator.getInstance().deleteForCharacter(entityManager, cid);
         InventoryItemAdministrator.getInstance().deleteForCharacter(entityManager, cid);
         KeyMapAdministrator.getInstance().deleteForCharacter(entityManager, cid);
         SavedLocationAdministrator.getInstance().deleteForCharacter(entityManager, cid);
         TeleportRockLocationAdministrator.getInstance().deleteForCharacter(entityManager, cid);
         SkillMacroAdministrator.getInstance().deleteForCharacter(entityManager, cid);
         SkillAdministrator.getInstance().deleteForCharacter(entityManager, cid);
         EventStatAdministrator.getInstance().deleteForCharacter(entityManager, cid);
         ServerQueueAdministrator.getInstance().deleteForCharacter(entityManager, cid);
      });
      Server.getInstance().deleteCharacterEntry(senderAccId, cid);
      return true;
   }

   private void cleanupInventoryEquipment(EntityManager entityManager, int cid) {
      InventoryItemProvider.getInstance().get(entityManager, cid).forEach(pair -> {
         InventoryEquipmentProvider.getInstance().getRings(entityManager, pair.getLeft()).stream()
               .filter(ringId -> ringId > 1)
               .forEach(ringId -> {
                  RingAdministrator.getInstance().deleteRing(entityManager, ringId);
                  MapleCashIdGenerator.getInstance().freeCashId(ringId);
               });
         InventoryEquipmentAdministrator.getInstance().deleteById(entityManager, pair.getLeft());
         if (pair.getRight() > -1) {
            PetAdministrator.getInstance().deletePet(entityManager, pair.getRight());
            MapleCashIdGenerator.getInstance().freeCashId(pair.getRight());
         }
      });
   }

   private void loadPlayerDiseases(EntityManager entityManager, CharacterData characterData, MapleCharacter mapleCharacter) {
      Map<MapleAbnormalStatus, Pair<Long, MobSkill>> loadedDiseases = new LinkedHashMap<>();
      PlayerDiseaseProvider.getInstance().getForCharacter(entityManager, characterData.id()).forEach(playerDiseaseData -> {
         final MapleAbnormalStatus disease = MapleAbnormalStatus.ordinal(playerDiseaseData.disease());
         if (disease != MapleAbnormalStatus.NULL) {
            MobSkill ms = MobSkillFactory.getMobSkill(playerDiseaseData.mobSkillId(), playerDiseaseData.mobSkillLevel());
            if (ms != null) {
               loadedDiseases.put(disease, new Pair<>((long) playerDiseaseData.length(), ms));
            }
         }
      });
      PlayerDiseaseAdministrator.getInstance().deleteForCharacter(entityManager, characterData.id());
      if (!loadedDiseases.isEmpty()) {
         Server.getInstance().getPlayerBuffStorage().addDiseasesToStorage(mapleCharacter.getId(), loadedDiseases);
      }
   }

   private void loadCoolDowns(EntityManager entityManager, CharacterData characterData, MapleCharacter mapleCharacter) {
      long curTime = Server.getInstance().getCurrentTime();
      CoolDownProvider.getInstance().getForCharacter(entityManager, characterData.id()).forEach(coolDownData -> {
         if (coolDownData.skillId() == 5221999 || (coolDownData.length() + coolDownData.startTime() >= curTime)) {
            mapleCharacter.giveCoolDowns(coolDownData.skillId(), coolDownData.startTime(), coolDownData.length());
         }
      });
      CoolDownAdministrator.getInstance().deleteForCharacter(entityManager, characterData.id());
   }

   private void loadSkills(EntityManager entityManager, CharacterData characterData, MapleCharacter mapleCharacter) {
      SkillProvider.getInstance().getSkills(entityManager, characterData.id()).forEach(skillData -> {
         Optional<Skill> skill = SkillFactory.getSkill(skillData.skillId());
         if (skill.isPresent()) {
            SkillEntry skillEntry = new SkillEntry(skillData.skillLevel(), skillData.masterLevel(), skillData.expiration());
            mapleCharacter.addSkill((skill.get()), skillEntry);
         }
      });
   }

   private void loadTeleportLocations(EntityManager entityManager, CharacterData data, MapleCharacter mapleCharacter) {
      List<Pair<Integer, Integer>> locations = TeleportRockProvider.getInstance().getTeleportLocations(entityManager, data.id());
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
         world.getMessenger(data.messengerId())
               .ifPresent(messenger -> mapleCharacter.setMessenger(messenger, data.messengerPosition()));
      }
   }

   private void loadPartyData(CharacterData data, MapleCharacter mapleCharacter, World world) {
      world.getParty(data.partyId()).ifPresent(party -> {
         //TODO this seems like a bug
         party.getMemberById(data.id()).ifPresent(mapleCharacter::setMPC);
         if (mapleCharacter.getMPC() != null) {
            mapleCharacter.setMPC(new MaplePartyCharacter(mapleCharacter));
            mapleCharacter.setParty(party);
         }
      });
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

   private void loadPetIgnores(EntityManager entityManager, CharacterData data, MapleCharacter mapleCharacter) {
      InventoryItemProvider.getInstance().getPetsForCharacter(entityManager, data.id()).forEach(petId -> {
         mapleCharacter.resetExcluded(petId);
         PetIgnoreProvider.getInstance().getIgnoresForPet(entityManager, petId)
               .forEach(itemId -> mapleCharacter.addExcluded(petId, itemId));
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

   private void loadInventory(int characterId, boolean channelServer, MapleCharacter mapleCharacter) {
      short sandboxCheck = 0x0;
      for (Pair<Item, MapleInventoryType> itemPair : ItemFactory.INVENTORY.loadItems(characterId, !channelServer)) {
         sandboxCheck |= itemPair.getLeft().flag();

         mapleCharacter.getInventory(itemPair.getRight()).addItemFromDB(itemPair.getLeft());
         Item item = itemPair.getLeft();
         if (item.petId() > -1) {
            if (item.pet() != null && item.pet().summoned()) {
               mapleCharacter.addPet(item.pet());
            }
            continue;
         }

         MapleInventoryType mit = itemPair.getRight();
         if (mit.equals(MapleInventoryType.EQUIP) || mit.equals(MapleInventoryType.EQUIPPED)) {
            Equip equip = (Equip) itemPair.getLeft();
            if (equip.ringId() > -1) {
               Ring ring = MapleRingProcessor.getInstance().loadFromDb(equip.ringId());
               if (itemPair.getRight().equals(MapleInventoryType.EQUIPPED)) {
                  ring = ring.equip();
               }

               mapleCharacter.addPlayerRing(ring);
            }
         }
      }
      if ((sandboxCheck & ItemConstants.SANDBOX) == ItemConstants.SANDBOX) {
         mapleCharacter.setHasSandboxItem();
      }
   }

   public MapleCharacter loadCharFromDB(int characterId, MapleClient client, boolean channelServer) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> {
         Optional<CharacterData> characterDataOptional = CharacterProvider.getInstance().getById(connection, characterId);
         if (characterDataOptional.isEmpty()) {
            return null;
         }
         CharacterData characterData = characterDataOptional.get();

         MapleCharacter mapleCharacter = loadFromCharacterData(characterData);
         mapleCharacter.setClient(client);
         mapleCharacter.setMGC(new MapleGuildCharacter(mapleCharacter));
         CharacterProcessor.getInstance().loadInventory(characterData.id(), channelServer, mapleCharacter);
         World world = Server.getInstance().getWorld(characterData.world());
         CharacterProcessor.getInstance().correctMarriageDatabaseData(characterData, mapleCharacter, world);
         NewYearCardProcessor.getInstance().loadPlayerNewYearCards(mapleCharacter);
         CharacterProcessor.getInstance().loadPetIgnores(connection, characterData, mapleCharacter);

         if (channelServer) {
            CharacterProcessor.getInstance().loadMapData(client, characterData, mapleCharacter);
            CharacterProcessor.getInstance().loadPartyData(characterData, mapleCharacter, world);
            CharacterProcessor.getInstance().loadMessengerData(characterData, mapleCharacter, world);

            mapleCharacter.setLoggedIn();
         }

         CharacterProcessor.getInstance().loadTeleportLocations(connection, characterData, mapleCharacter);

         DatabaseConnection.getInstance().withConnection(
               entityManager -> AccountProvider.getInstance().getAccountDataById(entityManager, characterData.accountId())
                     .ifPresent(accountData -> {
                        MapleClient retClient = mapleCharacter.getClient();
                        retClient.setAccountName(accountData.name());
                        retClient.setCharacterSlots(accountData.characterSlots().byteValue());
                        retClient.setLocale(new Locale(accountData.language(), accountData.country()));
                     }));

         AreaInfoProvider.getInstance().getAreaInfo(connection, characterData.id())
               .forEach(areaInfo -> mapleCharacter.getAreaInfos().put(areaInfo.area().shortValue(), areaInfo.info()));

         EventStatProvider.getInstance().getInfo(connection, characterData.id()).stream()
               .filter(info -> info.getLeft().contentEquals("rescueGaga"))
               .forEach(info -> mapleCharacter.getEvents().put(info.getLeft(), new RescueGaga(info.getRight())));

         mapleCharacter.initCashShop();
         mapleCharacter.initAutoBanManager();

         CharacterProvider.getInstance()
               .getHighestLevelOtherCharacterData(connection, characterData.accountId(), characterData.id())
               .ifPresent(otherCharacterData -> mapleCharacter
                     .setLinkedCharacterInformation(otherCharacterData.name(), otherCharacterData.level()));

         if (channelServer) {
            CharacterProcessor.getInstance().loadSkills(connection, characterData, mapleCharacter);
            CharacterProcessor.getInstance().loadCoolDowns(connection, characterData, mapleCharacter);
            CharacterProcessor.getInstance().loadPlayerDiseases(connection, characterData, mapleCharacter);

            SkillMacroProvider.getInstance().getForCharacter(connection, characterData.id()).forEach(skillMacroData -> {
               int position = skillMacroData.position();
               SkillMacro macro = new SkillMacro(skillMacroData.name(), skillMacroData.shout(), skillMacroData.skill1Id(),
                     skillMacroData.skill2Id(),
                     skillMacroData.skill3Id(),
                     position);
               mapleCharacter.updateMacros(position, macro);
            });

            KeyMapProvider.getInstance().getForCharacter(connection, characterData.id())
                  .forEach(keyMapData -> mapleCharacter.getKeymap()
                        .put(keyMapData.key(), new KeyBinding(keyMapData.theType(), keyMapData.action())));

            SavedLocationProvider.getInstance().getForCharacter(connection, characterData.id())
                  .forEach(savedLocationData -> mapleCharacter
                        .updateSavedLocation(SavedLocationType.valueOf(savedLocationData.locationType()).ordinal(),
                              new SavedLocation(savedLocationData.mapId(), savedLocationData.portalId())));

            FameLogProvider.getInstance().getForCharacter(connection, characterData.id())
                  .forEach(fameLogData -> mapleCharacter.giveFame(fameLogData.getLeft(), fameLogData.getRight().getTime()));

            BuddyListProcessor.getInstance().loadBuddies(mapleCharacter);
            mapleCharacter.setStorage(world.getAccountStorage(characterData.accountId()));

            mapleCharacter.reapplyLocalStats();
            mapleCharacter.changeHpMp(mapleCharacter.getHp(), mapleCharacter.getMp(), true);

            int mountId = mapleCharacter.getJobType() * 10000000 + 1004;

            MapleMount mapleMount;
            int mountItemId = 0;
            if (mapleCharacter.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -18) != null) {
               mountItemId = mapleCharacter.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -18).id();
            }

            mapleMount = new MapleMount(mountItemId, mountId, characterData.mountLevel(), characterData.mountExp(),
                  characterData.mountTiredness(), false);
            mapleCharacter.setMount(mapleMount);

            QuickSlotKeyMapProvider.getInstance().getForAccount(connection, characterData.accountId()).ifPresent(keyMap -> {
               mapleCharacter.setQuickSlotLoaded(LongTool.LongToBytes(keyMap.longValue()));
               mapleCharacter.setQuickSlotBinding(new MapleQuickSlotBinding(mapleCharacter.getQuickSlotLoaded()));
            });
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
      mapleCharacter.setGachaponExperience(characterData.gachaponExp());
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

      BuddyListProcessor.getInstance().getBuddyListCapacity(characterData.id(), capacity -> {
         if (capacity == 0) {
            BuddyListProcessor.getInstance().syncAndInitBuddyList(mapleCharacter);
         } else {
            mapleCharacter.initBuddyList(capacity);
         }
      });

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
      ret.setGMLevel(0);
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

      // Select a key binding method
      int[] selectedKey;
      int[] selectedType;
      int[] selectedAction;

      if (YamlConfig.config.server.USE_CUSTOM_KEYSET) {
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

   public final boolean insertNewChar(MapleCharacter character) {
      DatabaseConnection.getInstance().withConnection(entityManager -> {
         entityManager.getTransaction().begin();
         int key = CharacterAdministrator.getInstance()
               .create(entityManager, character.getStr(), character.getDex(), character.getLuk(), character.getInt(),
                     character.gmLevel(), character.getSkinColor().getId(),
                     character.getGender(), character.getJob().getId(), character.getHair(), character.getFace(),
                     character.getMapId(), Math.abs(character.getMeso()), character.getAccountID(), character.getName(),
                     character.getWorld(), character.getHp(), character.getMp(),
                     character.getMaxHp(), character.getMaxMp(), character.getLevel(), character.getRemainingAp(),
                     character.getRemainingSps());
         character.setId(key);

         // Select a key binding method
         int[] selectedKey;
         int[] selectedType;
         int[] selectedAction;

         if (YamlConfig.config.server.USE_CUSTOM_KEYSET) {
            selectedKey = GameConstants.getCustomKey(true);
            selectedType = GameConstants.getCustomType(true);
            selectedAction = GameConstants.getCustomAction(true);
         } else {
            selectedKey = GameConstants.getCustomKey(false);
            selectedType = GameConstants.getCustomType(false);
            selectedAction = GameConstants.getCustomAction(false);
         }

         for (int i = 0; i < selectedKey.length; i++) {
            KeyMapAdministrator.getInstance()
                  .create(entityManager, character.getId(), selectedKey[i], selectedType[i], selectedAction[i]);
         }

         createQuickSlots(entityManager, character);

         List<Pair<Item, MapleInventoryType>> itemsByType = new ArrayList<>();

         Arrays.stream(MapleInventoryType.values())
               .map(character::getInventory)
               .forEach(inventory -> inventory.list()
                     .forEach(item -> itemsByType.add(new Pair<>(item, inventory.getType()))));

         ItemFactory.INVENTORY.saveItems(itemsByType, character.getId(), entityManager);

         if (!character.getSkills().isEmpty()) {
            SkillAdministrator.getInstance().create(entityManager, character.getId(), character.getSkills().entrySet());
         }

         entityManager.getTransaction().commit();

         BuddyListProcessor.getInstance().syncCharacter(character.getAccountID(), character.getId());
      });
      return true;
   }

   public void createQuickSlots(EntityManager entityManager, MapleCharacter character) {
      boolean noChanges = character.getQuickSlotLoaded() == null ||
            (character.getQuickSlotLoaded() != null && Arrays
                  .equals(character.getQuickSlotLoaded(), character.getQuickSlotBinding().getQuickSlotKeyMapped()));
      if (!noChanges) {
         long value = LongTool.BytesToLong(character.getQuickSlotBinding().getQuickSlotKeyMapped());

         QuickSlotKeyMapProvider.getInstance().getForAccount(entityManager, character.getAccountID()).ifPresentOrElse(
               keyMap -> QuickSlotKeyMapAdministrator.getInstance()
                     .update(entityManager, character.getAccountID(), BigInteger.valueOf(value)),
               () -> QuickSlotKeyMapAdministrator.getInstance()
                     .create(entityManager, character.getAccountID(), BigInteger.valueOf(value)));
      }
   }

   public Optional<Byte> canDeleteCharacter(int characterId) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> {
         Optional<CharacterGuildFamilyData> guildFamilyData =
               CharacterProvider.getInstance().getGuildFamilyInformation(connection, characterId);
         if (guildFamilyData.isEmpty()) {
            return (byte) 0x09;
         }
         if (guildFamilyData.get().guildId() != 0 && guildFamilyData.get().guildRank() <= 1) {
            return (byte) 0x16;
         } else if (guildFamilyData.get().familyId() != -1) {
            MapleFamily family =
                  Server.getInstance().getWorld(guildFamilyData.get().world()).getFamily(guildFamilyData.get().familyId());
            if (family != null && family.getTotalMembers() > 1) {
               return (byte) 0x1D;
            }
         }
         int pendingWorldTransfers = WorldTransferProvider.getInstance().countOutstandingWorldTransfers(connection, characterId);
         if (pendingWorldTransfers > 0) {
            return (byte) 0x1A;
         }
         return null;
      });
   }
}
