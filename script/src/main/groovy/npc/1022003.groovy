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

class NPC1022003 {
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
         String selStr = I18nMessage.from("1022003_PROCESS_SOME_ORES").to(cm.getClient()).evaluate()
         UserMessage[] options = [I18nMessage.from("1022003_REFINE_A_MINERAL"),
                                  I18nMessage.from("1022003_REFINE_A_JEWEL"),
                                  I18nMessage.from("1022003_UPGRADE_A_HELMET"),
                                  I18nMessage.from("1022003_UPGRADE_A_SHIELD")]
         for (int i = 0; i < options.length; i++) {
            selStr += "\r\n#L" + i + "# " + options[i].to(cm.getClient()).evaluate() + "#l"
         }

         cm.sendSimple(SimpleMessage.from(selStr))
      } else if (status == 1 && mode == 1) {
         selectedType = selection
         if (selectedType == 0) { //mineral refine
            String selStr = I18nMessage.from("1022003_WHAT_KIND_OF_ORE").to(cm.getClient()).evaluate()
            UserMessage[] minerals = [I18nMessage.from("BRONZE"), I18nMessage.from("STEEL"),
                                      I18nMessage.from("MITHRIL"), I18nMessage.from("ADAMANTIUM"),
                                      I18nMessage.from("SILVER"), I18nMessage.from("ORIHALCON"),
                                      I18nMessage.from("GOLD")]
            for (int i = 0; i < minerals.length; i++) {
               selStr += "\r\n#L" + i + "# " + minerals[i].to(cm.getClient()).evaluate() + "#l"
            }
            cm.sendSimple(SimpleMessage.from(selStr))
            equip = false
         } else if (selectedType == 1) { //jewel refine
            String selStr = I18nMessage.from("1022003_WHAT_KIND_OF_JEWEL").to(cm.getClient()).evaluate()
            UserMessage[] jewels = [I18nMessage.from("GARNET"), I18nMessage.from("AMETHYST"),
                                    I18nMessage.from("AQUAMARINE"), I18nMessage.from("EMERALD"),
                                    I18nMessage.from("OPAL"), I18nMessage.from("SAPPHIRE"),
                                    I18nMessage.from("TOPAZ"), I18nMessage.from("DIAMOND"),
                                    I18nMessage.from("BLACK_CRYSTAL")]
            for (int i = 0; i < jewels.length; i++) {
               selStr += "\r\n#L" + i + "# " + jewels[i].to(cm.getClient()).evaluate() + "#l"
            }
            cm.sendSimple(SimpleMessage.from(selStr))
            equip = false
         } else if (selectedType == 2) { //helmet refine
            String selStr = I18nMessage.from("1022003_WHICH_HELMET").to(cm.getClient()).evaluate()
            UserMessage[] helmets = [I18nMessage.from("BLUE_METAL_GEAR"), I18nMessage.from("YELLOW_METAL_GEAR"),
                                     I18nMessage.from("METAL_KOIF"), I18nMessage.from("MITHRIL_KOIF"),
                                     I18nMessage.from("STEEL_HELMET"), I18nMessage.from("MITHRIL_HELMET"),
                                     I18nMessage.from("STEEL_FULL_HELMET"), I18nMessage.from("MITHRIL_FULL_HELMET"),
                                     I18nMessage.from("IRON_VIKING_HELMET"), I18nMessage.from("MITHRIL_VIKING_HELMET"),
                                     I18nMessage.from("STEEL_FOOTBALL_HELMET"), I18nMessage.from("MITHRIL_FOOTBALL_HELMET"),
                                     I18nMessage.from("MITHRIL_SHARP_HELMET"), I18nMessage.from("GOLD_SHARP_HELMET"),
                                     I18nMessage.from("ORIHALCON_BURGERNET_HELMET"), I18nMessage.from("GOLD_BURGERNET_HELMET"),
                                     I18nMessage.from("GREAT_RED_HELMET"), I18nMessage.from("GREAT_BLUE_HELMET"),
                                     I18nMessage.from("MITHRIL_NORDIC_HELMET"), I18nMessage.from("GOLD_NORDIC_HELMET"),
                                     I18nMessage.from("MITHRIL_CRUSADER_HELMET"), I18nMessage.from("SILVER_CRUSADER_HELMET"),
                                     I18nMessage.from("OLD_STEEL_NORDIC_HELMET"), I18nMessage.from("OLD_MITHRIL_NORDIC_HELMET")]
            for (int i = 0; i < helmets.length; i++) {
               selStr += "\r\n#L" + i + "# " + helmets[i].to(cm.getClient()).evaluate() + "#l"
            }
            cm.sendSimple(SimpleMessage.from(selStr))
            equip = true
         } else if (selectedType == 3) { //shield refine
            String selStr = I18nMessage.from("1022003_WHICH_SHIELD")
            UserMessage[] shields = [I18nMessage.from("ADAMANTIUM_TOWER_SHIELD"),
                                     I18nMessage.from("MITHRIL_TOWER_SHIELD"),
                                     I18nMessage.from("SILVER_LEGEND_SHIELD"),
                                     I18nMessage.from("ADAMANTIUM_LEGEND_SHIELD")]
            for (int i = 0; i < shields.length; i++) {
               selStr += "\r\n#L" + i + "# " + shields[i].to(cm.getClient()).evaluate() + "#l"
            }
            cm.sendSimple(SimpleMessage.from(selStr))
            equip = true
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
            int[] costSet = [300, 300, 300, 500, 500, 500, 800]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 1) { //jewel refine
            int[] itemSet = [4021000, 4021001, 4021002, 4021003, 4021004, 4021005, 4021006, 4021007, 4021008]
            List matSet = [4020000, 4020001, 4020002, 4020003, 4020004, 4020005, 4020006, 4020007, 4020008]
            List matQtySet = [10, 10, 10, 10, 10, 10, 10, 10, 10]
            int[] costSet = [500, 500, 500, 500, 500, 500, 500, 1000, 3000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         }

         cm.sendGetNumber(I18nMessage.from("1022003_HOW_MANY").with(item), 1, 1, 100)
      } else if (status == 3 && mode == 1) {
         if (equip) {
            selectedItem = selection
            qty = 1
         } else {
            qty = (selection > 0) ? selection : (selection < 0 ? -selection : 1)
         }

         if (selectedType == 2) { //helmet refine
            int[] itemSet = [1002042, 1002041, 1002002, 1002044, 1002003, 1002040, 1002007, 1002052, 1002011, 1002058, 1002009, 1002056, 1002087, 1002088, 1002050, 1002049, 1002047, 1002048, 1002099, 1002098, 1002085, 1002028, 1002022, 1002101]
            List matSet = [[1002001, 4011002], [1002001, 4021006], [1002043, 4011001], [1002043, 4011002], [1002039, 4011001], [1002039, 4011002], [1002051, 4011001], [1002051, 4011002], [1002059, 4011001], [1002059, 4011002],
                           [1002055, 4011001], [1002055, 4011002], [1002027, 4011002], [1002027, 4011006], [1002005, 4011005], [1002005, 4011006], [1002004, 4021000], [1002004, 4021005], [1002021, 4011002], [1002021, 4011006], [1002086, 4011002],
                           [1002086, 4011004], [1002100, 4011007, 4011001], [1002100, 4011007, 4011002]]
            List matQtySet = [[1, 1], [1, 1], [1, 1], [1, 1], [1, 1], [1, 1], [1, 2], [1, 2], [1, 3], [1, 3], [1, 3], [1, 3], [1, 4], [1, 4], [1, 5], [1, 5], [1, 3], [1, 3],
                              [1, 5], [1, 6], [1, 5], [1, 4], [1, 1, 7], [1, 1, 7]]
            int[] costSet = [500, 300, 500, 800, 500, 800, 1000, 1500, 1500, 2000, 1500, 2000, 2000, 4000, 4000, 5000, 8000, 10000, 12000, 15000, 20000, 25000, 30000, 30000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 3) { //shield refine
            int[] itemSet = [1092014, 1092013, 1092010, 1092011]
            List matSet = [[1092012, 4011003], [1092012, 4011002], [1092009, 4011007, 4011004], [1092009, 4011007, 4011003]]
            List matQtySet = [[1, 10], [1, 10], [1, 1, 15], [1, 1, 15]]
            int[] costSet = [100000, 100000, 120000, 120000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         }
         String itemPrompt = ""
         if (qty == 1) {
            itemPrompt += "a #t" + item + "#?"
         } else {
            itemPrompt += qty + " #t" + item + "#?"
         }

         String materialList = ""
         if (mats instanceof ArrayList && matQty instanceof ArrayList) {
            for (int i = 0; i < mats.size(); i++) {
               materialList += "\r\n#i" + mats[i] + "# " + ((matQty[i] as Integer) * qty) + " #t" + mats[i] + "#"
            }
         } else {
            materialList += "\r\n#i" + mats + "# " + ((matQty as Integer) * qty) + " #t" + mats + "#"
         }
         if (cost > 0) {
            materialList += "\r\n#i4031138# " + cost * qty + " meso"
         }
         cm.sendYesNo(I18nMessage.from("1022003_CONFIRM").with(itemPrompt, materialList))
      } else if (status == 4 && mode == 1) {
         boolean complete = true

         if (!cm.canHold(item, qty)) {
            cm.sendOk(I18nMessage.from("1022003_NEED_FREE_SLOT"))
            cm.dispose()
            return
         } else if (cm.getMeso() < cost * qty) {
            cm.sendOk(I18nMessage.from("1022003_CANNOT_AFFORD"))
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
            cm.sendOk(I18nMessage.from("1022003_MISSING_SOMETHING"))
         } else {
            if (mats instanceof ArrayList && matQty instanceof ArrayList) {
               for (int i = 0; i < mats.size(); i++) {
                  cm.gainItem(mats[i] as Integer, (short) ((-matQty[i] as Integer) * qty))
               }
            } else {
               cm.gainItem(mats as Integer, (short) ((-matQty as Integer) * qty))
            }
            cm.gainMeso(-cost * qty)
            cm.gainItem(item, (short) qty)
            cm.sendOk(I18nMessage.from("1022003_FINISHED"))
         }
         cm.dispose()
      }
   }
}

NPC1022003 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1022003(cm: cm))
   }
   return (NPC1022003) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }