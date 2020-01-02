package client.command.commands.gm3;

import client.MapleClient;
import client.command.Command;
import tools.PacketCreator;
import tools.packet.GetEnergy;

public class MaxEnergyCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      c.getPlayer().setDojoEnergy(10000);
      PacketCreator.announce(c, new GetEnergy("energy", 10000));
   }
}
