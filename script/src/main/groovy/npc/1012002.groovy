package npc

import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1012002 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int selectedType = -1
   int selectedItem = -1
   int item
   int[] items
   Object mats
   Object matQty
   int cost
   int qty = 1

   def start() {
      cm.getPlayer().setCS(true)
      String selStr = "Hello. I am Vicious, retired Sniper. However, I used to be the top student of Athena Pierce. Though I no longer hunt, I can make some archer items that will be useful for you...#b"
      String[] options = ["Create a bow", "Create a crossbow", "Make a glove", "Upgrade a glove", "Create materials", "Create Arrows"]
      for (int i = 0; i < options.length; i++) {
         selStr += "\r\n#L" + i + "# " + options[i] + "#l"
      }
      cm.sendSimple(selStr)
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         cm.dispose()
         return
      }
      if (status == 0) {
         String selStr = ""
         if (selection == 0) { //bow refine
            selStr = "I may have been a Sniper, but bows and crossbows aren't too much different. Anyway, which would you like to make?#b"
            items = [1452002, 1452003, 1452001, 1452000, 1452005, 1452006, 1452007]
            for (int i = 0; i < items.length; i++) {
               selStr += "\r\n#L" + i + "##t" + items[i] + "##k - Bowman Lv. " + (10 + (i * 5)) + "#l#b"
            }
         } else if (selection == 1) { //xbow refine
            selStr = "I was a Sniper. Crossbows are my specialty. Which would you like me to make for you?#b"
            items = [1462001, 1462002, 1462003, 1462000, 1462004, 1462005, 1462006, 1462007]
            for (int i = 0; i < items.length; i++) {
               selStr += "\r\n#L" + i + "##t" + items[i] + "##k - Bowman Lv. " + (10 + (i * 5)) + "#l#b"
            }
         } else if (selection == 2) { //glove refine
            selStr = "Okay, so which glove do you want me to make?#b"
            items = [1082012, 1082013, 1082016, 1082048, 1082068, 1082071, 1082084, 1082089]
            for (int i = 0; i < items.length; i++) {
               selStr += "\r\n#L" + i + "##t" + items[i] + "##k - Bowman Lv. " + (15 + (i * 5) > 40 ? ((i - 1) * 10) : 15 + (i * 5)) + "#l#b"
            }
         } else if (selection == 3) { //glove upgrade
            selStr = "Upgrade a glove? That shouldn't be too difficult. Which did you have in mind?#b"
            items = [1082015, 1082014, 1082017, 1082018, 1082049, 1082050, 1082069, 1082070, 1082072, 1082073, 1082085, 1082083, 1082090, 1082091]
            int x = 0
            for (int i = 0; i < items.length; i++) {
               selStr += "\r\n#L" + i + "##t" + items[i] + "##k" + "##k - Bowman Lv. " + (20 + (x * 5) > 40 ? ((x - 1) * 10) : 20 + (x * 5)) + "#l#b"
               x += (i + 1) % 2 == 0 ? 1 : 0
            }
         } else if (selection == 4) { //material refine
            selStr = "Materials? I know of a few materials that I can make for you...#b"
            String[] materials = ["Make Processed Wood with Tree Branch", "Make Processed Wood with Firewood", "Make Screws (packs of 15)"]
            for (int i = 0; i < materials.length; i++) {
               selStr += "\r\n#L" + i + "# " + materials[i] + "#l"
            }
         } else if (selection == 5) { //arrow refine
            selStr = "Arrows? Not a problem at all.#b"
            items = [2060000, 2061000, 2060001, 2061001, 2060002, 2061002]
            for (int i = 0; i < items.length; i++) {
               selStr += "\r\n#L" + i + "##t" + items[i] + "##l"
            }
         }
         selectedType = selection
         cm.sendSimple(selStr)
         if (selection != 4) {
            status++
         }
      } else if (status == 1) {
         selectedItem = selection
         items = [4003001, 4003001, 4003000]
         List matSet = [4000003, 4000018, [4011000, 4011001]]
         List matQtySet = [10, 5, [1, 1]]
         item = items[selection]
         mats = matSet[selection]
         matQty = matQtySet[selection]
         cost = 0
         cm.sendGetNumber("So, you want me to make some #t" + item + "#s? In that case, how many do you want me to make?", 1, 1, 100)
      } else if (status == 2) {
         if (selectedType != 4) {
            selectedItem = selection
         } else {
            qty = (selection > 0) ? selection : (selection < 0 ? -selection : 1)
         }
         // thanks br1337 for showing such a simple exploit with quantity on NPC crafters
         List matSet = []
         List matQtySet = []
         int[] costSet = []
         if (selectedType == 0) { //bow refine
            matSet = [[4003001, 4000000], [4011001, 4003000], [4003001, 4000016], [4011001, 4021006, 4003000], [4011001, 4011006, 4021003, 4021006, 4003000], [4011004, 4021000, 4021004, 4003000], [4021008, 4011001, 4011006, 4003000, 4000014]]
            matQtySet = [[5, 30], [1, 3], [30, 50], [2, 2, 8], [5, 5, 3, 3, 30], [7, 6, 3, 35], [1, 10, 3, 40, 50]]
            costSet = [800, 2000, 3000, 5000, 30000, 40000, 80000]
         } else if (selectedType == 1) { //xbow refine
            matSet = [[4003001, 4003000], [4011001, 4003001, 4003000], [4011001, 4003001, 4003000], [4011001, 4021006, 4021002, 4003000], [4011001, 4011005, 4021006, 4003001, 4003000], [4021008, 4011001, 4011006, 4021006, 4003000], [4021008, 4011004, 4003001, 4003000], [4021008, 4011006, 4021006, 4003001, 4003000]]
            matQtySet = [[7, 2], [1, 20, 5], [1, 50, 8], [2, 1, 1, 10], [5, 5, 3, 50, 15], [1, 8, 4, 2, 30], [2, 6, 30, 30], [2, 5, 3, 40, 40]]
            costSet = [1000, 2000, 3000, 10000, 30000, 50000, 80000, 200000]
         } else if (selectedType == 2) { //glove refine
            matSet = [[4000021, 4000009], [4000021, 4000009, 4011001], [4000021, 4000009, 4011006], [4000021, 4011006, 4021001], [4011000, 4011001, 4000021, 4003000], [4011001, 4021000, 4021002, 4000021, 4003000], [4011004, 4011006, 4021002, 4000030, 4003000], [4011006, 4011007, 4021006, 4000030, 4003000]]
            matQtySet = [[15, 20], [20, 20, 2], [40, 50, 2], [50, 2, 1], [1, 3, 60, 15], [3, 1, 3, 80, 25], [3, 1, 2, 40, 35], [2, 1, 8, 50, 50]]
            costSet = [5000, 10000, 15000, 20000, 30000, 40000, 50000, 70000]
         } else if (selectedType == 3) { //glove upgrade
            matSet = [[1082013, 4021003], [1082013, 4021000], [1082016, 4021000], [1082016, 4021008], [1082048, 4021003], [1082048, 4021008], [1082068, 4011002], [1082068, 4011006], [1082071, 4011006], [1082071, 4021008], [1082084, 4011000, 4021000], [1082084, 4011006, 4021008], [1082089, 4021000, 4021007], [1082089, 4021007, 4021008]]
            matQtySet = [[1, 2], [1, 1], [1, 3], [1, 1], [1, 3], [1, 1], [1, 4], [1, 2], [1, 4], [1, 2], [1, 1, 5], [1, 2, 2], [1, 5, 1], [1, 2, 2]]
            costSet = [7000, 7000, 10000, 12000, 15000, 20000, 22000, 25000, 30000, 40000, 55000, 60000, 70000, 80000]
         } else if (selectedType == 5) { //arrow refine
            matSet = [[4003001, 4003004], [4003001, 4003004], [4011000, 4003001, 4003004], [4011000, 4003001, 4003004], [4011001, 4003001, 4003005], [4011001, 4003001, 4003005]]
            matQtySet = [[1, 1], [1, 1], [1, 3, 10], [1, 3, 10], [1, 5, 15], [1, 5, 15]]
            costSet = [0, 0, 0, 0, 0, 0]
         }
         if (selectedType != 4) {
            item = items[selectedItem]
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
            prompt += "\r\n#i4031138# " + (cost * qty) + " meso"
         }
         cm.sendYesNo(prompt)
      } else if (status == 3) {
         boolean complete = true

         if (cm.getMeso() < (cost * qty)) {
            cm.sendOk("Sorry, but this is how I make my living. No meso, no item.")
            cm.dispose()
            return
         } else {
            if (mats instanceof ArrayList && matQty instanceof ArrayList) {
               for (int i = 0; complete && i < mats.size(); i++) {
                  if (!cm.haveItem(mats[i] as Integer, (matQty[i] as Integer) * qty)) {
                     complete = false
                  }
               }
            } else if (!cm.haveItem(mats as Integer, (matQty as Integer) * qty)) {
               complete = false
            }
         }
         if (!complete) {
            cm.sendOk("Surely you, of all people, would understand the value of having quality items? I can't do that without the items I require.")
         } else {
            int recvItem = item, recvQty

            if (item >= 2060000 && item <= 2060002) //bow arrows
            {
               recvQty = 1000 - (item - 2060000) * 100
            } else if (item >= 2061000 && item <= 2061002) //xbow arrows
            {
               recvQty = 1000 - (item - 2061000) * 100
            } else if (item == 4003000)//screws
            {
               recvQty = 15 * qty
            } else {
               recvQty = qty
            }

            if (cm.canHold(recvItem, recvQty)) {
               if (mats instanceof ArrayList && matQty instanceof ArrayList) {
                  for (int i = 0; i < mats.size(); i++) {
                     cm.gainItem(mats[i] as Integer, (short) -((matQty[i] as Integer) * qty))
                  }
               } else {
                  cm.gainItem(mats as Integer, (short) -((matQty as Integer) * qty))
               }
               cm.gainMeso(-(cost * qty))

               cm.gainItem(recvItem, (short) recvQty)
               cm.sendOk("A perfect item, as usual. Come and see me if you need anything else.")
            } else {
               cm.sendOk("Please make sure you have room in your inventory, and talk to me again.")
            }
         }
         cm.dispose()
      }
   }
}

NPC1012002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1012002(cm: cm))
   }
   return (NPC1012002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }