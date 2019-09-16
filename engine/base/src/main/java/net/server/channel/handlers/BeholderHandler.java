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

import java.util.Collection;

import client.MapleClient;
import constants.skills.DarkKnight;
import net.AbstractMaplePacketHandler;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.BeholderPacket;
import net.server.channel.packet.reader.BeholderReader;
import server.maps.MapleSummon;
import tools.data.input.SeekableLittleEndianAccessor;

/**
 * @author BubblesDev
 */
//TODO is anything actually happening here?
public final class BeholderHandler extends AbstractPacketHandler<BeholderPacket> {
   @Override
   public Class<BeholderReader> getReaderClass() {
      return BeholderReader.class;
   }

   @Override
   public void handlePacket(BeholderPacket packet, MapleClient client) {
      //System.out.println(slea.toString());
      Collection<MapleSummon> summons = client.getPlayer().getSummonsValues();
      MapleSummon summon = null;
      for (MapleSummon sum : summons) {
         if (sum.getObjectId() == packet.objectId()) {
            summon = sum;
         }
      }
      if (summon != null) {
      } else {
         client.getPlayer().clearSummons();
      }
   }
}
