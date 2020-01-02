package net.server.channel.handlers;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.NPCMoreTalkPacket;
import net.server.channel.packet.reader.NPCMoreTalkReader;
import scripting.npc.NPCScriptManager;
import scripting.quest.QuestScriptManager;

public final class NPCMoreTalkHandler extends AbstractPacketHandler<NPCMoreTalkPacket> {
   @Override
   public Class<NPCMoreTalkReader> getReaderClass() {
      return NPCMoreTalkReader.class;
   }

   @Override
   public void handlePacket(NPCMoreTalkPacket packet, MapleClient client) {
      if (packet.lastMessageType() == 2) {
         if (packet.action() != 0) {
            if (client.getQM() != null) {
               client.getQM().setGetText(packet.returnText());
               if (client.getQM().isStart()) {
                  QuestScriptManager.getInstance().start(client, packet.action(), packet.lastMessageType(), -1);
               } else {
                  QuestScriptManager.getInstance().end(client, packet.action(), packet.lastMessageType(), -1);
               }
            } else {
               client.getCM().setGetText(packet.returnText());
               NPCScriptManager.getInstance().action(client, packet.action(), packet.lastMessageType(), -1);
            }
         } else if (client.getQM() != null) {
            client.getQM().dispose();
         } else {
            client.getCM().dispose();
         }
      } else {
         if (client.getQM() != null) {
            if (client.getQM().isStart()) {
               QuestScriptManager.getInstance().start(client, packet.action(), packet.lastMessageType(), packet.selection());
            } else {
               QuestScriptManager.getInstance().end(client, packet.action(), packet.lastMessageType(), packet.selection());
            }
         } else if (client.getCM() != null) {
            NPCScriptManager.getInstance().action(client, packet.action(), packet.lastMessageType(), packet.selection());
         }
      }
   }
}