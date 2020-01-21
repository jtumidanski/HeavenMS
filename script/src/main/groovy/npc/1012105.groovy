package npc

import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Ms. Tan
	Map(s): 		Henesys Skin Change
	Description: 	
*/

class NPC1012105 {
   NPCConversationManager cm
   int status = -1
   int sel = -1
   int[] skin = [0, 1, 2, 3, 4]
   int price = 1000000

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            cm.sendSimple(I18nMessage.from("1012105_HELLO"))
         } else if (status == 1) {
            if (cm.haveItem(5153000)) {
               cm.sendStyle(I18nMessage.from("1012105_CHOOSE_STYLE"), skin)
            } else {
               cm.sendOk(I18nMessage.from("1012105_NEED_COUPON"))
               cm.dispose()
            }
         } else {
            cm.gainItem(5153000, (short) -1)
            cm.setSkin(selection)
            cm.sendOk(I18nMessage.from("1012105_ENJOY"))
            cm.dispose()
         }
      }
   }
}

NPC1012105 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1012105(cm: cm))
   }
   return (NPC1012105) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }