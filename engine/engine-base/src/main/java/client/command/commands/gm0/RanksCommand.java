package client.command.commands.gm0;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import client.database.data.WorldRankData;
import net.server.Server;
import tools.PacketCreator;
import tools.packet.guild.ShowPlayerRanks;

public class RanksCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();

      WorldRankData worldRanking = Server.getInstance().getWorldPlayerRanking(player.getWorld());
      PacketCreator.announce(player, new ShowPlayerRanks(9010000, worldRanking.getUserRanks()));
   }
}
