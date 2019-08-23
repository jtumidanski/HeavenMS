package npc

import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2080000 {
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
   int stimID

   int cd_item = 4001078
   int[] cd_mats = [4011001, 4011002, 4001079]
   int[] cd_matQty = [1, 1, 1]
   int cd_cost = 25000

   def start() {
      cm.getPlayer().setCS(true)
      String selStr = "A dragon's power is not to be underestimated. If you like, I can add its power to one of your weapons. However, the weapon must be powerful enough to hold its potential...#b"
      String[] options = ["What's a stimulator?", "Create a Warrior weapon", "Create a Bowman weapon", "Create a Magician weapon", "Create a Thief weapon", "Create a Pirate Weapon",
                          "Create a Warrior weapon with a Stimulator", "Create a Bowman weapon with a Stimulator", "Create a Magician weapon with a Stimulator", "Create a Thief weapon with a Stimulator", "Create a Pirate Weapon with a Stimulator"]

      if (cm.isQuestStarted(7301) || cm.isQuestStarted(7303)) {
         options << "Make #t4001078#"
      }

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
         if (selectedType > 5 && selectedType < 11) {
            stimulator = true
            selectedType -= 5
         } else {
            stimulator = false
         }
         if (selectedType == 0) { //What's a stim?
            cm.sendNext("A stimulator is a special potion that I can add into the process of creating certain items. It gives it stats as though it had dropped from a monster. However, it is possible to have no change, and it is also possible for the item to be below average. There's also a 10% chance of not getting any item when using a stimulator, so please choose wisely.")
            cm.dispose()
         } else if (selectedType == 1) { //warrior weapon
            String selStr = "Very well, then which Warrior weapon shall receive a dragon's power?#b"
            String[] weapon = ["Dragon Carbella#k - Lv. 110 One-Handed Sword#b", "Dragon Axe#k - Lv. 110 One-Handed Axe#b", "Dragon Mace#k - Lv. 110 One-Handed BW#b", "Dragon Claymore#k - Lv. 110 Two-Handed Sword#b", "Dragon Battle Axe#k - Lv. 110 Two-Handed Axe#b", "Dragon Flame#k - Lv. 110 Two-Handed BW#b",
                               "Dragon Faltizan#k - Lv. 110 Spear#b", "Dragon Chelbird#k - Lv. 110 Polearm#b"]
            for (int i = 0; i < weapon.length; i++) {
               selStr += "\r\n#L" + i + "# " + weapon[i] + "#l"
            }
            cm.sendSimple(selStr)
         } else if (selectedType == 2) { //bowman weapon
            String selStr = "Very well, then which Bowman weapon shall receive a dragon's power?#b"
            String[] weapon = ["Dragon Shiner Bow#k - Lv. 110 Bow#b", "Dragon Shiner Cross#k - Lv. 110 Crossbow#b"]
            for (int i = 0; i < weapon.length; i++) {
               selStr += "\r\n#L" + i + "# " + weapon[i] + "#l"
            }
            cm.sendSimple(selStr)
         } else if (selectedType == 3) { //magician weapon
            String selStr = "Very well, then which Magician weapon shall receive a dragon's power?#b"
            String[] weapon = ["Dragon Wand#k - Lv. 108 Wand#b", "Dragon Staff#k - Lv. 110 Staff#b"]
            for (int i = 0; i < weapon.length; i++) {
               selStr += "\r\n#L" + i + "# " + weapon[i] + "#l"
            }
            cm.sendSimple(selStr)
         } else if (selectedType == 4) { //thief weapon
            String selStr = "Very well, then which Thief weapon shall receive a dragon's power?#b"
            String[] weapon = ["Dragon Kanzir#k - Lv. 110 STR Dagger#b", "Dragon Kreda#k - Lv. 110 LUK Dagger#b", "Dragon Green Sleve#k - Lv. 110 Claw#b"]
            for (int i = 0; i < weapon.length; i++) {
               selStr += "\r\n#L" + i + "# " + weapon[i] + "#l"
            }
            cm.sendSimple(selStr)
         } else if (selectedType == 5) { //pirate weapon
            String selStr = "Very well, then which Pirate weapon shall receive a dragon's power?#b"
            String[] weapon = ["Dragon Slash Claw#k - Lv. 110 Knuckle#b", "Dragonfire Revolver#k - Lv. 110 Gun#b"]
            for (int i = 0; i < weapon.length; i++) {
               selStr += "\r\n#L" + i + "# " + weapon[i] + "#l"
            }
            cm.sendSimple(selStr)
         } else if (selectedType == 11) { //cornian's dagger
            String selStr = "Oh, are you trying to sneak into these lizards to save Moira? I will support your cause wherever I can. Bring me a couple of resources and I will make you an almost identical piece of #t4001078#."
            cm.sendNext(selStr)
         }
      } else if (status == 2) {
         selectedItem = selection

         if (selectedType == 1) { //warrior weapon
            int[] itemSet = [1302059, 1312031, 1322052, 1402036, 1412026, 1422028, 1432038, 1442045]
            List matSet = [[1302056, 4000244, 4000245, 4005000], [1312030, 4000244, 4000245, 4005000], [1322045, 4000244, 4000245, 4005000], [1402035, 4000244, 4000245, 4005000],
                           [1412021, 4000244, 4000245, 4005000], [1422027, 4000244, 4000245, 4005000], [1432030, 4000244, 4000245, 4005000], [1442044, 4000244, 4000245, 4005000]]
            List matQtySet = [[1, 20, 25, 8], [1, 20, 25, 8], [1, 20, 25, 8], [1, 20, 25, 8], [1, 20, 25, 8], [1, 20, 25, 8], [1, 20, 25, 8], [1, 20, 25, 8]]
            int[] costSet = [120000, 120000, 120000, 120000, 120000, 120000, 120000, 120000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 2) { //bowman weapon
            int[] itemSet = [1452044, 1462039]
            List matSet = [[1452019, 4000244, 4000245, 4005000, 4005002], [1462015, 4000244, 4000245, 4005000, 4005002]]
            List matQtySet = [[1, 20, 25, 3, 5], [1, 20, 25, 5, 3]]
            int[] costSet = [120000, 120000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 3) { //magician weapon
            int[] itemSet = [1372032, 1382036]
            List matSet = [[1372010, 4000244, 4000245, 4005001, 4005003], [1382035, 4000244, 4000245, 4005001, 4005003]]
            List matQtySet = [[1, 20, 25, 6, 2], [1, 20, 25, 6, 2]]
            int[] costSet = [120000, 120000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 4) { //thief weapon
            int[] itemSet = [1332049, 1332050, 1472051]
            List matSet = [[1332051, 4000244, 4000245, 4005000, 4005002], [1332052, 4000244, 4000245, 4005002, 4005003], [1472053, 4000244, 4000245, 4005002, 4005003]]
            List matQtySet = [[1, 20, 25, 5, 3], [1, 20, 25, 3, 5], [1, 20, 25, 2, 6]]
            int[] costSet = [120000, 120000, 120000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 5) { //pirate weapon
            int[] itemSet = [1482013, 1492013]
            List matSet = [[1482012, 4000244, 4000245, 4005000, 4005002], [1492012, 4000244, 4000245, 4005000, 4005002]]
            List matQtySet = [[1, 20, 25, 5, 3], [1, 20, 25, 3, 5]]
            int[] costSet = [120000, 120000]
            item = itemSet[selectedItem]
            mats = matSet[selectedItem]
            matQty = matQtySet[selectedItem]
            cost = costSet[selectedItem]
         } else if (selectedType == 11) { //cornian's dagger
            item = cd_item
            mats = cd_mats
            matQty = cd_matQty
            cost = cd_cost
         }

         String prompt = "You want me to make a #t" + item + "#? In that case, I'm going to need specific items from you in order to make it. Make sure you have room in your inventory, though!#b"
         if (stimulator) {
            stimID = getStimID(item)
            prompt += "\r\n#i" + stimID + "# 1 #t" + stimID + "#"
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
            cm.sendOk("Check your inventory for a free slot first.")
            cm.dispose()
            return
         } else if (cm.getMeso() < cost) {
            cm.sendOk("My fee is for the good of all of Leafre. If you cannot pay it, then begone.")
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
            if (!cm.haveItem(stimID)) {
               complete = false
            }
         }
         if (!complete) {
            cm.sendOk("I'm afraid that without the correct items, the dragon's essence would... not make for a very reliable weapon. Please bring the correct items next time.")
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
               cm.gainItem(stimID, (short) -1)
               int deleted = Math.floor(Math.random() * 10).intValue()
               if (deleted != 0) {
                  cm.gainItem(item, (short) 1, true, true)
                  cm.sendOk("The process is complete. Treat your weapon well, lest you bring the wrath of the dragons upon you.")
               } else {
                  cm.sendOk("Unfortunately, the dragon's essence has... conflicted with your weapon. My apologies for your loss.")
               }
            } else {//just give basic item
               cm.gainItem(item, (short) 1)
               cm.sendOk("The process is complete. Treat your weapon well, lest you bring the wrath of the dragons upon you.")
            }
         }
         cm.dispose()
      }
   }

   static def getStimID(int equipID) {
      int cat = Math.floor(equipID / 10000).intValue()
      switch (cat) {
         case 130: //1h sword
            return 4130002
         case 131: //1h axe
            return 4130003
         case 132: //1h bw
            return 4130004
         case 140: //2h sword
            return 4130005
         case 141: //2h axe
            return 4130006
         case 142: //2h bw
            return 4130007
         case 143: //spear
            return 4130008
         case 144: //polearm
            return 4130009
         case 137: //wand
            return 4130010
         case 138: //staff
            return 4130011
         case 145: //bow
            return 4130012
         case 146: //xbow
            return 4130013
         case 148: //knuckle
            return 4130016
         case 149: //pistol
            return 4130017
         case 133: //dagger
            return 4130014
         case 147: //claw
            return 4130015
      }
      return 4130002
   }
}

NPC2080000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2080000(cm: cm))
   }
   return (NPC2080000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }