package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201042 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int[] wishPrizes = [2000000, 2010004, 2020011, 2000004, 2000006, 2022015, 2000005, 1082174, 1002579, 1032039, 1002578, 1002580, 1002577, 1102078]
   int[] wishPrizesQty = [10, 10, 5, 5, 5, 5, 10, 1, 1, 1, 1, 1, 1, 1]
   int[] wishPrizesCst = [10, 15, 20, 30, 30, 50, 100, 400, 450, 500, 500, 530, 550, 600]

   int slctTicket
   int amntTicket
   boolean advance = true

   def start() {
      slctTicket = getTierTicket(cm.getPlayer().getLevel())
      amntTicket = cm.getItemQuantity(slctTicket)
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   static def getTierTicket(int level) {
      if (level < 50) {
         return 4031543
      } else if (level < 120) {
         return 4031544
      } else {
         return 4031545
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            cm.dispose()
            return
         }
         if (mode == 1 && advance) {
            status++
         } else {
            status--
         }

         advance = true

         if (status == 0) {
            cm.sendNext("Hi there, how is it going? Since you're passing by Amoria, have you heard about the instance my brother Amos is hosting? It is the #bAmorian Challenge#k, an instance for everyone above level 40.\r\n\r\nThere, you may find the #i4031543# #i4031544# #i4031545# #bWish Tickets#k that can be brought here to redeem prizes.")
         } else if (status == 1) {
            String listStr = ""
            for (int i = 0; i < wishPrizes.length; i++) {
               listStr += "#b#L" + i + "#" + wishPrizesQty[i] + " #z" + wishPrizes[i] + "##k"
               listStr += " - " + wishPrizesCst[i] + " wish tickets"
               listStr += "#l\r\n"
            }

            cm.sendSimple("You currently have #b" + amntTicket + " #i" + slctTicket + "# #t" + slctTicket + "##k.\r\n\r\nPurchase a prize:\r\n\r\n" + listStr)
         } else if (status == 2) {
            sel = selection

            if (amntTicket < wishPrizesCst[selection]) {
               cm.sendPrev("You will need #b" + wishPrizesCst[selection] + " #t" + slctTicket + "##k to purchase that! If you want this, come back another time when you have all the tickets at hand.")
               advance = false
            } else {
               cm.sendYesNo("You have selected #b" + wishPrizesQty[selection] + " #z" + wishPrizes[selection] + "##k, that will require #b" + wishPrizesCst[selection] + " #t" + slctTicket + "##k. Will you purchase it?")
            }
         } else {
            if (cm.canHold(wishPrizes[sel], wishPrizesQty[sel])) {
               cm.gainItem(wishPrizes[sel], (short) wishPrizesQty[sel])
               cm.gainItem(slctTicket, (short) -wishPrizesCst[sel])

               cm.sendOk("There you go, have a good day!")
            } else {
               cm.sendOk("Please have a slot available on your inventory before claiming the item.")
            }

            cm.dispose()
         }
      }
   }
}

NPC9201042 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201042(cm: cm))
   }
   return (NPC9201042) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }