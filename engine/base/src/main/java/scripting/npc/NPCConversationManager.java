/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation version 3 as published by
the Free Software Foundation. You may not use, modify or distribute
this program under any other version of the GNU Affero General Public
License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import constants.GameConstants;
import constants.ItemConstants;
import constants.LanguageConstants;
import constants.ServerConstants;
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
import server.MapleItemInformationProvider;
import server.MapleMarriage;
import server.MapleShop;
import server.MapleShopFactory;
import server.MapleSkillbookInformationProvider;
import server.MapleSkillbookInformationProvider.SkillBookEntry;
import server.MapleStatEffect;
import server.TimerManager;
import server.events.gm.MapleEvent;
import server.expeditions.MapleExpedition;
import server.expeditions.MapleExpeditionType;
import server.gachapon.MapleGachapon;
import server.gachapon.MapleGachapon.MapleGachaponItem;
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
import tools.FilePrinter;
import tools.LogHelper;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.packets.Wedding;

/**
 * @author Matze
 */
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

   private String getDefaultTalk(int npcid) {
      String talk = npcDefaultTalks.get(npcid);
      if (talk == null) {
         talk = MapleLifeFactory.getNPCDefaultTalk(npcid);
         npcDefaultTalks.put(npcid, talk);
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
      getClient().announce(MaplePacketCreator.enableActions());
   }

   public void sendNext(String text) {
      getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 01", (byte) 0));
   }

   public void sendPrev(String text) {
      getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 00", (byte) 0));
   }

   public void sendNextPrev(String text) {
      getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 01", (byte) 0));
   }

   public void sendOk(String text) {
      getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 00", (byte) 0));
   }

   public void sendDefault() {
      sendOk(getDefaultTalk(npc));
   }

   public void sendYesNo(String text) {
      getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 1, text, "", (byte) 0));
   }

   public void sendAcceptDecline(String text) {
      getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 0x0C, text, "", (byte) 0));
   }

   public void sendSimple(String text) {
      getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 4, text, "", (byte) 0));
   }

   public void sendNext(String text, byte speaker) {
      getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 01", speaker));
   }

   public void sendPrev(String text, byte speaker) {
      getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 00", speaker));
   }

   public void sendNextPrev(String text, byte speaker) {
      getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 01", speaker));
   }

   public void sendOk(String text, byte speaker) {
      getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 00", speaker));
   }

   public void sendYesNo(String text, byte speaker) {
      getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 1, text, "", speaker));
   }

   public void sendAcceptDecline(String text, byte speaker) {
      getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 0x0C, text, "", speaker));
   }

   public void sendSimple(String text, byte speaker) {
      getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 4, text, "", speaker));
   }

   public void sendStyle(String text, int[] styles) {
      if (styles.length > 0) {
         getClient().announce(MaplePacketCreator.getNPCTalkStyle(npc, text, styles));
      } else {    // thanks Conrad for noticing empty styles crashing players
         sendOk("Sorry, there are no options of cosmetics available for you here at the moment.");
         dispose();
      }
   }

   public void sendGetNumber(String text, int def, int min, int max) {
      getClient().announce(MaplePacketCreator.getNPCTalkNum(npc, text, def, min, max));
   }

   public void sendGetText(String text) {
      getClient().announce(MaplePacketCreator.getNPCTalkText(npc, text, ""));
   }

   /*
    * 0 = ariant colliseum
    * 1 = Dojo
    * 2 = Carnival 1
    * 3 = Carnival 2
    * 4 = Ghost Ship PQ?
    * 5 = Pyramid PQ
    * 6 = Kerning Subway
    */
   public void sendDimensionalMirror(String text) {
      getClient().announce(MaplePacketCreator.getDimensionalMirror(text));
   }

   public void setGetText(String text) {
      this.getText = text;
   }

   public String getText() {
      return this.getText;
   }

   public int getJobId() {
      return getPlayer().getJob().getId();
   }

   public MapleJob getJob() {
      return getPlayer().getJob();
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

   public int getLevel() {
      return getPlayer().getLevel();
   }

   @Override
   public void showEffect(String effect) {
      getPlayer().getMap().broadcastMessage(MaplePacketCreator.environmentChange(effect, 3));
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

   public int itemQuantity(int itemid) {
      return getPlayer().getInventory(ItemConstants.getInventoryType(itemid)).countById(itemid);
   }

   public void displayGuildRanks() {
      MapleGuildProcessor.getInstance().displayGuildRanks(getClient(), npc);
   }

   public boolean canSpawnPlayerNpc(int mapid) {
      MapleCharacter chr = getPlayer();
      return !ServerConstants.PLAYERNPC_AUTODEPLOY && chr.getLevel() >= chr.getMaxClassLevel() && !chr.isGM() && MaplePlayerNPC.canSpawnPlayerNpc(chr.getName(), mapid);
   }

   public MaplePlayerNPC getPlayerNPCByScriptid(int scriptId) {
      for (MapleMapObject pnpcObj : getPlayer().getMap().getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.PLAYER_NPC))) {
         MaplePlayerNPC pn = (MaplePlayerNPC) pnpcObj;

         if (pn.getScriptId() == scriptId) {
            return pn;
         }
      }

      return null;
   }

   @Override
   public MapleParty getParty() {
      return getPlayer().getParty();
   }

   @Override
   public void resetMap(int mapid) {
      getClient().getChannelServer().getMapFactory().getMap(mapid).resetReactors();
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
         shop.sendShop(c);
      } else {    // check for missing shopids thanks to resinate
         FilePrinter.printError(FilePrinter.NPC_UNCODED, "Shop ID: " + id + " is missing from database.");
         MapleShopFactory.getInstance().getShop(11000).sendShop(c);
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

      Item itemGained = gainItem(item.getId(), (short) (item.getId() / 10000 == 200 ? 100 : 1), true, true); // For normal potions, make it give 100.

      sendNext("You have obtained a #b#t" + item.getId() + "##k.");

      String map = c.getChannelServer().getMapFactory().getMap(maps[(getNpc() != 9100117 && getNpc() != 9100109) ? (getNpc() - 9100100) : getNpc() == 9100109 ? 8 : 9]).getMapName();

      LogHelper.logGacha(getPlayer(), item.getId(), map);

      if (item.getTier() > 0) { //Uncommon and Rare
         Server.getInstance().broadcastMessage(c.getWorld(), MaplePacketCreator.gachaponMessage(itemGained, map, getPlayer()));
      }
   }

   public void upgradeAlliance() {
      int allianceId = c.getPlayer().getGuild().map(MapleGuild::getAllianceId).orElse(0);
      Server.getInstance().getAlliance(allianceId)
            .ifPresent(alliance -> {
               alliance.increaseCapacity(1);
               Server.getInstance().allianceMessage(allianceId, MaplePacketCreator.getGuildAlliances(alliance, c.getWorld()), -1, -1);
               Server.getInstance().allianceMessage(allianceId, MaplePacketCreator.allianceNotice(allianceId, alliance.getNotice()), -1, -1);
               c.announce(MaplePacketCreator.updateAllianceInfo(alliance, c.getWorld()));  // thanks Vcoc for finding an alliance update to leader issue
            });
   }

   public void disbandAlliance(MapleClient c, int allianceId) {
      MapleAllianceProcessor.getInstance().disbandAlliance(allianceId);
   }

   public boolean canBeUsedAllianceName(String name) {
      return MapleAllianceProcessor.getInstance().canBeUsedAllianceName(name);
   }

   public MapleAlliance createAlliance(String name) {
      return MapleAllianceProcessor.getInstance().createAlliance(getParty(), name);
   }

   public int getAllianceCapacity() {
      return getPlayer()
            .getGuild()
            .map(MapleGuild::getAllianceId)
            .flatMap(allianceId -> Server.getInstance().getAlliance(allianceId))
            .map(MapleAlliance::getCapacity)
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
      c.announce(MaplePacketCreator.getFredrick(getPlayer()));
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
         getPlayer().setTeam(getEvent().getLimit() % 2); //muhaha :D
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

      MapleParty partyz = getPlayer().getParty();
      MapleMapManager mapManager = c.getChannelServer().getMapFactory();

      MapleMap map = null;
      int mapid = 926010100;
      if (party) {
         mapid += 10000;
      }
      mapid += (mod.getMode() * 1000);

      for (byte b = 0; b < 5; b++) {//They cannot warp to the next map before the timer ends (:
         map = mapManager.getMap(mapid + b);
         if (map.getCharacters().size() > 0) {
         } else {
            break;
         }
      }

      if (map == null) {
         return false;
      }

      if (!party) {
         partyz = new MapleParty(-1, new MaplePartyCharacter(getPlayer()));
      }
      Pyramid py = new Pyramid(partyz, mod, map.getId());
      getPlayer().setPartyQuest(py);
      py.warp(mapid);
      dispose();
      return true;
   }

   public boolean itemExists(int itemid) {
      return MapleItemInformationProvider.getInstance().getName(itemid) != null;
   }

   public int getCosmeticItem(int itemid) {
      if (itemExists(itemid)) {
         return itemid;
      }

      int baseid;
      if (itemid < 30000) {
         baseid = (itemid / 1000) * 1000 + (itemid % 100);
      } else {
         baseid = (itemid / 10) * 10;
      }

      return itemid != baseid && itemExists(baseid) ? baseid : -1;
   }

   private int getEquippedItemid(int itemid) {
      if (itemid < 30000) {
         return getPlayer().getFace();
      } else {
         return getPlayer().getHair();
      }
   }

   public boolean isCosmeticEquipped(int itemid) {
      return getEquippedItemid(itemid) == itemid;
   }

   public boolean isUsingOldPqNpcStyle() {
      return ServerConstants.USE_OLD_GMS_STYLED_PQ_NPCS && this.getPlayer().getParty() != null;
   }

   public int[] getAvailableMasteryBooks() {
      return MapleItemInformationProvider.getInstance().usableMasteryBooks(this.getPlayer()).stream().mapToInt(id -> id).toArray();
   }

   public int[] getAvailableSkillBooks() {
      return MapleItemInformationProvider.getInstance().usableSkillBooks(this.getPlayer()).stream().mapToInt(id -> id).toArray();
   }

   public String[] getNamesWhoDropsItem(Integer itemId) {
      return MapleItemInformationProvider.getInstance().getWhoDrops(itemId).toArray(String[]::new);
   }

   public String getSkillBookInfo(int itemid) {
      SkillBookEntry sbe = MapleSkillbookInformationProvider.getInstance().getSkillbookAvailability(itemid);
      return sbe != SkillBookEntry.UNAVAILABLE ? "    Obtainable through #rquestline#k." : "";
   }

   // By Drago/Dragohe4rt CPQ + WED
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

   public boolean sendCPQMapLists() {
      StringBuilder msg = new StringBuilder(LanguageConstants.getMessage(getPlayer(), LanguageConstants.CPQPickRoom));
      int msgLen = msg.length();
      for (int i = 0; i < 6; i++) {
         if (fieldTaken(i)) {
            if (fieldLobbied(i)) {
               msg.append("#b#L").append(i).append("#Carnival Field ").append(i + 1).append(" (Level: "  // "Carnival field" GMS-like improvement thanks to Jayd
               ).append(cpqCalcAvgLvl(980000100 + i * 100)).append(" / ").append(getPlayerCount(980000100 + i * 100)).append("x").append(getPlayerCount(980000100 + i * 100)).append(")  #l\r\n");
            }
         } else {
            if (i >= 0 && i <= 3) {
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

   public boolean fieldTaken(int field) {
      if (!c.getChannelServer().canInitMonsterCarnival(true, field)) {
         return true;
      }
      if (!c.getChannelServer().getMapFactory().getMap(980000100 + field * 100).getAllPlayer().isEmpty()) {
         return true;
      }
      if (!c.getChannelServer().getMapFactory().getMap(980000101 + field * 100).getAllPlayer().isEmpty()) {
         return true;
      }
      return !c.getChannelServer().getMapFactory().getMap(980000102 + field * 100).getAllPlayer().isEmpty();
   }

   public boolean fieldLobbied(int field) {
      return !c.getChannelServer().getMapFactory().getMap(980000100 + field * 100).getAllPlayer().isEmpty();
   }

   public void cpqLobby(int field) {
      try {
         final MapleMap map, mapExit;
         Channel cs = c.getChannelServer();
         map = cs.getMapFactory().getMap(980000100 + 100 * field);
         mapExit = cs.getMapFactory().getMap(980000000);

         c.getPlayer().getParty().getMembers().stream()
               .map(MaplePartyCharacter::getPlayer)
               .forEach(character -> {
                  character.setChallenged(false);
                  character.changeMap(map, map.getPortal(0));
                  MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.LIGHT_BLUE, LanguageConstants.getMessage(character, LanguageConstants.CPQEntryLobby));
                  TimerManager tMan = TimerManager.getInstance();
                  tMan.schedule(() -> mapClock(3 * 60), 1500);
                  character.setCpqTimer(TimerManager.getInstance().schedule(() -> character.changeMap(mapExit, mapExit.getPortal(0)), 3 * 60 * 1000));
               });
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   public MapleCharacter getChrById(int id) {
      return c.getChannelServer().getPlayerStorage().getCharacterById(id).orElse(null);
   }

   public void cancelCPQLobby() {
      c.getPlayer().getParty().getMembers().parallelStream()
            .map(MaplePartyCharacter::getPlayer)
            .forEach(MapleCharacter::clearCpqTimer);
   }

   private void warpoutCPQLobby(MapleMap lobbyMap) {
      MapleMap out = lobbyMap.getChannelServer().getMapFactory().getMap((lobbyMap.getId() < 980030000) ? 980000000 : 980030000);
      for (MapleCharacter mc : lobbyMap.getAllPlayers()) {
         mc.resetCP();
         mc.setTeam(-1);
         mc.setMonsterCarnival(null);
         mc.changeMap(out, out.getPortal(0));
      }
   }

   public void startCPQ(final MapleCharacter challenger, final int field) {
      try {
         cancelCPQLobby();

         final MapleMap lobbyMap = getPlayer().getMap();
         if (challenger != null) {
            if (challenger.getParty() == null) {
               throw new RuntimeException("No opponent found!");
            }

            challenger.getParty().getMembers().parallelStream()
                  .map(MaplePartyCharacter::getPlayer)
                  .forEach(character -> {
                     character.changeMap(lobbyMap, lobbyMap.getPortal(0));
                     TimerManager tMan = TimerManager.getInstance();
                     tMan.schedule(() -> mapClock(10), 1500);
                  });

            getPlayer().getParty().getMembers().parallelStream()
                  .map(member -> c.getChannelServer().getPlayerStorage().getCharacterById(member.getId()))
                  .flatMap(Optional::stream)
                  .forEach(character -> {
                     TimerManager tMan = TimerManager.getInstance();
                     tMan.schedule(() -> mapClock(10), 1500);
                  });
         }

         final int mapid = c.getPlayer().getMapId() + 1;
         TimerManager tMan = TimerManager.getInstance();
         tMan.schedule(new Runnable() {
            @Override
            public void run() {
               try {
                  getPlayer().getParty().getMembers().stream()
                        .map(MaplePartyCharacter::getPlayer)
                        .forEach(character -> character.setMonsterCarnival(null));

                  challenger.getParty().getMembers().stream()
                        .map(MaplePartyCharacter::getPlayer)
                        .forEach(character -> character.setMonsterCarnival(null));
               } catch (NullPointerException npe) {
                  warpoutCPQLobby(lobbyMap);
                  return;
               }

               new MonsterCarnival(getPlayer().getParty(), challenger.getParty(), mapid, true, (field / 100) % 10);
            }
         }, 11000);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void startCPQ2(final MapleCharacter challenger, final int field) {
      try {
         cancelCPQLobby();

         final MapleMap lobbyMap = getPlayer().getMap();
         if (challenger != null) {
            if (challenger.getParty() == null) {
               throw new RuntimeException("No opponent found!");
            }

            challenger.getParty().getMembers().stream()
                  .map(MaplePartyCharacter::getPlayer)
                  .forEach(character -> {
                     character.changeMap(lobbyMap, lobbyMap.getPortal(0));
                     mapClock(10);
                  });
         }
         final int mapid = c.getPlayer().getMapId() + 100;
         TimerManager tMan = TimerManager.getInstance();
         tMan.schedule(new Runnable() {
            @Override
            public void run() {
               try {
                  getPlayer().getParty().getMembers().stream()
                        .map(MaplePartyCharacter::getPlayer)
                        .forEach(character -> character.setMonsterCarnival(null));
                  challenger.getParty().getMembers().stream()
                        .map(MaplePartyCharacter::getPlayer)
                        .forEach(character -> character.setMonsterCarnival(null));
               } catch (NullPointerException npe) {
                  warpoutCPQLobby(lobbyMap);
                  return;
               }

               new MonsterCarnival(getPlayer().getParty(), challenger.getParty(), mapid, false, (field / 1000) % 10);
            }
         }, 10000);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public boolean sendCPQMapLists2() {
      StringBuilder msg = new StringBuilder(LanguageConstants.getMessage(getPlayer(), LanguageConstants.CPQPickRoom));
      int msgLen = msg.length();
      for (int i = 0; i < 3; i++) {
         if (fieldTaken2(i)) {
            if (fieldLobbied2(i)) {
               msg.append("#b#L").append(i).append("#Carnival Field ").append(i + 1).append(" (Level: "  // "Carnival field" GMS-like improvement thanks to Jayd
               ).append(cpqCalcAvgLvl(980031000 + i * 1000)).append(" / ").append(getPlayerCount(980031000 + i * 1000)).append("x").append(getPlayerCount(980031000 + i * 1000)).append(")  #l\r\n");
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

   public boolean fieldTaken2(int field) {
      if (!c.getChannelServer().canInitMonsterCarnival(false, field)) {
         return true;
      }
      if (!c.getChannelServer().getMapFactory().getMap(980031000 + field * 1000).getAllPlayer().isEmpty()) {
         return true;
      }
      if (!c.getChannelServer().getMapFactory().getMap(980031100 + field * 1000).getAllPlayer().isEmpty()) {
         return true;
      }
      return !c.getChannelServer().getMapFactory().getMap(980031200 + field * 1000).getAllPlayer().isEmpty();
   }

   public boolean fieldLobbied2(int field) {
      return !c.getChannelServer().getMapFactory().getMap(980031000 + field * 1000).getAllPlayer().isEmpty();
   }

   public void cpqLobby2(int field) {
      try {
         final MapleMap map, mapExit;
         Channel cs = c.getChannelServer();
         mapExit = cs.getMapFactory().getMap(980030000);
         map = cs.getMapFactory().getMap(980031000 + 1000 * field);

         c.getPlayer().getParty().getMembers().stream()
               .map(MaplePartyCharacter::getPlayer)
               .forEach(character -> {
                  character.setChallenged(false);
                  character.changeMap(map, map.getPortal(0));
                  MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.LIGHT_BLUE, LanguageConstants.getMessage(character, LanguageConstants.CPQEntryLobby));
                  TimerManager tMan = TimerManager.getInstance();
                  tMan.schedule(() -> mapClock(3 * 60), 1500);
                  character.setCpqTimer(TimerManager.getInstance().schedule(() -> character.changeMap(mapExit, mapExit.getPortal(0)), 3 * 60 * 1000));
               });
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   public void mapClock(int time) {
      getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(time));
   }

   private boolean sendCPQChallenge(String cpqType, int leaderid) {
      Set<Integer> cpqLeaders = new HashSet<>();
      cpqLeaders.add(leaderid);
      cpqLeaders.add(getPlayer().getId());

      return c.getWorldServer().getMatchCheckerCoordinator().createMatchConfirmation(MatchCheckerType.CPQ_CHALLENGE, c.getWorld(), getPlayer().getId(), cpqLeaders, cpqType);
   }

   public void answerCPQChallenge(boolean accept) {
      c.getWorldServer().getMatchCheckerCoordinator().answerMatchConfirmation(getPlayer().getId(), accept);
   }

   public void challengeParty2(int field) {
      MapleCharacter leader = null;
      MapleMap map = c.getChannelServer().getMapFactory().getMap(980031000 + 1000 * field);
      for (MapleMapObject mmo : map.getAllPlayer()) {
         MapleCharacter mc = (MapleCharacter) mmo;
         if (mc.getParty() == null) {
            sendOk(LanguageConstants.getMessage(mc, LanguageConstants.CPQFindError));
            return;
         }
         if (mc.getParty().getLeader().getId() == mc.getId()) {
            leader = mc;
            break;
         }
      }
      if (leader != null) {
         if (leader.canBeChallenged()) {
            if (!sendCPQChallenge("cpq2", leader.getId())) {
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
      MapleCharacter leader = null;
      MapleMap map = c.getChannelServer().getMapFactory().getMap(980000100 + 100 * field);
      if (map.getAllPlayer().size() != getPlayer().getParty().getMembers().size()) {
         sendOk("An unexpected error regarding the other party has occurred.");
         return;
      }
      for (MapleMapObject mmo : map.getAllPlayer()) {
         MapleCharacter mc = (MapleCharacter) mmo;
         if (mc.getParty() == null) {
            sendOk(LanguageConstants.getMessage(mc, LanguageConstants.CPQFindError));
            return;
         }
         if (mc.getParty().getLeader().getId() == mc.getId()) {
            leader = mc;
            break;
         }
      }
      if (leader != null) {
         if (leader.canBeChallenged()) {
            if (!sendCPQChallenge("cpq1", leader.getId())) {
               sendOk(LanguageConstants.getMessage(leader, LanguageConstants.CPQChallengeRoomAnswer));
            }
         } else {
            sendOk(LanguageConstants.getMessage(leader, LanguageConstants.CPQChallengeRoomAnswer));
         }
      } else {
         sendOk(LanguageConstants.getMessage(leader, LanguageConstants.CPQLeaderNotFound));
      }
   }

   private synchronized boolean setupAriantBattle(MapleExpedition exped, int mapid) {
      MapleMap arenaMap = this.getMap().getChannelServer().getMapFactory().getMap(mapid + 1);
      if (!arenaMap.getAllPlayers().isEmpty()) {
         return false;
      }

      new AriantColiseum(arenaMap, exped);
      return true;
   }

   public String startAriantBattle(MapleExpeditionType expedType, int mapid) {
      if (!GameConstants.isAriantColiseumLobby(mapid)) {
         return "You cannot start an Ariant tournament from outside the Battle Arena Entrance.";
      }

      MapleExpedition exped = this.getMap().getChannelServer().getExpedition(expedType);
      if (exped == null) {
         return "Please register on an expedition before attempting to start an Ariant tournament.";
      }

      List<MapleCharacter> players = exped.getActiveMembers();

      int playersSize = players.size();
      if (!(playersSize >= exped.getMinSize() && playersSize <= exped.getMaxSize())) {
         return "Make sure there are between #r" + exped.getMinSize() + " ~ " + exped.getMaxSize() + " players#k in this room to start the battle.";
      }

      MapleMap leaderMap = this.getMap();
      for (MapleCharacter mc : players) {
         if (mc.getMap() != leaderMap) {
            return "All competing players should be on this area to start the battle.";
         }

         if (mc.getParty() != null) {
            return "All competing players must not be on a party to start the battle.";
         }

         int level = mc.getLevel();
         if (!(level >= expedType.getMinLevel() && level <= expedType.getMaxLevel())) {
            return "There are competing players outside of the acceptable level range in this room. All players must be on #blevel between 20~30#k to start the battle.";
         }
      }

      if (setupAriantBattle(exped, mapid)) {
         return "";
      } else {
         return "Other players are already competing on the Ariant tournament in this room. Please wait a while until the arena becomes available again.";
      }
   }

   public void sendMarriageWishlist(boolean groom) {
      MapleCharacter player = this.getPlayer();
      MapleMarriage marriage = player.getMarriageInstance();
      if (marriage != null) {
         int cid = marriage.getIntProperty(groom ? "groomId" : "brideId");
         MapleCharacter chr = marriage.getPlayerById(cid);
         if (chr != null) {
            if (chr.getId() == player.getId()) {
               player.announce(Wedding.OnWeddingGiftResult((byte) 0xA, marriage.getWishlistItems(groom), marriage.getGiftItems(player.getClient(), groom)));
            } else {
               marriage.setIntProperty("wishlistSelection", groom ? 0 : 1);
               player.announce(Wedding.OnWeddingGiftResult((byte) 0x09, marriage.getWishlistItems(groom), marriage.getGiftItems(player.getClient(), groom)));
            }
         }
      }
   }

   public void sendMarriageGifts(List<Item> gifts) {
      this.getPlayer().announce(Wedding.OnWeddingGiftResult((byte) 0xA, Collections.singletonList(""), gifts));
   }

   public boolean createMarriageWishlist() {
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
               getClient().announce(Wedding.sendWishList());
               return true;
            }
         }
      }

      return false;
   }
}