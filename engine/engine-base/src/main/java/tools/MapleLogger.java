package tools;

import java.util.HashSet;
import java.util.Set;

import client.MapleCharacter;
import client.MapleClient;
import net.opcodes.RecvOpcode;

public class MapleLogger {

   public static Set<Integer> monitored = new HashSet<>();
   public static Set<Integer> ignored = new HashSet<>();

   public static void logRecv(MapleClient c, short packetId, Object message) {
      MapleCharacter chr = c.getPlayer();
      if (chr == null) {
         return;
      }
      if (!monitored.contains(chr.getId())) {
         return;
      }
      RecvOpcode op = getOpcodeFromValue(packetId);
      if (isRecvBlocked(op)) {
         return;
      }
      String packet = op.toString() + "\r\n" + HexTool.toString((byte[]) message);
      FilePrinter.printError(FilePrinter.PACKET_LOGS + c.getAccountName() + "-" + chr.getName() + ".txt", packet);
   }

   private static boolean isRecvBlocked(RecvOpcode op) {
      switch (op) {
         case MOVE_PLAYER:
         case GENERAL_CHAT:
         case TAKE_DAMAGE:
         case MOVE_PET:
         case MOVE_LIFE:
         case NPC_ACTION:
         case FACE_EXPRESSION:
            return true;
         default:
            return false;
      }
   }

   private static RecvOpcode getOpcodeFromValue(int value) {
      for (RecvOpcode op : RecvOpcode.values()) {
         if (op.getValue() == value) {
            return op;
         }
      }
      return null;
   }
}
