package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1202009 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (cm.haveItemWithId(1902016, true)) {
         cm.warp(140010210, 0)
      } else {
         cm.sendOk("What is it? If you you're here to waste my time, get lost!")
      }

      cm.dispose()
   }
}

NPC1202009 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1202009(cm: cm))
   }
   return (NPC1202009) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }