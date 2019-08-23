package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9102101 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendYesNo("#b(I can see something covered in grass. Should I pull it out?)")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (!(mode == -1)) {
         if (mode == 0) {
            cm.sendNext("#b(I didn't think much of it, so I didn't touch it.)")
         } else if (mode == 1) {
            cm.sendNext("#b(Yuck... it's pet poop!)")
            cm.gainItem(4031922, (short) 1)
         }
      }
      cm.dispose()
   }
}

NPC9102101 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9102101(cm: cm))
   }
   return (NPC9102101) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }