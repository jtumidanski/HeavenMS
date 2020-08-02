package npc
import tools.I18nMessage

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201112 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (status == 1 && mode == 0) {
            cm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }
         EventInstanceManager eim = cm.getEventInstance()
         if (eim == null) {
            cm.sendNext(I18nMessage.from("9201112_NOT_STARTED"))
            cm.dispose()
            return
         }
         switch (cm.getPlayer().getMapId()) {
            case 610030100:
               if (status == 0) {
                  cm.sendNext(I18nMessage.from("9201112_MADE_IT_IN"))
               } else if (status == 1) {
                  cm.sendNext(I18nMessage.from("9201112_FIND_AN_ALTERNATE_WAY"))
               } else if (status == 2) {
                  cm.sendNext(I18nMessage.from("9201112_FIND_THE_PORTAL"))
                  cm.dispose()
               }
               break
            case 610030200:
               if (status == 0) {
                  cm.sendNext(I18nMessage.from("9201112_A_SUCCESS"))
               } else if (status == 1) {
                  cm.sendNext(I18nMessage.from("9201112_USE_THEIR_SKILLS"))
                  cm.dispose()
               }
               break
            case 610030300:
               if (status == 0) {
                  cm.sendNext(I18nMessage.from("9201112_TREAD_LIGHTLY"))
               } else if (status == 1) {
                  cm.sendNext(I18nMessage.from("9201112_BEWARE_OF_TRAPS"))
                  cm.dispose()
               }
               break
            case 610030400:
               if (status == 0) {
                  cm.sendNext(I18nMessage.from("9201112_ALL_JOBS_MUST_FILL_ROLES"))
               } else if (status == 1) {
                  cm.sendNext(I18nMessage.from("9201112_MERELY_A_DISTRACTION"))
                  cm.dispose()
               }
               break
            case 610030500:
               if (status == 0) {
                  cm.sendNext(I18nMessage.from("9201112_SURPRISED"))
               } else if (status == 1) {
                  cm.sendNext(I18nMessage.from("9201112_FIVE_ROOMS"))
               } else if (status == 2) {
                  cm.sendNext(I18nMessage.from("9201112_SUSPECT_EACH_ROOM_HAS_A_WEAPON"))
               } else if (status == 3) {
                  cm.sendNext(I18nMessage.from("9201112_BRING_BACK_THE_WEAPONS"))
                  cm.dispose()
               }
               break
            case 610030700:
               cm.sendNext(I18nMessage.from("9201112_GOOD_WORK"))
               cm.dispose()
               break
         }
      }
   }
}

NPC9201112 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201112(cm: cm))
   }
   return (NPC9201112) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }