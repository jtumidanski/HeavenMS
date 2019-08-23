package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2091009 {
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
         cm.sendGetText("The entrance of the Sealed Shrine... #bPassword#k!")
      } else if (status == 1) {
         if (cm.getWarpMap(925040100).countPlayers() > 0) {
            cm.sendOk("Someone is already attending the Sealed Shrine.")
            cm.dispose()
            return
         }
         if (cm.getText() == "Actions speak louder than words") {
            if (cm.isQuestStarted(21747) && cm.getQuestProgress(21747, 9300351) == 0) {
               cm.warp(925040100, 0)
            } else {
               cm.playerMessage(5, "Although you said the right answer, some mysterious forces is blocking the way in.")
            }

            cm.dispose()
         } else {
            cm.sendOk("#rWrong!")
         }
      } else if (status == 2) {
         cm.dispose()
      }
   }
}

NPC2091009 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2091009(cm: cm))
   }
   return (NPC2091009) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }