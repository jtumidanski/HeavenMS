package tools.packet.factory;

import net.opcodes.SendOpcode;
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
      registry.setHandler(GetCharacterInfo.class, packet -> this.getCharInfo((GetCharacterInfo) packet));
      registry.setHandler(WarpToMap.class, packet -> this.getWarpToMap((WarpToMap) packet));
   }

   /**
    * Gets character info for a character.
    *
    * @return The character info packet.
    */
   protected byte[] getCharInfo(GetCharacterInfo packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SET_FIELD.getValue());
      mplew.writeInt(packet.getCharacter().getClient().getChannel() - 1);
      mplew.write(1);
      mplew.write(1);
      mplew.writeShort(0);
      for (int i = 0; i < 3; i++) {
         mplew.writeInt(Randomizer.nextInt());
      }
      addCharacterInfo(mplew, packet.getCharacter());
      mplew.writeLong(getTime(System.currentTimeMillis()));
      return mplew.getPacket();
   }

   /**
    * Gets a packet telling the client to change maps.
    *
    * @return The map change packet.
    */
   protected byte[] getWarpToMap(WarpToMap packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SET_FIELD.getValue());
      mplew.writeInt(packet.channelId() - 1);
      mplew.writeInt(0);//updated
      mplew.write(0);//updated
      mplew.writeInt(packet.mapId());
      mplew.write(packet.spawnPoint());
      mplew.writeShort(packet.characterHp());
      if (packet.spawnPosition().isDefined()) {
         mplew.writeBool(true);
         mplew.writeInt(packet.spawnPosition().get().x);    // spawn position placement thanks to Arnah (Vertisy)
         mplew.writeInt(packet.spawnPosition().get().y);
      } else {
         mplew.writeBool(false);
      }
      mplew.writeLong(getTime(Server.getInstance().getCurrentTime()));
      mplew.skip(18);
      return mplew.getPacket();
   }
}