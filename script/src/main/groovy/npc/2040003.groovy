package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2040003 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int entry

   def start() {
      if (cm.getPlayer().getMapId() == 922000000) {
         entry = 0
         cm.sendYesNo(I18nMessage.from("2040003_QUIT_THIS_STAGE"))
         status++
      } else if (cm.isQuestStarted(3239)) {
         entry = 1
         cm.sendYesNo(I18nMessage.from("2040003_DO_YOU_WANT_TO_ENTER"))
         status++
      } else {
         cm.sendOk(I18nMessage.from("2040003_ACCESS_RESTRICTED"))
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (status == 1) {
         if (entry == 0) {
            if (mode <= 0) {
               cm.sendOk(I18nMessage.from("2040003_CALL_ME"))
               cm.dispose()
               return
            }

            cm.warp(922000009, 0)
            if (!(cm.isQuestStarted(3239) && cm.haveItem(4031092, 10))) {
               cm.removeAll(4031092)
            }
            cm.dispose()
         } else {
            if (mode <= 0) {
               cm.dispose()
               return
            }

            if (cm.getWarpMap(922000000).countPlayers() == 0) {
               cm.warp(922000000, 0)
               if (!(cm.isQuestStarted(3239) && cm.haveItem(4031092, 10))) {
                  cm.removeAll(4031092)
               }
            } else {
               cm.sendOk(I18nMessage.from("2040003_SOMEONE_ALREADY_ATTEMPTING"))
            }

            cm.dispose()
         }
      }
   }
}

NPC2040003 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040003(cm: cm))
   }
   return (NPC2040003) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }