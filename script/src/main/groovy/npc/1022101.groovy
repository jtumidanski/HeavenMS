package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Rooney
	Map(s): 		
	Description: 	Happyville Warp NPC
*/


class NPC1022101 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            cm.sendYesNo("Santa told me to go to here, only he didn't told me when...  I hope I'm here on the right time! Oh! By the way, I'm Rooney, I can take you to #bHappyVille#k. Are you ready to go?")
         } else {
            cm.getPlayer().saveLocation("HAPPYVILLE")
            cm.warp(209000000, 0)
            cm.dispose()
         }
      }
   }
}

NPC1022101 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1022101(cm: cm))
   }
   return (NPC1022101) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }