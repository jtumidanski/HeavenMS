package net.mina;

import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import client.MapleClient;
import config.YamlConfig;
import constants.net.OpcodeConstants;
import net.server.coordinator.session.MapleSessionCoordinator;
import tools.HexTool;
import tools.MapleAESOFB;
import tools.data.input.ByteArrayByteStream;
import tools.data.input.GenericLittleEndianAccessor;

public class MaplePacketDecoder extends CumulativeProtocolDecoder {
   private static final String DECODER_STATE_KEY = MaplePacketDecoder.class.getName() + ".STATE";

   @Override
   protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
      final MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
      if (client == null) {
         MapleSessionCoordinator.getInstance().closeSession(session, true);
         return false;
      }

      DecoderState decoderState = (DecoderState) session.getAttribute(DECODER_STATE_KEY);
      if (decoderState == null) {
         decoderState = new DecoderState();
         session.setAttribute(DECODER_STATE_KEY, decoderState);
      }

      MapleAESOFB receiveCrypto = client.getReceiveCrypto();
      if (in.remaining() >= 4 && decoderState.packetLength == -1) {
         int packetHeader = in.getInt();
         if (!receiveCrypto.checkPacket(packetHeader)) {
            MapleSessionCoordinator.getInstance().closeSession(session, true);
            return false;
         }
         decoderState.packetLength = MapleAESOFB.getPacketLength(packetHeader);
      } else if (in.remaining() < 4 && decoderState.packetLength == -1) {
         return false;
      }
      if (in.remaining() >= decoderState.packetLength) {
         byte[] decryptedPacket = new byte[decoderState.packetLength];
         in.get(decryptedPacket, 0, decoderState.packetLength);
         decoderState.packetLength = -1;
         receiveCrypto.crypt(decryptedPacket);
         MapleCustomEncryption.decryptData(decryptedPacket);
         out.write(decryptedPacket);
         if (YamlConfig.config.server.USE_DEBUG_SHOW_PACKET) {
            int packetLen = decryptedPacket.length;
            int pHeader = readFirstShort(decryptedPacket);
            String pHeaderStr = Integer.toHexString(pHeader).toUpperCase();
            String op = lookupSend(pHeader);
            String Send = "ClientSend:" + op + " [" + pHeaderStr + "] (" + packetLen + ")\r\n";
            if (packetLen <= 3000) {
               String SendTo = Send + HexTool.toString(decryptedPacket) + "\r\n" + HexTool.toStringFromAscii(decryptedPacket);
               LoggerUtil.printDebug(LoggerOriginator.ENGINE, SendTo);
               if (op == null) {
                  LoggerUtil.printDebug(LoggerOriginator.ENGINE, "UnknownPacket:" + SendTo);
               }
            } else {
               LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.PACKET_STREAM, MapleSessionCoordinator.getSessionRemoteAddress(session) + HexTool.toString(new byte[]{decryptedPacket[0], decryptedPacket[1]}) + "...");
            }
         }
         return true;
      }
      return false;
   }

   private String lookupSend(int val) {
      return OpcodeConstants.recvOpcodeNames.get(val);
   }

   private int readFirstShort(byte[] arr) {
      return new GenericLittleEndianAccessor(new ByteArrayByteStream(arr)).readShort();
   }

   private static class DecoderState {
      public int packetLength = -1;
   }
}
