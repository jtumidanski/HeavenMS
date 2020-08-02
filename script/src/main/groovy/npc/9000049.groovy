package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9000049 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int stage = 1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 0) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0 && mode == 1) {
            if (cm.getPlayer().isGM()) {
               String event = "CLOSED"
               int
               stage = cm.getClient().getChannelServer().getStoredVar(9000049)
               if (stage == 1) {
                  event = "EASY"
               }
               if (stage == 2) {
                  event = "MEDIUM"
               }
               if (stage == 3) {
                  event = "HARD"
               }
               cm.sendSimple(I18nMessage.from("9000049_HELLO").with(event))
            } else {
               int stage = cm.getClient().getChannelServer().getStoredVar(9000049)
               if (stage == 0) {
                  cm.sendOk(I18nMessage.from("9000049_TOWER_IS_NOT_UNLOCKED"))
               } else {
                  cm.warp(980040000 + stage * 1000, 0)
               }
               cm.dispose()
            }
         } else if (status == 1 && cm.getPlayer().isGM()) {
            if (selection == 0) {
               int stage = cm.getClient().getChannelServer().getStoredVar(9000049)
               if (stage == 0) {
                  cm.sendOk(I18nMessage.from("9000049_TOWER_IS_NOT_UNLOCKED"))
               } else {
                  cm.warp(980040000 + stage * 1000, 0)
               }
               cm.dispose()
               return
            }
            cm.getClient().getChannelServer().setStoredVar(9000049, selection - 1)
            cm.dispose()
         } else {
            cm.dispose()
         }
      }
   }
}

NPC9000049 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9000049(cm: cm))
   }
   return (NPC9000049) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }