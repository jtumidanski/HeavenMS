package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2081000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int temp
   int cost

   def start() {
      cm.sendSimple(I18nMessage.from("2081000_CAN_I_HELP_YOU"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || (mode == 0 && status < 3)) {
         cm.dispose()
         return
      } else if (mode == 0) {
         cm.sendOk(I18nMessage.from("2081000_THINK_CAREFULLY"))
         cm.dispose()
         return
      }
      status++
      if (status == 1) {
         if (selection == 0) {
            cm.sendSimple(I18nMessage.from("2081000_HOW_CAN_I_HELP"))
         } else {
            cm.sendNext(I18nMessage.from("2081000_UNDER_DEVELOPMENT"))
            cm.dispose()
         }
      } else if (status == 2) {
         cm.sendGetNumber("#b#t4031346##k is a precious item. I cannot give it to you just like that. How about doing me a little favor? Then I'll give it to you. I'll sell the #b#t4031346##k to you for #b30,000 mesos#k each. Are you willing to make the purchase? How many would you like, then?", 0, 0, 99)
      } else if (status == 3) {
         if (selection == 0) {
            cm.sendOk(I18nMessage.from("2081000_MORE_THAN_0"))
            cm.dispose()
         } else {
            temp = selection
            cost = temp * 30000
            cm.sendYesNo(I18nMessage.from("2081000_WILLING_TO_MAKE_PURCHASE").with(temp, cost))
         }
      } else if (status == 4) {
         if (cm.getMeso() < cost || !cm.canHold(4031346)) {
            cm.sendOk(I18nMessage.from("2081000_NOT_ENOUGH_MESOS"))
         } else {
            cm.sendOk(I18nMessage.from("2081000_SEE_YOU_AGAIN"))
            cm.gainItem(4031346, (short) temp)
            cm.gainMeso(-cost)
         }
         cm.dispose()
      }
   }
}

NPC2081000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2081000(cm: cm))
   }
   return (NPC2081000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }