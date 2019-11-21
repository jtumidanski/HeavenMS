package tools.packet.factory;

import java.util.List;

import constants.game.GameConstants;
import net.server.Server;
import net.server.world.World;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.transfer.world.WorldTransferCancel;
import tools.packet.transfer.world.WorldTransferError;

public class WorldTransferPacketFactory extends AbstractPacketFactory {
   private static WorldTransferPacketFactory instance;

   public static WorldTransferPacketFactory getInstance() {
      if (instance == null) {
         instance = new WorldTransferPacketFactory();
      }
      return instance;
   }

   private WorldTransferPacketFactory() {
      Handler.handle(WorldTransferError.class).decorate(this::sendWorldTransferRules).register(registry);
      Handler.handle(WorldTransferCancel.class).decorate(this::showWorldTransferCancel).register(registry);
   }

   /*  1: cannot find char info,
            2: cannot transfer under 20,
            3: cannot send banned,
            4: cannot send married,
            5: cannot send guild leader,
            6: cannot send if account already requested transfer,
            7: cannot transfer within 30days,
            8: must quit family,
            9: unknown error
        */
   protected void sendWorldTransferRules(MaplePacketLittleEndianWriter writer, WorldTransferError packet) {
      writer.writeInt(0); //ignored
      writer.write(packet.error());
      writer.writeInt(0);
      writer.writeBool(packet.error() == 0); //0 = ?, otherwise list servers
      if (packet.error() == 0) {
         List<World> worlds = Server.getInstance().getWorlds();
         writer.writeInt(worlds.size());
         for (World world : worlds) {
            writer.writeMapleAsciiString(GameConstants.WORLD_NAMES[world.getId()]);
         }
      }
   }

   protected void showWorldTransferCancel(MaplePacketLittleEndianWriter writer, WorldTransferCancel packet) {
      writer.writeBool(packet.success());
      if (!packet.success()) {
         writer.write(0);
      }
      //writer.writeMapleAsciiString("Custom message."); //only if ^ != 0
   }
}