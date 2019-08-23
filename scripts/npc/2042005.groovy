package npc

import constants.ServerConstants
import net.server.world.MaplePartyCharacter
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2042005 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int cpqMinLvl = 51
   int cpqMaxLvl = 70
   int cpqMinAmt = 2
   int cpqMaxAmt = 6

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            if (cm.getParty() == null) {
               status = 10
               cm.sendOk("You need to create a party before you can participate in Monster Carnival!")
            } else if (!cm.isLeader()) {
               status = 10
               cm.sendOk("If you want to start the battle, let the #bleader#k come and speak to me.")
            } else {
               int leaderMapid = cm.getMapId()
               MaplePartyCharacter[] party = cm.getParty().getMembers()
               int inMap = cm.partyMembersInMap()
               int lvlOk = 0
               int isOutMap = 0
               for (def i = 0; i < party.size(); i++) {
                  if (party[i].getLevel() >= cpqMinLvl && party[i].getLevel() <= cpqMaxLvl) {
                     lvlOk++

                     if (party[i].getPlayer().getMapId() != leaderMapid) {
                        isOutMap++
                     }
                  }
               }

               if (party >= 1) {
                  status = 10
                  cm.sendOk("You do not have enough people in your party. You need a party with #b" + cpqMinAmt + "#k - #r" + cpqMaxAmt + "#k members and they should be on the map with you.")
               } else if (lvlOk != inMap) {
                  status = 10
                  cm.sendOk("Make sure everyone in your party is among the correct levels (" + cpqMinLvl + "~" + cpqMaxLvl + ")!")
               } else if (isOutMap > 0) {
                  status = 10
                  cm.sendOk("There are some of the party members that is not on the map!")
               } else {
                  if (!cm.sendCPQMapLists2()) {
                     cm.sendOk("All Monster Carnival fields are currently in use! Try again later.")
                     cm.dispose()
                  }
               }
            }
         } else if (status == 1) {
            if (cm.fieldTaken2(selection)) {
               if (cm.fieldLobbied2(selection)) {
                  cm.challengeParty2(selection)
                  cm.dispose()
               } else {
                  cm.sendOk("The room is currently full.")
                  cm.dispose()
               }
            } else {
               MaplePartyCharacter[] party = cm.getParty().getMembers()
               if ((selection == 0 || selection == 1) && party.size() < (ServerConstants.USE_ENABLE_SOLO_EXPEDITIONS ? 1 : 2)) {
                  cm.sendOk("You need at least 2 players to participate in the battle!")
               } else if ((selection == 2) && party.size() < (ServerConstants.USE_ENABLE_SOLO_EXPEDITIONS ? 1 : 3)) {
                  cm.sendOk("You need at least 3 players to participate in the battle!")
               } else {
                  cm.cpqLobby2(selection)
               }
               cm.dispose()
            }
         } else if (status == 11) {
            cm.dispose()
         }
      }
   }
}

NPC2042005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2042005(cm: cm))
   }
   return (NPC2042005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }