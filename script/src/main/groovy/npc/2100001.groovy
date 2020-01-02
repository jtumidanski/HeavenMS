package npc

import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2100001 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int selectedType = -1
   int selectedItem = -1
   int item
   Object mats
   Object matQty
   int cost
   int qty
   boolean equip

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode <= 0 && status == 0) {
         cm.sendNext("If you aren't in a hurry, then please come back in a bit. As you can see, there's so much work going on right now that I can't possibly give them to you on time.")
         cm.dispose()
         return
      }
      if (mode <= 0 && status >= 1) {
         cm.dispose()
         return
      }
      if (mode == 1) {
         status++
      } else {
         status--
      }

      if (status == 0) {
         cm.sendYesNo("Are you here to refine the ores of a mineral or a jewel? It doesn't matter how many ores you have, if you don't have them refined by a master like me, then they won't see the light of day. What do you think, do you want to refine them right now?")
      }
      if (status == 1 && mode == 1) {
         String selStr = "I like your attitude! Let's just take care of this right now. What kind of ores would you like to refine? #b"
         String[] options = ["Refine mineral ore", "Refine jewel ores", "Refine crystal ores"]
         for (int i = 0; i < options.length; i++) {
            selStr += "\r\n#L" + i + "# " + options[i] + "#l"
         }
         cm.sendSimple(selStr)
      } else if (status == 2 && mode == 1) {
         selectedType = selection

         if (selectedType == 0) { //mineral refine
            String selStr = "Which mineral would you like to refine?#b"
            String[] minerals = ["Bronze Plate", "Steel Plate", "Mithril Plate", "Adamantium Plate", "Silver Plate", "Orihalcon Plate", "Gold Plate", "Lithium"]
            for (int i = 0; i < minerals.length; i++) {
               selStr += "\r\n#L" + i + "# " + minerals[i] + "#l"
            }
            cm.sendSimple(selStr)
            equip = false
         } else if (selectedType == 1) { //jewel refine
            String selStr = "Which jewel would you like to refine?#b"
            String[] jewels = ["Garnet", "Amethyst", "Aquamarine", "Emerald", "Opal", "Sapphire", "Topaz", "Diamond", "Black Crystal"]
            for (int i = 0; i < jewels.length; i++) {
               selStr += "\r\n#L" + i + "# " + jewels[i] + "#l"
            }
            cm.sendSimple(selStr)
            equip = false
         } else if (selectedType == 2) { //Crystal refine
            String selStr = "A crystal? That's a rare item indeed. Don't worry, I can refine it just as well as others. Which crystal would you like to refine? #b"
            String[] crystals = ["Power Crystal", "Wisdom Crystal", "DEX Crystal", "LUK Crystal"]
            for (int i = 0; i < crystals.length; i++) {
               selStr += "\r\n#L" + i + "# " + crystals[i] + "#l"
            }
            cm.sendSimple(selStr)
            equip = false
         }
      } else if (status == 3 && mode == 1) {
         selectedItem = selection

         if (selectedType == 0) { //mineral refine
            int[] itemSet = [4011000, 4011001, 4011002, 4011003, 4011004, 4011005, 4011006, 4011008]
            List matSet = [4010000, 4010001, 4010002, 4010003, 4010004, 4010005, 4010006, 4010007]
            List matQtySet = [10, 10, 10, 10, 10, 10, 10, 10]
            int[] costSet = [270, 270, 270, 450, 450, 450, 720, 270]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 1) { //jewel refine
            int[] itemSet = [4021000, 4021001, 4021002, 4021003, 4021004, 4021005, 4021006, 4021007, 4021008]
            List matSet = [4020000, 4020001, 4020002, 4020003, 4020004, 4020005, 4020006, 4020007, 4020008]
            List matQtySet = [10, 10, 10, 10, 10, 10, 10, 10, 10]
            int[] costSet = [450, 450, 450, 450, 450, 450, 450, 900, 2700]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 2) { //Crystal refine
            int[] itemSet = [4005000, 4005001, 4005002, 4005003]
            List matSet = [4004000, 4004001, 4004002, 4004003]
            List matQtySet = [10, 10, 10, 10]
            int[] costSet = [4500, 4500, 4500, 4500]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         }

         String prompt = "So, you want me to make some #t" + item + "#s? In that case, how many do you want me to make?"
         cm.sendGetNumber(prompt, 1, 1, 100)
      } else if (status == 4 && mode == 1) {
         if (equip) {
            selectedItem = selection
            qty = 1
         } else {
            qty = (selection > 0) ? selection : (selection < 0 ? -selection : 1)
         }

         String prompt = "You want me to make "
         if (qty == 1) {
            prompt += "a #t" + item + "#?"
         } else {
            prompt += qty + " #t" + item + "#?"
         }

         prompt += " In that case, I'm going to need specific items from you in order to make it. Make sure you have room in your inventory, though!#b"

         if (mats instanceof ArrayList && matQty instanceof ArrayList) {
            for (int i = 0; i < mats.size(); i++) {
               prompt += "\r\n#i" + mats[i] + "# " + ((matQty[i] as Integer) * qty) + " #t" + mats[i] + "#"
            }
         } else {
            prompt += "\r\n#i" + mats + "# " + ((matQty as Integer) * qty) + " #t" + mats + "#"
         }

         if (cost > 0) {
            prompt += "\r\n#i4031138# " + cost * qty + " meso"
         }

         cm.sendYesNo(prompt)
      } else if (status == 5 && mode == 1) {
         boolean complete = true
         int recvItem = item, recvQty

         if (item >= 2060000 && item <= 2060002) //bow arrows
         {
            recvQty = 1000 - (item - 2060000) * 100
         } else if (item >= 2061000 && item <= 2061002) //crossbow arrows
         {
            recvQty = 1000 - (item - 2061000) * 100
         } else if (item == 4003000)//screws
         {
            recvQty = 15 * qty
         } else {
            recvQty = qty
         }

         if (!cm.canHold(recvItem, recvQty)) {
            cm.sendOk("I'm afraid you are short in inventory slots for this.")
         } else if (cm.getMeso() < cost * qty) {
            cm.sendOk("I'm afraid you cannot afford my services.")
         } else {
            if (mats instanceof ArrayList && matQty instanceof ArrayList) {
               for (int i = 0; complete && i < mats.size(); i++) {
                  if (matQty[i] * qty == 1) {
                     if (!cm.haveItem(mats[i] as Integer)) {
                        complete = false
                     }
                  } else {

                     if (cm.haveItem(mats[i] as Integer, (matQty[i] as Integer) * qty)) {
                        complete = false
                     }
                  }
               }
            } else {
               if (!cm.haveItem(mats as Integer, (matQty as Integer) * qty)) {
                  complete = false
               }
            }

            if (!complete) {
               cm.sendOk("Please check and see if you have all the necessary items with you. If so, then please check your etc. inventory and see if you have an empty space.")
            } else {
               if (mats instanceof ArrayList && matQty instanceof ArrayList) {
                  for (int i = 0; i < mats.size(); i++) {
                     cm.gainItem(mats[i] as Integer, (short) ((-matQty[i] as Integer) * qty))
                  }
               } else {
                  cm.gainItem(mats as Integer, (short) ((-matQty as Integer) * qty))
               }

               if (cost > 0) {
                  cm.gainMeso(-cost * qty)
               }

               cm.gainItem(recvItem, (short) recvQty)
               cm.sendOk("There, finished. What do you think, a piece of art, isn't it? Well, if you need anything else, you know where to find me.")
            }
         }

         cm.dispose()
      }
   }
}

NPC2100001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2100001(cm: cm))
   }
   return (NPC2100001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }