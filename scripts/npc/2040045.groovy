package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Pink Balloon
	Map(s): 		Hidden-Street <Stage B>
	Description: 	
*/


class NPC2040045 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()//ExitChat
      } else if (mode == 0) {
         cm.sendOk("Wise choice. Who wouldn't want free mesos from the #bBonus Stage#k?")
         cm.dispose()//No
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            cm.sendYesNo("Would you like to leave the bonus stage?")
         } else {
            cm.warp(922011100)
            cm.dispose()
         }
      }
   }
}

NPC2040045 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040045(cm: cm))
   }
   return (NPC2040045) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }