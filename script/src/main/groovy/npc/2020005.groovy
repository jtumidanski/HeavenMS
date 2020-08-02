package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2020005 {
   NPCConversationManager cm
   int status = -1
   int selected = -1

   int amount
   int totalCost
   int[] item = [2050003, 2050004, 4006000, 4006001]
   int[] cost = [300, 400, 5000, 5000]
   String[] msg = ["that cures the state of being sealed and cursed", "that cures all", ", possessing magical power, that is used for high-quality skills", ", possessing the power of summoning that is used for high-quality skills"]

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (!cm.isQuestCompleted(3035)) {
         cm.sendNext(I18nMessage.from("2020005_HELP_ME_OUT"))
         cm.dispose()
         return
      }
      if (mode == 0 && status == 2) {
         cm.sendNext(I18nMessage.from("2020005_TAKE_A_LOOK_AROUND"))
         cm.dispose()
         return
      }
      if (mode < 1) {
         cm.dispose()
         return
      }

      status++
      if (status == 0) {
         String selStr = ""
         for (int i = 0; i < item.length; i++) {
            selStr += "\r\n#L" + i + "# #b#t" + item[i] + "# (Price: " + cost[i] + " mesos)#k#l"
         }
         cm.sendSimple("Thanks to you #b#t4031056##k is safely sealed. Of course, also as a result, I used up about half of the power I have accumulated over the last 800 years or so...but now I can die in peace. Oh, by the way... are you looking for rare items by any chance? As a sign of appreciation for your hard work, I'll sell some items I have to you, and ONLY you. Pick out the one you want!" + selStr)
      } else if (status == 1) {
         selected = selection
         cm.sendGetNumber("Is #b#t" + item[selected] + "##k really the item that you need? It's the item " + msg[selected] + ". It may not be the easiest item to acquire, but I'll give you a good deal on it. It'll cost you #b" + cost[selected] + " mesos#k per item. How many would you like to purchase?", 0, 1, 100)
      } else if (status == 2) {
         amount = selection
         totalCost = cost[selected] * amount
         if (amount == 0) {
            cm.sendOk(I18nMessage.from("2020005_IF_YOU_DO_NOT_BUY_I_CANNOT_SELL"))
            cm.dispose()
         }
         cm.sendYesNo(I18nMessage.from("2020005_ARE_YOU_SURE").with(amount, item[selected], cost[selected], item[selected], totalCost))
      } else if (status == 3) {
         if (cm.getMeso() < totalCost || !cm.canHold(item[selected])) {
            cm.sendNext(I18nMessage.from("2020005_NOT_ENOUGH_MESOS").with(totalCost))
            cm.dispose()
         }
         cm.sendNext(I18nMessage.from("2020005_THANK_YOU"))
         cm.gainMeso(-totalCost)
         cm.gainItem(item[selected], (short) amount)
         cm.dispose()
      }
   }
}

NPC2020005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2020005(cm: cm))
   }
   return (NPC2020005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }