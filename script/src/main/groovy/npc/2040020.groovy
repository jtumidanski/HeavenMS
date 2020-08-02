package npc

import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2040020 {
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

   boolean stimulator = false
   int stimulatorId = 4130000

   def start() {
      cm.getPlayer().setCS(true)

      String selStr = "Hello, and welcome to the Ludibrium Glove Store. How can I help you today?#b"
      String[] options = ["What's a stimulator?", "Create a Warrior glove", "Create a Bowman glove", "Create a Magician glove", "Create a Thief glove",
                          "Create a Warrior glove with a Stimulator", "Create a Bowman glove with a Stimulator", "Create a Magician glove with a Stimulator", "Create a Thief glove with a Stimulator"]
      for (int i = 0; i < options.length; i++) {
         selStr += "\r\n#L" + i + "# " + options[i] + "#l"
      }
      cm.sendSimple(selStr)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode > 0) {
         status++
      } else {
         cm.dispose()
         return
      }
      if (status == 1) {
         selectedType = selection
         stimulator = selectedType > 4
         if (selectedType == 0) { //What's a stimulator?
            cm.sendNext(I18nMessage.from("2040020_STIMULATOR_INFO"))
            cm.dispose()
         } else if (selectedType == 1) { //warrior glove
            String selStr = "Warrior glove? Sure thing, which kind?#b"
            String[] items = ["Bronze Missel#k - Warrior Lv. 30#b", "Steel Briggon#k - Warrior Lv. 35#b", "Iron Knuckle#k - Warrior Lv. 40#b", "Steel Brist#k - Warrior Lv. 50#b"]
            for (int i = 0; i < items.length; i++) {
               selStr += "\r\n#L" + i + "# " + items[i] + "#l"
            }
            cm.sendSimple(selStr)
         } else if (selectedType == 2) { //bowman glove
            String selStr = "Bowman glove? Sure thing, which kind?#b"
            String[] items = ["Brown Marker#k - Bowman Lv. 30#b", "Bronze Scaler#k - Bowman Lv. 35#b", "Aqua Brace#k - Bowman Lv. 40#b", "Blue Willow#k - Bowman Lv. 50#b"]
            for (int i = 0; i < items.length; i++) {
               selStr += "\r\n#L" + i + "# " + items[i] + "#l"
            }
            cm.sendSimple(selStr)
         } else if (selectedType == 3) { //magician glove
            String selStr = "Magician glove? Sure thing, which kind?#b"
            String[] items = ["Red Lutia#k - Magician Lv. 30#b", "Red Noel#k - Magician Lv. 35#b", "Red Arten#k - Magician Lv. 40#b", "Red Pennance#k - Magician Lv. 50#b"]
            for (int i = 0; i < items.length; i++) {
               selStr += "\r\n#L" + i + "# " + items[i] + "#l"
            }
            cm.sendSimple(selStr)
         } else if (selectedType == 4) { //thief glove
            String selStr = "Thief glove? Sure thing, which kind?#b"
            String[] gloves = ["Steel Sylvia#k - Thief Lv. 30#b", "Steel Arbion#k - Thief Lv. 35#b", "Red Cleave#k - Thief Lv. 40#b", "Blue Moon Glove#k - Thief Lv. 50#b"]
            for (int i = 0; i < gloves.length; i++) {
               selStr += "\r\n#L" + i + "# " + gloves[i] + "#l"
            }
            cm.sendSimple(selStr)
         } else if (selectedType == 5) { //warrior glove w/ Stim
            String selStr = "Warrior glove with a stimulator? Sure thing, which kind?#b"
            String[] crystals = ["Steel Missel#k - Warrior Lv. 30#b", "Orihalcon Missel#k - Warrior Lv. 30#b", "Yellow Briggon#k - Warrior Lv. 35#b", "Dark Briggon#k - Warrior Lv. 35#b",
                                 "Adamantium Knuckle#k - Warrior Lv. 40#b", "Dark Knuckle#k - Warrior Lv. 40#b", "Mithril Brist#k - Warrior Lv. 50#b", "Gold Brist#k - Warrior Lv. 50#b"]
            for (int i = 0; i < crystals.length; i++) {
               selStr += "\r\n#L" + i + "# " + crystals[i] + "#l"
            }
            cm.sendSimple(selStr)
         } else if (selectedType == 6) { //bowman glove w/ stim
            String selStr = "Bowman glove with a stimulator? Sure thing, which kind?#b"
            String[] crystals = ["Green Marker#k - Bowman Lv. 30#b", "Black Marker#k - Bowman Lv. 30#b", "Mithril Scaler#k - Bowman Lv. 35#b", "Gold Scaler#k - Bowman Lv. 35#b", "Gold Brace#k - Bowman Lv. 40#b", "Dark Brace#k - Bowman Lv. 40#b", "Red Willow#k - Bowman Lv. 50#b", "Dark Willow#k - Bowman Lv. 50#b"]
            for (int i = 0; i < crystals.length; i++) {
               selStr += "\r\n#L" + i + "# " + crystals[i] + "#l"
            }
            cm.sendSimple(selStr)
         } else if (selectedType == 7) { //magician glove w/ stim
            String selStr = "Magician glove with a stimulator? Sure thing, which kind?#b"
            String[] items = ["Blue Lutia#k - Magician Lv. 30#b", "Black Lutia#k - Magician Lv. 30#b", "Blue Noel#k - Magician Lv. 35#b", "Dark Noel#k - Magician Lv. 35#b",
                              "Blue Arten#k - Magician Lv. 40#b", "Dark Arten#k - Magician Lv. 40#b", "Blue Pennance#k - Magician Lv. 50#b", "Dark Pennance#k - Magician Lv. 50#b"]
            for (int i = 0; i < items.length; i++) {
               selStr += "\r\n#L" + i + "# " + items[i] + "#l"
            }
            cm.sendSimple(selStr)
         } else if (selectedType == 8) { //thief glove w/ stimulator
            String selStr = "Thief glove with a stimulator? Sure thing, which kind?#b"
            String[] gloves = ["Silver Sylvia#k - Thief Lv. 30#b", "Gold Sylvia#k - Thief Lv. 30#b", "Orihalcon Arbion#k - Thief Lv. 35#b", "Gold Arbion#k - Thief Lv. 35#b", "Gold Cleave#k - Thief Lv. 40#b",
                               "Dark Cleave#k - Thief Lv. 40#b", "Red Moon Glove#k - Thief Lv. 50#b", "Brown Moon Glove#k - Thief Lv. 50#b"]
            for (int i = 0; i < gloves.length; i++) {
               selStr += "\r\n#L" + i + "# " + gloves[i] + "#l"
            }
            cm.sendSimple(selStr)
         }
      } else if (status == 2) {
         selectedItem = selection
         if (selectedType == 1) { //warrior glove
            int[] itemSet = [1082007, 1082008, 1082023, 1082009]
            List matSet = [[4011000, 4011001, 4003000], [4000021, 4011001, 4003000], [4000021, 4011001, 4003000], [4011001, 4021007, 4000030, 4003000]]
            List matQtySet = [[3, 2, 15], [30, 4, 15], [50, 5, 40], [3, 2, 30, 45]]
            int[] costSet = [18000, 27000, 36000, 45000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 2) { //bowman glove
            int[] itemSet = [1082048, 1082068, 1082071, 1082084]
            List matSet = [[4000021, 4011006, 4021001], [4011000, 4011001, 4000021, 4003000], [4011001, 4021000, 4021002, 4000021, 4003000], [4011004, 4011006, 4021002, 4000030, 4003000]]
            List matQtySet = [[50, 2, 1], [1, 3, 60, 15], [3, 1, 3, 80, 25], [3, 1, 2, 40, 35]]
            int[] costSet = [18000, 27000, 36000, 45000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 3) { //magician glove
            int[] itemSet = [1082051, 1082054, 1082062, 1082081]
            List matSet = [[4000021, 4021006, 4021000], [4000021, 4011006, 4011001, 4021000], [4000021, 4021000, 4021006, 4003000], [4021000, 4011006, 4000030, 4003000]]
            List matQtySet = [[60, 1, 2], [70, 1, 3, 2], [80, 3, 3, 30], [3, 2, 35, 40]]
            int[] costSet = [22500, 27000, 36000, 45000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 4) { //thief glove
            int[] itemSet = [1082042, 1082046, 1082075, 1082065]
            List matSet = [[4011001, 4000021, 4003000], [4011001, 4011000, 4000021, 4003000], [4021000, 4000101, 4000021, 4003000], [4021005, 4021008, 4000030, 4003000]]
            List matQtySet = [[2, 50, 10], [3, 1, 60, 15], [3, 100, 80, 30], [3, 1, 40, 30]]
            int[] costSet = [22500, 27000, 36000, 45000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 5) { //warrior glove w/stimulator
            int[] itemSet = [1082005, 1082006, 1082035, 1082036, 1082024, 1082025, 1082010, 1082011]
            List matSet = [[1082007, 4011001], [1082007, 4011005], [1082008, 4021006], [1082008, 4021008], [1082023, 4011003], [1082023, 4021008],
                           [1082009, 4011002], [1082009, 4011006]]
            List matQtySet = [[1, 1], [1, 2], [1, 3], [1, 1], [1, 4], [1, 2], [1, 5], [1, 4]]
            int[] costSet = [18000, 22500, 27000, 36000, 40500, 45000, 49500, 54000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 6) { //bowman glove w/stimulator
            int[] itemSet = [1082049, 1082050, 1082069, 1082070, 1082072, 1082073, 1082085, 1082083]
            List matSet = [[1082048, 4021003], [1082048, 4021008], [1082068, 4011002], [1082068, 4011006], [1082071, 4011006], [1082071, 4021008], [1082084, 4011000, 4021000], [1082084, 4011006, 4021008]]
            List matQtySet = [[1, 3], [1, 1], [1, 4], [1, 2], [1, 4], [1, 2], [1, 1, 5], [1, 2, 2]]
            int[] costSet = [13500, 18000, 19800, 22500, 27000, 36000, 49500, 54000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 7) { //magician glove w/ stimulator
            int[] itemSet = [1082052, 1082053, 1082055, 1082056, 1082063, 1082064, 1082082, 1082080]
            List matSet = [[1082051, 4021005], [1082051, 4021008], [1082054, 4021005], [1082054, 4021008], [1082062, 4021002], [1082062, 4021008],
                           [1082081, 4021002], [1082081, 4021008]]
            List matQtySet = [[1, 3], [1, 1], [1, 3], [1, 1], [1, 4], [1, 2], [1, 5], [1, 3]]
            int[] costSet = [31500, 36000, 36000, 40500, 40500, 45000, 49500, 54000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 8) { //thief glove w/ stimulator
            int[] itemSet = [1082043, 1082044, 1082047, 1082045, 1082076, 1082074, 1082067, 1082066]
            List matSet = [[1082042, 4011004], [1082042, 4011006], [1082046, 4011005], [1082046, 4011006], [1082075, 4011006], [1082075, 4021008], [1082065, 4021000], [1082065, 4011006, 4021008]]
            List matQtySet = [[1, 2], [1, 1], [1, 3], [1, 2], [1, 4], [1, 2], [1, 5], [1, 2, 1]]
            int[] costSet = [13500, 18000, 19800, 22500, 36000, 45000, 49500, 54000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         }
         String prompt = "You want me to make a #t" + item + "#? In that case, I'm going to need specific items from you in order to make it. Make sure you have room in your inventory, though!#b"
         if (stimulator) {
            prompt += "\r\n#i" + stimulatorId + "# 1 #t" + stimulatorId + "#"
         }
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
      } else if (status == 3) {
         boolean complete = true

         if (!cm.canHold(item, 1)) {
            cm.sendOk(I18nMessage.from("2040020_NEED_FREE_SLOT"))
            cm.dispose()
            return
         } else if (cm.getMeso() < cost) {
            cm.sendOk(I18nMessage.from("2040020_ONLY_ACCEPT_MESO"))
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
         if (stimulator) { //check for stimulator
            if (!cm.haveItem(stimulatorId)) {
               complete = false
            }
         }
         if (!complete) {
            cm.sendOk(I18nMessage.from("2040020_MISSING_ITEMS"))
         } else {
            if (mats instanceof ArrayList && matQty instanceof ArrayList) {
               for (int i = 0; i < mats.size(); i++) {
                  cm.gainItem(mats[i] as Integer, (short) (-matQty[i] as Integer))
               }
            } else {
               cm.gainItem(mats as Integer, (short) (-matQty as Integer))
            }
            cm.gainMeso(-cost)
            if (stimulator) { //check for stimulator
               cm.gainItem(stimulatorId, (short) -1)
               int deleted = Math.floor(Math.random() * 10).intValue()
               if (deleted != 0) {
                  //TODO
                  cm.gainRandomItem(newItem)
                  cm.sendOk(I18nMessage.from("2040020_GLOVE_SUCCESS"))
               } else {
                  cm.sendOk(I18nMessage.from("2040020_STIMULATOR_ERROR"))
               }
            } else {
               cm.gainItem(item, (short) 1)
               cm.sendOk(I18nMessage.from("2040020_GLOVE_SUCCESS"))
            }
         }
         cm.dispose()
      }
   }
}

NPC2040020 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040020(cm: cm))
   }
   return (NPC2040020) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }