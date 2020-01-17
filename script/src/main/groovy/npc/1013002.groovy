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


class NPC1013002 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.forceCompleteQuest(22011)
      MessageBroadcaster.getInstance().sendServerNotice(cm.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("1013002_DRAGON_EGG_ACQUIRED"))
      cm.warp(900090103, 0)
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC1013002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1013002(cm: cm))
   }
   return (NPC1013002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }