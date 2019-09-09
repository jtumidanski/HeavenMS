package npc


import scripting.npc.NPCConversationManager

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
   int[][] items = [[2000002,310],[2022003,1060],[2022000,1600],[2001000,3120]]
   int[] item

   def start() {
      if (cm.isQuestCompleted(2013)) {
         cm.sendNext("It's you ... thanks to you I was able to get a lot done. Nowadays I've been making a bunch of items. If you need anything let me know.")
      } else {
         if (cm.isQuestCompleted(2010)) {
            cm.sendNext("You don't seem strong enough to be able to purchase my potion ...")
         } else {
            cm.sendOk("My dream is to travel everywhere, much like you. My father, however, does not allow me to do it, because he thinks it's very dangerous. He may say yes, though, if I show him some sort of a proof that I'm not the weak girl that he thinks I am ...")
         }
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0 && type == 1) {
            cm.sendNext("I still have quite a few of the materials you got me before. The items are all there so take your time choosing.")
         }
         cm.dispose()
         return
      }
      if (status == 0) {
         def selStr = "Which item would you like to buy?#b"
         for (def i = 0; i < items.length; i++ )
         selStr += "\r\n#L" + i + "##i" + items[i][0] + "# (Price : " + items[i][1] + " mesos)#l"
         cm.sendSimple(selStr)
      } else if (status == 1) {
         item = items[selection]
         def recHpMp = ["300 HP.", "1000 HP.", "800 MP", "1000 HP and MP."]
         cm.sendGetNumber("You want #b#t" + item[0] + "##k? #t" + item[0] + "# allows you to recover " + recHpMp[selection] + " How many would you like to buy?", 1, 1, 100)
      } else if (status == 2) {
         cm.sendYesNo("Will you purchase #r" + selection + "#k #b#t" + item[0] + "#(s)#k? #t" + item[0] + "# costs " + item[1] + " mesos for one, so the total comes out to be #r" + (item[1] * selection) + "#k mesos.")
         amount = (short) selection
      } else if (status == 3) {
         if (cm.getMeso() < item[1] * amount) {
            cm.sendNext("Are you lacking mesos by any chance? Please check and see if you have an empty slot available at your etc. inventory, and if you have at least #r" + (item[1] * selection) + "#k mesos with you.")
         } else {
            if (cm.canHold(item[0])) {
               cm.gainMeso(-item[1] * amount)
               cm.gainItem(item[0], amount)
               cm.sendNext("Thank you for coming. Stuff here can always be made so if you need something, please come again.")
            } else {
               cm.sendNext("Please check and see if you have an empty slot available at your etc. inventory.")
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