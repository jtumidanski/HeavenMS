package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1092090 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.getQuestProgressInt(2180, 1) == 1) {
         cm.sendNext(I18nMessage.from("1092090_CHECK_ANOTHER_COW"))
         cm.dispose()
         return
      }

      if (cm.canHold(4031848) && cm.haveItem(4031847)) {
         cm.sendNext(I18nMessage.from("1092090_ONE_THIRD_FULL"))
         cm.gainItem(4031847, (short) -1)
         cm.gainItem(4031848, (short) 1)

         cm.setQuestProgress(2180, 1, 1)
      } else if (cm.canHold(4031849, 1) && cm.haveItem(4031848)) {
         cm.sendNext(I18nMessage.from("1092090_TWO_THIRDS_FULL"))
         cm.gainItem(4031848, (short) -1)
         cm.gainItem(4031849, (short) 1)

         cm.setQuestProgress(2180, 1, 1)
      } else if (cm.canHold(4031850) && cm.haveItem(4031849)) {
         cm.sendNext(I18nMessage.from("1092090_FULL"))
         cm.gainItem(4031849, (short) -1)
         cm.gainItem(4031850, (short) 1)

         cm.setQuestProgress(2180, 1, 1)
      } else {
         cm.sendNext(I18nMessage.from("1092090_INVENTORY_FULL"))
      }
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC1092090 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1092090(cm: cm))
   }
   return (NPC1092090) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }