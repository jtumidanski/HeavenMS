package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1052011 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendNext(I18nMessage.from("1052011_DEVICE_CONNECTED_OUTSIDE"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else if (mode == 0) {
         cm.sendOk(I18nMessage.from("1052011_SEE_YOU_NEXT_TIME"))
         cm.dispose()
      } else {
         status++
         if (status == 1) {
            cm.sendNextPrev(I18nMessage.from("1052011_GIVE_UP_AND_LEAVE"))
         } else if (status == 2) {
            cm.sendYesNo(I18nMessage.from("1052011_START_FROM_SCRATCH"))
         } else if (status == 3) {
            cm.warp(103000100, 0)
            cm.dispose()
         }
      }
   }
}

NPC1052011 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1052011(cm: cm))
   }
   return (NPC1052011) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }