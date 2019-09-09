package npc

import client.MapleBuffStat
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2111015 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   static def isPillUsed(ch) {
      return ch.getBuffSource(MapleBuffStat.HPREC) == 2022198
   }

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
            if (cm.isQuestStarted(3314) && !cm.haveItem(2022198, 1) && !isPillUsed(cm.getPlayer())) {
               if (cm.canHold(2022198, 1)) {
                  cm.gainItem(2022198, (short) 1)
                  cm.sendOk("You took the pills that were laying on the desk.", (byte) 2)
               } else {
                  cm.sendOk("You don't have a USE slot available to get Russellon's pills.", (byte) 2)
               }
            }

            cm.dispose()
         }
      }
   }
}

NPC2111015 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2111015(cm: cm))
   }
   return (NPC2111015) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }