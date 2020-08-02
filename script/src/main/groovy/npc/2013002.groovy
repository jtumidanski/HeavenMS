package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2013002 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (cm.getPlayer().getMapId() == 920010100) { //Center tower
            if (status == 0) {
               cm.sendYesNo(I18nMessage.from("2013002_LIFTED_SPELL"))
            } else if (status == 1) {
               cm.warp(920011300, 0)
               cm.dispose()
            }

         } else if (cm.getPlayer().getMapId() == 920011100) {
            if (status == 0) {
               cm.sendYesNo(I18nMessage.from("2013002_READY_TO_EXIT"))
            } else if (status == 1) {
               cm.warp(920011300, 0)
               cm.dispose()
            }

         } else if (cm.getPlayer().getMapId() == 920011300) {
            if (status == 0) {
               cm.sendNext(I18nMessage.from("2013002_THANK_YOU"))
            } else if (status == 1) {
               if (cm.getEventInstance().giveEventReward(cm.getPlayer())) {
                  cm.warp(200080101, 0)
                  cm.dispose()
               } else {
                  cm.sendOk(I18nMessage.from("2013002_MAKE_ROOM"))
                  cm.dispose()
               }
            }
         }
      }
   }
}

NPC2013002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2013002(cm: cm))
   }
   return (NPC2013002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }