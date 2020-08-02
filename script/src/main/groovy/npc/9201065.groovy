package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201065 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int price = 1000000
   int[] skin = [0, 1, 2, 3, 4]

   def start() {
      cm.sendSimple(I18nMessage.from("9201065_HELLO"))
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
         if (status == 1) {
            if (selection == 2) {
               cm.sendStyle("With our specialized machine, you can see the way you'll look after the treatment PRIOR to the procedure. What kind of a look are you looking for? Go ahead and choose the style of your liking~!", skin)
            }
         } else if (status == 2) {
            if (cm.haveItem(5153009)) {
               cm.gainItem(5153009, (short) -1)
               cm.setSkin(skin[selection])
               cm.sendOk(I18nMessage.from("9201065_ENJOY_NEW_SKIN"))
            } else {
               cm.sendOk(I18nMessage.from("9201065_MISSING_SKIN_COUPON"))
            }

            cm.dispose()
         }
      }
   }
}

NPC9201065 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201065(cm: cm))
   }
   return (NPC9201065) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }