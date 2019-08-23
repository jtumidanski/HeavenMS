package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPCMagatiaPassword {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || (mode == 0 && status == 0)) {
         cm.dispose()
         return
      } else if (mode == 0) {
         status--
      } else {
         status++
      }

      if (status == 0) {
         cm.sendGetText("The door reacts to the entry pass inserted. #bPassword#k!")
      } else if (status == 1) {
         if (cm.getText() == cm.getStringQuestProgress(3360, 0)) {
            cm.setQuestProgress(3360, 1, 1)
            cm.warp((cm.getMapId() == 261010000) ? 261020200 : 261010000, "secret00")
         } else {
            cm.sendOk("#rWrong!")
         }

         cm.dispose()
      }
   }
}

NPCMagatiaPassword getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPCMagatiaPassword(cm: cm))
   }
   return (NPCMagatiaPassword) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }