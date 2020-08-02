package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9270025 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int[] skin = [0, 1, 2, 3, 4]

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
            cm.sendSimple(I18nMessage.from("9270025_HELLO"))
         } else if (status == 1) {
            if (!cm.haveItem(5153010)) {
               cm.sendOk(I18nMessage.from("9270025_MISSING_SKIN_COUPON"))
               cm.dispose()
               return
            }
            cm.sendStyle("With our specialized service, you can see the way you'll look after the treatment in advance. What kind of a skin-treatment would you like to do? Go ahead and choose the style of your liking...", skin)
         } else {
            cm.gainItem(5153010, (short) -1)
            cm.setSkin(selection)
            cm.sendOk(I18nMessage.from("9270025_ENJOY_NEW_SKIN"))

            cm.dispose()
         }
      }
   }
}

NPC9270025 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9270025(cm: cm))
   }
   return (NPC9270025) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }