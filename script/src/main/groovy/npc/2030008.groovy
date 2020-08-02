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


class NPC2030008 {
   NPCConversationManager cm
   int status = -1
   int selectedType = -1
   EventManager em

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (cm.haveItem(4001109, 1)) {
            cm.warp(921100000, "out00")
            cm.dispose()
            return
         }

         if (!(cm.isQuestStarted(100200) || cm.isQuestCompleted(100200))) {
            if (cm.getPlayer().getLevel() >= 50) {
               cm.sendOk(I18nMessage.from("2030008_BEWARE"))
            } else {
               cm.sendOk(I18nMessage.from("2030008_BEWARE_SHORT"))
            }

            cm.dispose()
            return
         }

         em = cm.getEventManager("ZakumPQ")
         if (em == null) {
            cm.sendOk(I18nMessage.from("2030008_PQ_ERROR"))
            cm.dispose()
            return
         }

         if (status == 0) {
            cm.sendSimple(I18nMessage.from("2030008_CHOICES").with(em.getProperty("party")))
         } else if (status == 1) {
            if (selection == 0) {
               if (cm.getParty().isEmpty()) {
                  cm.sendOk(I18nMessage.from("2030008_NEED_TO_BE_IN_PARTY"))
                  cm.dispose()
               } else if (!cm.isLeader()) {
                  cm.sendOk(I18nMessage.from("2030008_PARTY_LEADER_MUST_START"))
                  cm.dispose()
               } else {
                  MaplePartyCharacter[] eli = em.getEligibleParty(cm.getParty().orElseThrow())
                  if (eli.size() > 0) {
                     if (!em.startInstance(cm.getParty().orElseThrow(), cm.getPlayer().getMap(), 1)) {
                        cm.sendOk(I18nMessage.from("2030008_ANOTHER_PARTY_HAS_ENTERED"))
                     }
                  } else {
                     cm.sendOk(I18nMessage.from("2030008_PARTY_REQUIREMENTS"))
                  }

                  cm.dispose()
               }
            } else if (selection == 1) {
               if (cm.haveItem(4031061) && !cm.haveItem(4031062)) {
                  cm.sendYesNo(I18nMessage.from("2030008_ATTEMPT_BREATH_OF_LAVA"))
               } else {
                  if (cm.haveItem(4031062)) {
                     cm.sendNext(I18nMessage.from("2030008_ALREADY_HAVE_BREATH_OF_LAVA"))
                  } else {
                     cm.sendNext(I18nMessage.from("2030008_COMPLETE_EARLIER_TRIALS_FIRST"))
                  }

                  cm.dispose()
               }
            } else {
               if (cm.haveItem(4031061) && cm.haveItem(4031062)) {
                  if (!cm.haveItem(4000082, 30)) {
                     cm.sendOk(I18nMessage.from("2030008_STILL_NEED"))
                  } else {
                     cm.completeQuest(100201)
                     cm.gainItem(4031061, (short) -1)
                     cm.gainItem(4031062, (short) -1)
                     cm.gainItem(4000082, (short) -30)

                     cm.gainItem(4001017, (short) 5)
                     cm.sendNext(I18nMessage.from("2030008_APPROVED_TO_CHALLENGE_ZAKUM"))
                  }

                  cm.dispose()
               } else {
                  cm.sendOk(I18nMessage.from("2030008_LACK_REQUIRED_ITEMS"))
                  cm.dispose()
               }
            }
         } else if (status == 2) {
            cm.warp(280020000, 0)
            cm.dispose()
         }
      }
   }
}

NPC2030008 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2030008(cm: cm))
   }
   return (NPC2030008) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }