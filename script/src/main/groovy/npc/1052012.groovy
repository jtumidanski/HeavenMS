package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1052012 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            cm.sendYesNo("So, are you going to use the Internet Cafe? There is a fee to use the spaces there, that is #b5,000 mesos#k. Are you going to enter the Cafe?")
         } else if (status == 1) {
            if (cm.getMeso() < 5000) {
               cm.sendOk("Oh, you don't have the money, right? Sorry, I can't let you in.")
            } else {
               cm.gainMeso(-5000)
               cm.warp(193000000)
            }

            cm.dispose()
         }
      }
   }
}

NPC1052012 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1052012(cm: cm))
   }
   return (NPC1052012) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }