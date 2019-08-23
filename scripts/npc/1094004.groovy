package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1094004 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int rolled = 0

   def start() {
      if (!cm.isQuestStarted(2186)) {
         cm.sendOk("Just a pile of boxes, nothing special...")
         cm.dispose()
         return
      }

      cm.sendNext("Do you want to obtain a glasses?")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (!(cm.haveItem(4031853) || cm.haveItem(4031854) || cm.haveItem(4031855))) {
         rolled = Math.floor(Math.random() * 3).intValue()

         if (rolled == 0) {
            cm.gainItem(4031853, (short) 1)
         } else if (rolled == 1) {
            cm.gainItem(4031854, (short) 1)
         } else {
            cm.gainItem(4031855, (short) 1)
         }
      } else {
         cm.sendOk("You #balready have#k the glasses that was here!")
      }

      cm.dispose()
   }
}

NPC1094004 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1094004(cm: cm))
   }
   return (NPC1094004) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }