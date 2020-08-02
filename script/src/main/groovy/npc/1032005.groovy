package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1032005 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int cost = 10000

   def start() {
      cm.sendNext(I18nMessage.from("1032005_HELLO"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode == -1) {
         cm.dispose()
         return
      } else if (mode == 0) {
         cm.sendOk(I18nMessage.from("1032005_THIS_TOWN_HAS_A_LOT_TO_OFFER"))
         cm.dispose()
         return
      }
      if (status == 1) {
         if (cm.getJobId() == 0) {
            cm.sendYesNo(I18nMessage.from("1032005_BEGINNER_SPECIAL"))
         } else {
            cm.sendYesNo(I18nMessage.from("1032005_NON_BEGINNER"))
         }
         cost /= ((cm.getJobId() == 0) ? 10 : 1)
      } else if (status == 2) {
         if (cm.getMeso() < cost) {
            cm.sendNext(I18nMessage.from("1032005_NOT_ENOUGH_MESOS"))
         } else {
            cm.gainMeso(-cost)
            cm.warp(105070001)
         }
         cm.dispose()
      }
   }
}

NPC1032005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1032005(cm: cm))
   }
   return (NPC1032005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }