package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage
import tools.SimpleMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1032113 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int map = 910120000
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
            cm.sendOk(I18nMessage.from("1032113_LEVEL_REQUIREMENT"))
            cm.dispose()
            return
         }

         String selStr = I18nMessage.from("1032113_WOULD_YOU_LIKE_TO_GO").to(cm.getClient()).evaluate()
         for (def i = 0; i < num; i++) {
            selStr += "\r\n#b#L" + i + "#Training Center " + i + " (" + cm.getPlayerCount(map + i) + "/" + maxPlayerCount + ")#l#k"
         }
         cm.sendSimple(SimpleMessage.from(selStr))
      } else if (status == 1) {
         if (selection < 0 || selection >= num) {
            cm.dispose()
         } else if (cm.getPlayerCount(map + selection) >= maxPlayerCount) {
            cm.sendNext(I18nMessage.from("1032113_FULL"))
            status = -1
         } else {
            cm.warp(map + selection, 0)
            cm.dispose()
         }
      }
   }
}

NPC1032113 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1032113(cm: cm))
   }
   return (NPC1032113) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }