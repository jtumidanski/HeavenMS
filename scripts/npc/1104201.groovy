package npc

import scripting.npc.NPCConversationManager

import java.awt.*

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1104201 {
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
            if (!(cm.isQuestCompleted(20407) || cm.isQuestStarted(20407) && cm.getQuestProgress(20407, 9001010) != 0) && cm.getMap().countMonster(9001010) == 0 && cm.getMap().getNPCById(1104002) == null) {
               cm.sendOk("... Hnngh... #b#h0##k, is that you...? #r#p1104002##k... She's already here... #b#h0##k, I'm truly sorry I can't help you right now in this state, just when a bigger threat appeared I could do nothing for my people.... Please I beg you, please defeat her, #b#h0##k!! ....")
               cm.spawnNpc(1104002, new Point(850, 0), cm.getMap())
            } else {
               cm.sendOk("...")
            }

            cm.dispose()
         }
      }
   }
}

NPC1104201 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1104201(cm: cm))
   }
   return (NPC1104201) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }