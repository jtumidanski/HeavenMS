package npc

import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2010003 {
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
         String selStr = "Hello there. I'm Orbis' number one glove maker. Would you like me to make you something?#b"
         String[] options = ["Create or upgrade a Warrior glove", "Create or upgrade a Bowman glove", "Create or upgrade a Magician glove", "Create or upgrade a Thief glove"]
         for (int i = 0; i < options.length; i++) {
            selStr += "\r\n#L" + i + "# " + options[i] + "#l"
         }

         cm.sendSimple(selStr)
      } else if (status == 1 && mode == 1) {
         selectedType = selection
         if (selectedType == 0) { //warrior glove
            String selStr = "Warrior glove? Okay, then which one?#b"
            String[] gloves = ["Bronze Husk#k - Warrior Lv. 70#b", "Mithril Husk#k - Warrior Lv. 70#b", "Dark Husk#k - Warrior Lv. 70#b",
                               "Sapphire Emperor#k - Warrior Lv. 80#b", "Emerald Emperor#k - Warrior Lv. 80#b", "Blood Emperor#k - Warrior Lv. 80#b", "Dark Emperor#k - Warrior Lv. 80#b"]
            for (int i = 0; i < gloves.length; i++) {
               selStr += "\r\n#L" + i + "# " + gloves[i] + "#l"
            }
            cm.sendSimple(selStr)
         } else if (selectedType == 1) { //bowman glove
            String selStr = "Bowman glove? Okay, then which one?#b"
            String[] gloves = ["Blue Eyes#k - Bowman Lv. 70#b", "Gold Eyes#k - Bowman Lv. 70#b", "Dark Eyes#k - Bowman Lv. 70#b",
                               "Red Cordon#k - Bowman Lv. 80#b", "Blue Cordon#k - Bowman Lv. 80#b", "Green Cordon#k - Bowman Lv. 80#b", "Dark Cordon#k - Bowman Lv. 80#b"]
            for (int i = 0; i < gloves.length; i++) {
               selStr += "\r\n#L" + i + "# " + gloves[i] + "#l"
            }
            cm.sendSimple(selStr)
         } else if (selectedType == 2) { //magician glove
            String selStr = "Magician glove? Okay, then which one?#b"
            String[] gloves = ["Brown Lorin#k - Magician Lv. 70#b", "Blue Lorin#k - Magician Lv. 70#b", "Dark Lorin#k - Magician Lv. 70#b",
                               "Green Clarity#k - Magician Lv. 80#b", "Blue Clarity#k - Magician Lv. 80#b", "Dark Clarity#k - Magician Lv. 80#b"]
            for (int i = 0; i < gloves.length; i++) {
               selStr += "\r\n#L" + i + "# " + gloves[i] + "#l"
            }
            cm.sendSimple(selStr)
         } else if (selectedType == 3) { //thief glove
            String selStr = "Thief glove? Okay, then which one?#b"
            String[] gloves = ["Bronze Rover#k - Thief Lv. 70#b", "Silver Rover#k - Thief Lv. 70#b", "Gold Rover#k - Thief Lv. 70#b",
                               "Green Larceny#k - Thief Lv. 80#b", "Purple Larceny#k - Thief Lv. 80#b", "Dark Larceny#k - Thief Lv. 80#b"]
            for (int i = 0; i < gloves.length; i++) {
               selStr += "\r\n#L" + i + "# " + gloves[i] + "#l"
            }
            cm.sendSimple(selStr)
         }
      } else if (status == 2 && mode == 1) {
         selectedItem = selection

         if (selectedType == 0) { //warrior glove
            int[] itemSet = [1082103, 1082104, 1082105, 1082114, 1082115, 1082116, 1082117, 1082118]
            List matSet = [[4005000, 4011000, 4011006, 4000030, 4003000], [1082103, 4011002, 4021006], [1082103, 4021006, 4021008], [4005000, 4005002, 4021005, 4000030, 4003000], [1082114, 4005000, 4005002, 4021003], [1082114, 4005002, 4021000], [1082114, 4005000, 4005002, 4021008]]
            List matQtySet = [[2, 8, 3, 70, 55], [1, 6, 4], [1, 8, 3], [2, 1, 8, 90, 60], [1, 1, 1, 7], [1, 3, 8], [1, 2, 1, 4]]
            int[] costSet = [90000, 90000, 100000, 100000, 110000, 110000, 120000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 1) { //bowman glove
            int[] itemSet = [1082106, 1082107, 1082108, 1082109, 1082110, 1082111, 1082112]
            List matSet = [[4005002, 4021005, 4011004, 4000030, 4003000], [1082106, 4021006, 4011006], [1082106, 4021007, 4021008], [4005002, 4005000, 4021000, 4000030, 4003000], [1082109, 4005002, 4005000, 4021005], [1082109, 4005002, 4005000, 4021003], [1082109, 4005002, 4005000, 4021008]]
            List matQtySet = [[2, 8, 3, 70, 55], [1, 5, 3], [1, 2, 3], [2, 1, 8, 90, 60], [1, 1, 1, 7], [1, 1, 1, 7], [1, 2, 1, 4]]
            int[] costSet = [90000, 90000, 100000, 100000, 110000, 110000, 120000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 2) { //magician glove
            int[] itemSet = [1082098, 1082099, 1082100, 1082121, 1082122, 1082123]
            List matSet = [[4005001, 4011000, 4011004, 4000030, 4003000], [1082098, 4021002, 4021007], [1082098, 4021008, 4011006], [4005001, 4005003, 4021003, 4000030, 4003000], [1082121, 4005001, 4005003, 4021005], [1082121, 4005001, 4005003, 4021008]]
            List matQtySet = [[2, 6, 6, 70, 55], [1, 6, 2], [1, 3, 3], [2, 1, 8, 90, 60], [1, 1, 1, 7], [1, 2, 1, 4]]
            int[] costSet = [90000, 90000, 100000, 100000, 110000, 120000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 3) { //thief glove
            int[] itemSet = [1082095, 1082096, 1082097, 1082118, 1082119, 1082120]
            List matSet = [[4005003, 4011000, 4011003, 4000030, 4003000], [1082095, 4011004, 4021007], [1082095, 4021007, 4011006], [4005003, 4005002, 4011002, 4000030, 4003000], [1082118, 4005003, 4005002, 4021001], [1082118, 4005003, 4005002, 4021000]]
            List matQtySet = [[2, 6, 6, 70, 55], [1, 6, 2], [1, 3, 3], [2, 1, 8, 90, 60], [1, 1, 1, 7], [1, 2, 1, 8]]
            int[] costSet = [90000, 90000, 100000, 100000, 110000, 120000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         }

         String prompt = "You want me to make a #t" + item + "#? In that case, I'm going to need specific items from you in order to make it. Make sure you have room in your inventory, though!#b"

         if (mats instanceof ArrayList && matQty instanceof ArrayList) {
            for (int i = 0; i < mats.size(); i++) {
               prompt += "\r\n#i" + mats[i] + "# " + matQty[i] + " #t" + mats[i] + "#"
            }
         } else {
            prompt += "\r\n#i" + mats + "# " + matQty + " #t" + mats + "#"
         }

         if (cost > 0) {
            prompt += "\r\n#i4031138# " + cost + " meso"
         }

         cm.sendYesNo(prompt)
      } else if (status == 3 && mode == 1) {
         boolean complete = true

         if (!cm.canHold(item, 1)) {
            cm.sendOk("Check your inventory for a free slot first.")
            cm.dispose()
            return
         } else if (cm.getMeso() < cost) {
            cm.sendOk("I'm afraid you cannot afford my services.")
            cm.dispose()
            return
         } else {
            if (mats instanceof ArrayList && matQty instanceof ArrayList) {
               for (int i = 0; complete && i < mats.size(); i++) {
                  if (!cm.haveItem(mats[i] as Integer, matQty[i] as Integer)) {
                     complete = false
                  }
               }
            } else if (!cm.haveItem(mats as Integer, matQty as Integer)) {
               complete = false
            }
         }

         if (!complete) {
            cm.sendOk("I'm afraid that substitute items are unacceptable, if you want your gloves made properly.")
         } else {
            if (mats instanceof ArrayList && matQty instanceof ArrayList) {
               for (int i = 0; i < mats.size(); i++) {
                  cm.gainItem(mats[i] as Integer, (short) (-matQty[i] as Integer))
               }
            } else {
               cm.gainItem(mats as Integer, (short) (-matQty as Integer))
            }

            cm.gainMeso(-cost)
            cm.gainItem(item, (short) 1)
            cm.sendOk("Done. If you need anything else, just ask again.")
         }
         cm.dispose()
      }
   }
}

NPC2010003 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2010003(cm: cm))
   }
   return (NPC2010003) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }