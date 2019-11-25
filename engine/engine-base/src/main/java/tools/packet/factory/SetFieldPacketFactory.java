package tools.packet.factory;

import net.server.Server;
import tools.Randomizer;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.field.set.GetCharacterInfo;
import tools.packet.field.set.WarpToMap;

public class SetFieldPacketFactory extends AbstractPacketFactory {
   private static SetFieldPacketFactory instance;

   public static SetFieldPacketFactory getInstance() {
      if (instance == null) {
         instance = new SetFieldPacketFactory();
      }
      return instance;
   }

   private SetFieldPacketFactory() {
      Handler.handle(GetCharacterInfo.class).decorate(this::getCharInfo).register(registry);
      Handler.handle(WarpToMap.class).decorate(this::getWarpToMap).register(registry);
   }

   /**
    * Gets character info for a character.
    *
    * @return The character info packet.
    */
   protected void getCharInfo(MaplePacketLittleEndianWriter writer, GetCharacterInfo packet) {
      writer.writeInt(packet.getCharacter().getClient().getChannel() - 1);
      writer.write(1);
      writer.write(1);
      writer.writeShort(0);
      for (int i = 0; i < 3; i++) {
         writer.writeInt(Randomizer.nextInt());
      }
      addCharacterInfo(writer, packet.getCharacter());
      writer.writeLong(getTime(System.currentTimeMillis()));
   }

   /**
    * Gets a packet telling the client to change maps.
    *
    * @return The map change packet.
    */
   protected void getWarpToMap(MaplePacketLittleEndianWriter writer, WarpToMap packet) {
      writer.writeInt(packet.channelId() - 1);
      writer.writeInt(0);//updated
      writer.write(0);//updated
      writer.writeInt(packet.mapId());
      writer.write(packet.spawnPoint());
      writer.writeShort(packet.characterHp());
      if (packet.spawnPosition().isDefined()) {
         writer.writeBool(true);
         writer.writeInt(packet.spawnPosition().get().x);    // spawn position placement thanks to Arnah (Vertisy)
         writer.writeInt(packet.spawnPosition().get().y);
      } else {
         writer.writeBool(false);
      }
      writer.writeLong(getTime(Server.getInstance().getCurrentTime()));
   }
}