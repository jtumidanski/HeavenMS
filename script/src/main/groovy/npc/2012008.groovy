package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2012008 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int price = 1000000
   int[] skin = [0, 1, 2, 3, 4]

   def start() {
      cm.sendSimple(I18nMessage.from("2012008_HELLO"))
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
            cm.dispose()
            if (cm.haveItem(5153001)) {
               cm.gainItem(5153001, (short) -1)
               cm.setSkin(selection + 1)
               cm.sendOk(I18nMessage.from("2012008_ENJOY_NEW_SKIN"))
            } else {
               cm.sendOk(I18nMessage.from("2012008_MISSING_SKIN_COUPON"))
            }
         }
      }
   }
}

NPC2012008 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2012008(cm: cm))
   }
   return (NPC2012008) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }