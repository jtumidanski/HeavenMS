package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201130 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int map = 677000008
   int quest = 28219
   int questItem = 4032493

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 1) {
         status++
      } else {
         cm.dispose()
         return
      }
      if (status == 0) {
         if (cm.isQuestStarted(quest)) {
            if (cm.haveItem(questItem)) {
               cm.sendYesNo(I18nMessage.from("9201130_WOULD_YOU_LIKE_TO_MOVE").with(map))
            } else {
               cm.sendOk(I18nMessage.from("9201130_HOLD_AN_EMBLEM"))
               cm.dispose()
            }
         } else {
            cm.sendOk(I18nMessage.from("9201130_STRANGE_FORCE"))
            cm.dispose()
         }
      } else {
         if (cm.haveItem(4032485, 1)) {
            cm.gainItem(4032485, (short) -1)
         }
         if (cm.haveItem(4001355, 1)) {
            cm.gainItem(4001355, (short) -1)
         }

         cm.warp(map, 0)
         cm.dispose()
      }
   }
}

NPC9201130 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201130(cm: cm))
   }
   return (NPC9201130) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }