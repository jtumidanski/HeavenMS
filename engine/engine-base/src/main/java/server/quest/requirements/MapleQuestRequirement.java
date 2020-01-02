package server.quest.requirements;

import client.MapleCharacter;
import provider.MapleData;
import server.quest.MapleQuestRequirementType;

/**
 * Base class for a Quest Requirement. Quest system uses it for all requirements.
 */
public abstract class MapleQuestRequirement {
   private final MapleQuestRequirementType type;

   public MapleQuestRequirement(MapleQuestRequirementType type) {
      this.type = type;
   }

   /**
    * Checks the requirement to see if the player currently meets it.
    *
    * @param chr   The {@link MapleCharacter} to check on.
    * @param npcId The NPC ID it was called from.
    * @return boolean   If the check was passed or not.
    */
   public abstract boolean check(MapleCharacter chr, Integer npcId);

   /**
    * Processes the data and stores it in the class for future use.
    *
    * @param data The data to process.
    */
   public abstract void processData(MapleData data);

   public MapleQuestRequirementType getType() {
      return type;
   }
}