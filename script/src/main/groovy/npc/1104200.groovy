package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1104200 {
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
            cm.sendNext(I18nMessage.from("1104200_YOU_TRAPPED_ME"))
         } else if (status == 1) {
            cm.sendYesNo(I18nMessage.from("1104200_YOU_READY_TO_FACE_ELEANOR"))
         } else if (status == 2) {
            if (cm.getWarpMap(913030000).countPlayers() == 0) {
               cm.warp(913030000, 0)
            } else {
               cm.sendOk(I18nMessage.from("1104200_SOMEONE_ALREADY_CHALLENGING"))
            }

            cm.dispose()
         }
      }
   }
}

NPC1104200 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1104200(cm: cm))
   }
   return (NPC1104200) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }