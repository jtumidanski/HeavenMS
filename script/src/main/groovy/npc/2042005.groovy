package npc
import tools.I18nMessage

import config.YamlConfig
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
               cm.sendOk(I18nMessage.from("2042005_NEED_TO_BE_IN_A_PARTY"))
            } else if (!cm.isLeader()) {
               status = 10
               cm.sendOk(I18nMessage.from("2042005_LEADER_MUST_START"))
            } else {
               int leaderMapId = cm.getMapId()
               MaplePartyCharacter[] party = cm.getParty().orElseThrow().getMembers()
               int inMap = cm.partyMembersInMap()
               int lvlOk = 0
               int isOutMap = 0
               for (def i = 0; i < party.size(); i++) {
                  if (party[i].getLevel() >= cpqMinLvl && party[i].getLevel() <= cpqMaxLvl) {
                     lvlOk++

                     if (!party[i].inMap(leaderMapId)) {
                        isOutMap++
                     }
                  }
               }

               if (party >= 1) {
                  status = 10
                  cm.sendOk(I18nMessage.from("2042005_NOT_ENOUGH_PEOPLE").with(cpqMinAmt, cpqMaxAmt))
               } else if (lvlOk != inMap) {
                  status = 10
                  cm.sendOk(I18nMessage.from("2042005_LEVEL_RANGE").with(cpqMinLvl, cpqMaxLvl))
               } else if (isOutMap > 0) {
                  status = 10
                  cm.sendOk(I18nMessage.from("2042005_PARTY_MEMBERS_NOT_IN_MAP"))
               } else {
                  if (!cm.sendCPQMapLists2()) {
                     cm.sendOk(I18nMessage.from("2042005_ALL_FIELDS_CURRENTLY_FULL"))
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
                  cm.sendOk(I18nMessage.from("2042005_ROOM_IS_FULL"))
                  cm.dispose()
               }
            } else {
               MaplePartyCharacter[] party = cm.getParty().orElseThrow().getMembers()
               if ((selection == 0 || selection == 1) && party.size() < (YamlConfig.config.server.USE_ENABLE_SOLO_EXPEDITIONS ? 1 : 2)) {
                  cm.sendOk(I18nMessage.from("2042005_NEED_AT_LEAST_2_PLAYERS"))
               } else if ((selection == 2) && party.size() < (YamlConfig.config.server.USE_ENABLE_SOLO_EXPEDITIONS ? 1 : 3)) {
                  cm.sendOk(I18nMessage.from("2042005_NEED_AT_LEAST_3_PLAYERS"))
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