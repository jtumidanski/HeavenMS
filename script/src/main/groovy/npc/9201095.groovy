package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201095 {
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
      if (!cm.isQuestCompleted(8225)) {
         cm.sendOk(I18nMessage.from("9201095_STEP_ASIDE"))

         cm.dispose()
         return
      }

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
         String selStr = "Hey, partner! If you have the right goods, I can turn it into something very nice...#b"
         String[] options = ["Weapon Forging", "Weapon Upgrading"]
         for (int i = 0; i < options.length; i++) {
            selStr += "\r\n#L" + i + "# " + options[i] + "#l"
         }
         cm.sendSimple(selStr)
      } else if (status == 1 && mode == 1) {
         selectedType = selection
         if (selectedType == 0) { //weapon forge
            String selStr = "So, what kind of weapon would you like me to forge?#b"
            String[] weapon = ["#t2070018#", "#t1382060#", "#t1442068#", "#t1452060#"]
            for (int i = 0; i < weapon.length; i++) {
               selStr += "\r\n#L" + i + "# " + weapon[i] + "#l"
            }

            cm.sendSimple(selStr)
         } else if (selectedType == 1) { //weapon upgrade
            String selStr = "An upgraded weapon? Of course, but note that upgrades won't carry over to the new item... #b"
            String[] weapon = ["#t1472074#", "#t1472073#", "#t1472075#", "#t1332079#", "#t1332078#", "#t1332080#", "#t1462054#", "#t1462053#", "#t1462055#", "#t1402050#", "#t1402049#", "#t1402051#"]
            for (int i = 0; i < weapon.length; i++) {
               selStr += "\r\n#L" + i + "# " + weapon[i] + "#l"
            }

            cm.sendSimple(selStr)
         }

      } else if (status == 2 && mode == 1) {
         qty = 1
         selectedItem = selection

         if (selectedType == 0) { // weapon forge
            int[] itemSet = [2070018, 1382060, 1442068, 1452060]
            List matSet = [[4032015, 4032016, 4032017, 4021008, 4032005], [4032016, 4032017, 4032004, 4032005, 4032012, 4005001], [4032015, 4032017, 4032004, 4032005, 4032012, 4005000], [4032015, 4032016, 4032004, 4032005, 4032012, 4005002]]
            List matQtySet = [[1, 1, 1, 100, 30], [1, 1, 400, 10, 30, 4], [1, 1, 500, 40, 20, 4], [1, 1, 300, 75, 10, 4]]
            int[] costSet = [70000, 70000, 70000, 70000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 1) { // weapon upgrade
            int[] itemSet = [1472074, 1472073, 1472075, 1332079, 1332078, 1332080, 1462054, 1462053, 1462055, 1402050, 1402049, 1402051]
            List matSet = [[4032017, 4005001, 4021008], [4032015, 4005002, 4021008], [4032016, 4005000, 4021008], [4032017, 4005001, 4021008], [4032015, 4005002, 4021008], [4032016, 4005000, 4021008], [4032017, 4005001, 4021008], [4032015, 4005002, 4021008], [4032016, 4005000, 4021008], [4032017, 4005001, 4021008], [4032015, 4005002, 4021008], [4032016, 4005000, 4021008]]
            List matQtySet = [[1, 10, 20], [1, 10, 30], [1, 5, 20], [1, 10, 20], [1, 10, 30], [1, 5, 20], [1, 10, 20], [1, 10, 30], [1, 5, 20], [1, 10, 20], [1, 10, 30], [1, 5, 20]]
            int[] costSet = [75000, 50000, 50000, 75000, 50000, 50000, 75000, 50000, 50000, 75000, 50000, 50000]
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
      } else if (status == 3 && mode == 1) {
         boolean complete = true
         int recvItem = item, recvQty

         recvQty = qty

         if (!cm.canHold(recvItem, recvQty)) {
            cm.sendOk(I18nMessage.from("9201095_FREE_SLOT_NEEDED"))

            cm.dispose()
            return
         } else if (cm.getMeso() < cost * qty) {
            cm.sendOk(I18nMessage.from("9201095_NOT_ENOUGH_MESO"))

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
            cm.sendOk(I18nMessage.from("9201095_NEED_ITEMS"))

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
            cm.sendOk(I18nMessage.from("9201095_ALL_DONE"))

         }
         cm.dispose()
      }
   }
}

NPC9201095 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201095(cm: cm))
   }
   return (NPC9201095) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }