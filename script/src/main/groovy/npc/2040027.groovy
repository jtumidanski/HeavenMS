package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Fourth Eos Rock
	Map(s): 		Ludibrium : Eos Tower 1st Floor
	Description: 	
*/


class NPC2040027 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(4001020)) {
         cm.sendYesNo("You can use #bEos Rock Scroll#k to activate #bFourth Eos Rock#k. Will you head over to #bThird Eos Rock#k at the 41st floor?")
      } else {
         cm.sendOk("There's a rock that will enable you to teleport to #bThird Eos Rock#k, but it cannot be activated without the scroll.")
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (!(mode < 1)) {
         cm.gainItem(4001020, (short) -1)
         cm.warp(221021700, 3)
      }
      cm.dispose()
   }
}

NPC2040027 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040027(cm: cm))
   }
   return (NPC2040027) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }