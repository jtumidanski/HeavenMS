package npc
import tools.I18nMessage


import net.server.world.MaplePartyCharacter
import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9020000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int state
   EventManager em

   def start() {
      status = -1
      state = (cm.getMapId() >= 103000800 && cm.getMapId() <= 103000805) ? 1 : 0
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
            if (state == 1) {
               cm.sendYesNo(I18nMessage.from("9020000_ABANDON"))

            } else {
               em = cm.getEventManager("KerningPQ")
               if (em == null) {
                  cm.sendOk(I18nMessage.from("9020000_PQ_ENCOUNTERED_ERROR"))

                  cm.dispose()
               } else if (cm.isUsingOldPqNpcStyle()) {
                  action((byte) 1, (byte) 0, 0)
                  return
               }

               cm.sendSimple(I18nMessage.from("9020000_PARTY_QUEST_INFO").with(em.getProperty("party"), cm.getPlayer().isRecvPartySearchInviteEnabled() ? "disable" : "enable"))

            }
         } else if (status == 1) {
            if (state == 1) {
               cm.warp(103000000)
               cm.dispose()
            } else {
               if (selection == 0) {
                  if (cm.getParty().isEmpty()) {
                     cm.sendOk(I18nMessage.from("9020000_MUST_BE_IN_PARTY"))

                     cm.dispose()
                  } else if (!cm.isLeader()) {
                     cm.sendOk(I18nMessage.from("9020000_PARTY_LEADER"))

                     cm.dispose()
                  } else {
                     MaplePartyCharacter[] eli = em.getEligibleParty(cm.getParty().orElseThrow())
                     if (eli.size() > 0) {
                        if (!em.startInstance(cm.getParty().orElseThrow(), cm.getPlayer().getMap(), 1)) {
                           cm.sendOk(I18nMessage.from("9020000_ANOTHER_PARTY"))

                        }
                     } else {
                        cm.sendOk(I18nMessage.from("9020000_PARTY_REQUIREMENTS"))

                     }

                     cm.dispose()
                  }
               } else if (selection == 1) {
                  boolean psState = cm.getPlayer().toggleRecvPartySearchInvite()
                  cm.sendOk(I18nMessage.from("9020000_PARTY_SEARCH_STATUS").with((psState ? "enabled" : "disabled")))

                  cm.dispose()
               } else {
                  cm.sendOk(I18nMessage.from("9020000_PARTY_QUEST_INFO_2"))

                  cm.dispose()
               }
            }
         }
      }
   }
}

NPC9020000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9020000(cm: cm))
   }
   return (NPC9020000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }