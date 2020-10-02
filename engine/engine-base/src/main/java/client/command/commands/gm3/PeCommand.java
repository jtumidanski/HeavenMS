package client.command.commands.gm3;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.MaplePacketHandler;
import net.PacketProcessor;
import tools.HexTool;
import tools.I18nMessage;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.MessageBroadcaster;
import tools.data.input.ByteArrayByteStream;
import tools.data.input.GenericSeekableLittleEndianAccessor;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.data.output.MaplePacketLittleEndianWriter;

public class PeCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      String packet;
      try {
         InputStreamReader is = new FileReader("pe.txt");
         Properties packetProps = new Properties();
         packetProps.load(is);
         is.close();
         packet = packetProps.getProperty("pe");
      } catch (IOException ex) {
         ex.printStackTrace();
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("PE_COMMAND_FAILURE"));
         return;

      }
      MaplePacketLittleEndianWriter writer = new MaplePacketLittleEndianWriter();
      writer.write(HexTool.getByteArrayFromHexString(packet));
      SeekableLittleEndianAccessor accessor = new GenericSeekableLittleEndianAccessor(new ByteArrayByteStream(writer.getPacket()));
      short packetId = accessor.readShort();
      final MaplePacketHandler packetHandler = PacketProcessor.getProcessor(0, c.getChannel()).getHandler(packetId);
      if (packetHandler != null && packetHandler.validateState(c)) {
         try {
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("PE_COMMAND_RECEIVING").with(packet));
            packetHandler.handlePacket(accessor, c);
         } catch (final Throwable t) {
            LoggerUtil.printError(LoggerOriginator.PACKET_HANDLER, "Error for " + (c.getPlayer() == null ? "" : "player ; " + c.getPlayer() + " on map ; " + c.getPlayer().getMapId() + " - ") + "account ; " + c.getAccountName() + "\r\n" + accessor.toString());
         }
      }
   }
}
