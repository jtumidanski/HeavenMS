package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9220020 {
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
            if (!cm.isEventLeader()) {
               cm.sendNext("Please let your party leader talk to me for further instructions to proceed to the next stage.")
               cm.dispose()
               return
            }

            EventInstanceManager eim = cm.getEventInstance()
            if (eim.getIntProperty("statusStg1") == 1) {
               cm.sendNext("Go through this tunnel for the boss battle.")
            } else {
               if (cm.haveItem(4032118, 15)) {
                  cm.gainItem(4032118, (short) -15)

                  eim.setIntProperty("statusStg1", 1)
                  eim.showClearEffect()
                  eim.giveEventPlayersStageReward(1)

                  cm.sendNext("You got the letters, great! Now, you can proceed to the room MV is through this tunnel. Be prepared!")
               } else {
                  cm.sendNext("Please hand me #r15 secret letters#k.")
               }
            }

            cm.dispose()
         }
      }
   }
}

NPC9220020 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9220020(cm: cm))
   }
   return (NPC9220020) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }