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
import server.MapleTrade;
import server.maps.MapleMap;
import server.maps.MaplePortal;
import tools.FilePrinter;
import tools.MaplePacketCreator;

public final class ChangeMapHandler extends AbstractPacketHandler<ChangeMapPacket, ChangeMapReader> {
   @Override
   public Class<ChangeMapReader> getReaderClass() {
      return ChangeMapReader.class;
   }

   @Override
   public void handlePacket(ChangeMapPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();

      if (chr.isChangingMaps() || chr.isBanned()) {
         if (chr.isChangingMaps()) {
            FilePrinter.printError(FilePrinter.PORTAL_STUCK + chr.getName() + ".txt", "Player " + chr.getName() + " got stuck when changing maps. Timestamp: " + Calendar.getInstance().getTime().toString() + " Last visited mapids: " + chr.getLastVisitedMapids());
         }

         client.announce(MaplePacketCreator.enableActions());
         return;
      }
      if (chr.getTrade() != null) {
         MapleTrade.cancelTrade(chr, MapleTrade.TradeResult.UNSUCCESSFUL_ANOTHER_MAP);
      }
      if (packet.cashShop()) { //Cash Shop :)
         if (!chr.getCashShop().isOpened()) {
            client.disconnect(false, false);
            return;
         }
         String[] socket = client.getChannelServer().getIP().split(":");
         chr.getCashShop().open(false);

         client.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION);
         chr.setSessionTransitionState();
         try {
            client.announce(MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1])));
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
                     // thanks lucasziron for showing revivePlayer() also being triggered by Wheel

                     MapleInventoryManipulator.removeById(client, MapleInventoryType.CASH, 5510000, 1, true, false);
                     chr.announce(MaplePacketCreator.showWheelsLeft(chr.getItemQuantity(5510000, false)));

                     chr.updateHp(50);
                     chr.changeMap(map, map.findClosestPlayerSpawnpoint(chr.getPosition()));
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
                           client.announce(MaplePacketCreator.lockUI(false));
                           client.announce(MaplePacketCreator.disableUI(false));
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
               client.announce(MaplePacketCreator.blockedMessage(1));
               client.announce(MaplePacketCreator.enableActions());
               return;
            }

            if (chr.getMapId() == 109040004) {
               chr.getFitness().resetTimes();
            } else if (chr.getMapId() == 109030003 || chr.getMapId() == 109030103) {
               chr.getOla().resetTimes();
            }

            if (portal != null) {
               if (portal.getPosition().distanceSq(chr.getPosition()) > 400000) {
                  client.announce(MaplePacketCreator.enableActions());
                  return;
               }

               portal.enterPortal(client);
            } else {
               client.announce(MaplePacketCreator.enableActions());
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }
}