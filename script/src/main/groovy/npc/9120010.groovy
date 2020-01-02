package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9120010 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int[] eQuestChoices = [4000064, 4000065, 4000066, 4000075, 4000077, 4000089, 4000090, 4000091, 4000092, 4000093, 4000094]
   int[][][] eQuestPrizes = [
         [[2000000, 1], [2000006, 1], [2000003, 5], [2000002, 5], [4020006, 2], [4020000, 2], [4020004, 2], [2000003, 10], [2000003, 20], [2000002, 10], [2000002, 20], [2022026, 15], [2022024, 15], [1002393, 1]],   // Crow feather
         [[2000006, 1], [2000002, 5], [4020006, 2], [2000002, 10], [2000003, 10], [2000002, 20], [2000003, 20], [2022024, 15], [2022026, 15]],   // Raccoon firewood
         [[2000006, 1], [2000002, 5], [2000003, 5], [4020000, 2], [2000003, 10], [2000002, 10], [2000003, 20], [2000002, 20], [2022024, 15], [1002393, 1]],   // Cloud foxtail
         [[2060003, 1000], [4010004, 2], [4010006, 2], [2022022, 5], [2022022, 10], [2022022, 15], [2022019, 5], [2022019, 10], [2022019, 15], [2001002, 15], [2001001, 15], [1102040, 1], [1102043, 1]],// Tringular bandana of the nightghost
         [[2000003, 1], [2022019, 5], [2000006, 5], [4010002, 2], [4010003, 2], [2000006, 10], [2000006, 15], [2022019, 10], [2022019, 15], [2060003, 1000], [2061003, 1000], [1082150, 1], [1082149, 1]],// Dark cloud foxtail
         [[2000006, 1], [2000003, 5], [2000002, 5], [2000003, 10], [2000003, 20], [2000002, 10], [2000002, 15], [2060003, 1000], [2061003, 1000], [2022026, 15], [1002395, 1]],   // Littleman A's badge
         [[2022019, 5], [2000006, 5], [4010003, 2], [2022019, 10], [2022019, 15], [2000006, 10], [2000006, 15], [2060003, 1000], [2061003, 1000]],                // Littleman B's name plate[[2000003, 1], [2000006, 1], [2022019, 1], [2000006, 5], [4010002, 2], [4020001, 2], [2022019, 10], [2022019, 15], [2000006, 10], [2000006, 15], [2060003, 1000], [2061003, 1000]],// Littleman C's necklace
         [[2022019, 5], [2022022, 5], [4010006, 2], [2022019, 10], [2022019, 15], [2022022, 10], [2022022, 15], [2001002, 15], [2001001, 15], [1102043, 1]],   // Leader A's shades[[4010004, 5], [2022019, 5], [2022022, 15], [2022019, 15], [2001002, 15], [2001001, 15], [1102043, 1]],   // Leader B's charm
         [[1102207, 1], [1442026, 1], [1302037, 1], [2070007, 1], [2340000, 1], [2330005, 1], [2022060, 25], [2022061, 20], [2022062, 15]]]
   // Boss pomade

   int requiredItem = 0
   int prizeItem = 0
   int prizeQuantity = 0
   int itemSet
   int[][] reward

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            cm.sendOk("Really? Let me know if you ever change your mind.")
            cm.dispose()
            return
         }
         if (mode == 0 && status == 1) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         }
         if (status == 0) {
            cm.sendYesNo("If you're looking for someone that can pinpoint the characteristics of various items, you're looking at one right now. I'm currently looking for something. Would you like to hear my story?")
         } else if (status == 1) {
            String eQuestChoice = makeChoices(eQuestChoices)
            cm.sendSimple(eQuestChoice)
         } else if (status == 2) {
            requiredItem = eQuestChoices[selection]
            reward = eQuestPrizes[selection]
            itemSet = (Math.floor(Math.random() * reward.length)).intValue()
            prizeItem = reward[itemSet][0]
            prizeQuantity = reward[itemSet][1]
            if (!cm.canHold(prizeItem)) {
               cm.sendNext("I can't give you the reward if your equip, use, or etc. inventory is full. Please go take a look right now.")
            } else if (cm.hasItem(requiredItem, 100)) {   // check they have >= 100 in Inventory
               cm.gainItem(requiredItem, (short) -100)
               cm.gainItem(prizeItem, (short) prizeQuantity)
               cm.sendOk("Hmmm ... if not for this minor scratch ... sigh. I'm afraid I can only deem this a standard-quality item. Well, here's \r\n#t" + prizeItem + "# for you.")
            } else {
               cm.sendOk("Hey, what do you think you're doing? Go lie to someone that DOESN'T know what he's talking about. Not me!")
            }
            cm.dispose()
         }
      }
   }

   static def makeChoices(int[] a) {
      String result = "The items I'm looking for are 1,2,3 ... phew, too many to\r\nmention. Anyhow, if you gather up 100 of the same items,\r\nthen i may trade it with something similar. What? You may\r\nnot know this, but i keep my end of the promise, so you\r\nneed not worry. Now, shall we trade?\r\n"
      for (int x = 0; x < a.length; x++) {
         result += " #L" + x + "##v" + a[x] + "##t" + a[x] + "##l\r\n"
      }
      return result
   }
}

NPC9120010 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9120010(cm: cm))
   }
   return (NPC9120010) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }