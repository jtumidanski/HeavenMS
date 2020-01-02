package npc


import scripting.npc.NPCConversationManager

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
         cm.sendNext("If you decide to help me out, then in return, I'll make the item available for sale.")
         cm.dispose()
         return
      }
      if (mode == 0 && status == 2) {
         cm.sendNext("I see. Understand that I have many different items here. Take a look around. I'm only selling these items to you, so I won't be ripping you off in any way shape or form.")
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
            cm.sendOk("If you're not going to buy anything, then I've got nothing to sell neither.")
            cm.dispose()
         }
         cm.sendYesNo("Are you sure you want to buy #r" + amount + " #t" + item[selected] + "(s)##k? It'll cost you " + cost[selected] + " mesos per #t" + item[selected] + "#, which will cost you #r" + totalCost + " mesos#k in total.")
      } else if (status == 3) {
         if (cm.getMeso() < totalCost || !cm.canHold(item[selected])) {
            cm.sendNext("Are you sure you have enough mesos? Please check and see if your etc. or use inventory is full, or if you have at least #r" + totalCost + "#k mesos.")
            cm.dispose()
         }
         cm.sendNext("Thank you. If you ever find yourself needing items down the road, make sure to drop by here. I may have gotten old over the years, but I can still make magic items with ease.")
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