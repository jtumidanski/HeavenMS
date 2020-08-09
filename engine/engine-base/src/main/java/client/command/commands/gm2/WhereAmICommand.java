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
import tools.I18nMessage;

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
         if (mmo instanceof MapleNPC npc) {
            npcSet.add(npc);
         } else if (mmo instanceof MapleCharacter mc) {
            chars.add(mc);
         } else if (mmo instanceof MapleMonster mob) {
            if (mob.isAlive()) {
               mobs.add(mob);
            }
         } else if (mmo instanceof MaplePlayerNPC npc) {
            playerNpcSet.add(npc);
         }
      }

      MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("WHERE_AM_I_COMMAND_MAP").with(player.getMap().getId()));
      MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("WHERE_AM_I_COMMAND_PLAYER_TITLE"));
      chars.forEach(character -> MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("WHERE_AM_I_COMMAND_PLAYER_BODY").with(character.getName(), character.getId(), character.objectId())));

      if (!playerNpcSet.isEmpty()) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("WHERE_AM_I_COMMAND_PLAYER_NPC_TITLE"));
         playerNpcSet.forEach(playerNpc -> MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("WHERE_AM_I_COMMAND_PLAYER_NPC_BODY").with(playerNpc.getName(), playerNpc.getScriptId(), playerNpc.objectId())));
      }

      if (!npcSet.isEmpty()) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("WHERE_AM_I_COMMAND_NPC_TITLE"));
         npcSet.forEach(npc -> MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("WHERE_AM_I_COMMAND_NPC_BODY").with(npc.getName(), npc.id(), npc.objectId())));
      }

      if (!mobs.isEmpty()) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("WHERE_AM_I_COMMAND_MONSTER_TITLE"));
         mobs.stream()
               .filter(MapleMonster::isAlive)
               .forEach(mob -> MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("WHERE_AM_I_COMMAND_MONSTER_BODY").with(mob.getName(), mob.id(), mob.objectId())));
      }
   }
}
