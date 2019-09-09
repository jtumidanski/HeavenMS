package npc

import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1052002 {
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
         String selStr = "Pst... If you have the right goods, I can turn it into something niice...#b"
         String[] options = ["Create a glove", "Upgrade a glove", "Create a claw", "Upgrade a claw", "Create materials"]
         for (int i = 0; i < options.length; i++) {
            selStr += "\r\n#L" + i + "# " + options[i] + "#l"
         }
         cm.sendSimple(selStr)
      } else if (status == 1 && mode == 1) {
         selectedType = selection
         if (selectedType == 0) { //glove refine
            String selStr = "So, what kind of glove would you like me to make?#b"
            String[] gloves = ["Work Gloves#k - Common Lv. 10#b", "Brown Duo#k - Thief Lv. 15#b", "Blue Duo#k - Thief Lv. 15#b", "Black Duo#k - Thief Lv. 15#b", "Bronze Mischief#k - Thief Lv. 20#b", "Bronze Wolfskin#k - Thief Lv. 25#b", "Steel Sylvia#k - Thief Lv. 30#b",
                               "Steel Arbion#k - Thief Lv. 35#b", "Red Cleave#k - Thief Lv. 40#b", "Blue Moon Glove#k - Thief Lv. 50#b", "Bronze Pow#k - Thief Lv. 60#b"]
            for (int i = 0; i < gloves.length; i++) {
               selStr += "\r\n#L" + i + "# " + gloves[i] + "#l"
            }
            equip = true
            cm.sendSimple(selStr)
         } else if (selectedType == 1) { //glove upgrade
            String selStr = "An upgraded glove? Sure thing, but note that upgrades won't carry over to the new item... #b"
            String[] gloves = ["Mithril Mischief#k - Thief Lv. 20#b", "Dark Mischief#k - Thief Lv. 20#b", "Mithril Wolfskin#k - Thief Lv. 25#b",
                               "Dark Wolfskin#k - Thief Lv. 25#b", "Silver Sylvia#k - Thief Lv. 30#b", "Gold Sylvia#k - Thief Lv. 30#b", "Orihalcon Arbion#k - Thief Lv. 35#b", "Gold Arbion#k - Thief Lv. 35#b", "Gold Cleave#k - Thief Lv. 40#b",
                               "Dark Cleave#k - Thief Lv. 40#b", "Red Moon Glove#k - Thief Lv. 50#b", "Brown Moon Glove#k - Thief Lv. 50#b", "Silver Pow#k - Thief Lv. 60#b", "Gold Pow#k - Thief Lv. 60#b"]
            for (int i = 0; i < gloves.length; i++) {
               selStr += "\r\n#L" + i + "# " + gloves[i] + "#l"
            }
            equip = true
            cm.sendSimple(selStr)
         } else if (selectedType == 2) { //claw refine
            String selStr = "So, what kind of claw would you like me to make?#b"
            String[] claws = ["Steel Titans#k - Thief Lv. 15#b", "Bronze Igor#k - Thief Lv. 20#b", "Meba#k - Thief Lv. 25#b", "Steel Guards#k - Thief Lv. 30#b", "Bronze Guardian#k - Thief Lv. 35#b", "Steel Avarice#k - Thief Lv. 40#b", "Steel Slain#k - Thief Lv. 50#b"]
            for (int i = 0; i < claws.length; i++) {
               selStr += "\r\n#L" + i + "# " + claws[i] + "#l"
            }
            equip = true
            cm.sendSimple(selStr)
         } else if (selectedType == 3) { //claw upgrade
            String selStr = "An upgraded claw? Sure thing, but note that upgrades won't carry over to the new item...#b"
            String[] claws = ["Mithril Titans#k - Thief Lv. 15#b", "Gold Titans#k - Thief Lv. 15#b", "Steel Igor#k - Thief Lv. 20#b", "Adamantium Igor#k - Thief Lv. 20#b", "Mithril Guards#k - Thief Lv. 30#b", "Adamantium Guards#k - Thief Lv. 30#b",
                              "Silver Guardian#k - Thief Lv. 35#b", "Dark Guardian#k - Thief Lv. 35#b", "Blood Avarice#k - Thief Lv. 40#b", "Adamantium Avarice#k - Thief Lv. 40#b", "Dark Avarice#k - Thief Lv. 40#b", "Blood Slain#k - Thief Lv. 50#b", "Sapphire Slain#k - Thief Lv. 50#b"]
            for (int i = 0; i < claws.length; i++) {
               selStr += "\r\n#L" + i + "# " + claws[i] + "#l"
            }
            equip = true
            cm.sendSimple(selStr)
         } else if (selectedType == 4) { //material refine
            String selStr = "Materials? I know of a few materials that I can make for you...#b"
            String[] materials = ["Make Processed Wood with Tree Branch", "Make Processed Wood with Firewood", "Make Screws (packs of 15)"]
            for (int i = 0; i < materials.length; i++) {
               selStr += "\r\n#L" + i + "# " + materials[i] + "#l"
            }
            equip = false
            cm.sendSimple(selStr)
         }
         if (equip) {
            status++
         }
      } else if (status == 2 && mode == 1) {
         selectedItem = selection
         if (selectedType == 4) { //material refine
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
            int[] itemSet = [1082002, 1082029, 1082030, 1082031, 1082032, 1082037, 1082042, 1082046, 1082075, 1082065, 1082092]
            List matSet = [4000021, [4000021, 4000018], [4000021, 4000015], [4000021, 4000020], [4011000, 4000021], [4011000, 4011001, 4000021], [4011001, 4000021, 4003000], [4011001, 4011000, 4000021, 4003000], [4021000, 4000014, 4000021, 4003000], [4021005, 4021008, 4000030, 4003000], [4011007, 4011000, 4021007, 4000030, 4003000]]
            List matQtySet = [15, [30, 20], [30, 20], [30, 20], [2, 40], [2, 1, 10], [2, 50, 10], [3, 1, 60, 15], [3, 200, 80, 30], [3, 1, 40, 30], [1, 8, 1, 50, 50]]
            int[] costSet = [1000, 7000, 7000, 7000, 10000, 15000, 25000, 30000, 40000, 50000, 70000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 1) { //glove upgrade
            int[] itemSet = [1082033, 1082034, 1082038, 1082039, 1082043, 1082044, 1082047, 1082045, 1082076, 1082074, 1082067, 1082066, 1082093, 1082094]
            List matSet = [[1082032, 4011002], [1082032, 4021004], [1082037, 4011002], [1082037, 4021004], [1082042, 4011004], [1082042, 4011006], [1082046, 4011005], [1082046, 4011006], [1082075, 4011006], [1082075, 4021008], [1082065, 4021000], [1082065, 4011006, 4021008], [1082092, 4011001, 4000014], [1082092, 4011006, 4000027]]
            List matQtySet = [[1, 1], [1, 1], [1, 2], [1, 2], [1, 2], [1, 1], [1, 3], [1, 2], [1, 4], [1, 2], [1, 5], [1, 2, 1], [1, 7, 200], [1, 7, 150]]
            int[] costSet = [5000, 7000, 10000, 12000, 15000, 20000, 22000, 25000, 40000, 50000, 55000, 60000, 70000, 80000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 2) { //claw refine
            int[] itemSet = [1472001, 1472004, 1472007, 1472008, 1472011, 1472014, 1472018]
            List matSet = [[4011001, 4000021, 4003000], [4011000, 4011001, 4000021, 4003000], [1472000, 4011001, 4000021, 4003001], [4011000, 4011001, 4000021, 4003000], [4011000, 4011001, 4000021, 4003000], [4011000, 4011001, 4000021, 4003000], [4011000, 4011001, 4000030, 4003000]]
            List matQtySet = [[1, 20, 5], [2, 1, 30, 10], [1, 3, 20, 30], [3, 2, 50, 20], [4, 2, 80, 25], [3, 2, 100, 30], [4, 2, 40, 35]]
            int[] costSet = [2000, 3000, 5000, 15000, 30000, 40000, 50000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 3) { //claw upgrade
            int[] itemSet = [1472002, 1472003, 1472005, 1472006, 1472009, 1472010, 1472012, 1472013, 1472015, 1472016, 1472017, 1472019, 1472020]
            List matSet = [[1472001, 4011002], [1472001, 4011006], [1472004, 4011001], [1472004, 4011003], [1472008, 4011002], [1472008, 4011003], [1472011, 4011004], [1472011, 4021008], [1472014, 4021000], [1472014, 4011003], [1472014, 4021008], [1472018, 4021000], [1472018, 4021005]]
            List matQtySet = [[1, 1], [1, 1], [1, 2], [1, 2], [1, 3], [1, 3], [1, 4], [1, 1], [1, 5], [1, 5], [1, 2], [1, 6], [1, 6]]
            int[] costSet = [1000, 2000, 3000, 5000, 10000, 15000, 20000, 25000, 30000, 30000, 35000, 40000, 40000]
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
            cm.sendOk("I'm afraid you cannot afford my services.")
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
            cm.sendOk("What are you trying to pull? I can't make anything unless you bring me what I ask for.")
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
            cm.sendOk("All done. If you need anything else... Well, I'm not going anywhere.")
         }
         cm.dispose()
      }
   }
}

NPC1052002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1052002(cm: cm))
   }
   return (NPC1052002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }