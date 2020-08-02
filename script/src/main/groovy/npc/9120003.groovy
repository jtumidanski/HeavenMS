package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9120003 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int price = 300

   def start() {
      cm.sendYesNo(I18nMessage.from("9120003_ENTER_BATHHOUSE").with(price))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         if (mode == 0) {
            cm.sendOk(I18nMessage.from("9120003_COME_BACK_SOME_OTHER_TIME"))
         }
         cm.dispose()
         return
      }
      if (cm.getMeso() < price) {
         cm.sendOk(I18nMessage.from("9120003_CHECK_AND_SEE").with(price))
      } else {
         cm.gainMeso(-price)
         cm.warp(801000100 + 100 * cm.getPlayer().getGender(), "out00")
      }
      cm.dispose()
   }
}

NPC9120003 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9120003(cm: cm))
   }
   return (NPC9120003) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }