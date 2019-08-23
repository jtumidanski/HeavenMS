package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2112018 {
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
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         EventInstanceManager eim = cm.getEventInstance()

         if (status == 0) {
            if (eim.getIntProperty("escortFail") == 1) {
               cm.sendNext("Thanks to you, we were capable of reunion once again. Yulete will now be forwarded to jail for attempt against the Law of Magatia. Once again, thank you.")
            } else {
               cm.sendNext("Thanks to you, we were capable of reunion once again. Yulete will now pass through rehabilitation, as his studies are invaluable for the growth of our town, and all his doings were being made because he was blinded by the greed for power, although it was for the sake of Magatia. Once again, thank you.")
            }
         } else {
            if (eim.giveEventReward(cm.getPlayer())) {
               cm.warp((eim.getIntProperty("isAlcadno") == 0) ? 261000011 : 261000021)
            } else {
               cm.sendOk("Please free a slot on one of your inventories before receiving your reward.")
            }

            cm.dispose()
         }
      }
   }
}

NPC2112018 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2112018(cm: cm))
   }
   return (NPC2112018) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }