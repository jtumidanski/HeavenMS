package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201043 {
   NPCConversationManager cm
   int status = -1
   int MySelection = -1

   def start() {
      cm.sendSimple(I18nMessage.from("9201043_MY_NAME_IS"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (status >= 0 && mode == 0) {
            cm.sendOk(I18nMessage.from("9201043_COME_BACK"))
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 1 && selection == 0) {
            if (cm.haveItem(4031592, 1)) {
               cm.sendYesNo(I18nMessage.from("9201043_LIKE_TO_ENTER"))
               MySelection = selection
            } else {
               cm.sendOk(I18nMessage.from("9201043_MUST_HAVE_A_TICKET"))
               cm.dispose()
            }
         } else if (status == 1 && selection == 1) {
            if (cm.haveItem(4031592)) {
               cm.sendOk(I18nMessage.from("9201043_ALREADY_HAVE_A_TICKET"))
               cm.dispose()
            } else if (cm.haveItem(4031593, 10)) {
               cm.sendYesNo(I18nMessage.from("9201043_WOULD_YOU_LIKE_A_TICKET"))
               MySelection = selection
            } else {
               cm.sendOk(I18nMessage.from("9201043_GET_ME"))
               cm.dispose()
            }
         } else if (status == 2 && MySelection == 0) {
            cm.warp(670010100, 0)
            cm.gainItem(4031592, (short) -1)
            cm.dispose()
         } else if (status == 2 && MySelection == 1) {
            cm.gainItem(4031593, (short) -10)
            cm.gainItem(4031592, (short) 1)
            cm.dispose()
         }
      }
   }
}

NPC9201043 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201043(cm: cm))
   }
   return (NPC9201043) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }