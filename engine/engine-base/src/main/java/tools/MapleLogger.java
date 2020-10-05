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
      LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.PACKET_LOGS, String.format("[%s] [%s] %s", c.getAccountName(), chr.getName(), packet));
   }

   private static boolean isRecvBlocked(RecvOpcode op) {
      return switch (op) {
         case MOVE_PLAYER, GENERAL_CHAT, TAKE_DAMAGE, MOVE_PET, MOVE_LIFE, NPC_ACTION, FACE_EXPRESSION -> true;
         default -> false;
      };
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
