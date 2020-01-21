package npc

import scripting.npc.NPCConversationManager
import tools.I18nMessage
import tools.SimpleMessage

/*
	NPC Name: Jane the Alchemist
	Map(s):
	Description:
*/

class NPC1002100 {
   NPCConversationManager cm
   int status = -1
   int sel = -1
   short amount = -1
   int[][] items = [[2000002, 310], [2022003, 1060], [2022000, 1600], [2001000, 3120]]
   int[] item

   def start() {
      if (cm.isQuestCompleted(2013)) {
         cm.sendNext(I18nMessage.from("1002100_ITS_YOU"))
      } else {
         if (cm.isQuestCompleted(2010)) {
            cm.sendNext(I18nMessage.from("1002100_NOT_STRONG_ENOUGH"))
         } else {
            cm.sendOk(I18nMessage.from("1002100_MY_DREAM"))
         }
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0 && type == 1) {
            cm.sendNext(I18nMessage.from("1002100_STILL_HAVE_A_FEW"))
         }
         cm.dispose()
         return
      }
      if (status == 0) {
         def selStr = I18nMessage.from("1002100_WHICH_ITEM_WOULD_YOU_LIKE_TO_BUY").to(cm.getClient()).evaluate()
         for (def i = 0; i < items.length; i++) {
            selStr += "\r\n#L" + i + "##i" + items[i][0] + "# (Price : " + items[i][1] + " mesos)#l"
         }
         cm.sendSimple(SimpleMessage.from(selStr))
      } else if (status == 1) {
         item = items[selection]
         def recHpMp = ["300 HP.", "1000 HP.", "800 MP", "1000 HP and MP."]
         cm.sendGetNumber(I18nMessage.from("1002100_HOW_MANY").with(item[0], item[0], recHpMp[selection]), 1, 1, 100)
      } else if (status == 2) {
         cm.sendYesNo(I18nMessage.from("1002100_WILL_YOU_PURCHASE").with(selection, item[0], item[0], item[1], (item[1] * selection)))
         amount = (short) selection
      } else if (status == 3) {
         if (cm.getMeso() < item[1] * amount) {
            cm.sendNext(I18nMessage.from("1002100_LACKING_MESOS").with(item[1] * selection))
         } else {
            if (cm.canHold(item[0])) {
               cm.gainMeso(-item[1] * amount)
               cm.gainItem(item[0], amount)
               cm.sendNext(I18nMessage.from("1002100_THANK_YOU"))
            } else {
               cm.sendNext(I18nMessage.from("1002100_PLEASE_CHECK_INVENTORY"))
            }
         }
         cm.dispose()
      }
   }
}

NPC1002100 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1002100(cm: cm))
   }
   return (NPC1002100) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }