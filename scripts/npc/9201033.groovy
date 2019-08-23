package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201033 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int smap = 681000000
   int hv = 209000000
   int tst, b2h

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (status == 0 && mode == 0) {
            cm.sendNext("Let me know if you've changed your mind!")
            cm.dispose()
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            if (cm.getMapId() == hv) {
               tst = 1 //to shalom temple
               cm.sendYesNo("The Shalom Temple is unlike any other place in Happyville, would you like to head to #bShalom Temple#k?")
               //not GMS lol
            } else if (cm.getMapId() == smap) {
               b2h = 1 //back to happyville
               cm.sendYesNo("Would you like to head back to Happyville?")
            }
         } else if (status == 1) {
            if (tst == 1) {
               cm.warp(smap, 0)
               cm.dispose()
            } else if (b2h == 1) {
               cm.warp(hv, 0)
               cm.dispose()
            }
         }
      }
   }
}

NPC9201033 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201033(cm: cm))
   }
   return (NPC9201033) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }