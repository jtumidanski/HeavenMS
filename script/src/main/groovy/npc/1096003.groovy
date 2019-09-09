package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1096003 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 0 && type == 0) {
         status--
      } else if (mode == -1) {
         cm.dispose()
         return
      } else {
         status++
      }
      if (status == 0) {
         cm.sendDirectionInfo(4, 1096003)//else you will crash sending sendNext
         cm.sendDirectionInfo(3, 4)
         cm.sendNext("Ook! Ook!")
         cm.showIntro("Effect/Direction4.img/cannonshooter/face00")
      } else if (status == 1) {
         cm.unlockUI()
         cm.dispose()
      }
   }
}

NPC1096003 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1096003(cm: cm))
   }
   return (NPC1096003) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }