package npc

import scripting.npc.NPCConversationManager
import tools.I18nMessage
import tools.SimpleMessage
import tools.UserMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1032002 {
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
         String selStr = I18nMessage.from("1032002_WHAT_WOULD_YOU_LIKE").to(cm.getClient()).evaluate()
         UserMessage[] options = [I18nMessage.from("1032002_MAKE_A_GLOVE"), I18nMessage.from("1032002_UPGRADE_A_GLOVE"), I18nMessage.from("1032002_UPGRADE_A_HAT"),
                                  I18nMessage.from("1032002_MAKE_A_WAND"), I18nMessage.from("1032002_MAKE_A_STAFF")]
         for (int i = 0; i < options.length; i++) {
            selStr += "\r\n#L" + i + "# " + options[i].to(cm.getClient()).evaluate() + "#l"
         }

         cm.sendSimple(SimpleMessage.from(selStr))
      } else if (status == 1 && mode == 1) {
         selectedType = selection
         if (selectedType == 0) { //glove refine
            String selStr = I18nMessage.from("1032002_WHAT_KIND_OF_GLOVE").to(cm.getClient()).evaluate()
            UserMessage[] items = [I18nMessage.from("1032002_LEMONA"), I18nMessage.from("1032002_BLUE_MORRICAN"), I18nMessage.from("1032002_OCEAN_MESANA"),
                              I18nMessage.from("1032002_RED_LUTIA"), I18nMessage.from("1032002_RED_NOEL"), I18nMessage.from("1032002_RED_ARTEN"),
                              I18nMessage.from("1032002_RED_PENNANCE"), I18nMessage.from("1032002_STEEL_MANUTE")]
            for (int i = 0; i < items.length; i++) {
               selStr += "\r\n#L" + i + "# " + items[i].to(cm.getClient()).evaluate() + "#l"
            }
            cm.sendSimple(SimpleMessage.from(selStr))
         } else if (selectedType == 1) { //glove upgrade
            String selStr = I18nMessage.from("1032002_GLOVE_UPGRADE").to(cm.getClient()).evaluate()
            UserMessage[] items = [I18nMessage.from("1032002_GREEN_MORRICAN"), I18nMessage.from("1032002_PURPLE_MORRICAN"), I18nMessage.from("1032002_BLOOD_MESANA"),
                                   I18nMessage.from("1032002_DARK_MESANA"), I18nMessage.from("1032002_BLUE_LUTIA"), I18nMessage.from("1032002_BLACK_LUTIA"),
                                   I18nMessage.from("1032002_BLUE_NOEL"), I18nMessage.from("1032002_DARK_NOEL"), I18nMessage.from("1032002_BLUE_ARTEN"),
                                   I18nMessage.from("1032002_DARK_ARTEN"), I18nMessage.from("1032002_BLUE_PENNANCE"), I18nMessage.from("1032002_DARK_PENNANCE"),
                                   I18nMessage.from("1032002_GOLD_MANUTE"), I18nMessage.from("1032002_DARK_MANUTE")]
            for (int i = 0; i < items.length; i++) {
               selStr += "\r\n#L" + i + "# " + items[i].to(cm.getClient()).evaluate() + "#l"
            }
            cm.sendSimple(SimpleMessage.from(selStr))
         } else if (selectedType == 2) { //hat upgrade
            String selStr = I18nMessage.from("1032002_WHICH_HAT").to(cm.getClient()).evaluate()
            UserMessage[] items = [I18nMessage.from("1032002_STEEL_PRIDE"), I18nMessage.from("1032002_GOLDEN_PRIDE")]
            for (int i = 0; i < items.length; i++) {
               selStr += "\r\n#L" + i + "# " + items[i].to(cm.getClient()).evaluate() + "#l"
            }
            cm.sendSimple(SimpleMessage.from(selStr))
         } else if (selectedType == 3) { //wand refine
            String selStr = I18nMessage.from("1032002_A_WAND").to(cm.getClient()).evaluate()
            UserMessage[] items = [I18nMessage.from("1032002_WOODEN_WAND"), I18nMessage.from("1032002_HARDWOOD_WAND"), I18nMessage.from("1032002_METAL_WAND"),
                              I18nMessage.from("1032002_ICE_WAND"), I18nMessage.from("1032002_MITHRIL_WAND"), I18nMessage.from("1032002_WIZARD_WAND"),
                              I18nMessage.from("1032002_FAIRY_WAND"), I18nMessage.from("1032002_CROMI")]
            for (int i = 0; i < items.length; i++) {
               selStr += "\r\n#L" + i + "# " + items[i].to(cm.getClient()).evaluate() + "#l"
            }
            cm.sendSimple(SimpleMessage.from(selStr))
         } else if (selectedType == 4) { //staff refine
            String selStr = I18nMessage.from("1032002_A_STAFF").to(cm.getClient()).evaluate()
            UserMessage[] items = [I18nMessage.from("1032002_WOODEN_STAFF"), I18nMessage.from("1032002_SAPPHIRE_STAFF"), I18nMessage.from("1032002_EMERALD_STAFF"),
                              I18nMessage.from("1032002_OLD_WOODEN_STAFF"), I18nMessage.from("1032002_WIZARD_STAFF"), I18nMessage.from("1032002_ARC_STAFF")]
            for (int i = 0; i < items.length; i++) {
               selStr += "\r\n#L" + i + "# " + items[i].to(cm.getClient()).evaluate() + "#l"
            }
            cm.sendSimple(SimpleMessage.from(selStr))
         }
      } else if (status == 2 && mode == 1) {
         selectedItem = selection

         if (selectedType == 0) { //glove refine
            int[] itemSet = [1082019, 1082020, 1082026, 1082051, 1082054, 1082062, 1082081, 1082086]
            List matSet = [4000021, [4000021, 4011001], [4000021, 4011006], [4000021, 4021006, 4021000], [4000021, 4011006, 4011001, 4021000],
                           [4000021, 4021000, 4021006, 4003000], [4021000, 4011006, 4000030, 4003000], [4011007, 4011001, 4021007, 4000030, 4003000]]
            List matQtySet = [15, [30, 1], [50, 2], [60, 1, 2], [70, 1, 3, 2], [80, 3, 3, 30], [3, 2, 35, 40], [1, 8, 1, 50, 50]]
            int[] costSet = [7000, 15000, 20000, 25000, 30000, 40000, 50000, 70000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 1) { //glove upgrade
            int[] itemSet = [1082021, 1082022, 1082027, 1082028, 1082052, 1082053, 1082055, 1082056, 1082063, 1082064, 1082082, 1082080, 1082087, 1082088]
            int[][] matSet = [[1082020, 4011001], [1082020, 4021001], [1082026, 4021000], [1082026, 4021008], [1082051, 4021005],
                              [1082051, 4021008], [1082054, 4021005], [1082054, 4021008], [1082062, 4021002], [1082062, 4021008],
                              [1082081, 4021002], [1082081, 4021008], [1082086, 4011004, 4011006], [1082086, 4021008, 4011006]]
            int[][] matQtySet = [[1, 1], [1, 2], [1, 3], [1, 1], [1, 3], [1, 1], [1, 3], [1, 1], [1, 4],
                                 [1, 2], [1, 5], [1, 3], [1, 3, 5], [1, 2, 3]]
            int[] costSet = [20000, 25000, 30000, 40000, 35000, 40000, 40000, 45000, 45000, 50000, 55000, 60000, 70000, 80000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 2) { //hat upgrade
            int[] itemSet = [1002065, 1002013]
            List matSet = [[1002064, 4011001], [1002064, 4011006]]
            List matQtySet = [[1, 3], [1, 3]]
            int[] costSet = [40000, 50000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 3) { //wand refine
            int[] itemSet = [1372005, 1372006, 1372002, 1372004, 1372003, 1372001, 1372000, 1372007]
            List matSet = [4003001, [4003001, 4000001], [4011001, 4000009, 4003000], [4011002, 4003002, 4003000], [4011002, 4021002, 4003000],
                           [4021006, 4011002, 4011001, 4003000], [4021006, 4021005, 4021007, 4003003, 4003000], [4011006, 4021003, 4021007, 4021002, 4003002, 4003000]]
            List matQtySet = [5, [10, 50], [1, 30, 5], [2, 1, 10], [3, 1, 10], [5, 3, 1, 15], [5, 5, 1, 1, 20], [4, 3, 2, 1, 1, 30]]
            int[] costSet = [1000, 3000, 5000, 12000, 30000, 60000, 120000, 200000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 4) { //staff refine
            int[] itemSet = [1382000, 1382003, 1382005, 1382004, 1382002, 1382001]
            List matSet = [4003001, [4021005, 4011001, 4003000], [4021003, 4011001, 4003000], [4003001, 4011001, 4003000],
                           [4021006, 4021001, 4011001, 4003000], [4011001, 4021006, 4021001, 4021005, 4003000, 4000010, 4003003]]
            List matQtySet = [5, [1, 1, 5], [1, 1, 5], [50, 1, 10], [2, 1, 1, 15], [8, 5, 5, 5, 30, 50, 1]]
            int[] costSet = [2000, 2000, 2000, 5000, 12000, 180000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         }

         String prompt = I18nMessage.from("1032002_WANT_ME_TO_MAKE_A").to(cm.getClient()).with(item).evaluate()

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

         cm.sendYesNo(SimpleMessage.from(prompt))
      } else if (status == 3 && mode == 1) {
         boolean complete = true

         if (!cm.canHold(item, 1)) {
            cm.sendOk(I18nMessage.from("1032002_CHECK_YOUR_INVENTORY_FOR_FREE_SLOT"))
            cm.dispose()
            return
         } else if (cm.getMeso() < cost) {
            cm.sendOk(I18nMessage.from("1032002_NEED_MORE_MESOS"))
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
            cm.sendOk(I18nMessage.from("1032002_MISSING_MATERIALS"))
         } else {
            if (mats instanceof ArrayList && matQty instanceof ArrayList) {
               for (int i = 0; i < mats.size(); i++) {
                  cm.gainItem(mats[i] as Integer, (short) (-matQty[i] as Integer))
               }
            } else {
               cm.gainItem(mats as Integer, (short) (-matQty as Integer))
            }

            if (cost > 0) {
               cm.gainMeso(-cost)
            }

            cm.gainItem(item, (short) 1)
            cm.sendOk(I18nMessage.from("1032002_SUCCESS"))
         }
         cm.dispose()
      }
   }
}

NPC1032002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1032002(cm: cm))
   }
   return (NPC1032002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }