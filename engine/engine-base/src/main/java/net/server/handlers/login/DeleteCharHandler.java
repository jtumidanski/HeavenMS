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

import java.util.Optional;

import client.MapleClient;
import client.processor.CharacterProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.reader.DeleteCharacterReader;
import net.server.login.packet.DeleteCharacterPacket;
import tools.FilePrinter;
import tools.PacketCreator;
import tools.packet.DeleteCharacter;
import tools.packet.DeleteCharacterResponse;

public final class DeleteCharHandler extends AbstractPacketHandler<DeleteCharacterPacket> {
   @Override
   public Class<DeleteCharacterReader> getReaderClass() {
      return DeleteCharacterReader.class;
   }

   @Override
   public void handlePacket(DeleteCharacterPacket packet, MapleClient client) {
      if (client.checkPic(packet.pic())) {
         //check for family, guild leader, pending marriage, world transfer
         Optional<Byte> state = CharacterProcessor.getInstance().canDeleteCharacter(packet.characterId());
         if (state.isPresent()) {
            PacketCreator.announce(client, new DeleteCharacter(packet.characterId(), DeleteCharacterResponse.fromValue(state.get())));
            return;
         }

         if (client.deleteCharacter(packet.characterId(), client.getAccID())) {
            FilePrinter.print(FilePrinter.DELETED_CHAR + client.getAccountName() + ".txt", client.getAccountName() + " deleted CID: " + packet.characterId());
            PacketCreator.announce(client, new DeleteCharacter(packet.characterId(), DeleteCharacterResponse.SUCCESS));
         } else {
            PacketCreator.announce(client, new DeleteCharacter(packet.characterId(), DeleteCharacterResponse.UNKNOWN_ERROR));
         }
      } else {
         PacketCreator.announce(client, new DeleteCharacter(packet.characterId(), DeleteCharacterResponse.INCORRECT_PIC));
      }
   }
}
