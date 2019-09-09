package npc


import scripting.npc.NPCConversationManager

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
         cm.sendNext("Aran, you're awake! How are you feeling? Hm? You want to know what's been going on?")
      } else if (status == 1) {
         cm.sendNext("We're almost done preparing for the escape. You don't have to worry. Everyone I could possibly find has boarded the ark, and Shinsoo has agreed to guide the way. We'll head to Victoria Island as soon as we finish the remaining preparations.")
      } else if (status == 2) {
         cm.sendNext("The other heroes? They've left to fight the Black Mage. They're buying us time to escape. What? You want to fight with them? No! You can't! You're hurt. You must leave with us!")
      } else if (status == 3) {
         cm.updateQuest(21002, "1")
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