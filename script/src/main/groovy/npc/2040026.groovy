package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Third Eos Rock
	Map(s): 		Ludibrium : Eos Tower 41st Floor
	Description: 	
*/


class NPC2040026 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int map

   def start() {
      if (cm.haveItem(4001020)) {
         cm.sendSimple("You can use #bEos Rock Scroll#k to activate #bThird Eos Rock#k. Which of these rocks would you like to teleport to?#b\r\n#L0#Second Eos Rock (71st Floor)#l\r\n#L1#Fourth Eos Rock (1st Floor)#l")
      } else {
         cm.sendOk("There's a rock that will enable you to teleport to #bSecond Eos Rock or Fourth Eos Rock#k, but it cannot be activated without the scroll.")
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (status == 1) {
            if (selection == 0) {
               cm.sendYesNo("You can use #bEos Rock Scroll#k to activate #bThird Eos Rock#k. Will you teleport to #bSecond Eos Rock#k at the 71st Floor?")
               map = 221022900
            } else {
               cm.sendYesNo("You can use #bEos Rock Scroll#k to activate #bThird Eos Rock#k. Will you teleport to #bFourth Eos Rock#k at the 1st Floor?")
               map = 221020000
            }
         } else if (status == 2) {
            cm.gainItem(4001020, (short) -1)
            cm.warp(map, map % 1000 == 900 ? 3 : 4)
            cm.dispose()
         }
      }
   }
}

NPC2040026 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040026(cm: cm))
   }
   return (NPC2040026) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }