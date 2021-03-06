package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.life.MapleLifeFactory;
import server.life.MapleNPC;
import tools.I18nMessage;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.packet.spawn.SpawnNPC;

public class NpcCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("NPC_COMMAND_SYNTAX"));
         return;
      }
      MapleNPC npc = MapleLifeFactory.getNPC(Integer.parseInt(params[0]));
      if (npc != null) {
         npc.setPosition(player.position());
         npc.setCy(player.position().y);
         npc.setRx0(player.position().x + 50);
         npc.setRx1(player.position().x - 50);
         npc.setFh(player.getMap().getFootholds().findBelow(c.getPlayer().position()).id());
         player.getMap().addMapObject(npc);
         MasterBroadcaster.getInstance().sendToAllInMap(player.getMap(), new SpawnNPC(npc));
      }
   }
}
