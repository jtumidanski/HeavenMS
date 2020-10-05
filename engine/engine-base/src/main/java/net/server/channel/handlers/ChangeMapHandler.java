package net.server.channel.handlers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.ChangeMapPacket;
import net.server.channel.packet.reader.ChangeMapReader;
import server.MapleTradeResult;
import server.maps.MapleMap;
import server.maps.MaplePortal;
import server.processor.MapleTradeProcessor;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.PacketCreator;
import tools.packet.ChangeChannel;
import tools.packet.foreigneffect.ShowBlockedMessage;
import tools.packet.showitemgaininchat.ShowWheelsLeft;
import tools.packet.stat.EnableActions;
import tools.packet.ui.DisableUI;
import tools.packet.ui.LockUI;

public final class ChangeMapHandler extends AbstractPacketHandler<ChangeMapPacket> {
   @Override
   public Class<ChangeMapReader> getReaderClass() {
      return ChangeMapReader.class;
   }

   @Override
   public void handlePacket(ChangeMapPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();

      if (chr.isChangingMaps() || chr.isBanned()) {
         if (chr.isChangingMaps()) {
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.PORTAL_STUCK, "Player " + chr.getName() + " got stuck when changing maps. Timestamp: " + Calendar.getInstance().getTime().toString() + " Last visited map ids: " + chr.getLastVisitedMapIds());
         }

         PacketCreator.announce(client, new EnableActions());
         return;
      }

      chr.getTrade().ifPresent(trade -> MapleTradeProcessor.getInstance().cancelTrade(chr, MapleTradeResult.UNSUCCESSFUL_ANOTHER_MAP));

      if (packet.cashShop()) { //Cash Shop :)
         if (!chr.getCashShop().isOpened()) {
            client.disconnect(false, false);
            return;
         }
         String[] socket = client.getChannelServer().getIP().split(":");
         chr.getCashShop().open(false);
         chr.setSessionTransitionState();
         try {
            PacketCreator.announce(client, new ChangeChannel(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1])));
         } catch (UnknownHostException ex) {
            ex.printStackTrace();
         }
      } else {
         if (chr.getCashShop().isOpened()) {
            client.disconnect(false, false);
            return;
         }
         try {
            MaplePortal portal = chr.getMap().getPortal(packet.startWarp());

            if (packet.targetId() != -1) {
               if (!chr.isAlive()) {
                  MapleMap map = chr.getMap();
                  if (packet.wheel() && chr.haveItemWithId(5510000, false)) {
                     MapleInventoryManipulator.removeById(client, MapleInventoryType.CASH, 5510000, 1, true, false);
                     PacketCreator.announce(chr, new ShowWheelsLeft(chr.getItemQuantity(5510000, false)));

                     chr.updateHp(50);
                     chr.changeMap(map, map.findClosestPlayerSpawnPoint(chr.position()));
                  } else {
                     boolean executeStandardPath = true;
                     if (chr.getEventInstance() != null) {
                        executeStandardPath = chr.getEventInstance().revivePlayer(chr);
                     }
                     if (executeStandardPath) {
                        chr.respawn(map.getReturnMapId());
                     }
                  }
               } else {
                  if (chr.isGM()) {
                     MapleMap to = chr.getWarpMap(packet.targetId());
                     chr.changeMap(to, to.getPortal(0));
                  } else {
                     final int divi = chr.getMapId() / 100;
                     boolean warp = false;
                     if (divi == 0) {
                        if (packet.targetId() == 10000) {
                           warp = true;
                        }
                     } else if (divi == 20100) {
                        if (packet.targetId() == 104000000) {
                           PacketCreator.announce(client, new LockUI(false));
                           PacketCreator.announce(client, new DisableUI(false));
                           warp = true;
                        }
                     } else if (divi == 9130401) { // Only allow warp if player is already in Intro map, or else = hack
                        if (packet.targetId() == 130000000 || packet.targetId() / 100 == 9130401) { // Cygnus introduction
                           warp = true;
                        }
                     } else if (divi == 9140900) { // Aran Introduction
                        if (packet.targetId() == 914090011 || packet.targetId() == 914090012 || packet.targetId() == 914090013 || packet.targetId() == 140090000) {
                           warp = true;
                        }
                     } else if (divi / 10 == 1020) { // Adventurer movie clip Intro
                        if (packet.targetId() == 1020000) {
                           warp = true;
                        }
                     } else if (divi / 10 >= 980040 && divi / 10 <= 980045) {
                        if (packet.targetId() == 980040000) {
                           warp = true;
                        }
                     }
                     if (warp) {
                        final MapleMap to = chr.getWarpMap(packet.targetId());
                        chr.changeMap(to, to.getPortal(0));
                     }
                  }
               }
            }

            if (portal != null && !portal.getPortalStatus()) {
               PacketCreator.announce(client, new ShowBlockedMessage(1));
               PacketCreator.announce(client, new EnableActions());
               return;
            }

            if (chr.getMapId() == 109040004) {
               chr.getFitness().resetTimes();
            } else if (chr.getMapId() == 109030003 || chr.getMapId() == 109030103) {
               chr.getOla().resetTimes();
            }

            if (portal != null) {
               if (portal.getPosition().distanceSq(chr.position()) > 400000) {
                  PacketCreator.announce(client, new EnableActions());
                  return;
               }

               portal.enterPortal(client);
            } else {
               PacketCreator.announce(client, new EnableActions());
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }
}