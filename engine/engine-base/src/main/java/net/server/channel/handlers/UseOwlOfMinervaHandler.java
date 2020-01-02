package net.server.channel.handlers;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import client.MapleClient;
import constants.game.GameConstants;
import net.server.AbstractPacketHandler;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import tools.PacketCreator;
import tools.Pair;
import tools.packet.owl.GetOwlOpen;

public final class UseOwlOfMinervaHandler extends AbstractPacketHandler<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      List<Pair<Integer, Integer>> owlSearched = client.getWorldServer().getOwlSearchedItems();
      List<Integer> owlLeaderboard;

      if (owlSearched.size() < 5) {
         owlLeaderboard = new LinkedList<>();
         for (int i : GameConstants.OWL_DATA) {
            owlLeaderboard.add(i);
         }
      } else {
         // descending order
         Comparator<Pair<Integer, Integer>> comparator = (p1, p2) -> p2.getRight().compareTo(p1.getRight());

         PriorityQueue<Pair<Integer, Integer>> queue = new PriorityQueue<>(Math.max(1, owlSearched.size()), comparator);
         queue.addAll(owlSearched);

         owlLeaderboard = new LinkedList<>();
         for (int i = 0; i < Math.min(owlSearched.size(), 10); i++) {
            owlLeaderboard.add(queue.remove().getLeft());
         }
      }

      PacketCreator.announce(client, new GetOwlOpen(owlLeaderboard));
   }
}