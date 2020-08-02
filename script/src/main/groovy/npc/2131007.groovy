package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2131007 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int exchangeItem = 4000438

   def start() {
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 1) {
         status++
      } else {
         cm.dispose()
         return
      }
      if (status == 0) {
         cm.sendSimple(I18nMessage.from("2131007_MONSTERS_ARE_A_PIECE_OF_CAKE"))
      } else if (status == 1) {
         if (!cm.haveItem(exchangeItem, 100)) {
            cm.sendNext(I18nMessage.from("2131007_NEED_AT_LEAST_100"))
            cm.dispose()
         } else {
            double itemQuantity = cm.itemQuantity(exchangeItem) / 100
            cm.sendGetNumber(I18nMessage.from("2131007_PERFECT_PITCH").with(exchangeItem, exchangeItem))
         }
      } else if (status == 2) {
         if (selection >= 1 && selection <= cm.itemQuantity(exchangeItem) / 100) {
            if (!cm.canHold(4310000, selection)) {
               cm.sendOk(I18nMessage.from("2131007_MAKE_SOME_ETC_SPACE"))
            } else {
               cm.gainItem(4310000, (short) selection)
               cm.gainItem(exchangeItem, (short) -(selection * 100))
               cm.sendOk(I18nMessage.from("2131007_THANKS"))
            }
         }
         cm.dispose()
      }
   }
}

NPC2131007 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2131007(cm: cm))
   }
   return (NPC2131007) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }