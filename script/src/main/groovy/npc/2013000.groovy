package npc

import net.server.world.MaplePartyCharacter
import scripting.event.EventManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Wonky
	Map(s): 		Orbis - The Unknown Tower
	Description: 	
*/


class NPC2013000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   EventManager em

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

         if (cm.getMapId() == 200080101) {
            if (status == 0) {
               em = cm.getEventManager("OrbisPQ")
               if (em == null) {
                  cm.sendOk(I18nMessage.from("2013000_PQ_ENCOUNTERED_ERROR"))
                  cm.dispose()
                  return
               } else if (cm.isUsingOldPqNpcStyle()) {
                  action((byte) 1, (byte) 0, 0)
                  return
               }

               cm.sendSimple(I18nMessage.from("2013000_WHAT_WOULD_YOU_LIKE_TO_DO").with(em.getProperty("party"), (cm.getPlayer().isRecvPartySearchInviteEnabled() ? "disable" : "enable")))
            } else if (status == 1) {
               if (selection == 0) {
                  if (cm.getParty().isEmpty()) {
                     cm.sendOk(I18nMessage.from("2013000_NEED_A_PARTY"))
                     cm.dispose()
                  } else if (!cm.isLeader()) {
                     cm.sendOk(I18nMessage.from("2013000_PARTY_LEADER_MUST_START"))
                     cm.dispose()
                  } else {
                     MaplePartyCharacter[] eli = em.getEligibleParty(cm.getParty().orElseThrow())
                     if (eli.size() > 0) {
                        if (!em.startInstance(cm.getParty().orElseThrow(), cm.getPlayer().getMap(), 1)) {
                           cm.sendOk(I18nMessage.from("2013000_ANOTHER_PARTY_HAS_ENTERED"))
                        }
                     } else {
                        cm.sendOk(I18nMessage.from("2013000_PARTY_REQUIREMENT_ISSUE"))
                     }

                     cm.dispose()
                  }
               } else if (selection == 1) {
                  boolean psState = cm.getPlayer().toggleRecvPartySearchInvite()
                  cm.sendOk(I18nMessage.from("2013000_PARTY_SEARCH_STATUS").with(psState ? "enabled" : "disabled"))
                  cm.dispose()
               } else if (selection == 2) {
                  cm.sendOk(I18nMessage.from("2013000_INTRO"))
                  cm.dispose()
               } else {
                  cm.sendSimple(I18nMessage.from("2013000_WHAT_PRIZE"))
               }
            } else if (status == 2) {
               if (selection == 0) {
                  if (!cm.haveItem(1082232) && cm.haveItem(4001158, 10)) {
                     cm.gainItem(1082232, (short) 1)
                     cm.gainItem(4001158, (short) -10)
                     cm.dispose()
                  } else {
                     cm.sendOk(I18nMessage.from("2013000_ALREADY_HAVE_PRIZE_OR_NOT_ENOUGH"))
                     cm.dispose()
                  }
               }
            }
         } else {
            if (status == 0) {
               cm.sendYesNo(I18nMessage.from("2013000_ARE_YOU_GOING_TO_DROP_OUT"))
            } else if (status == 1) {
               cm.warp(920011200)
               cm.dispose()
            }
         }
      }
   }
}

NPC2013000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2013000(cm: cm))
   }
   return (NPC2013000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }