package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Jack
	Map(s): 		Nautilus' Port
	Description: 	
*/


class NPC1092010 {
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
            if (!cm.haveItem(4220153)) {
               cm.sendOk("(Scratch scratch...)")
               cm.dispose()
            } else {
               cm.sendYesNo("Hey, nice #bTreasure Map#k you have there? #rCan I keep it#k for the Nautilus crew, if you don't need it any longer?")
            }
         } else if (status == 1) {
            cm.gainItem(4220153, (short) -1)
            cm.dispose()
         }
      }
   }
}

NPC1092010 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1092010(cm: cm))
   }
   return (NPC1092010) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }