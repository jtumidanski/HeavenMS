package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Hotel Receptionist
	Map(s): 		
	Description: 	Sleepywood Hotel
*/


class NPC1061100 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int regularCost = 499
   int vipCost = 999
   int isRegular = 0

   def start() {
      cm.sendNext("Welcome. We're the Sleepywood Hotel. Our hotel works hard to serve you the best at all times. If you are tired and worn out from hunting, how about a relaxing stay at our hotel?")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || (mode == 0 && status == 1)) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 2) {
            cm.sendNext("We offer other kinds of services, too, so please think carefully and then make your decision.")
            cm.dispose()
            return
         }
         status++
         if (status == 1) {
            cm.sendSimple("We offer two kinds of rooms for our service. Please choose the one of your liking.\r\n#b#L0#Regular sauna (" + regularCost + " mesos per use)#l\r\n#L1#VIP sauna (" + vipCost + " mesos per use)#l")
            isRegular = 1
         } else if (status == 2) {
            if (selection == 0) {
               cm.sendYesNo("You have chosen the regular sauna. Your HP and MP will recover fast and you can even purchase some items there. Are you sure you want to go in?")
            } else if (selection == 1) {
               cm.sendYesNo("You've chosen the VIP sauna. Your HP and MP will recover even faster than that of the regular sauna and you can even find a special item in there. Are you sure you want to go in?")
               isRegular = 0
            }
         } else if (status == 3) {
            if (isRegular == 1) {
               if (cm.getMeso() >= regularCost) {
                  cm.warp(105040401)
                  cm.gainMeso(-regularCost)
               } else {
                  cm.sendNext("I'm sorry. It looks like you don't have enough mesos. It will cost you at least " + regularCost + "mesos to stay at our hotel.")
               }
            } else {
               if (cm.getMeso() >= vipCost) {
                  cm.warp(105040402)
                  cm.gainMeso(-vipCost)
               } else {
                  cm.sendNext("I'm sorry. It looks like you don't have enough mesos. It will cost you at least " + vipCost + "mesos to stay at our hotel.")
               }
            }
            cm.dispose()
         }
      }
   }
}

NPC1061100 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1061100(cm: cm))
   }
   return (NPC1061100) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }