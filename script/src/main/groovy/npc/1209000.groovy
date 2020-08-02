package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1209000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 0 && type == 0) {
         status--
      } else if (mode == -1) {
         cm.dispose()
         return
      } else {
         status++
      }
      if (mode == 1) {
         status++
      } else {
         status--
      }
      if (status == 0) {
         cm.sendNext(I18nMessage.from("1209000_HOW_ARE_YOU_FEELING"))
      } else if (status == 1) {
         cm.sendNext(I18nMessage.from("1209000_ALMOST_DONE"))
      } else if (status == 2) {
         cm.sendNext(I18nMessage.from("1209000_OTHER_HEROES"))
      } else if (status == 3) {
         //cm.updateQuest(21002, "1")
         cm.showIntro("Effect/Direction1.img/aranTutorial/Trio")
         cm.dispose()
      }
   }
}

NPC1209000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1209000(cm: cm))
   }
   return (NPC1209000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }