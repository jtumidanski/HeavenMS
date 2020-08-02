package npc


import scripting.npc.NPCConversationManager
import tools.MessageBroadcaster
import tools.ServerNoticeType
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2091009 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || (mode == 0 && status == 0)) {
         cm.dispose()
         return
      } else if (mode == 0) {
         status--
      } else {
         status++
      }



      if (status == 0) {
         cm.sendGetText(I18nMessage.from("2091009_SEALED_SHRINE"))
      } else if (status == 1) {
         if (cm.getWarpMap(925040100).countPlayers() > 0) {
            cm.sendOk(I18nMessage.from("2091009_SOMEONE_IS_ALREADY_ATTENDING"))
            cm.dispose()
            return
         }
         if (cm.getText() == "Actions speak louder than words") {
            if (cm.isQuestStarted(21747) && cm.getQuestProgressInt(21747, 9300351) == 0) {
               cm.warp(925040100, 0)
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(cm.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("CORRECT_BUT_FORCES_BLOCKING"))
            }

            cm.dispose()
         } else {
            cm.sendOk(I18nMessage.from("2091009_WRONG"))
         }
      } else if (status == 2) {
         cm.dispose()
      }
   }
}

NPC2091009 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2091009(cm: cm))
   }
   return (NPC2091009) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }