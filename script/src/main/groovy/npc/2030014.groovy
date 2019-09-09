package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Ancient Icy Stone
	Map(s): 		
	Description: 	4th job I/L skill
*/


class NPC2030014 {
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
            if (cm.haveItem(4031450, 1)) {
               if (cm.canHold(2280011, 1)) {
                  cm.gainItem(2280011, (short) 1)
                  cm.gainItem(4031450, (short) -1)
               }
            }
            cm.dispose()
         }
      }
   }
}

NPC2030014 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2030014(cm: cm))
   }
   return (NPC2030014) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }