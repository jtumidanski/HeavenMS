package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9270018 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int k2s
   int airport
   int s2k

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
         return
      }
      if (mode == 1) {
         status++
      }
      if (mode == 0) {
         if (cm.getMapId() == 540010101) {
            cm.sendOk(I18nMessage.from("9270018_HOLD_ON"))
            cm.dispose()
            return
         } else {
            cm.sendOk(I18nMessage.from("9270018_HOLD_ON_KERNING_CITY"))
            cm.dispose()
            return
         }
      }
      if (status == 0) {
         if (cm.getMapId() == 540010001) {
            cm.sendYesNo(I18nMessage.from("9270018_ARE_YOU_SURE"))
            airport = 1
         } else if (cm.getMapId() == 540010002) {
            cm.sendOk(I18nMessage.from("9270018_SIT_AND_WAIT"))
            cm.dispose()
            s2k = 1
         } else if (cm.getMapId() == 540010101) {
            cm.sendOk(I18nMessage.from("9270018_SIT_AND_WAIT_SINGAPORE"))
            cm.dispose()
            k2s = 1
         }
      } else if (status == 1) {
         if (k2s == 1) {
            cm.warp(103000000)
            cm.sendOk(I18nMessage.from("9270018_SEE_YOU_AGAIN"))
            cm.dispose()
         } else if (airport == 1) {
            cm.warp(540010000)
            cm.sendOk(I18nMessage.from("9270018_SEE_YOU_AGAIN"))
            cm.dispose()
         } else if (s2k == 1) {
            cm.warp(540010000)
            cm.sendOk(I18nMessage.from("9270018_SEE_YOU_AGAIN"))
            cm.dispose()
         }
      }
   }
}

NPC9270018 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9270018(cm: cm))
   }
   return (NPC9270018) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }