package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9220005 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = 0
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0) {
            cm.sendOk("Talk to me again when you want to.")
            cm.dispose()
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 1) {
            if (cm.getChar().getMapId() == 209000000) {
               cm.sendYesNo("Do you wish to head to where the #bSnow Sprinkler#k is?")
               status = 9
            } else if (cm.getChar().getMapId() == 209080000) {
               cm.sendYesNo("Do you wish to return to Happyville?")
               status = 19
            } else {
               cm.sendOk("You alright?")
               cm.dispose()
            }
         } else if (status == 10) {
            cm.warp(209080000, 0)
            cm.dispose()
         } else if (status == 20) {
            cm.warp(209000000, 0)
            cm.dispose()
         } else {
            cm.sendOk("You alrighty?")
            cm.dispose()
         }
      }
   }
}

NPC9220005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9220005(cm: cm))
   }
   return (NPC9220005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }