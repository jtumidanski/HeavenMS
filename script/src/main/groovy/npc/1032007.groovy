package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1032007 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int cost = 5000

   def start() {
      cm.sendYesNo(I18nMessage.from("1032007_HELLO").with(cost))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0) {
            cm.sendNext(I18nMessage.from("1032007_BUSINESS_TO_TAKE_CARE_OF_HERE"))
            cm.dispose()
            return
         }
         status++
         if (status == 1) {
            if (cm.getMeso() >= cost && cm.canHold(4031045)) {
               cm.gainItem(4031045, (short) 1)
               cm.gainMeso(-cost)
               cm.dispose()
            } else {
               cm.sendOk(I18nMessage.from("1032007_NOT_ENOUGH_MESOS").with(cost))
               cm.dispose()
            }
         }
      }
   }
}

NPC1032007 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1032007(cm: cm))
   }
   return (NPC1032007) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }