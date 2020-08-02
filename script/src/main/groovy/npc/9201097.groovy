package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201097 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int[] eQuestChoices = [4032007, 4032006, 4032009, 4032008, 4032007, 4032006, 4032009, 4032008]

   int[][][] eQuestPrizes = [

         [[1002801, 1],  // Raven Ninja Bandana
          [1462052, 1],   // Raven's Eye
          [1462006, 1],   // Silver Crow
          [1462009, 1],   // Gross Jaeger
          [1452012, 1],   // Marine Arund
          [1472031, 1],        // Black Mamba
          [2044701, 1],        // Claw for ATT 60%
          [2044501, 1],        // Bow for ATT 60%
          [3010041, 1],        // Skull Throne
          [0, 750000]],       // Mesos

         [[1332077, 1],  // Raven's Beak
          [1322062, 1],   // Crushed Skull
          [1302068, 1],   // Onyx Blade
          [4032016, 1],        // Tao of Sight
          [2043001, 1],        // One Handed Sword for Att 60%
          [2043201, 1],        // One Handed BW for Att 60%
          [2044401, 1],        // Polearm for Att 60%
          [2044301, 1],        // Spear for Att 60%
          [3010041, 1],        // Skull Throne
          [0, 1250000]],       // Mesos

         [[1472072, 1],   //Raven's Claw
          [1332077, 1],   // Raven's Beak
          [1402048, 1],   // Raven's Wing
          [1302068, 1],        // Onyx Blade
          [4032017, 1],        // Tao of Harmony
          [4032015, 1],        // Tao of Shadows
          [2043023, 1],        // One-Handed Sword for Att 100%[2]
          [2043101, 1],        // One-Handed Axe for Att 60%
          [2043301, 1],        // Dagger for Att 60%
          [3010040, 1],        // The Stirge Seat
          [0, 2500000]],       // Mesos

         [[1002801, 1],   //Raven Ninja Bandana
          [1382008, 1],   // Kage
          [1382006, 1],   // Thorns
          [4032016, 1],        // Tao of Sight
          [4032015, 1],        // Tao of Shadows
          [2043701, 1],        // Wand for Magic Att 60%
          [2043801, 1],        // Staff for Magic Att 60%
          [3010040, 1],        // The Stirge Seat
          [0, 1750000]]
         ,       // Mesos
         [[0, 3500000]],   // Mesos
         [[0, 3500000]],   // Mesos
         [[0, 3500000]],   // Mesos
         [[0, 3500000]]   // Mesos
   ]

   int requiredItem = 0
   int lastSelection = 0
   int prizeItem = 0
   int prizeQuantity = 0
   int itemSet
   int qnt

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode <= 0) {
         cm.sendOk(I18nMessage.from("9201097_SHOULD_NOT_BE_A_BAD_DEAL"))
         cm.dispose()
         return
      }

      status++
      if (status == 0) { // first interaction with NPC
         if (cm.getQuestStatus(8225) != 2) {
            cm.sendNext(I18nMessage.from("9201097_NOT_A_BANDIT"))
            cm.dispose()
            return
         }

         cm.sendNext(I18nMessage.from("9201097_GOT_A_BIT_OF_TIME"))
      } else if (status == 1) {
         cm.sendYesNo(I18nMessage.from("9201097_DEAL_IS_SIMPLE"))
      } else if (status == 2) {
         String eQuestChoice = makeChoices(eQuestChoices)
         cm.sendSimple(eQuestChoice)
      } else if (status == 3) {
         lastSelection = selection
         requiredItem = eQuestChoices[selection]

         if (selection < 4) {
            qnt = 50
         } else {
            qnt = 25
         }

         cm.sendYesNo(I18nMessage.from("9201097_YOU_WANT_TO_TRADE").with(qnt, requiredItem))
      } else if (status == 4) {
         itemSet = (Math.floor(Math.random() * eQuestPrizes[lastSelection].length)).intValue()
         int[][] reward = eQuestPrizes[lastSelection]
         prizeItem = reward[itemSet][0]
         prizeQuantity = reward[itemSet][1]
         if (!cm.haveItem(requiredItem, qnt)) {
            cm.sendOk(I18nMessage.from("9201097_ARE_YOU_SURE").with(qnt, requiredItem))
         } else if (prizeItem == 0) {
            cm.gainItem(requiredItem, (short) -qnt)
            cm.gainMeso(prizeQuantity)
            cm.sendOk(I18nMessage.from("9201097_FOR_YOUR").with(qnt, requiredItem, prizeQuantity))
         } else if (!cm.canHold(prizeItem)) {
            cm.sendOk(I18nMessage.from("9201097_USE_AND_ETC_INVENTORY_FULL"))
         } else {
            cm.gainItem(requiredItem, (short) -qnt)
            cm.gainItem(prizeItem, (short) prizeQuantity)
            cm.sendOk(I18nMessage.from("9201097_FOR_YOUR_ITEM").with(qnt, requiredItem, prizeQuantity, prizeItem))
         }
         cm.dispose()
      }
   }

   static def makeChoices(int[] a) {
      String result = "Ok! First you need to choose the item that you'll trade with. The better the item, the more likely the chance that I'll give you something much nicer in return.\r\n"
      int[] quantities = [50, 25]

      for (int x = 0; x < a.length; x++) {
         result += " #L"
         result += x
         result += "##v"
         result += a[x]
         result += "#  #b#t"
         result += a[x] + "# #kx "
         result += quantities[Math.floor(x / 4).intValue()]
         result += "#l\r\n"
      }
      return result
   }
}

NPC9201097 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201097(cm: cm))
   }
   return (NPC9201097) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }