package server.minigame;

import client.MapleClient;
import client.inventory.Item;
import client.inventory.manipulator.MapleInventoryManipulator;
import tools.PacketCreator;
import tools.Randomizer;
import tools.packet.rps.RPSMode;
import tools.packet.rps.RPSSelection;

public class MapleRockPaperScissor {

   private int round = 0;
   private boolean ableAnswer = true;
   private boolean win = false;

   public MapleRockPaperScissor(final MapleClient c, final byte mode) {
      PacketCreator.announce(c, new RPSMode((byte) (9 + mode)));
      if (mode == 0) {
         c.getPlayer().gainMeso(-1000, true, true, true);
      }
   }

   public final boolean answer(final MapleClient c, final int answer) {
      if (ableAnswer && !win && answer >= 0 && answer <= 2) {
         final int response = Randomizer.nextInt(3);
         if (response == answer) {
            PacketCreator.announce(c, new RPSSelection((byte) response, (byte) round));
            // dont do anything. they can still answer once a draw
         } else if ((answer == 0 && response == 2) || (answer == 1 && response == 0) || (answer == 2 && response == 1)) { // they win
            PacketCreator.announce(c, new RPSSelection((byte) response, (byte) (round + 1)));
            ableAnswer = false;
            win = true;
         } else { // they lose
            PacketCreator.announce(c, new RPSSelection((byte) response, (byte) -1));
            ableAnswer = false;
         }
         return true;
      }
      reward(c);
      return false;
   }

   public final boolean timeOut(final MapleClient c) {
      if (ableAnswer && !win) {
         ableAnswer = false;
         PacketCreator.announce(c, new RPSMode((byte) 0x0A));
         return true;
      }
      reward(c);
      return false;
   }

   public final boolean nextRound(final MapleClient c) {
      if (win) {
         round++;
         if (round < 10) {
            win = false;
            ableAnswer = true;
            PacketCreator.announce(c, new RPSMode((byte) 0x0C));
            return true;
         } else {
            round = 10;
         }
      }
      reward(c);
      return false;
   }

   public final void reward(final MapleClient c) {
      if (win) {
         MapleInventoryManipulator.addFromDrop(c, new Item(4031332 + round, (short) 0, (short) 1), true);
      }
      c.getPlayer().setRPS(null);
   }

   public final void dispose(final MapleClient c) {
      reward(c);
      PacketCreator.announce(c, new RPSMode((byte) 0x0D));
   }
}
