package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9209001 {
   NPCConversationManager cm
   int status = -1
   int sel = -1
   int sel2 = -1

   def start() {
      cm.sendOk("Hello, the Maple 7th Day Market is currently unavailable.")
      cm.dispose()

      //cm.sendSimple("Hello, the Maple 7th Day Market opens today.#b\r\n#L0#Move to Maple 7th Day Market map\r\n#L1#Listen for an explanation about the Maple 7th Day Market")
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (status == 6 && mode == 1) {
         sel2 = -1
         status = 0
      }
      if (mode != 1) {
         if (mode == 0 && type == 0) {
            status -= 2
         } else {
            cm.dispose()
            return
         }
      }
      if (status == 0) {
         if (sel == -1) {
            sel = selection
         }
         if (selection == 0) {
            cm.sendNext("Okay, we will send you to the Maple 7th Day Market map.")
         } else {
            cm.sendSimple("What would you like to know about the Maple 7th Day Market?#b\r\n#L0#Where does the Maple 7th Day Market take place?\r\n#L1#What can you do at the Maple 7th Day Market?\r\n#L2#I do not have any questions.")
         }
      } else if (status == 1) {
         if (sel == 0) {
            cm.getPlayer().saveLocation("EVENT")
            cm.warp(680100000 + (Math.random() * 3).intValue())
            cm.dispose()
         } else if (selection == 0) {
            cm.sendNext("The Maple 7th Day Market opens only on Sundays. You can enter if you find me in any town, Henesys, New Leaf City, Leafre, Kerning City, Ludibrium, I'm just about everywhere!")
            status -= 2
         } else if (selection == 1) {
            cm.sendSimple("You can find rare goods that are hard to find elsewhere at the Maple 7th Day Market.#b\r\n#L0#Purchase Special Items\r\n#L1#Help the Poultry Farm Owner")
         } else {
            cm.sendNext("I guess you don't have any question. Please keep us in your thoughts, and ask if you are curious about anything.")
            cm.dispose()
         }
      } else if (status == 2) {
         if (sel2 == -1) {
            sel2 = selection
         }
         if (sel2 == 0) {
            cm.sendNext("You can find many items at the Maple 7th Day Market. The prices are subject to change, so you'd better get them when they're cheap!")
         } else {
            cm.sendNext("Aside from the merchants, you can also find the lazy daughter of the poultry farm owner at the Maple 7th Day Market. Help Mimi and hatch her egg until it grows to be a chicken!")
         }
      } else if (status == 3) {
         if (sel2 == 0) {
            cm.sendNextPrev("The purchases made here can be sold back to the merchant intermediary, Abdula. He won't accept anything more than a week old, so make sure you re-sell by Saturday!")
         } else {
            cm.sendNextPrev("Since she can't just trust anyone with the egg, she'll ask for deposit money. Pay her the deposit and take good care of the egg.")
         }
      } else if (status == 4) {
         if (sel2 == 0) {
            cm.sendNextPrev("Abdula adjusts his reselling rates as well, so it would be wise to sell when you can make the most profit. The prices tend to fluctuate hourly, so remember to check often.")
         } else {
            cm.sendNextPrev("If you manage to successfully grow the egg into a chicken and take it back to Mimi, Mimi will reward you. She may be lazy but she's not ungrateful.")
         }
      } else if (status == 5) {
         if (sel2 == 0) {
            cm.sendNextPrev("Test your business wit by buying good at low prices in the Maple 7th Day Market and selling it to the merchant intermediary when its value goes up!")
         } else {
            cm.sendNextPrev("You can click on the egg to check on its growth. You have to be diligent with the egg since the EXP you gain and the egg will grow together.")
         }
      }
   }
}

NPC9209001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9209001(cm: cm))
   }
   return (NPC9209001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }