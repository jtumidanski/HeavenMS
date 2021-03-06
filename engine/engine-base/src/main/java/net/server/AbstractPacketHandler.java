package net.server;

import java.util.Optional;

import client.MapleClient;
import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import net.MaplePacketHandler;
import net.server.channel.packet.PacketReaderFactory;
import tools.data.input.SeekableLittleEndianAccessor;

public abstract class AbstractPacketHandler<T extends MaplePacket> implements MaplePacketHandler {
   @Override
   public void handlePacket(SeekableLittleEndianAccessor accessor, MapleClient client) {
      if (!successfulProcess(client)) {
         return;
      }

      Optional<T> packet = readPacket(accessor, client);
      if (packet.isEmpty()) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.PACKET_HANDLER, "Cannot find reader for packet: " + getReaderClass());
         return;
      }

      handlePacket(packet.get(), client);
   }

   protected Optional<T> readPacket(SeekableLittleEndianAccessor accessor, MapleClient client) {
      return PacketReaderFactory.getInstance().read(getReaderClass(client), accessor);
   }

   public boolean successfulProcess(MapleClient client) {
      return true;
   }

   public Class<? extends PacketReader<T>> getReaderClass(MapleClient client) {
      return getReaderClass();
   }

   public abstract Class<? extends PacketReader<T>> getReaderClass();

   public abstract void handlePacket(T packet, MapleClient client);

   @Override
   public boolean validateState(MapleClient client) {
      return client.isLoggedIn();
   }

   protected long currentServerTime() {
      return Server.getInstance().getCurrentTime();
   }
}
