package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Pink Balloon
	Map(s): 		Hidden-Street <Stage B>
	Description: 	
*/


class NPC2040045 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()//ExitChat
      } else if (mode == 0) {
         cm.sendOk(I18nMessage.from("2040045_WHO_WOULD_NOT"))

         cm.dispose()//No
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            cm.sendYesNo(I18nMessage.from("2040045_LIKE_TO_LEAVE"))

         } else {
            cm.warp(922011100, "st00")
            cm.dispose()
         }
      }
   }
}

NPC2040045 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040045(cm: cm))
   }
   return (NPC2040045) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }