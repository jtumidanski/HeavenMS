package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager
import tools.PacketCreator
import tools.packet.showitemgaininchat.ShowSpecialEffect

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
         cm.sendGetText(I18nMessage.from("magatiaPassword_THE_DOOR_REACTS"))
      } else if (status == 1) {
         if (cm.getText() == cm.getQuestProgress(3360)) {
            cm.setQuestProgress(3360, 1)
            PacketCreator.announce(cm.getPlayer(), new ShowSpecialEffect(7))
            cm.warp(261030000, "sp_" + ((cm.getMapId() == 261010000) ? "jenu" : "alca"))
         } else {
            cm.sendOk(I18nMessage.from("magatiaPassword_WRONG"))
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