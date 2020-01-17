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


class NPC9201114 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(3992041, 1)) {
         cm.warp(610030020, "out00")
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(cm.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("GIANT_GATE_NO_BUDGE"))
      }

      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9201114 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201114(cm: cm))
   }
   return (NPC9201114) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }