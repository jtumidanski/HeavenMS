package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2040025 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int map = 221024400

   def start() {
      if (cm.haveItem(4001020)) {
         cm.sendSimple("You can use #bEos Rock Scroll#k to activate #bSecond Eos Rock#k. Which of these rocks would you like to teleport to?#b\r\n#L0#First Eos Rock (100th Floor)#l\r\n#L1#Third Eos Rock (41st Floor)#l")
      } else {
         cm.sendOk("There's a rock that will enable you to teleport to #bFirst Eos Rock or Third Eos Rock#k, but it cannot be activated without the scroll.")
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (status >= 0 && mode == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 1) {
            if (selection == 0) {
               cm.sendYesNo("You can use #bEos Rock Scroll#k to activate #bSecond Eos Rock#k. Will you teleport to #bFirst Eos Rock#k at the 100th Floor?")
            } else {
               cm.sendYesNo("You can use #bEos Rock Scroll#k to activate #bSecond Eos Rock#k. Will you teleport to #bThird Eos Rock#k at the 41st Floor?")
               map = 221021700
            }
         } else if (status == 2) {
            cm.gainItem(4001020, (short) -1)
            cm.warp(map, 3)
            cm.dispose()
         }
      }
   }
}

NPC2040025 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040025(cm: cm))
   }
   return (NPC2040025) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }