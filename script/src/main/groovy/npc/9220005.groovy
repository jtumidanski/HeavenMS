package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9220005 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = 0
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0) {
            cm.sendOk(I18nMessage.from("9220005_WHEN_YOU_WANT_TO"))
            cm.dispose()
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 1) {
            if (cm.getChar().getMapId() == 209000000) {
               cm.sendYesNo(I18nMessage.from("9220005_WISH_TO_HEAD_WHERE"))
               status = 9
            } else if (cm.getChar().getMapId() == 209080000) {
               cm.sendYesNo(I18nMessage.from("9220005_RETURN_TO_HAPPYVILLE"))
               status = 19
            } else {
               cm.sendOk(I18nMessage.from("9220005_YOU_ALRIGHT"))
               cm.dispose()
            }
         } else if (status == 10) {
            cm.warp(209080000, 0)
            cm.dispose()
         } else if (status == 20) {
            cm.warp(209000000, 0)
            cm.dispose()
         } else {
            cm.sendOk(I18nMessage.from("9220005_YOU_ALRIGHT"))
            cm.dispose()
         }
      }
   }
}

NPC9220005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9220005(cm: cm))
   }
   return (NPC9220005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }