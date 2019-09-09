package npc

import client.MapleJob
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201125 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int map = 101000003
   String job = "Magician"
   int jobType = 2
   String no = "Come back to me if you decided to be a #b" + job + "#k."

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.sendOk(no)
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            cm.sendOk(no)
            cm.dispose()
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            if (cm.getJob().getId() == MapleJob.BEGINNER.getId()) {
               if (cm.getLevel() >= 8 && cm.canGetFirstJob(jobType)) {
                  cm.sendYesNo("Hey #h #, I can send you to #b#m" + map + "##k if you want to be a #b" + job + "#k. Do you want to go now?")
               } else {
                  cm.sendOk("If you want to be a #b" + job + "#k, train yourself further until you reach #blevel 8, " + cm.getFirstJobStatRequirement(jobType) + "#k.")
                  cm.dispose()
               }
            } else {
               cm.sendOk("You're much stronger now. Keep training!")
               cm.dispose()
            }
         } else if (status == 1) {
            cm.warp(map, 0)
            cm.dispose()
         }
      }
   }
}

NPC9201125 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201125(cm: cm))
   }
   return (NPC9201125) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }