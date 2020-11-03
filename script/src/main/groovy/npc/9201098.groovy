package npc
import tools.I18nMessage



import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201098 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.isQuestCompleted(8223)) {
         if (cm.haveItem(3992041)) {
            cm.sendOk(I18nMessage.from("9201098_DEFENDERS"))

         } else {
            if (!cm.canHold(3992041)) {
               cm.sendOk(I18nMessage.from("9201098_NEED_SETUP_SLOT"))

            } else {
               cm.sendOk(I18nMessage.from("9201098_LOST_YOUR_KEY"))

               cm.gainItem(3992041, (short) 1)
            }
         }
      } else {
         cm.sendOk(I18nMessage.from("9201098_BRAVE_ADVENTURER"))

      }

      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9201098 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201098(cm: cm))
   }
   return (NPC9201098) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }