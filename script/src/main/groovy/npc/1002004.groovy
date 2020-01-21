package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		VIP Cab
	Map(s): 		Victoria Road : Lith Harbor (104000000)
	Description:		Takes you places
*/
class NPC1002004 {
   NPCConversationManager cm
   int status = 0
   int sel = -1
   int cost = 10000

   def start() {
      cm.sendNext(I18nMessage.from("1002004_HELLO"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode == -1) {
         cm.dispose()
         return
      } else if (mode == 0) {
         cm.sendOk(I18nMessage.from("1002004_TOWN_HAS_A_LOT_TO_OFFER"))
         cm.dispose()
         return
      }
      if (status == 1) {
         cm.sendYesNo(cm.getJobId() == 0 ? I18nMessage.from("1002004_BEGINNER_DISCOUNT") : I18nMessage.from("1002004_REGULAR"))
         cost /= ((cm.getJobId() == 0) ? 10 : 1)
      } else if (status == 2) {
         if (cm.getMeso() < cost) {
            cm.sendNext(I18nMessage.from("1002004_NOT_ENOUGH_MESO"))
         } else {
            cm.gainMeso(-cost)
            cm.warp(105070001)
         }
         cm.dispose()
      }
   }
}

NPC1002004 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1002004(cm: cm))
   }
   return (NPC1002004) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }