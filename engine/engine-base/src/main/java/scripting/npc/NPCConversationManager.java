package scripting.npc;

import java.awt.Point;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleJob;
import client.MapleSkinColor;
import client.MapleStat;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MaplePet;
import client.processor.PetProcessor;
import config.YamlConfig;
import constants.game.GameConstants;
import constants.inventory.ItemConstants;
import constants.string.LanguageConstants;
import net.server.Server;
import net.server.channel.Channel;
import net.server.coordinator.matchchecker.MatchCheckerListenerFactory.MatchCheckerType;
import net.server.guild.MapleAlliance;
import net.server.guild.MapleGuild;
import net.server.processor.MapleAllianceProcessor;
import net.server.processor.MapleGuildProcessor;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import provider.MapleData;
import provider.MapleDataProviderFactory;
import scripting.AbstractPlayerInteraction;
import server.MapleGachaponItem;
import server.MapleItemInformationProvider;
import server.MapleMarriage;
import server.MapleShop;
import server.MapleShopFactory;
import server.MapleSkillBookInformationProvider;
import server.MapleSkillBookInformationProvider.SkillBookEntry;
import server.MapleStatEffect;
import server.TimerManager;
import server.events.gm.MapleEvent;
import server.expeditions.MapleExpedition;
import server.expeditions.MapleExpeditionType;
import server.gachapon.MapleGachapon;
import server.life.MapleLifeFactory;
import server.life.MaplePlayerNPC;
import server.maps.MapleMap;
import server.maps.MapleMapManager;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.partyquest.AriantColiseum;
import server.partyquest.MonsterCarnival;
import server.partyquest.Pyramid;
import server.partyquest.Pyramid.PyramidMode;
import server.processor.MapleShopProcessor;
import tools.FilePrinter;
import tools.LogHelper;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.alliance.AllianceNotice;
import tools.packet.alliance.GetGuildAlliances;
import tools.packet.alliance.UpdateAllianceInfo;
import tools.packet.field.effect.EnvironmentChange;
import tools.packet.fredrick.GetFredrickInfo;
import tools.packet.message.GachaponMessage;
import tools.packet.npctalk.GetDimensionalMirror;
import tools.packet.npctalk.GetNPCTalk;
import tools.packet.npctalk.GetNPCTalkNum;
import tools.packet.npctalk.GetNPCTalkStyle;
import tools.packet.npctalk.GetNPCTalkText;
import tools.packet.stat.EnableActions;
import tools.packet.ui.GetClock;
import tools.packet.wedding.SendWishList;
import tools.packet.wedding.WeddingGiftResult;

public class NPCConversationManager extends AbstractPlayerInteraction {

   private int npc;
   private int npcOid;
   private String scriptName;
   private String getText;
   private boolean itemScript;
   private List<MaplePartyCharacter> otherParty;

   private Map<Integer, String> npcDefaultTalks = new HashMap<>();

   public NPCConversationManager(MapleClient c, int npc, String scriptName) {
      this(c, npc, -1, scriptName, false);
   }

   public NPCConversationManager(MapleClient c, int npc, List<MaplePartyCharacter> otherParty, boolean test) {
      super(c);
      this.c = c;
      this.npc = npc;
      this.otherParty = otherParty;
   }

   public NPCConversationManager(MapleClient c, int npc, int oid, String scriptName, boolean itemScript) {
      super(c);
      this.npc = npc;
      this.npcOid = oid;
      this.scriptName = scriptName;
      this.itemScript = itemScript;
   }

   private String getDefaultTalk(int npcId) {
      String talk = npcDefaultTalks.get(npcId);
      if (talk == null) {
         talk = MapleLifeFactory.getNPCDefaultTalk(npcId);
         npcDefaultTalks.put(npcId, talk);
      }

      return talk;
   }

   public int getNpc() {
      return npc;
   }

   public int getNpcObjectId() {
      return npcOid;
   }

   public String getScriptName() {
      return scriptName;
   }

   public boolean isItemScript() {
      return itemScript;
   }

   public void resetItemScript() {
      this.itemScript = false;
   }

   public void dispose() {
      NPCScriptManager.getInstance().dispose(this);
      PacketCreator.announce(getClient(), new EnableActions());
   }

   public void sendNext(String text) {
      PacketCreator.announce(getClient(), new GetNPCTalk(npc, (byte) 0, text, "00 01", (byte) 0));
   }

   public void sendPrev(String text) {
      PacketCreator.announce(getClient(), new GetNPCTalk(npc, (byte) 0, text, "01 00", (byte) 0));
   }

   public void sendNextPrev(String text) {
      PacketCreator.announce(getClient(), new GetNPCTalk(npc, (byte) 0, text, "01 01", (byte) 0));
   }

   public void sendOk(String text) {
      PacketCreator.announce(getClient(), new GetNPCTalk(npc, (byte) 0, text, "00 00", (byte) 0));
   }

   public void sendDefault() {
      sendOk(getDefaultTalk(npc));
   }

   public void sendYesNo(String text) {
      PacketCreator.announce(getClient(), new GetNPCTalk(npc, (byte) 1, text, "", (byte) 0));
   }

   public void sendAcceptDecline(String text) {
      PacketCreator.announce(getClient(), new GetNPCTalk(npc, (byte) 0x0C, text, "", (byte) 0));
   }

   public void sendSimple(String text) {
      PacketCreator.announce(getClient(), new GetNPCTalk(npc, (byte) 4, text, "", (byte) 0));
   }

   public void sendNext(String text, byte speaker) {
      PacketCreator.announce(getClient(), new GetNPCTalk(npc, (byte) 0, text, "00 01", speaker));
   }

   public void sendPrev(String text, byte speaker) {
      PacketCreator.announce(getClient(), new GetNPCTalk(npc, (byte) 0, text, "01 00", speaker));
   }

   public void sendNextPrev(String text, byte speaker) {
      PacketCreator.announce(getClient(), new GetNPCTalk(npc, (byte) 0, text, "01 01", speaker));
   }

   public void sendOk(String text, byte speaker) {
      PacketCreator.announce(getClient(), new GetNPCTalk(npc, (byte) 0, text, "00 00", speaker));
   }

   public void sendYesNo(String text, byte speaker) {
      PacketCreator.announce(getClient(), new GetNPCTalk(npc, (byte) 1, text, "", speaker));
   }

   public void sendAcceptDecline(String text, byte speaker) {
      PacketCreator.announce(getClient(), new GetNPCTalk(npc, (byte) 0x0C, text, "", speaker));
   }

   public void sendSimple(String text, byte speaker) {
      PacketCreator.announce(getClient(), new GetNPCTalk(npc, (byte) 4, text, "", speaker));
   }

   public void sendStyle(String text, int[] styles) {
      if (styles.length > 0) {
         PacketCreator.announce(getClient(), new GetNPCTalkStyle(npc, text, styles));
      } else {
         sendOk("Sorry, there are no options of cosmetics available for you here at the moment.");
         dispose();
      }
   }

   public void sendGetNumber(String text, int def, int min, int max) {
      PacketCreator.announce(getClient(), new GetNPCTalkNum(npc, text, def, min, max));
   }

   public void sendGetText(String text) {
      PacketCreator.announce(getClient(), new GetNPCTalkText(npc, text, ""));
   }

   /*
    * 0 = ariant coliseum
    * 1 = Dojo
    * 2 = Carnival 1
    * 3 = Carnival 2
    * 4 = Ghost Ship PQ?
    * 5 = Pyramid PQ
    * 6 = Kerning Subway
    */
   public void sendDimensionalMirror(String text) {
      PacketCreator.announce(getClient(), new GetDimensionalMirror(text));
   }

   public void setGetText(String text) {
      this.getText = text;
   }

   public String getText() {
      return this.getText;
   }

   @Override
   public boolean forceStartQuest(int id) {
      return forceStartQuest(id, npc);
   }

   @Override
   public boolean forceCompleteQuest(int id) {
      return forceCompleteQuest(id, npc);
   }

   @Override
   public boolean startQuest(short id) {
      return startQuest((int) id);
   }

   @Override
   public boolean completeQuest(short id) {
      return completeQuest((int) id);
   }

   @Override
   public boolean startQuest(int id) {
      return startQuest(id, npc);
   }

   @Override
   public boolean completeQuest(int id) {
      return completeQuest(id, npc);
   }

   public int getMeso() {
      return getPlayer().getMeso();
   }

   public void gainMeso(int gain) {
      getPlayer().gainMeso(gain);
   }

   public void gainExp(int gain) {
      getPlayer().gainExp(gain, true, true);
   }

   @Override
   public void showEffect(String effect) {
      MasterBroadcaster.getInstance().sendToAllInMap(getPlayer().getMap(), new EnvironmentChange(effect, 3));
   }

   public void setHair(int hair) {
      getPlayer().setHair(hair);
      getPlayer().updateSingleStat(MapleStat.HAIR, hair);
      getPlayer().equipChanged();
   }

   public void setFace(int face) {
      getPlayer().setFace(face);
      getPlayer().updateSingleStat(MapleStat.FACE, face);
      getPlayer().equipChanged();
   }

   public void setSkin(int color) {
      getPlayer().setSkinColor(MapleSkinColor.getById(color));
      getPlayer().updateSingleStat(MapleStat.SKIN, color);
      getPlayer().equipChanged();
   }

   public int itemQuantity(int itemId) {
      return getPlayer().getInventory(ItemConstants.getInventoryType(itemId)).countById(itemId);
   }

   public void displayGuildRanks() {
      MapleGuildProcessor.getInstance().displayGuildRanks(getClient(), npc);
   }

   public boolean canSpawnPlayerNpc(int mapId) {
      MapleCharacter chr = getPlayer();
      return !YamlConfig.config.server.PLAYERNPC_AUTODEPLOY && chr.getLevel() >= chr.getMaxClassLevel() && !chr.isGM() && MaplePlayerNPC.canSpawnPlayerNpc(chr.getName(), mapId);
   }

   public MaplePlayerNPC getPlayerNPCByScriptId(int scriptId) {
      for (MapleMapObject playerNpc : getPlayer().getMap().getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.PLAYER_NPC))) {
         MaplePlayerNPC pn = (MaplePlayerNPC) playerNpc;

         if (pn.getScriptId() == scriptId) {
            return pn;
         }
      }

      return null;
   }

   @Override
   public Optional<MapleParty> getParty() {
      return getPlayer().getParty();
   }

   @Override
   public void resetMap(int mapId) {
      getClient().getChannelServer().getMapFactory().getMap(mapId).resetReactors();
   }

   public void gainCloseness(int closeness) {
      for (MaplePet pet : getPlayer().getPets()) {
         if (pet != null) {
            PetProcessor.getInstance().gainClosenessFullness(pet, getPlayer(), closeness, 0, 0);
         }
      }
   }

   public String getName() {
      return getPlayer().getName();
   }

   public int getGender() {
      return getPlayer().getGender();
   }

   public void changeJobById(int a) {
      getPlayer().changeJob(MapleJob.getById(a));
   }

   public void changeJob(MapleJob job) {
      getPlayer().changeJob(job);
   }

   public String getJobName(int id) {
      return GameConstants.getJobName(id);
   }

   public MapleStatEffect getItemEffect(int itemId) {
      return MapleItemInformationProvider.getInstance().getItemEffect(itemId);
   }

   public void resetStats() {
      getPlayer().resetStats();
   }

   public void openShopNPC(int id) {
      MapleShop shop = MapleShopFactory.getInstance().getShop(id);

      if (shop != null) {
         MapleShopProcessor.getInstance().sendShop(shop, c);
      } else {
         FilePrinter.printError(FilePrinter.NPC_UNCODED, "Shop ID: " + id + " is missing from database.");
         MapleShopProcessor.getInstance().sendShop(MapleShopFactory.getInstance().getShop(11000), c);
      }
   }

   public void maxMastery() {
      for (MapleData skill_ : MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/" + "String.wz")).getData("Skill.img").getChildren()) {
         try {
            int skillId = Integer.parseInt(skill_.getName());
            SkillFactory.getSkill(skillId).ifPresent(skill -> getPlayer().changeSkillLevel(skill, (byte) 0, skill.getMaxLevel(), -1));
         } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            break;
         } catch (NullPointerException npe) {
            npe.printStackTrace();
         }
      }
   }

   public void doGachapon() {
      int[] maps = {100000000, 101000000, 102000000, 103000000, 105040300, 800000000, 809000101, 809000201, 600000000, 120000000};

      MapleGachaponItem item = MapleGachapon.getInstance().process(npc);

      Item itemGained = gainItem(item.id(), (short) (item.id() / 10000 == 200 ? 100 : 1), true, true); // For normal potions, make it give 100.

      sendNext("You have obtained a #b#t" + item.id() + "##k.");

      String map = c.getChannelServer().getMapFactory().getMap(maps[(getNpc() != 9100117 && getNpc() != 9100109) ? (getNpc() - 9100100) : getNpc() == 9100109 ? 8 : 9]).getMapName();

      LogHelper.logGachapon(getPlayer(), item.id(), map);

      if (item.tier() > 0) { //Uncommon and Rare
         Server.getInstance().broadcastMessage(c.getWorld(), PacketCreator.create(new GachaponMessage(itemGained, map, getPlayer().getName())));
      }
   }

   public void upgradeAlliance() {
      int allianceId = c.getPlayer().getGuild().map(MapleGuild::getAllianceId).orElse(0);
      Server.getInstance().getAlliance(allianceId)
            .ifPresent(alliance -> {
               Server.getInstance().allianceMessage(allianceId, new GetGuildAlliances(alliance, c.getWorld()), -1, -1);
               Server.getInstance().allianceMessage(allianceId, new AllianceNotice(allianceId, alliance.notice()), -1, -1);
               alliance.increaseCapacity(1);
               PacketCreator.announce(c, new UpdateAllianceInfo(alliance, c.getWorld()));
            });
   }

   public void disbandAlliance(MapleClient c, int allianceId) {
      MapleAllianceProcessor.getInstance().disbandAlliance(allianceId);
   }

   public boolean canBeUsedAllianceName(String name) {
      return MapleAllianceProcessor.getInstance().canBeUsedAllianceName(name);
   }

   public MapleAlliance createAlliance(String name) {
      return MapleAllianceProcessor.getInstance().createAlliance(getParty().orElseThrow(), name);
   }

   public int getAllianceCapacity() {
      return getPlayer()
            .getGuild()
            .map(MapleGuild::getAllianceId)
            .flatMap(allianceId -> Server.getInstance().getAlliance(allianceId))
            .map(MapleAlliance::capacity)
            .orElse(0);
   }

   public boolean hasMerchant() {
      return getPlayer().hasMerchant();
   }

   public boolean hasMerchantItems() {
      if (!ItemFactory.MERCHANT.loadItems(getPlayer().getId(), false).isEmpty()) {
         return true;
      }
      return getPlayer().getMerchantMeso() != 0;
   }

   public void showFredrick() {
      PacketCreator.announce(c, new GetFredrickInfo(getPlayer().getId(), getPlayer().getMerchantNetMeso()));
   }

   public int partyMembersInMap() {
      int inMap = 0;
      for (MapleCharacter char2 : getPlayer().getMap().getCharacters()) {
         if (char2.getParty() == getPlayer().getParty()) {
            inMap++;
         }
      }
      return inMap;
   }

   public MapleEvent getEvent() {
      return c.getChannelServer().getEvent();
   }

   public void divideTeams() {
      if (getEvent() != null) {
         getPlayer().setTeam(getEvent().getLimit() % 2);
      }
   }

   public MapleCharacter getMapleCharacter(String player) {
      return Server.getInstance().getWorld(c.getWorld()).getChannel(c.getChannel()).getPlayerStorage().getCharacterByName(player).orElse(null);
   }

   public void logLeaf(String prize) {
      LogHelper.logLeaf(getPlayer(), true, prize);
   }

   public boolean createPyramid(String mode, boolean party) {//lol
      PyramidMode mod = PyramidMode.valueOf(mode);

      MapleMapManager mapManager = c.getChannelServer().getMapFactory();

      MapleMap map = null;
      int mapId = 926010100;
      if (party) {
         mapId += 10000;
      }
      mapId += (mod.getMode() * 1000);

      for (byte b = 0; b < 5; b++) {//They cannot warp to the next map before the timer ends (:
         map = mapManager.getMap(mapId + b);
         if (map.getCharacters().size() > 0) {
         } else {
            break;
         }
      }

      if (map == null) {
         return false;
      }

      MapleParty mapleParty = getPlayer().getParty().orElse(null);
      if (!party) {
         mapleParty = new MapleParty(-1, c.getWorld(), new MaplePartyCharacter(getPlayer()));
      }
      Pyramid py = new Pyramid(mapleParty, mod, map.getId());
      getPlayer().setPartyQuest(py);
      py.warp(mapId);
      dispose();
      return true;
   }

   public boolean itemExists(int itemId) {
      return MapleItemInformationProvider.getInstance().getName(itemId) != null;
   }

   public boolean cosmeticExistsAndIsntEquipped(int itemId) {
      return (itemId = getCosmeticItem(itemId)) != -1 && !isCosmeticEquipped(itemId);
   }

   public int getCosmeticItem(int itemId) {
      if (itemExists(itemId)) {
         return itemId;
      }

      int baseId;
      if (itemId < 30000) {
         baseId = (itemId / 1000) * 1000 + (itemId % 100);
      } else {
         baseId = (itemId / 10) * 10;
      }

      return itemId != baseId && itemExists(baseId) ? baseId : -1;
   }

   private int getEquippedCosmeticId(int itemId) {
      if (itemId < 30000) {
         return getPlayer().getFace();
      } else {
         return getPlayer().getHair();
      }
   }

   public boolean isCosmeticEquipped(int itemId) {
      return getEquippedCosmeticId(itemId) == itemId;
   }

   public boolean isUsingOldPqNpcStyle() {
      return YamlConfig.config.server.USE_OLD_GMS_STYLED_PQ_NPCS && this.getPlayer().getParty().isPresent();
   }

   public int[] getAvailableMasteryBooks() {
      return MapleItemInformationProvider.getInstance().usableMasteryBooks(this.getPlayer()).stream().mapToInt(id -> id).toArray();
   }

   public int[] getAvailableSkillBooks() {
      List<Integer> ret = MapleItemInformationProvider.getInstance().usableSkillBooks(this.getPlayer());
      ret.addAll(MapleSkillBookInformationProvider.getInstance().getTeachableSkills(this.getPlayer()));
      return ret.stream().mapToInt(id -> id).toArray();
   }

   public String[] getNamesWhoDropsItem(Integer itemId) {
      return MapleItemInformationProvider.getInstance().getWhoDrops(itemId).toArray(String[]::new);
   }

   public String getSkillBookInfo(int itemId) {
      SkillBookEntry sbe = MapleSkillBookInformationProvider.getInstance().getSkillBookAvailability(itemId);
      switch (sbe) {
         case UNAVAILABLE:
            return "";
         case QUEST_BOOK:
            return "    Obtainable through #rquestline#k (collecting book).";
         case QUEST_REWARD:
            return "    Obtainable through #rquestline#k (quest reward).";
         default:
            return "    Obtainable through #rquestline#k.";
      }
   }

   public int cpqCalcAvgLvl(int map) {
      int num = 0;
      int avg = 0;
      for (MapleMapObject mmo : c.getChannelServer().getMapFactory().getMap(map).getAllPlayer()) {
         avg += ((MapleCharacter) mmo).getLevel();
         num++;
      }
      avg /= num;
      return avg;
   }

   protected boolean sendCPQMapLists(int mapId, int offset) {
      StringBuilder msg = new StringBuilder(LanguageConstants.getMessage(getPlayer(), LanguageConstants.CPQPickRoom));
      int msgLen = msg.length();
      for (int i = 0; i < 3; i++) {
         if (fieldTaken2(i)) {
            if (fieldLobbied2(i)) {
               msg.append("#b#L").append(i).append("#Carnival Field ").append(i + 1).append(" (Level: "
               ).append(cpqCalcAvgLvl(mapId + i * offset)).append(" / ").append(getPlayerCount(mapId + i * offset)).append("x").append(getPlayerCount(mapId + i * offset)).append(")  #l\r\n");
            }
         } else {
            if (i == 0 || i == 1) {
               msg.append("#b#L").append(i).append("#Carnival Field ").append(i + 1).append(" (2x2) #l\r\n");
            } else {
               msg.append("#b#L").append(i).append("#Carnival Field ").append(i + 1).append(" (3x3) #l\r\n");
            }
         }
      }

      if (msg.length() > msgLen) {
         sendSimple(msg.toString());
         return true;
      } else {
         return false;
      }
   }

   public boolean sendCPQMapLists() {
      return sendCPQMapLists(980000100, 100);
   }

   public boolean sendCPQMapLists2() {
      return sendCPQMapLists(980031000, 1000);
   }

   protected boolean fieldTaken(int field, int offset, int map1, int map2, int map3) {
      if (!c.getChannelServer().canInitMonsterCarnival(false, field)) {
         return true;
      }
      if (!c.getChannelServer().getMapFactory().getMap(map1 + field * offset).getAllPlayer().isEmpty()) {
         return true;
      }
      if (!c.getChannelServer().getMapFactory().getMap(map2 + field * offset).getAllPlayer().isEmpty()) {
         return true;
      }
      return !c.getChannelServer().getMapFactory().getMap(map3 + field * offset).getAllPlayer().isEmpty();
   }

   public boolean fieldTaken(int field) {
      return fieldTaken(field, 100, 980000100, 980000101, 980000102);
   }

   public boolean fieldTaken2(int field) {
      return fieldTaken(field, 1000, 980031000, 980031100, 980031200);
   }

   public boolean fieldLobbied(int field) {
      return !c.getChannelServer().getMapFactory().getMap(980000100 + field * 100).getAllPlayer().isEmpty();
   }

   public boolean fieldLobbied2(int field) {
      return !c.getChannelServer().getMapFactory().getMap(980031000 + field * 1000).getAllPlayer().isEmpty();
   }

   protected void cpqLobby(int field, int mapId) {
      try {
         final MapleMap map, mapExit;
         Channel cs = c.getChannelServer();
         mapExit = cs.getMapFactory().getMap(mapId);
         map = cs.getMapFactory().getMap(mapId + 1000 * field);

         c.getPlayer().getParty()
               .map(MapleParty::getMembers).orElse(Collections.emptyList()).parallelStream()
               .map(MaplePartyCharacter::getPlayer)
               .flatMap(Optional::stream)
               .forEach(character -> {
                  character.setChallenged(false);
                  character.changeMap(map, map.getPortal(0));
                  MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("CPQ_ENTER_LOBBY"));
                  TimerManager tMan = TimerManager.getInstance();
                  tMan.schedule(() -> mapClock(3 * 60), 1500);
                  character.setCpqTimer(TimerManager.getInstance().schedule(() -> character.changeMap(mapExit, mapExit.getPortal(0)), 3 * 60 * 1000));
               });
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   public void cpqLobby(int field) {
      cpqLobby(field, 980000000);
   }

   public void cpqLobby2(int field) {
      cpqLobby(field, 980031000);
   }

   public void cancelCPQLobby() {
      c.getPlayer().getParty()
            .map(MapleParty::getMembers).orElse(Collections.emptyList()).parallelStream()
            .map(MaplePartyCharacter::getPlayer)
            .flatMap(Optional::stream)
            .forEach(MapleCharacter::clearCpqTimer);
   }

   private void warpOutCPQLobby(MapleMap lobbyMap) {
      MapleMap out = lobbyMap.getChannelServer().getMapFactory().getMap((lobbyMap.getId() < 980030000) ? 980000000 : 980030000);
      for (MapleCharacter mc : lobbyMap.getAllPlayers()) {
         mc.resetCP();
         mc.setTeam(-1);
         mc.setMonsterCarnival(null);
         mc.changeMap(out, out.getPortal(0));
      }
   }

   private int isCPQParty(MapleMap lobby, MapleParty party) {
      int cpqMinLvl, cpqMaxLvl;

      if (lobby.isCPQLobby()) {
         cpqMinLvl = 30;
         cpqMaxLvl = 50;
      } else {
         cpqMinLvl = 51;
         cpqMaxLvl = 70;
      }

      List<MaplePartyCharacter> partyMembers = party.getPartyMembers();
      for (MaplePartyCharacter partyCharacter : partyMembers) {
         if (partyCharacter.getLevel() >= cpqMinLvl && partyCharacter.getLevel() <= cpqMaxLvl) {
            if (lobby.getCharacterById(partyCharacter.getId()) == null) {
               return 1;  // party member detected out of area
            }
         } else {
            return 2;  // party member doesn't fit requirements
         }
      }

      return 0;
   }

   private int canStartCPQ(MapleMap lobby, MapleParty party, MapleParty challenger) {
      int ret = isCPQParty(lobby, party);
      if (ret != 0) {
         return ret;
      }

      ret = isCPQParty(lobby, challenger);
      if (ret != 0) {
         return -ret;
      }

      return 0;
   }

   protected void startCPQ(final MapleCharacter challenger, final int field, BiConsumer<MapleCharacter, MapleMap> warp, int mapOffset, int roomOffset, int delay) {
      try {
         cancelCPQLobby();

         final MapleMap lobbyMap = getPlayer().getMap();
         if (challenger != null) {
            if (challenger.getParty().isEmpty()) {
               throw new RuntimeException("No opponent found!");
            }

            challenger.getParty()
                  .map(MapleParty::getMembers).orElse(Collections.emptyList()).parallelStream()
                  .map(MaplePartyCharacter::getPlayer)
                  .flatMap(Optional::stream)
                  .forEach(character -> {
                     character.changeMap(lobbyMap, lobbyMap.getPortal(0));
                     TimerManager tMan = TimerManager.getInstance();
                     tMan.schedule(() -> mapClock(10), 1500);
                  });

            getPlayer().getParty()
                  .map(MapleParty::getMembers).orElse(Collections.emptyList()).parallelStream()
                  .map(member -> c.getChannelServer().getPlayerStorage().getCharacterById(member.getId()))
                  .flatMap(Optional::stream)
                  .forEach(character -> warp.accept(character, lobbyMap));
         }

         final int mapId = c.getPlayer().getMapId() + mapOffset;
         TimerManager tMan = TimerManager.getInstance();
         tMan.schedule(() -> {
            try {
               getPlayer().getParty()
                     .map(MapleParty::getMembers).orElse(Collections.emptyList()).parallelStream()
                     .map(MaplePartyCharacter::getPlayer)
                     .flatMap(Optional::stream)
                     .forEach(character -> character.setMonsterCarnival(null));

               challenger.getParty()
                     .map(MapleParty::getMembers).orElse(Collections.emptyList()).parallelStream()
                     .map(MaplePartyCharacter::getPlayer)
                     .flatMap(Optional::stream)
                     .forEach(character -> character.setMonsterCarnival(null));
            } catch (NullPointerException npe) {
               warpOutCPQLobby(lobbyMap);
               return;
            }

            MapleParty lobbyParty = getPlayer().getParty().orElseThrow(), challengerParty = challenger.getParty().orElseThrow();
            int status = canStartCPQ(lobbyMap, lobbyParty, challengerParty);
            if (status == 0) {
               new MonsterCarnival(lobbyParty, challengerParty, mapId, true, (field / roomOffset) % 10);
            } else {
               warpOutCPQLobby(lobbyMap);
            }
         }, delay);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void startCPQ(final MapleCharacter challenger, final int field) {
      startCPQ(challenger, field, (character, map) -> {
         TimerManager tMan = TimerManager.getInstance();
         tMan.schedule(() -> mapClock(10), 1500);
      }, 1, 100, 11000);
   }

   public void startCPQ2(final MapleCharacter challenger, final int field) {
      startCPQ(challenger, field, (character, map) -> {
         character.changeMap(map, map.getPortal(0));
         mapClock(10);
      }, 100, 1000, 10000);
   }

   public void mapClock(int time) {
      MasterBroadcaster.getInstance().sendToAllInMap(getPlayer().getMap(), new GetClock(time));
   }

   private boolean sendCPQChallenge(String cpqType, int leaderId) {
      Set<Integer> cpqLeaders = new HashSet<>();
      cpqLeaders.add(leaderId);
      cpqLeaders.add(getPlayer().getId());

      return c.getWorldServer().getMatchCheckerCoordinator().createMatchConfirmation(MatchCheckerType.CPQ_CHALLENGE, c.getWorld(), getPlayer().getId(), cpqLeaders, cpqType);
   }

   public void answerCPQChallenge(boolean accept) {
      c.getWorldServer().getMatchCheckerCoordinator().answerMatchConfirmation(getPlayer().getId(), accept);
   }

   protected void challengeParty(int field, int mapId, String type) {
      MapleCharacter leader = null;
      MapleMap map = c.getChannelServer().getMapFactory().getMap(mapId + 100 * field);

      if (getPlayer().getParty().map(party -> party.getMembers().size() != map.getAllPlayers().size()).orElse(true)) {
         sendOk("An unexpected error regarding the other party has occurred.");
         return;
      }
      for (MapleMapObject mmo : map.getAllPlayer()) {
         MapleCharacter mc = (MapleCharacter) mmo;
         if (mc.getParty().isEmpty()) {
            sendOk(LanguageConstants.getMessage(mc, LanguageConstants.CPQFindError));
            return;
         }
         if (mc.getParty().map(party -> party.getLeader().getId() == mc.getId()).orElse(false)) {
            leader = mc;
            break;
         }
      }
      if (leader != null) {
         if (leader.canBeChallenged()) {
            if (!sendCPQChallenge(type, leader.getId())) {
               sendOk(LanguageConstants.getMessage(leader, LanguageConstants.CPQChallengeRoomAnswer));
            }
         } else {
            sendOk(LanguageConstants.getMessage(leader, LanguageConstants.CPQChallengeRoomAnswer));
         }
      } else {
         sendOk(LanguageConstants.getMessage(leader, LanguageConstants.CPQLeaderNotFound));
      }
   }

   public void challengeParty(int field) {
      challengeParty(field, 980000100, "cpq1");
   }

   public void challengeParty2(int field) {
      challengeParty(field, 980031000, "cpq2");
   }

   private synchronized boolean setupAriantBattle(MapleExpedition expedition, int mapId) {
      MapleMap arenaMap = this.getMap().getChannelServer().getMapFactory().getMap(mapId + 1);
      if (!arenaMap.getAllPlayers().isEmpty()) {
         return false;
      }

      new AriantColiseum(arenaMap, expedition);
      return true;
   }

   public String startAriantBattle(MapleExpeditionType expeditionType, int mapId) {
      if (!GameConstants.isAriantColiseumLobby(mapId)) {
         return "You cannot start an Ariant tournament from outside the Battle Arena Entrance.";
      }

      MapleExpedition expedition = this.getMap().getChannelServer().getExpedition(expeditionType);
      if (expedition == null) {
         return "Please register on an expedition before attempting to start an Ariant tournament.";
      }

      List<MapleCharacter> players = expedition.getActiveMembers();

      int playersSize = players.size();
      if (!(playersSize >= expedition.getMinSize() && playersSize <= expedition.getMaxSize())) {
         return "Make sure there are between #r" + expedition.getMinSize() + " ~ " + expedition.getMaxSize() + " players#k in this room to start the battle.";
      }

      MapleMap leaderMap = this.getMap();
      for (MapleCharacter mc : players) {
         if (mc.getMap() != leaderMap) {
            return "All competing players should be on this area to start the battle.";
         }

         if (mc.getParty().isPresent()) {
            return "All competing players must not be on a party to start the battle.";
         }

         int level = mc.getLevel();
         if (!(level >= expeditionType.getMinLevel() && level <= expeditionType.getMaxLevel())) {
            return "There are competing players outside of the acceptable level range in this room. All players must be on #blevel between 20~30#k to start the battle.";
         }
      }

      if (setupAriantBattle(expedition, mapId)) {
         return "";
      } else {
         return "Other players are already competing on the Ariant tournament in this room. Please wait a while until the arena becomes available again.";
      }
   }

   public void sendMarriageWishList(boolean groom) {
      MapleCharacter player = this.getPlayer();
      MapleMarriage marriage = player.getMarriageInstance();
      if (marriage != null) {
         int cid = marriage.getIntProperty(groom ? "groomId" : "brideId");
         MapleCharacter chr = marriage.getPlayerById(cid);
         if (chr != null) {
            if (chr.getId() == player.getId()) {
               PacketCreator.announce(player, new WeddingGiftResult((byte) 0xA, marriage.getWishListItems(groom), marriage.getGiftItems(player.getClient(), groom)));
            } else {
               marriage.setIntProperty("wishlistSelection", groom ? 0 : 1);
               PacketCreator.announce(player, new WeddingGiftResult((byte) 0x09, marriage.getWishListItems(groom), marriage.getGiftItems(player.getClient(), groom)));
            }
         }
      }
   }

   public void sendMarriageGifts(List<Item> gifts) {
      PacketCreator.announce(getPlayer(), new WeddingGiftResult((byte) 0xA, Collections.singletonList(""), gifts));
   }

   public boolean createMarriageWishList() {
      MapleMarriage marriage = this.getPlayer().getMarriageInstance();
      if (marriage != null) {
         Boolean groom = marriage.isMarriageGroom(this.getPlayer());
         if (groom != null) {
            String wlKey;
            if (groom) {
               wlKey = "groomWishlist";
            } else {
               wlKey = "brideWishlist";
            }

            if (marriage.getProperty(wlKey).contentEquals("")) {
               PacketCreator.announce(getClient(), new SendWishList());
               return true;
            }
         }
      }

      return false;
   }
}