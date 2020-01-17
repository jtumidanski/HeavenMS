package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.life.MapleLifeFactory;
import server.life.MapleNPC;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.I18nMessage;
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
         npc.position_$eq(player.position());
         npc.cy_$eq(player.position().y);
         npc.rx0_$eq(player.position().x + 50);
         npc.rx1_$eq(player.position().x - 50);
         npc.fh_$eq(player.getMap().getFootholds().findBelow(c.getPlayer().position()).id());
         player.getMap().addMapObject(npc);
         MasterBroadcaster.getInstance().sendToAllInMap(player.getMap(), new SpawnNPC(npc));
      }
   }
}
