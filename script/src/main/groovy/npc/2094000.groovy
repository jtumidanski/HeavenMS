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


class NPC2094000 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   EventManager em = null

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
            em = cm.getEventManager("PiratePQ")
            if (em == null) {
               cm.sendOk(I18nMessage.from("2094000_PQ_ENCOUNTERED_ERROR"))

               cm.dispose()
               return
            } else if (cm.isUsingOldPqNpcStyle()) {
               action((byte) 1, (byte) 0, 0)
               return
            }

            cm.sendSimple(I18nMessage.from("2094000_PARTY_QUEST_INFO").with(em.getProperty("party"), cm.getPlayer().isRecvPartySearchInviteEnabled() ? "disable" : "enable"))

         } else if (status == 1) {
            if (selection == 0) {
               if (cm.getParty().isEmpty()) {
                  cm.sendOk(I18nMessage.from("2094000_MUST_BE_IN_PARTY"))

                  cm.dispose()
               } else if (!cm.isLeader()) {
                  cm.sendOk(I18nMessage.from("2094000_LEADER_MUST_START"))

                  cm.dispose()
               } else {
                  MaplePartyCharacter[] eli = em.getEligibleParty(cm.getParty().orElseThrow())
                  if (eli.size() > 0) {
                     if (!em.startInstance(cm.getParty().orElseThrow(), cm.getPlayer().getMap(), 1)) {
                        cm.sendOk(I18nMessage.from("2094000_ANOTHER_PARTY_STARTED"))

                     }
                  } else {
                     cm.sendOk(I18nMessage.from("2094000_PARTY_REQUIREMENTS"))

                  }

                  cm.dispose()
               }
            } else if (selection == 1) {
               boolean psState = cm.getPlayer().toggleRecvPartySearchInvite()
               cm.sendOk(I18nMessage.from("2094000_PARTY_SEARCH_STATUS").with((psState ? "enabled" : "disabled")))

               cm.dispose()
            } else {
               cm.sendOk(I18nMessage.from("2094000_PARTY_QUEST_INFO_SHORT"))

               cm.dispose()
            }
         }
      }
   }
}

NPC2094000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2094000(cm: cm))
   }
   return (NPC2094000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }