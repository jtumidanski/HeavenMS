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
package net.server.handlers.login;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import client.MapleClient;
import client.creator.novice.BeginnerCreator;
import client.creator.novice.LegendCreator;
import client.creator.novice.NoblesseCreator;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.reader.CreateCharacterReader;
import net.server.login.packet.CreateCharacterPacket;
import tools.FilePrinter;
import tools.MaplePacketCreator;

public final class CreateCharHandler extends AbstractPacketHandler<CreateCharacterPacket, CreateCharacterReader> {
   @Override
   public Class<CreateCharacterReader> getReaderClass() {
      return CreateCharacterReader.class;
   }

   @Override
   public void handlePacket(CreateCharacterPacket packet, MapleClient client) {
      int[] items = new int[]{packet.weapon(), packet.top(), packet.bottom(), packet.shoes(), packet.hair(), packet.face()};
      for (int item : items) {
         if (!isLegal(item)) {
            FilePrinter.printError(FilePrinter.EXPLOITS + packet.name() + ".txt", "Owner from account '" + client.getAccountName() + "' tried to packet edit in char creation.");
            client.disconnect(true, false);
            return;
         }
      }

      int status;
      if (packet.job() == 0) { // Knights of Cygnus
         status = NoblesseCreator.createCharacter(client, packet.name(), packet.face(), packet.hair() + packet.hairColor(), packet.skinColor(), packet.top(), packet.bottom(), packet.shoes(), packet.weapon(), packet.gender());
      } else if (packet.job() == 1) { // Adventurer
         status = BeginnerCreator.createCharacter(client, packet.name(), packet.face(), packet.hair() + packet.hairColor(), packet.skinColor(), packet.top(), packet.bottom(), packet.shoes(), packet.weapon(), packet.gender());
      } else if (packet.job() == 2) { // Aran
         status = LegendCreator.createCharacter(client, packet.name(), packet.face(), packet.hair() + packet.hairColor(), packet.skinColor(), packet.top(), packet.bottom(), packet.shoes(), packet.weapon(), packet.gender());
      } else {
         client.announce(MaplePacketCreator.deleteCharResponse(0, 9));
         return;
      }

      if (status == -2) {
         client.announce(MaplePacketCreator.deleteCharResponse(0, 9));
      }
   }

   private final Set<Integer> IDs = new HashSet<>(Arrays.asList(1302000, 1312004, 1322005, 1442079,// weapons
         1040002, 1040006, 1040010, 1041002, 1041006, 1041010, 1041011, 1042167,// bottom
         1060002, 1060006, 1061002, 1061008, 1062115, // top
         1072001, 1072005, 1072037, 1072038, 1072383,// shoes
         30000, 30010, 30020, 30030, 31000, 31040, 31050,// hair
         20000, 20001, 20002, 21000, 21001, 21002, 21201, 20401, 20402, 21700, 20100  //face
         //#NeverTrustStevenCode
   ));

   private boolean isLegal(Integer toCompare) {
      return IDs.contains(toCompare);
   }
}