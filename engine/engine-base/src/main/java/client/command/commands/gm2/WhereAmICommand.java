package client.command.commands.gm2;

import java.util.HashSet;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.life.MapleMonster;
import server.life.MapleNPC;
import server.life.MaplePlayerNPC;
import server.maps.MapleMapObject;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class WhereAmICommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();

      HashSet<MapleCharacter> chars = new HashSet<>();
      HashSet<MapleNPC> npcSet = new HashSet<>();
      HashSet<MaplePlayerNPC> playerNpcSet = new HashSet<>();
      HashSet<MapleMonster> mobs = new HashSet<>();

      for (MapleMapObject mmo : player.getMap().getMapObjects()) {
         if (mmo instanceof MapleNPC) {
            MapleNPC npc = (MapleNPC) mmo;
            npcSet.add(npc);
         } else if (mmo instanceof MapleCharacter) {
            MapleCharacter mc = (MapleCharacter) mmo;
            chars.add(mc);
         } else if (mmo instanceof MapleMonster) {
            MapleMonster mob = (MapleMonster) mmo;
            if (mob.isAlive()) {
               mobs.add(mob);
            }
         } else if (mmo instanceof MaplePlayerNPC) {
            MaplePlayerNPC npc = (MaplePlayerNPC) mmo;
            playerNpcSet.add(npc);
         }
      }

      player.yellowMessage("Map ID: " + player.getMap().getId());

      player.yellowMessage("Players on this map:");
      for (MapleCharacter chr : chars) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, ">> " + chr.getName() + " - " + chr.getId() + " - Oid: " + chr.objectId());
      }

      if (!playerNpcSet.isEmpty()) {
         player.yellowMessage("PlayerNPCs on this map:");
         for (MaplePlayerNPC playerNpc : playerNpcSet) {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, ">> " + playerNpc.getName() + " - ScriptId: " + playerNpc.getScriptId() + " - Oid: " + playerNpc.objectId());
         }
      }

      if (!npcSet.isEmpty()) {
         player.yellowMessage("NPCs on this map:");
         for (MapleNPC npc : npcSet) {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, ">> " + npc.getName() + " - " + npc.id() + " - Oid: " + npc.objectId());
         }
      }

      if (!mobs.isEmpty()) {
         player.yellowMessage("Monsters on this map:");
         for (MapleMonster mob : mobs) {
            if (mob.isAlive()) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, ">> " + mob.getName() + " - " + mob.id() + " - Oid: " + mob.objectId());
            }
         }
      }
   }
}
