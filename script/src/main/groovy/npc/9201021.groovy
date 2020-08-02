package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201021 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.getMapId() != 680000401) {
         cm.sendSimple(I18nMessage.from("9201021_HELLO"))
      } else {
         cm.sendSimple(I18nMessage.from("9201021_HELLO_RETURN"))
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.sendOk(I18nMessage.from("9201021_GOOD_BYE"))
         cm.dispose()
         return
      }
      if (mode == 1) {
         status++
      } else {
         status--
      }
      if (status == 1) {
         if (selection < 1) {
            if (!cm.haveItem(4000313, 1)) {
               cm.sendOk(I18nMessage.from("9201021_SEEMS_LIKE_YOU_LOST"))
               cm.dispose()
               return
            }

            cm.warp(680000400, 0)
         } else if (selection < 2) {
            if (cm.haveItem(4031217, 7)) {
               cm.gainItem(4031217, (short) -7)
               cm.warp(680000401, 0)
            } else {
               cm.sendOk(I18nMessage.from("9201021_MISSING_THE_7_KEYS"))
            }
         } else if (selection > 1) {
            if (cm.getMapId() != 680000401) {
               cm.warp(680000500, 0)
               cm.sendOk(I18nMessage.from("9201021_GOOD_BYE"))
            } else {
               cm.warp(680000400, 0)
            }
         }

         cm.dispose()
      }
   }
}

NPC9201021 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201021(cm: cm))
   }
   return (NPC9201021) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }