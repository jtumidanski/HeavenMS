package npc
import tools.I18nMessage



import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2081009 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode <= 0) {
         cm.dispose()
         return
      }

      status++
      if (status == 0) {
         if (cm.isQuestStarted(6180) && cm.getQuestProgressInt(6180, 9300096) < 200) {
            cm.sendYesNo(I18nMessage.from("2081009_PAY_ATTENTION"))

         } else {
            cm.sendOk(I18nMessage.from("2081009_ASSIGNED_PERSONNEL"))

            cm.dispose()
         }
      } else if (status == 1) {
         if (cm.getPlayer().haveItemEquipped(1092041)) {
            cm.sendNext(I18nMessage.from("2081009_SHIELD_EQUIPPED"))

         } else {
            cm.sendOk(I18nMessage.from("2081009_PLEASE_EQUIP"))

            cm.dispose()
         }
      } else {
         cm.warp(924000001, 0)
         cm.dispose()
      }
   }
}

NPC2081009 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2081009(cm: cm))
   }
   return (NPC2081009) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }