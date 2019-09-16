/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2018 RonanLana

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
import client.processor.CharacterProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.TransferNameResultPacket;
import net.server.channel.packet.reader.TransferNameResultReader;
import tools.MaplePacketCreator;

/**
 * @author Ronan
 */
public final class TransferNameResultHandler extends AbstractPacketHandler<TransferNameResultPacket> {
   @Override
   public Class<TransferNameResultReader> getReaderClass() {
      return TransferNameResultReader.class;
   }

   @Override
   public void handlePacket(TransferNameResultPacket packet, MapleClient client) {
      client.announce(MaplePacketCreator.sendNameTransferCheck(packet.name(), CharacterProcessor.getInstance().canCreateChar(packet.name())));
   }
}