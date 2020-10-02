package server.partyquest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import client.MapleCharacter;
import net.server.Server;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import tools.LoggerOriginator;
import tools.LoggerUtil;

public class PartyQuest {
   int channel, world;
   MapleParty party;
   List<MapleCharacter> participants = new ArrayList<>();

   public PartyQuest(MapleParty party) {
      this.party = party;
      MaplePartyCharacter leader = party.getLeader();
      channel = leader.getChannel();
      world = leader.getWorld();
      int mapId = leader.getMapId();

      party.getMembers().stream()
            .filter(member -> member.getChannel() == channel && member.getMapId() == mapId)
            .map(member -> Server.getInstance().getWorld(world).getChannel(channel).getPlayerStorage().getCharacterById(member.getId()))
            .flatMap(Optional::stream)
            .forEach(character -> participants.add(character));
   }

   public static int getExp(String PQ, int level) {
      switch (PQ) {
         case "HenesysPQ":
            return 1250 * level / 5;
         case "KerningPQFinal":
            return 500 * level / 5;
         case "KerningPQ4th":
            return 400 * level / 5;
         case "KerningPQ3rd":
            return 300 * level / 5;
         case "KerningPQ2nd":
            return 200 * level / 5;
         case "KerningPQ1st":
            return 100 * level / 5;
         case "LudiMazePQ":
            return 2000 * level / 5;
         case "LudiPQ1st":
            return 100 * level / 5;
         case "LudiPQ2nd":
            return 250 * level / 5;
         case "LudiPQ3rd":
            return 350 * level / 5;
         case "LudiPQ4th":
            return 350 * level / 5;
         case "LudiPQ5th":
            return 400 * level / 5;
         case "LudiPQ6th":
            return 450 * level / 5;
         case "LudiPQ7th":
            return 500 * level / 5;
         case "LudiPQ8th":
            return 650 * level / 5;
         case "LudiPQLast":
            return 800 * level / 5;
      }
      LoggerUtil.printError(LoggerOriginator.NPC, "Unhandled PartyQuest: " + PQ);
      return 0;
   }

   public MapleParty getParty() {
      return party;
   }

   public List<MapleCharacter> getParticipants() {
      return participants;
   }

   public void removeParticipant(MapleCharacter chr) throws Throwable {
      synchronized (participants) {
         participants.remove(chr);
         chr.setPartyQuest(null);
         if (participants.isEmpty()) {
            super.finalize();
         }
         //System.gc();
      }
   }
}
