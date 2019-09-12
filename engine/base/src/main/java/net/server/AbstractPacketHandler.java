package net.server;

import java.util.Optional;

import client.MapleClient;
import net.MaplePacketHandler;
import net.server.channel.worker.PacketReaderFactory;
import tools.FilePrinter;
import tools.data.input.SeekableLittleEndianAccessor;

public abstract class AbstractPacketHandler<T extends MaplePacket, U extends PacketReader<T>> implements MaplePacketHandler {
   @Override
   public void handlePacket(SeekableLittleEndianAccessor accessor, MapleClient client) {
      if (!successfulProcess(client)) {
         return;
      }

      Optional<T> packet = readPacket(accessor);
      if (packet.isEmpty()) {
         FilePrinter.printError(FilePrinter.PACKET_HANDLER, "Cannot find reader for packet: " + getReaderClass());
         return;
      }
      handlePacket(packet.get(), client);
   }

   protected Optional<T> readPacket(SeekableLittleEndianAccessor accessor) {
      return PacketReaderFactory.getInstance().read(getReaderClass(), accessor);
   }

   public boolean successfulProcess(MapleClient client) {
      return true;
   }

   public abstract Class<U> getReaderClass();

   public abstract void handlePacket(T packet, MapleClient client);

   @Override
   public boolean validateState(MapleClient c) {
      return c.isLoggedIn();
   }

   protected long currentServerTime() {
      return Server.getInstance().getCurrentTime();
   }
}
