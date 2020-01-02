package npc


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
      cm.sendSimple("...Can I help you?\r\n#L0##bBuy the Magic Seed#k#l\r\n#L1##bDo something for Leafre#k#l")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || (mode == 0 && status < 3)) {
         cm.dispose()
         return
      } else if (mode == 0) {
         cm.sendOk("Please think carefully. Once you have made your decision, let me know.")
         cm.dispose()
         return
      }
      status++
      if (status == 1) {
         if (selection == 0) {
            cm.sendSimple("You don't seem to be from out town. How can I help you?#L0##bI would like some #t4031346#.#k#l")
         } else {
            cm.sendNext("Under development...")
            cm.dispose()
         }
      } else if (status == 2) {
         cm.sendGetNumber("#b#t4031346##k is a precious item. I cannot give it to you just like that. How about doing me a little favor? Then I'll give it to you. I'll sell the #b#t4031346##k to you for #b30,000 mesos#k each. Are you willing to make the purchase? How many would you like, then?", 0, 0, 99)
      } else if (status == 3) {
         if (selection == 0) {
            cm.sendOk("I can't sell you 0.")
            cm.dispose()
         } else {
            temp = selection
            cost = temp * 30000
            cm.sendYesNo("Buying #b" + temp + " #t4031346#(s)#k will cost you #b" + cost + " mesos#k. Are you sure you want to make the purchase?")
         }
      } else if (status == 4) {
         if (cm.getMeso() < cost || !cm.canHold(4031346)) {
            cm.sendOk("Please check and see if you have enough mesos to make the purchase. Also, I suggest you check the etc. inventory and see if you have enough space available to make the purchase.")
         } else {
            cm.sendOk("See you again~")
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