package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1052114 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int map = 910310000
   int num = 5
   int maxPlayerCount = 5

   def start() {
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 1) {
         status++
      } else {
         if (status <= 1) {
            cm.dispose()
            return
         }
         status--
      }
      if (status == 0) {
         if (cm.getLevel() >= 20) {
            cm.sendOk(I18nMessage.from("1052114_LEVEL_REQUIREMENT"))
            cm.dispose()
            return
         }

         String selStr = "Would you like to go into the Training Center?"
         for (def i = 0; i < num; i++) {
            selStr += "\r\n#b#L" + i + "#Training Center " + i + " (" + cm.getPlayerCount(map + i) + "/" + maxPlayerCount + ")#l#k"
         }
         cm.sendSimple(selStr)
      } else if (status == 1) {
         if (selection < 0 || selection >= num) {
            cm.dispose()
         } else if (cm.getPlayerCount(map + selection) >= maxPlayerCount) {
            cm.sendNext(I18nMessage.from("1052114_FULL"))
            status = -1
         } else {
            cm.warp(map + selection, 0)
            cm.dispose()
         }
      }
   }
}

NPC1052114 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1052114(cm: cm))
   }
   return (NPC1052114) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }