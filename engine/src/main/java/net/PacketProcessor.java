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
package net;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.opcodes.RecvOpcode;
import net.server.HandlerFactory;

public final class PacketProcessor {

   private final static Map<String, PacketProcessor> instances = new LinkedHashMap<>();
   private Map<Integer, MaplePacketHandler> handlers;

   private PacketProcessor() {
      handlers = new HashMap<>();
   }

   public synchronized static PacketProcessor getProcessor(int world, int channel) {
      final String lolpair = world + " " + channel;
      PacketProcessor processor = instances.get(lolpair);
      if (processor == null) {
         processor = new PacketProcessor();
         HandlerFactory.getInstance().registerHandlers(processor, channel);
         instances.put(lolpair, processor);
      }
      return processor;
   }

   public MaplePacketHandler getHandler(short packetId) {
      return handlers.get(Integer.valueOf(packetId));
   }

   public void registerHandler(RecvOpcode code, MaplePacketHandler handler) {
      try {
         handlers.put(code.getValue(), handler);
      } catch (ArrayIndexOutOfBoundsException e) {
         e.printStackTrace();
         System.out.println("Error registering handler - " + code.name());
      }
   }

   public void reset(int channel) {
      handlers = new HashMap<>();
   }
}