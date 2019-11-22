package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2111018 {
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
            if (cm.isQuestStarted(3339)) {
               int progress = cm.getQuestProgressInt(23339, 1)

               if (progress == 3) {
                  cm.sendGetText("The pipe reacts as the water starts flowing. A secret compartment with a keypad shows up. #bPassword#k!")
               } else if (progress == 2) {
                  cm.setQuestProgress(23339, 1, 3)
                  cm.sendGetText("The pipe reacts as the water starts flowing. A secret compartment with a keypad shows up. #bPassword#k!")
               } else if (progress < 3) {
                  cm.setQuestProgress(23339, 1, 0)
                  cm.dispose()
               } else {
                  cm.warp(261000001, 1)
                  cm.dispose()
               }
            } else {
               if (cm.isQuestCompleted(3339)) {
                  cm.warp(261000001, 1)
               }

               cm.dispose()
            }
         } else if (status == 1) {
            if (cm.getText() == "my love Phyllia") {
               cm.setQuestProgress(23339, 1, 4)
               cm.warp(261000001, 1)
               cm.dispose()
            } else {
               cm.sendOk("#rWrong!")
               cm.dispose()
            }
         }
      }
   }
}

NPC2111018 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2111018(cm: cm))
   }
   return (NPC2111018) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }