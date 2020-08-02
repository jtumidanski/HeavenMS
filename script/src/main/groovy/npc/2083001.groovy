package npc

import net.server.world.MaplePartyCharacter
import scripting.event.EventManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2083001 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int price = 100000
   EventManager em = null
   boolean hasPass

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   static def isRecruitingMap(mapId) {
      return mapId == 240050000
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

         if (isRecruitingMap(cm.getMapId())) {
            if (status == 0) {
               em = cm.getEventManager("HorntailPQ")
               if (em == null) {
                  cm.sendOk(I18nMessage.from("2083001_PQ_ENCOUNTERED_ERROR"))

                  cm.dispose()
                  return
               } else if (cm.isUsingOldPqNpcStyle()) {
                  action((byte) 1, (byte) 0, 0)
                  return
               }

               cm.sendSimple(I18nMessage.from("2083001_PARTY_QUEST_INTRO").with(em.getProperty("party"), (cm.getPlayer().isRecvPartySearchInviteEnabled() ? "disable" : "enable")))

            } else if (status == 1) {
               if (selection == 0) {
                  if (cm.getParty().isEmpty()) {
                     cm.sendOk(I18nMessage.from("2083001_MUST_BE_IN_PARTY"))

                     cm.dispose()
                  } else if (!cm.isLeader()) {
                     cm.sendOk(I18nMessage.from("2083001_LEADER_MUST_TALK"))

                     cm.dispose()
                  } else {
                     MaplePartyCharacter[] eli = em.getEligibleParty(cm.getParty().orElseThrow())
                     if (eli.size() > 0) {
                        if (!em.startInstance(cm.getParty().orElseThrow(), cm.getPlayer().getMap(), 1)) {
                           cm.sendOk(I18nMessage.from("2083001_ANOTHER_PARTY_HAS_STARTED"))

                        }
                     } else {
                        cm.sendOk(I18nMessage.from("2083001_PARTY_MEMBERS_NOT_IN_MAP"))

                     }

                     cm.dispose()
                  }
               } else if (selection == 1) {
                  boolean psState = cm.getPlayer().toggleRecvPartySearchInvite()
                  cm.sendOk(I18nMessage.from("2083001_PARTY_SEARCH_STATUS").with((psState ? "enabled" : "disabled")))

                  cm.dispose()
               } else {
                  cm.sendOk(I18nMessage.from("2083001_PARTY_QUEST_INTRO_2"))

                  cm.dispose()
               }
            }
         } else {
            if (!cm.isEventLeader()) {
               cm.sendOk(I18nMessage.from("2083001_PARTY_LEADER_MUST_INTERACT"))

            } else if (cm.getMapId() == 240050100) {
               if (cm.haveItem(4001087) && cm.haveItem(4001088) && cm.haveItem(4001089) && cm.haveItem(4001090) && cm.haveItem(4001091)) {
                  cm.gainItem(4001087, (short) -1)
                  cm.gainItem(4001088, (short) -1)
                  cm.gainItem(4001089, (short) -1)
                  cm.gainItem(4001090, (short) -1)
                  cm.gainItem(4001091, (short) -1)

                  cm.getEventInstance().warpEventTeam(240050200)
               } else {
                  cm.sendOk(I18nMessage.from("2083001_MISSING_KEYS"))

               }
            } else if (cm.getMapId() == 240050300) {
               if (cm.haveItem(4001092, 1) && cm.haveItem(4001093, 6)) {
                  cm.gainItem(4001092, (short) -1)
                  cm.gainItem(4001093, (short) -6)
                  cm.getEventInstance().clearPQ()
               } else {
                  cm.sendOk(I18nMessage.from("2083001_MISSING_KEYS_DETAIL"))

               }
            } else if (cm.getMapId() == 240050310) {
               if (cm.haveItem(4001092, 1) && cm.haveItem(4001093, 6)) {
                  cm.gainItem(4001092, (short) -1)
                  cm.gainItem(4001093, (short) -6)
                  cm.getEventInstance().clearPQ()
               } else {
                  cm.sendOk(I18nMessage.from("2083001_MISSING_KEYS_DETAIL"))

               }
            }

            cm.dispose()
         }
      }
   }
}

NPC2083001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2083001(cm: cm))
   }
   return (NPC2083001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }