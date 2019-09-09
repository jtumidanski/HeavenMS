package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201105 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.getMapId() == 610020005) {
         cm.sendOk("The Crimsonwood Keep lies right ahead, a great feat has been made by you this day, salute to thee. Pass through these woods to enter the gates of the Keep.")
      } else {
         cm.sendOk("So far your progress is splendid, good job. However, to make it to the Keep, you must face and accomplish this ordeal, carry on.")
      }
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9201105 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201105(cm: cm))
   }
   return (NPC9201105) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }