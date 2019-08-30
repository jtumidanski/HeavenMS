package npc


import scripting.npc.NPCConversationManager
import tools.MessageBroadcaster
import tools.ServerNoticeType

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1013200 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (!cm.isQuestStarted(22015)) {
         cm.sendOk("#b(You are too far from the Piglet. Go closer to grab it.)")
      } else {
         cm.gainItem(4032449, true)
         cm.forceCompleteQuest(22015)
         MessageBroadcaster.getInstance().sendServerNotice(cm.getPlayer(), ServerNoticeType.PINK_TEXT, "You have rescued the Piglet.")
      }
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC1013200 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1013200(cm: cm))
   }
   return (NPC1013200) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }