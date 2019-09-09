package npc

import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1022004 {
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
      cm.getPlayer().setCS(true)
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
         String selStr = "Um... Hi, I'm Mr. Thunder's apprentice. He's getting up there in age, so he handles most of the heavy-duty work while I handle some of the lighter jobs. What can I do for you?#b"
         String[] options = ["Make a glove", "Upgrade a glove", "Create materials"]
         for (int i = 0; i < options.length; i++) {
            selStr += "\r\n#L" + i + "# " + options[i] + "#l"
         }

         cm.sendSimple(selStr)
      } else if (status == 1 && mode == 1) {
         selectedType = selection
         if (selectedType == 0) { //glove refine
            String selStr = "Okay, so which glove do you want me to make?#b"
            String[] items = ["Juno#k - Warrior Lv. 10#b", "Steel Fingerless Gloves#k - Warrior Lv. 15#b", "Venon#k - Warrior Lv. 20#b", "White Fingerless Gloves#k - Warrior Lv. 25#b",
                              "Bronze Missel#k - Warrior Lv. 30#b", "Steel Briggon#k - Warrior Lv. 35#b", "Iron Knuckle#k - Warrior Lv. 40#b", "Steel Brist#k - Warrior Lv. 50#b", "Bronze Clench#k - Warrior Lv. 60#b"]
            for (int i = 0; i < items.length; i++) {
               selStr += "\r\n#L" + i + "# " + items[i] + "#l"
            }
            cm.sendSimple(selStr)
            equip = true
         } else if (selectedType == 1) { //glove upgrade
            String selStr = "Upgrade a glove? That shouldn't be too difficult. Which did you have in mind?#b"
            String[] crystals = ["Steel Missel#k - Warrior Lv. 30#b", "Orihalcon Missel#k - Warrior Lv. 30#b", "Yellow Briggon#k - Warrior Lv. 35#b", "Dark Briggon#k - Warrior Lv. 35#b",
                                 "Adamantium Knuckle#k - Warrior Lv. 40#b", "Dark Knuckle#k - Warrior Lv. 40#b", "Mithril Brist#k - Warrior Lv. 50#b", "Gold Brist#k - Warrior Lv. 50#b",
                                 "Sapphire Clench#k - Warrior Lv. 60#b", "Dark Clench#k - Warrior Lv. 60#b"]
            for (int i = 0; i < crystals.length; i++) {
               selStr += "\r\n#L" + i + "# " + crystals[i] + "#l"
            }
            cm.sendSimple(selStr)
            equip = true
         } else if (selectedType == 2) { //material refine
            String selStr = "Materials? I know of a few materials that I can make for you...#b"
            String[] materials = ["Make Processed Wood with Tree Branch", "Make Processed Wood with Firewood", "Make Screws (packs of 15)"]
            for (int i = 0; i < materials.length; i++) {
               selStr += "\r\n#L" + i + "# " + materials[i] + "#l"
            }
            cm.sendSimple(selStr)
            equip = false
         }
         if (equip) {
            status++
         }
      } else if (status == 2 && mode == 1) {
         selectedItem = selection
         if (selectedType == 2) { //material refine
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

         if (selectedType == 0) { //glove refine
            int[] itemSet = [1082003, 1082000, 1082004, 1082001, 1082007, 1082008, 1082023, 1082009, 1082059]
            List matSet = [[4000021, 4011001], 4011001, [4000021, 4011000], 4011001, [4011000, 4011001, 4003000], [4000021, 4011001, 4003000], [4000021, 4011001, 4003000],
                           [4011001, 4021007, 4000030, 4003000], [4011007, 4011000, 4011006, 4000030, 4003000]]
            List matQtySet = [[15, 1], 2, [40, 2], 2, [3, 2, 15], [30, 4, 15], [50, 5, 40], [3, 2, 30, 45], [1, 8, 2, 50, 50]]
            int[] costSet = [1000, 2000, 5000, 10000, 20000, 30000, 40000, 50000, 70000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 1) { //glove upgrade
            int[] itemSet = [1082005, 1082006, 1082035, 1082036, 1082024, 1082025, 1082010, 1082011, 1082060, 1082061]
            List matSet = [[1082007, 4011001], [1082007, 4011005], [1082008, 4021006], [1082008, 4021008], [1082023, 4011003], [1082023, 4021008],
                           [1082009, 4011002], [1082009, 4011006], [1082059, 4011002, 4021005], [1082059, 4021007, 4021008]]
            List matQtySet = [[1, 1], [1, 2], [1, 3], [1, 1], [1, 4], [1, 2], [1, 5], [1, 4], [1, 3, 5], [1, 2, 2]]
            int[] costSet = [20000, 25000, 30000, 40000, 45000, 50000, 55000, 60000, 70000, 80000]
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

         if (item == 4003000)//screws
         {
            recvQty = 15 * qty
         } else {
            recvQty = qty
         }

         if (!cm.canHold(recvItem, recvQty)) {
            cm.sendOk("Check your inventory for a free slot first.")
            cm.dispose()
            return
         } else if (cm.getMeso() < cost * qty) {
            cm.sendOk("I may still be an apprentice, but I do need to earn a living.")
            cm.dispose()
            return
         } else {
            if (mats instanceof ArrayList && matQty instanceof ArrayList) {
               for (int i = 0; complete && i < mats.size(); i++) {
                  if (!cm.haveItem(mats[i] as Integer, ((matQty[i] as Integer) * qty))) {
                     complete = false
                  }
               }
            } else if (!cm.haveItem(mats as Integer, ((matQty as Integer) * qty))) {
               complete = false
            }
         }

         if (!complete) {
            cm.sendOk("I'm still an apprentice, I don't know if I can substitute other items in yet... Can you please bring what the recipe calls for?")
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
            cm.sendOk("Did that come out right? Come by me again if you have anything for me to practice on.")
         }
         cm.dispose()
      }
   }
}

NPC1022004 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1022004(cm: cm))
   }
   return (NPC1022004) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }