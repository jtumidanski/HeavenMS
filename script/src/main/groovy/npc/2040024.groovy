package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		First Eos Rock
	Map(s): 		Ludibrium : Eos Tower 100th Floor
	Description: 	
*/


class NPC2040024 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(4001020)) {
         cm.sendYesNo("You can use #bEos Rock Scroll#k to activate #bFirst Eos Rock#k. Will you teleport to #bSecond Eos Rock#k at the 71st floor?")
      } else {
         cm.sendOk("There's a rock that will enable you to teleport to #bSecond Eos Rock#k, but it cannot be activated without the scroll.")
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (!(mode < 1)) {
         cm.gainItem(4001020, (short) -1)
         cm.warp(221022900, 3)
      }
      cm.dispose()
   }
}

NPC2040024 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040024(cm: cm))
   }
   return (NPC2040024) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }