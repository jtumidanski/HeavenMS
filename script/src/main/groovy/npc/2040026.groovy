package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Third Eos Rock
	Map(s): 		Ludibrium : Eos Tower 41st Floor
	Description: 	
*/


class NPC2040026 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int map

   def start() {
      if (cm.haveItem(4001020)) {
         cm.sendSimple(I18nMessage.from("2040026_CHOICES"))
      } else {
         cm.sendOk(I18nMessage.from("2040026_NEED_SCROLL"))
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (status == 1) {
            if (selection == 0) {
               cm.sendYesNo(I18nMessage.from("2040026_TO_71"))
               map = 221022900
            } else {
               cm.sendYesNo(I18nMessage.from("2040026_TO_1"))
               map = 221020000
            }
         } else if (status == 2) {
            cm.gainItem(4001020, (short) -1)
            cm.warp(map, map % 1000 == 900 ? 3 : 4)
            cm.dispose()
         }
      }
   }
}

NPC2040026 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040026(cm: cm))
   }
   return (NPC2040026) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }