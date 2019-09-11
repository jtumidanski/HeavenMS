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

import client.MapleClient;
import net.opcodes.SendOpcode;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.npc.BaseNPCAnimationPacket;
import net.server.channel.packet.npc.NPCMovePacket;
import net.server.channel.packet.npc.NPCTalkPacket;
import net.server.channel.packet.reader.NPCAnimationReader;
import tools.data.output.MaplePacketLittleEndianWriter;

public final class NPCAnimationHandler extends AbstractPacketHandler<BaseNPCAnimationPacket, NPCAnimationReader> {
   @Override
   public Class<NPCAnimationReader> getReaderClass() {
      return NPCAnimationReader.class;
   }

   @Override
   public void handlePacket(BaseNPCAnimationPacket packet, MapleClient client) {
      if (client.getPlayer().isChangingMaps()) {
         return;
      }

      MaplePacketLittleEndianWriter writer = new MaplePacketLittleEndianWriter();
      if (packet instanceof NPCTalkPacket) {
         writer.writeShort(SendOpcode.NPC_ACTION.getValue());
         writer.writeInt(((NPCTalkPacket) packet).first());
         writer.writeShort(((NPCTalkPacket) packet).second());
      } else if (packet instanceof NPCMovePacket) {
         writer.writeShort(SendOpcode.NPC_ACTION.getValue());
         writer.write(((NPCMovePacket) packet).movement());
      }
      client.announce(writer.getPacket());
   }
}
