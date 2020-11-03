package net.server.channel.handlers;

import java.util.Optional;

import com.ms.qos.rest.CharacterQuestManagerAttributes;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.NPCMoreTalkPacket;
import net.server.channel.packet.reader.NPCMoreTalkReader;
import rest.DataContainer;
import scripting.npc.NPCScriptManager;
import server.processor.QuestProcessor;
import tools.SimpleMessage;

public final class NPCMoreTalkHandler extends AbstractPacketHandler<NPCMoreTalkPacket> {
   @Override
   public Class<NPCMoreTalkReader> getReaderClass() {
      return NPCMoreTalkReader.class;
   }

   @Override
   public void handlePacket(NPCMoreTalkPacket packet, MapleClient client) {
      Optional<DataContainer<CharacterQuestManagerAttributes>> qm =
            QuestProcessor.getInstance().getQuestManagerInfo(client.getPlayer().getId());
      if (packet.lastMessageType() == 2) {
         if (packet.action() != 0) {
            if (qm.isPresent()) {
               //TODO reintegrate? client.getQM().setGetText(SimpleMessage.from(packet.returnText()));
               if (qm.get().getData().getAttributes().isStart()) {
                  QuestProcessor.getInstance().startScript(client.getPlayer().getId(), packet.action(), packet.lastMessageType(), -1);
               } else {
                  QuestProcessor.getInstance().completeScript(client.getPlayer().getId(), packet.action(), packet.lastMessageType(), -1);
               }
            } else {
               client.getCM().setGetText(SimpleMessage.from(packet.returnText()));
               NPCScriptManager.getInstance().action(client, packet.action(), packet.lastMessageType(), -1);
            }
         } else if (qm.isPresent()) {
            QuestProcessor.getInstance().disposeScript(client.getPlayer().getId());
         } else {
            client.getCM().dispose();
         }
      } else {
         if (qm.isPresent()) {
            if (qm.get().getData().getAttributes().isStart()) {
               QuestProcessor.getInstance()
                     .startScript(client.getPlayer().getId(), packet.action(), packet.lastMessageType(), packet.selection());
            } else {
               QuestProcessor.getInstance()
                     .completeScript(client.getPlayer().getId(), packet.action(), packet.lastMessageType(), packet.selection());
            }
         } else if (client.getCM() != null) {
            NPCScriptManager.getInstance().action(client, packet.action(), packet.lastMessageType(), packet.selection());
         }
      }
   }
}