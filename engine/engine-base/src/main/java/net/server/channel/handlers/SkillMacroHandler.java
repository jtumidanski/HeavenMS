package net.server.channel.handlers;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.SkillMacroPacket;
import net.server.channel.packet.reader.SkillMacroReader;

public final class SkillMacroHandler extends AbstractPacketHandler<SkillMacroPacket> {
   @Override
   public Class<SkillMacroReader> getReaderClass() {
      return SkillMacroReader.class;
   }

   @Override
   public void handlePacket(SkillMacroPacket packet, MapleClient client) {
      for (int i = 0; i < packet.macros().length; i++) {
         client.getPlayer().updateMacros(i, packet.macros()[i]);
      }
   }
}
