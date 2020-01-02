package net.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import client.MapleClient;
import config.YamlConfig;
import constants.net.OpcodeConstants;
import net.server.coordinator.session.MapleSessionCoordinator;
import tools.FilePrinter;
import tools.HexTool;
import tools.MapleAESOFB;
import tools.data.input.ByteArrayByteStream;
import tools.data.input.GenericLittleEndianAccessor;

public class MaplePacketEncoder implements ProtocolEncoder {

   @Override
   public void encode(final IoSession session, final Object message, final ProtocolEncoderOutput out) {
      final MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);

      try {
         if (client.tryAcquireEncoder()) {
            try {
               final MapleAESOFB send_crypto = client.getSendCrypto();
               final byte[] input = (byte[]) message;
               if (YamlConfig.config.server.USE_DEBUG_SHOW_PACKET) {
                  int packetLen = input.length;
                  int pHeader = readFirstShort(input);
                  String pHeaderStr = Integer.toHexString(pHeader).toUpperCase();
                  String op = lookupRecv(pHeader);
                  String Recv = "ServerSend:" + op + " [" + pHeaderStr + "] (" + packetLen + ")\r\n";
                  if (packetLen <= 50000) {
                     String RecvTo = Recv + HexTool.toString(input) + "\r\n" + HexTool.toStringFromAscii(input);
                     System.out.println(RecvTo);
                     if (op == null) {
                        System.out.println("UnknownPacket:" + RecvTo);
                     }
                  } else {
                     FilePrinter.print(FilePrinter.PACKET_STREAM + MapleSessionCoordinator.getSessionRemoteAddress(session) + ".txt", HexTool.toString(new byte[]{input[0], input[1]}) + " ...");
                  }
               }

               final byte[] unencrypted = new byte[input.length];
               System.arraycopy(input, 0, unencrypted, 0, input.length);
               final byte[] ret = new byte[unencrypted.length + 4];
               final byte[] header = send_crypto.getPacketHeader(unencrypted.length);
               MapleCustomEncryption.encryptData(unencrypted);

               send_crypto.crypt(unencrypted);
               System.arraycopy(header, 0, ret, 0, 4);
               System.arraycopy(unencrypted, 0, ret, 4, unencrypted.length);

               out.write(IoBuffer.wrap(ret));
            } finally {
               client.unlockEncoder();
            }
         }
//            System.arraycopy(unencrypted, 0, ret, 4, unencrypted.length);
//            out.write(ByteBuffer.wrap(ret));
      } catch (NullPointerException npe) {
         out.write(IoBuffer.wrap(((byte[]) message)));
      }
   }

   private String lookupRecv(int val) {
      return OpcodeConstants.sendOpcodeNames.get(val);
   }

   private int readFirstShort(byte[] arr) {
      return new GenericLittleEndianAccessor(new ByteArrayByteStream(arr)).readShort();
   }

   @Override
   public void dispose(IoSession session) {
   }
}