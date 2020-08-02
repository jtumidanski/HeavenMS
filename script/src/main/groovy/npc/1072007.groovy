package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1072007 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   boolean completed

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
            if (cm.haveItem(4031013, 30)) {
               completed = true
               cm.sendOk(I18nMessage.from("1072007_TRUE_HERO"))
            } else {
               completed = false
               cm.sendSimple(I18nMessage.from("1072007_COLLECT_MARBLES"))
            }
         } else if (status == 1) {
            if (completed) {
               cm.removeAll(4031013)
               cm.completeQuest(100010)
               cm.startQuest(100011)
               cm.gainItem(4031012)
            }

            cm.warp(102040000, 9)
            cm.dispose()
         }
      }
   }
}

NPC1072007 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1072007(cm: cm))
   }
   return (NPC1072007) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }