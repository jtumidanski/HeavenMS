package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9000002 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else if (mode == 0) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            cm.sendNext("Bam bam bam bam!! You have won the game from the \r\n#bEVENT#k. Congratulations on making it this far!")
         } else if (status == 1) {
            cm.sendNext("You'll be awarded the #bScroll of Secrets#k as the winning prize. On the scroll, it has secret information written in ancient characters.")
         } else if (status == 2) {
            cm.sendNext("The Scroll of Secrets can be deciphered by #rChun Ji#k or \r\n#rGeanie#k at Ludibrium. Bring it with you and something good's bound to happen.")
         } else if (status == 3) {
            if (cm.canHold(4031019)) {
               cm.gainItem(4031019)
               cm.warp(cm.getPlayer().getSavedLocation("EVENT"))
               cm.dispose()
            } else {
               cm.sendNext("I think your Etc window is full. Please make room, then talk to me.")
            }
         } else if (status == 4) {
            cm.dispose()
         }
      }
   }
}

NPC9000002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9000002(cm: cm))
   }
   return (NPC9000002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }