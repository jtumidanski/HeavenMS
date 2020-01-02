package net.server.task;

import java.util.Set;

import net.server.channel.Channel;
import net.server.world.World;
import tools.Pair;

public class WeddingReservationTask extends BaseTask implements Runnable {

   public WeddingReservationTask(World world) {
      super(world);
   }

   @Override
   public void run() {
      for (Channel ch : world.getChannels()) {
         Pair<Boolean, Pair<Integer, Set<Integer>>> wedding;

         wedding = ch.getNextWeddingReservation(true);   // start cathedral
         if (wedding != null) {
            ch.setOngoingWedding(true, wedding.getLeft(), wedding.getRight().getLeft(), wedding.getRight().getRight());
         } else {
            ch.setOngoingWedding(true, null, null, null);
         }

         wedding = ch.getNextWeddingReservation(false);  // start chapel
         if (wedding != null) {
            ch.setOngoingWedding(false, wedding.getLeft(), wedding.getRight().getLeft(), wedding.getRight().getRight());
         } else {
            ch.setOngoingWedding(false, null, null, null);
         }
      }
   }
}
