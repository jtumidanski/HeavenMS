package scripting.reactor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.IntStream;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import config.YamlConfig;
import constants.ItemConstants;
import constants.MapleInventoryType;
import scripting.AbstractPlayerInteraction;
import scripting.event.EventInstanceManager;
import server.MapleItemInformationProvider;
import server.TimerManager;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleNPCFactory;
import server.maps.MapMonitor;
import server.maps.MapleMap;
import server.maps.MapleReactor;
import server.maps.ReactorDropEntry;
import server.partyquest.MapleCarnivalFactory;
import server.partyquest.MapleCarnivalFactory.MCSkill;
import tools.MasterBroadcaster;
import tools.packet.monster.DamageMonster;

public class ReactorActionManager extends AbstractPlayerInteraction {
   private MapleReactor reactor;
   private ScriptEngine iv;
   private ScheduledFuture<?> sprayTask = null;

   public ReactorActionManager(MapleClient c, MapleReactor reactor, ScriptEngine iv) {
      super(c);
      this.reactor = reactor;
      this.iv = iv;
   }

   public void hitReactor() {
      reactor.hitReactor(c);
   }

   public void destroyNpc(int npcId) {
      reactor.getMap().destroyNPC(npcId);
   }

   private static void sortDropEntries(List<ReactorDropEntry> from, List<ReactorDropEntry> item,
                                       List<ReactorDropEntry> visibleQuest, List<ReactorDropEntry> otherQuest, MapleCharacter chr) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      for (ReactorDropEntry mde : from) {
         if (!ii.isQuestItem(mde.itemId())) {
            item.add(mde);
         } else {
            if (chr.needQuestItem(mde.questId(), mde.itemId())) {
               visibleQuest.add(mde);
            } else {
               otherQuest.add(mde);
            }
         }
      }
   }

   private static List<ReactorDropEntry> assembleReactorDropEntries(MapleCharacter chr, List<ReactorDropEntry> items) {
      final List<ReactorDropEntry> dropEntry = new ArrayList<>();
      final List<ReactorDropEntry> visibleQuestEntry = new ArrayList<>();
      final List<ReactorDropEntry> otherQuestEntry = new ArrayList<>();
      sortDropEntries(items, dropEntry, visibleQuestEntry, otherQuestEntry, chr);

      Collections.shuffle(dropEntry);
      Collections.shuffle(visibleQuestEntry);
      Collections.shuffle(otherQuestEntry);

      items.clear();
      items.addAll(dropEntry);
      items.addAll(visibleQuestEntry);
      items.addAll(otherQuestEntry);

      List<ReactorDropEntry> items1 = new ArrayList<>(items.size());
      List<ReactorDropEntry> items2 = new ArrayList<>(items.size() / 2);

      for (int i = 0; i < items.size(); i++) {
         if (i % 2 == 0) {
            items1.add(items.get(i));
         } else {
            items2.add(items.get(i));
         }
      }

      Collections.reverse(items1);
      items1.addAll(items2);

      return items1;
   }

   public void sprayItems() {
      sprayItems(false, 0, 0, 0, 0);
   }

   public void sprayItems(boolean meso, int mesoChance, int minMeso, int maxMeso) {
      sprayItems(meso, mesoChance, minMeso, maxMeso, 0);
   }

   public void sprayItems(boolean meso, int mesoChance, int minMeso, int maxMeso, int minItems) {
      sprayItems((int) reactor.position().getX(), (int) reactor.position().getY(), meso, mesoChance, minMeso, maxMeso, minItems);
   }

   public void sprayItems(int posX, int posY, boolean meso, int mesoChance, int minMeso, int maxMeso, int minItems) {
      dropItems(true, posX, posY, meso, mesoChance, minMeso, maxMeso, minItems);
   }

   public void dropItems() {
      dropItems(false, 0, 0, 0, 0);
   }

   public void dropItems(boolean meso, int mesoChance, int minMeso, int maxMeso) {
      dropItems(meso, mesoChance, minMeso, maxMeso, 0);
   }

   public void dropItems(boolean meso, int mesoChance, int minMeso, int maxMeso, int minItems) {
      dropItems((int) reactor.position().getX(), (int) reactor.position().getY(), meso, mesoChance, minMeso, maxMeso, minItems);
   }

   public void dropItems(int posX, int posY, boolean meso, int mesoChance, int minMeso, int maxMeso, int minItems) {
      dropItems(true, posX, posY, meso, mesoChance, minMeso, maxMeso, minItems);
   }

   public void dropItems(boolean delayed, int posX, int posY, boolean meso, int mesoChance, final int minMeso, final int maxMeso,
                         int minItems) {
      MapleCharacter chr = c.getPlayer();
      if (chr == null) {
         return;
      }

      List<ReactorDropEntry> items =
            assembleReactorDropEntries(chr, generateDropList(getDropChances(), chr.getDropRate(), meso, mesoChance, minItems));

      if (items.size() % 2 == 0) {
         posX -= 12;
      }
      final Point dropPos = new Point(posX, posY);

      if (!delayed) {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

         byte p = 1;
         for (ReactorDropEntry d : items) {
            dropPos.x = posX + ((p % 2 == 0) ? (25 * ((p + 1) / 2)) : -(25 * (p / 2)));
            p++;

            if (d.itemId() == 0) {
               int range = maxMeso - minMeso;
               int displayDrop = (int) (Math.random() * range) + minMeso;
               int mesoDrop = (displayDrop * c.getWorldServer().getMesoRate());
               reactor.getMap()
                     .spawnMesoDrop(mesoDrop, reactor.getMap().calcDropPos(dropPos, reactor.position()), reactor, c.getPlayer(),
                           false, (byte) 2);
            } else {
               Item drop;

               if (ItemConstants.getInventoryType(d.itemId()) != MapleInventoryType.EQUIP) {
                  drop = new Item(d.itemId(), (short) 0, (short) 1);
               } else {
                  drop = Equip.newBuilder(ii.getEquipById(d.itemId())).randomizeStats().build();
               }

               reactor.getMap().dropFromReactor(getPlayer(), reactor, drop, dropPos, (short) d.questId());
            }
         }
      } else {
         final MapleReactor r = reactor;
         final List<ReactorDropEntry> dropItems = items;
         final int worldMesoRate = c.getWorldServer().getMesoRate();

         dropPos.x -= (12 * items.size());

         sprayTask = TimerManager.getInstance().register(() -> {
            if (dropItems.isEmpty()) {
               sprayTask.cancel(false);
               return;
            }

            ReactorDropEntry d = dropItems.remove(0);
            if (d.itemId() == 0) {
               int range = maxMeso - minMeso;
               int displayDrop = (int) (Math.random() * range) + minMeso;
               int mesoDrop = (displayDrop * worldMesoRate);
               r.getMap().spawnMesoDrop(mesoDrop, r.getMap().calcDropPos(dropPos, r.position()), r, chr, false, (byte) 2);
            } else {
               Item drop;

               if (ItemConstants.getInventoryType(d.itemId()) != MapleInventoryType.EQUIP) {
                  drop = new Item(d.itemId(), (short) 0, (short) 1);
               } else {
                  MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                  drop = Equip.newBuilder(ii.getEquipById(d.itemId())).randomizeStats().build();
               }

               r.getMap().dropFromReactor(getPlayer(), r, drop, dropPos, (short) d.questId());
            }

            dropPos.x += 25;
         }, 200);
      }
   }

   private List<ReactorDropEntry> getDropChances() {
      return ReactorScriptManager.getInstance().getDrops(reactor.getId());
   }

   private List<ReactorDropEntry> generateDropList(List<ReactorDropEntry> drops, int dropRate, boolean meso, int mesoChance,
                                                   int minItems) {
      List<ReactorDropEntry> items = new ArrayList<>();
      if (meso && Math.random() < (1 / (double) mesoChance)) {
         items.add(new ReactorDropEntry(0, mesoChance, -1));
      }

      for (ReactorDropEntry mde : drops) {
         if (Math.random() < (dropRate / (double) mde.chance())) {
            items.add(mde);
         }
      }

      while (items.size() < minItems) {
         items.add(new ReactorDropEntry(0, mesoChance, -1));
      }

      return items;
   }

   public void spawnMonster(int id) {
      spawnMonster(id, 1, getPosition());
   }

   public void createMapMonitor(int mapId, String portal) {
      new MapMonitor(c.getChannelServer().getMapFactory().getMap(mapId), portal);
   }

   public void spawnMonster(int id, int qty) {
      spawnMonster(id, qty, getPosition());
   }

   public void spawnMonster(int id, int qty, int x, int y) {
      spawnMonster(id, qty, new Point(x, y));
   }

   public void spawnMonster(int id, int qty, Point pos) {
      IntStream.range(0, qty)
            .mapToObj(index -> MapleLifeFactory.getMonster(id))
            .flatMap(Optional::stream)
            .filter(Objects::nonNull)
            .forEach(monster -> reactor.getMap().spawnMonsterOnGroundBelow(monster, pos));
   }

   public Point getPosition() {
      Point pos = reactor.position();
      pos.y -= 10;
      return pos;
   }

   public void spawnNpc(int npcId) {
      spawnNpc(npcId, getPosition());
   }

   public void spawnNpc(int npcId, Point pos) {
      MapleNPCFactory.spawnNpc(npcId, pos, reactor.getMap());
   }

   public void hitMonsterWithReactor(int id, int hitsToKill) {  // until someone comes with a better solution, why not?
      int customTime = YamlConfig.config.server.MOB_REACTOR_REFRESH_TIME;
      if (customTime > 0) {
         reactor.setDelay(customTime);
      }

      MapleMap map = reactor.getMap();
      MapleMonster mm = map.getMonsterById(id);
      if (mm != null) {
         int damage = (int) Math.ceil(mm.getMaxHp() / hitsToKill);
         MapleCharacter chr = this.getPlayer();

         if (chr != null) {
            map.damageMonster(chr, mm, damage);
            MasterBroadcaster.getInstance().sendToAllInMap(map, new DamageMonster(mm.objectId(), damage));
         }
      }
   }

   public MapleReactor getReactor() {
      return reactor;
   }

   public void spawnFakeMonster(int id) {
      MapleLifeFactory.getMonster(id).ifPresent(monster -> reactor.getMap().spawnMonsterOnGroundBelow(monster, getPosition()));
   }

   public ScheduledFuture<?> schedule(String methodName, long delay) {
      return schedule(methodName, null, delay);
   }

   public ScheduledFuture<?> schedule(final String methodName, final EventInstanceManager eim, long delay) {
      return TimerManager.getInstance().schedule(() -> {
         try {
            ((Invocable) iv).invokeFunction(methodName, eim);
         } catch (ScriptException | NoSuchMethodException ex) {
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.REACTOR, ex);
         }
      }, delay);
   }

   public ScheduledFuture<?> scheduleAtTimestamp(final String methodName, long timestamp) {
      return TimerManager.getInstance().scheduleAtTimestamp(() -> {
         try {
            ((Invocable) iv).invokeFunction(methodName, (Object) null);
         } catch (ScriptException | NoSuchMethodException ex) {
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.REACTOR, ex);
         }
      }, timestamp);
   }

   public void dispelAllMonsters(int num, int team) { //dispels all mobs, cpq
      final MCSkill skill = MapleCarnivalFactory.getInstance().getGuardian(num);
      if (skill != null) {
         for (MapleMonster mons : getMap().getAllMonsters()) {
            if (mons.getTeam() == team) {
               mons.dispelSkill(skill.getSkill());
            }
         }
      }
      if (team == 0) {
         getPlayer().getMap().getRedTeamBuffs().remove(skill);
      } else {
         getPlayer().getMap().getBlueTeamBuffs().remove(skill);
      }
   }
}