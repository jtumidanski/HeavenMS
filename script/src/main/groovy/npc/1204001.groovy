package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Francis
	Map(s): 		
	Description: 	
*/


class NPC1204001 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || mode == 0 && type > 0) {
         cm.dispose()
         return
      }

      if (mode == 1) {
         status++
      } else {
         status--
      }
      if (status == 0) {
         cm.sendNext("I'm Francis, the Puppeteer of the Black Wings. How dare you disturb my puppets... It really upsets me, but I'll let it slide this time. If I catch you doing it again though, I swear in the name of the Black Mage, I will make you pay for it.", (byte) 9)
      } else if (status == 1) {
         cm.sendNextPrev("#b(The Black Wings? Huh? Who are they? And how is all this related to the Black Mage? Hm, maybe you should report this info to Tru.)#k", (byte) 3)
      } else if (status == 2) {
         cm.completeQuest(21719)
         cm.warp(105040200, 10)//104000004
         cm.dispose()
      }
   }
}

NPC1204001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1204001(cm: cm))
   }
   return (NPC1204001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }