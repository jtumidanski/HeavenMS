package npc


import scripting.npc.NPCConversationManager

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
         cm.sendYesNo("You can use #b#t4001019##k to activate #b#p2012014##k. Will you teleport to where #b#p2012015##k is?")
      } else {
         cm.sendOk("There's a #b#p2012015##k that'll enable you to teleport to where #b#p2012014##k is, but you can't activate it without the scroll.")
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