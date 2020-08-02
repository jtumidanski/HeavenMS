package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Orbis Magic Spot
	Map(s): 		Orbis Tower <1st Floor>
	Description: 	
*/


class NPC2012014 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(4001019)) {
         cm.sendYesNo(I18nMessage.from("2012014_WILL_YOU_TELEPORT"))
      } else {
         cm.sendOk(I18nMessage.from("2012014_NEED_THE_SCROLL"))
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode > 0) {
         cm.gainItem(4001019, (short) -1)
         cm.warp(200082100, 0)
      }
      cm.dispose()
   }
}

NPC2012014 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2012014(cm: cm))
   }
   return (NPC2012014) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }