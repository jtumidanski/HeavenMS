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

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.server.world.MapleParty;
import net.server.world.World;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.data.input.SeekableLittleEndianAccessor;

/**
 * @author XoticStory
 * @author BubblesDev
 * @author Ronan
 */
public class PartySearchStartHandler extends AbstractMaplePacketHandler {
   @Override
   public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
      int min = slea.readInt();
      int max = slea.readInt();

      MapleCharacter chr = c.getPlayer();
      if (min > max) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "The min. value is higher than the max!");
         c.announce(MaplePacketCreator.enableActions());
         return;
      }

      if (max - min > 30) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You can only search for party members within a range of 30 levels.");
         c.announce(MaplePacketCreator.enableActions());
         return;
      }

      if (chr.getLevel() < min || chr.getLevel() > max) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "The range of level for search has to include your own level.");
         c.announce(MaplePacketCreator.enableActions());
         return;
      }

      slea.readInt(); // members
      int jobs = slea.readInt();

      MapleParty party = c.getPlayer().getParty();
      if (party == null || !c.getPlayer().isPartyLeader()) return;

      World world = c.getWorldServer();
      world.getPartySearchCoordinator().registerPartyLeader(chr, min, max, jobs);
   }
}