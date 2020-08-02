package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		El Nath Magic Spot
	Map(s): 		Orbis Tower <20th Floor>
	Description: 	
*/


class NPC2012015 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(4001019)) {
         cm.sendYesNo(I18nMessage.from("2012015_WILL_YOU_TELEPORT"))
      } else {
         cm.sendOk(I18nMessage.from("2012015_NEED_THE_SCROLL"))
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode > 0) {
         cm.gainItem(4001019, (short) -1)
         cm.warp(200080200, 0)
      }
      cm.dispose()
   }
}

NPC2012015 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2012015(cm: cm))
   }
   return (NPC2012015) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }