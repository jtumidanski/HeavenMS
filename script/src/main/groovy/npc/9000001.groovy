package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9000001 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      cm.sendNext(I18nMessage.from("9000001_HELLO"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (status >= 2 && mode == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 1) {
            cm.sendNextPrev(I18nMessage.from("9000001_WHAT_SHOULD_I_DO"))
         } else if (status == 2) {
            cm.sendSimple(I18nMessage.from("9000001_GO_WITH_ME"))
         } else if (status == 3) {
            if (selection == 0) {
               cm.sendNext(I18nMessage.from("9000001_3RD_ANNIVERSARY"))
               cm.dispose()
            } else if (selection == 1) {
               cm.sendSimple(I18nMessage.from("9000001_MANY_GAMES"))
            } else if (selection == 2) {
               if (cm.getEvent() != null && cm.getEvent().getLimit() > 0) {
                  cm.getPlayer().saveLocation("EVENT")
                  if (cm.getEvent().getMapId() == 109080000 || cm.getEvent().getMapId() == 109060001) {
                     cm.divideTeams()
                  }

                  cm.getEvent().minusLimit()
                  cm.warp(cm.getEvent().getMapId(), 0)
                  cm.dispose()
               } else {
                  cm.sendNext(I18nMessage.from("9000001_EVENT_HAS_NOT_BEEN_STARTED"))
                  cm.dispose()
               }
            }
         } else if (status == 4) {
            if (selection == 0) {
               cm.sendNext(I18nMessage.from("9000001_OLA_OLA_INFO"))
               cm.dispose()
            } else if (selection == 1) {
               cm.sendNext(I18nMessage.from("9000001_FITNESS_INFO"))
               cm.dispose()
            } else if (selection == 2) {
               cm.sendNext(I18nMessage.from("9000001_SNOWBALL_INFO"))
               cm.dispose()
            } else if (selection == 3) {
               cm.sendNext(I18nMessage.from("9000001_COCONUT_INFO"))
               cm.dispose()
            } else if (selection == 4) {
               cm.sendNext(I18nMessage.from("9000001_OX_QUIZ_INFO"))
               cm.dispose()
            } else if (selection == 5) {
               cm.sendNext(I18nMessage.from("9000001_TREASURE_HUNT_INFO"))
               cm.dispose()
            }
         }
      }
   }
}

NPC9000001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9000001(cm: cm))
   }
   return (NPC9000001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }