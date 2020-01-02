package npc

import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2040016 {
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
      if (mode == 1) {
         status++
      } else {
         cm.dispose()
      }
      if (status == 0 && mode == 1) {
         String selStr = "Hm? Who might you be? Oh, you've heard about my forging skills? In that case, I'd be glad to process some of your ores... for a fee.#b"
         String[] options = ["Refine a mineral ore", "Refine a jewel ore", "Refine a rare jewel", "Refine a crystal ore", "Create materials", "Create Arrows"]
         for (int i = 0; i < options.length; i++) {
            selStr += "\r\n#L" + i + "# " + options[i] + "#l"
         }

         cm.sendSimple(selStr)
      } else if (status == 1 && mode == 1) {
         selectedType = selection
         if (selectedType == 0) { //mineral refine
            String selStr = "So, what kind of mineral ore would you like to refine?#b"
            String[] minerals = ["Bronze", "Steel", "Mithril", "Adamantium", "Silver", "Orihalcon", "Gold"]
            for (int i = 0; i < minerals.length; i++) {
               selStr += "\r\n#L" + i + "# " + minerals[i] + "#l"
            }
            equip = false
            cm.sendSimple(selStr)
         } else if (selectedType == 1) { //jewel refine
            String selStr = "So, what kind of jewel ore would you like to refine?#b"
            String[] jewels = ["Garnet", "Amethyst", "Aquamarine", "Emerald", "Opal", "Sapphire", "Topaz", "Diamond", "Black Crystal"]
            for (int i = 0; i < jewels.length; i++) {
               selStr += "\r\n#L" + i + "# " + jewels[i] + "#l"
            }
            equip = false
            cm.sendSimple(selStr)
         } else if (selectedType == 2) { //rock refine
            String selStr = "A rare jewel? Which one were you thinking of?#b"
            String[] items = ["Moon Rock", "Star Rock"]
            for (int i = 0; i < items.length; i++) {
               selStr += "\r\n#L" + i + "# " + items[i] + "#l"
            }
            equip = false
            cm.sendSimple(selStr)
         } else if (selectedType == 3) { //crystal refine
            String selStr = "Crystal ore? I love refining those!#b"
            String[] crystals = ["Power Crystal", "Wisdom Crystal", "DEX Crystal", "LUK Crystal"]
            for (int i = 0; i < crystals.length; i++) {
               selStr += "\r\n#L" + i + "# " + crystals[i] + "#l"
            }
            equip = false
            cm.sendSimple(selStr)
         } else if (selectedType == 4) { //material refine
            String selStr = "Materials? I know of a few materials that I can make for you...#b"
            String[] materials = ["Make Processed Wood with Tree Branch", "Make Processed Wood with Firewood", "Make Screws (packs of 15)"]
            for (int i = 0; i < materials.length; i++) {
               selStr += "\r\n#L" + i + "# " + materials[i] + "#l"
            }
            equip = false
            cm.sendSimple(selStr)
         } else if (selectedType == 5) { //arrow refine
            String selStr = "Arrows? Not a problem at all.#b"
            String[] arrows = ["Arrow for Bow", "Arrow for Crossbow", "Bronze Arrow for Bow", "Bronze Arrow for Crossbow", "Steel Arrow for Bow", "Steel Arrow for Crossbow"]
            for (int i = 0; i < arrows.length; i++) {
               selStr += "\r\n#L" + i + "# " + arrows[i] + "#l"
            }
            equip = true
            cm.sendSimple(selStr)
         }
         if (equip) {
            status++
         }
      } else if (status == 2 && mode == 1) {
         selectedItem = selection
         if (selectedType == 0) { //mineral refine
            int[] itemSet = [4011000, 4011001, 4011002, 4011003, 4011004, 4011005, 4011006]
            List matSet = [4010000, 4010001, 4010002, 4010003, 4010004, 4010005, 4010006]
            List matQtySet = [10, 10, 10, 10, 10, 10, 10]
            int[] costSet = [270, 270, 270, 450, 450, 450, 720]
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
         } else if (selectedType == 2) { //rock refine
            int[] itemSet = [4011007, 4021009]
            List matSet = [[4011000, 4011001, 4011002, 4011003, 4011004, 4011005, 4011006], [4021000, 4021001, 4021002, 4021003, 4021004, 4021005, 4021006, 4021007, 4021008]]
            List matQtySet = [[1, 1, 1, 1, 1, 1, 1], [1, 1, 1, 1, 1, 1, 1, 1, 1]]
            int[] costSet = [9000, 13500]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 3) { //crystal refine
            int[] itemSet = [4005000, 4005001, 4005002, 4005003]
            List matSet = [4004000, 4004001, 4004002, 4004003]
            List matQtySet = [10, 10, 10, 10]
            int[] costSet = [4500, 4500, 4500, 4500]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 4) { //material refine
            int[] itemSet = [4003001, 4003001, 4003000]
            List matSet = [4000003, 4000018, [4011000, 4011001]]
            List matQtySet = [10, 5, [1, 1]]
            int[] costSet = [0, 0, 0]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         }

         String prompt = "So, you want me to make some #t" + item + "#s? In that case, how many do you want me to make?"

         cm.sendGetNumber(prompt, 1, 1, 100)
      } else if (status == 3 && mode == 1) {
         if (equip) {
            selectedItem = selection
            qty = 1
         } else {
            qty = (selection > 0) ? selection : (selection < 0 ? -selection : 1)
         }

         if (selectedType == 5) { //arrow refine
            int[] itemSet = [2060000, 2061000, 2060001, 2061001, 2060002, 2061002]
            List matSet = [[4003001, 4003004], [4003001, 4003004], [4011000, 4003001, 4003004], [4011000, 4003001, 4003004],
                           [4011001, 4003001, 4003005], [4011001, 4003001, 4003005]]
            List matQtySet = [[1, 1], [1, 1], [1, 3, 10], [1, 3, 10], [1, 5, 15], [1, 5, 15]]
            int[] costSet = [0, 0, 0, 0, 0, 0]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
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
      } else if (status == 4 && mode == 1) {
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
            cm.sendOk("I'm afraid you have no slots available for this transaction.")
         } else if (cm.getMeso() < cost * qty) {
            cm.sendOk("I'm afraid you cannot afford my services.")
         } else {
            if (mats instanceof ArrayList && matQty instanceof ArrayList) {
               for (int i = 0; complete && i < mats.size(); i++) {
                  if (matQty[i] * qty == 1) {
                     if (!cm.haveItem((mats[i] as Integer) * qty)) {
                        complete = false
                     }
                  } else {
                     if (!cm.haveItem(mats[i] as Integer, (matQty[i] as Integer) * qty)) {
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
               cm.sendOk("Hold it, I can't finish that without all of the proper materials. Bring them first, then we'll talk.")
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
               cm.sendOk("All done. If you need anything else, you know where to find me.")
            }
         }

         cm.dispose()
      }
   }
}

NPC2040016 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040016(cm: cm))
   }
   return (NPC2040016) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }