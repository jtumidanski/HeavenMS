package net.server.channel.handlers;

import client.MapleClient;
import client.processor.npc.DueyProcessor;
import config.YamlConfig;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.NPCTalkPacket;
import net.server.channel.packet.reader.NPCTalkReader;
import scripting.npc.NPCScriptManager;
import server.life.MapleNPC;
import server.life.MaplePlayerNPC;
import server.maps.MapleMapObject;
import tools.FilePrinter;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.stat.EnableActions;

public final class NPCTalkHandler extends AbstractPacketHandler<NPCTalkPacket> {
   @Override
   public boolean successfulProcess(MapleClient client) {
      if (!client.getPlayer().isAlive()) {
         PacketCreator.announce(client, new EnableActions());
         return false;
      }

      if (currentServerTime() - client.getPlayer().getNpcCoolDown() < YamlConfig.config.server.BLOCK_NPC_RACE_CONDT) {
         PacketCreator.announce(client, new EnableActions());
         return false;
      }
      return true;
   }

   @Override
   public Class<NPCTalkReader> getReaderClass() {
      return NPCTalkReader.class;
   }

   @Override
   public void handlePacket(NPCTalkPacket packet, MapleClient client) {
      MapleMapObject obj = client.getPlayer().getMap().getMapObject(packet.objectId());
      if (obj instanceof MapleNPC) {
         MapleNPC npc = (MapleNPC) obj;
         if (YamlConfig.config.server.USE_DEBUG) {
            MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.PINK_TEXT, "Talking to NPC " + npc.id());
         }

         if (npc.id() == 9010009) {   //is duey
            DueyProcessor.dueySendTalk(client, false);
         } else {
            if (client.getCM() != null || client.getQM() != null) {
               PacketCreator.announce(client, new EnableActions());
               return;
            }
            if (npc.id() >= 9100100 && npc.id() <= 9100200) {
               // Custom handling for gachapon scripts to reduce the amount of scripts needed.
               NPCScriptManager.getInstance().start(client, npc.id(), "gachapon", null);
            } else if (npc.getName().endsWith("Maple TV")) {
               NPCScriptManager.getInstance().start(client, npc.id(), "mapleTV", null);
            } else {
               boolean hasNpcScript = NPCScriptManager.getInstance().start(client, npc.id(), packet.objectId(), null);
               if (!hasNpcScript) {
                  if (!npc.hasShop()) {
                     FilePrinter.printError(FilePrinter.NPC_UNCODED, "NPC " + npc.getName() + "(" + npc.id() + ") is not coded.");
                     return;
                  } else if (client.getPlayer().getShop() != null) {
                     PacketCreator.announce(client, new EnableActions());
                     return;
                  }

                  npc.sendShop(client);
               }
            }
         }
      } else if (obj instanceof MaplePlayerNPC) {
         MaplePlayerNPC playerNPC = (MaplePlayerNPC) obj;
         NPCScriptManager nsm = NPCScriptManager.getInstance();

         if (playerNPC.getScriptId() < 9977777 && !nsm.isNpcScriptAvailable(client, "" + playerNPC.getScriptId())) {
            nsm.start(client, playerNPC.getScriptId(), "rank_user", null);
         } else {
            nsm.start(client, playerNPC.getScriptId(), null);
         }
      }
   }
}