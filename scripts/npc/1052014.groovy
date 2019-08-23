package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1052014 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int[] itemSet_lv6 = [1442046, 1432018, 1102146, 1102145, 2022094, 2022544, 2022123, 2022310, 2040727, 2041058, 2040817, 4000030, 4003005, 4003000, 4011007, 4021009, 4011008, 3010098]
   int[] itemQty_lv6 = [1, 1, 1, 1, 35, 15, 20, 20, 1, 1, 1, 30, 30, 30, 1, 1, 3, 1]
   int[] itemSet_lv5 = [1382015, 1382016, 1442044, 1382035, 2022310, 2022068, 2022069, 2022190, 2022047, 2040727, 2040924, 2040501, 4000030, 4003005, 4003000, 4011003, 4011006, 4021004, 3010099]
   int[] itemQty_lv5 = [1, 1, 1, 1, 20, 40, 40, 30, 30, 1, 1, 1, 20, 20, 25, 3, 2, 3, 1]
   int[] itemSet_lv4 = [1332029, 1472027, 1462032, 1492019, 2022045, 2022048, 2022094, 2022123, 2022058, 2041304, 2041019, 2040826, 2040758, 4000030, 4003005, 4003000, 4010007, 4011003, 4021003, 3010016, 3010017]
   int[] itemQty_lv4 = [1, 1, 1, 1, 45, 40, 25, 20, 60, 1, 1, 1, 1, 10, 10, 20, 5, 1, 1, 1, 1]
   int[] itemSet_lv3 = [1302058, 1372008, 1422030, 1422031, 1022082, 2022279, 2022120, 2001001, 2001002, 2022071, 2022189, 2040914, 2041001, 2041041, 2041308, 4031203, 4000030, 4003005, 4003000, 4010004, 4010006, 4020000, 4020006, 3010002, 3010003]
   int[] itemQty_lv3 = [1, 1, 1, 1, 1, 65, 40, 40, 40, 25, 25, 1, 1, 1, 1, 10, 7, 10, 8, 5, 5, 5, 5, 1, 1]
   int[] itemSet_lv2 = [1022073, 1012098, 1012101, 1012102, 1012103, 2022055, 2022056, 2022103, 2020029, 2020032, 2020031, 2022191, 2022016, 2043300, 2043110, 2043800, 2041001, 2040903, 4031203, 4000021, 4003005, 4003000, 4003001, 4010000, 4010001, 4010003, 4010004, 4020004, 3010004, 3010005]
   int[] itemQty_lv2 = [1, 1, 1, 1, 1, 40, 40, 40, 40, 60, 60, 60, 60, 1, 1, 1, 1, 1, 4, 6, 7, 5, 2, 4, 4, 3, 3, 4, 1, 1]
   int[] itemSet_lv1 = [1302021, 1302024, 1302033, 1082150, 1002419, 2022053, 2022054, 2020032, 2022057, 2022096, 2022097, 2022192, 2020030, 2010005, 2022041, 2030000, 2040100, 2040004, 2040207, 2048004, 4031203, 4000021, 4003005, 4003000, 4003001, 4010000, 4010001, 4010002, 4010005, 4020004]
   int[] itemQty_lv1 = [1, 1, 1, 1, 1, 20, 20, 20, 20, 20, 25, 25, 25, 50, 50, 12, 1, 1, 1, 1, 3, 4, 2, 2, 1, 2, 2, 2, 2, 2]
   String[] levels = ["Tier 1", "Tier 2", "Tier 3", "Tier 4", "Tier 5", "Tier 6"]
   int[] tickets = [0, 0, 0, 0, 0, 0]
   int coinId = 4001158
   int coins = 0
   boolean hasCoin = false
   int currentTier
   int curItemQty
   int curItemSel
   boolean advance = true

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            cm.dispose()
            return
         }
         if (mode == 1 && advance) {
            status++
         } else {
            status--
         }

         advance = true

         if (status == 0) {
            hasCoin = cm.haveItem(coinId)
            cm.sendNext("This is the vending machine of the Internet Cafe. Place your erasers or #t" + coinId + "# earned throughout the quests to redeem a prize. You can place #bany amount of erasers#k, however take note that placing #rdifferent erasers#k and #rbigger shots of any of them#k will improve the reward possibilities!")
         } else if (status == 1) {
            String sendStr
            currentTier = getRewardTier()

            if (currentTier >= 0) {
               sendStr = "With the items you have currently placed, you can retrieve a #r" + levels[currentTier] + "#k prize. Place erasers:"
            } else {
               sendStr = "You have placed no erasers yet. Place erasers:"
            }

            String listStr = ""
            for (int i = 0; i < tickets.length; i++) {
               listStr += "#b#L" + i + "##t" + (4001009 + i) + "##k"
               if (tickets[i] > 0) {
                  listStr += " - " + tickets[i] + " erasers"
               }
               listStr += "#l\r\n"
            }
            if (hasCoin) {
               listStr += "#b#L" + tickets.length + "##t" + coinId + "##k"
               if (coins > 0) {
                  listStr += " - " + coins + " feathers"
               }
               listStr += "#l\r\n"
            }

            cm.sendSimple(sendStr + "\r\n\r\n" + listStr + "#r#L" + getRewardIndex(hasCoin) + "#Retrieve a prize!#l#k\r\n")

         } else if (status == 2) {
            if (selection == getRewardIndex(hasCoin)) {
               if (currentTier < 0) {
                  cm.sendPrev("You have set no erasers. Insert at least one to claim a prize.")
                  advance = false
               } else {
                  givePrize()
                  cm.dispose()
               }
            } else {
               int tickSel
               if (selection < tickets.length) {
                  tickSel = 4001009 + selection
               } else {
                  tickSel = coinId
               }

               curItemQty = cm.getItemQuantity(tickSel)
               curItemSel = selection

               if (curItemQty > 0) {
                  cm.sendGetText("How many of #b#t" + tickSel + "##k do you want to insert on the machine? (#r" + curItemQty + "#k available)#k")
               } else {
                  cm.sendPrev("You have got #rnone#k of #b#t" + tickSel + "##k to insert on the machine. Click '#rBack#k' to return to the main interface.")
                  advance = false
               }
            }
         } else if (status == 3) {
            String text = cm.getText()

            try {
               int placedQty = (text).toInteger()
               if (placedQty < 0) {
                  throw new Exception()
               }

               if (placedQty > curItemQty) {
                  cm.sendPrev("You cannot insert the given amount of erasers (#r" + curItemQty + "#k available). Click '#rBack#k' to return to the main interface.")
                  advance = false
               } else {
                  if (curItemSel < tickets.length) {
                     tickets[curItemSel] = placedQty
                  } else {
                     coins = placedQty
                  }

                  cm.sendPrev("Operation succeeded. Click '#rBack#k' to return to the main interface.")
                  advance = false
               }
            } catch (ignored) {
               cm.sendPrev("You must enter a positive number of erasers to insert. Click '#rBack#k' to return to the main interface.")
               advance = false
            }

            status = 2
         } else {
            cm.dispose()
         }
      }
   }

   def getRewardIndex(hasCoin) {
      return (!hasCoin) ? tickets.length : tickets.length + 1
   }

   def getRewardTier() {
      int points = getPoints()

      if (points <= 6) {
         if (points <= 0) {
            return -1
         } else {
            return 0
         }
      }
      if (points >= 46) {
         return 5
      }

      return Math.floor((points - 6) / 8).intValue()
   }

   def getPoints() {
      int points = 0

      for (int i = 0; i < tickets.length; i++) {
         if (tickets[i] <= 0) {
            continue
         }

         points += (6 + ((tickets[i] - 1) * getTicketMultiplier(i)))
         //6 from uniques + rest from each ticket difficulty
      }
      points += Math.ceil(0.46 * coins)  // 100 coins for a LV6 tier item.

      return points
   }

   static def getTicketMultiplier(ticket) {
      if (ticket == 1 || ticket == 3) {
         return 3
      } else {
         return 1
      }
   }

   def givePrize() {
      int[] lvTarget, lvQty

      if (currentTier == 0) {
         lvTarget = itemSet_lv1
         lvQty = itemQty_lv1
      } else if (currentTier == 1) {
         lvTarget = itemSet_lv2
         lvQty = itemQty_lv2
      } else if (currentTier == 2) {
         lvTarget = itemSet_lv3
         lvQty = itemQty_lv3
      } else if (currentTier == 3) {
         lvTarget = itemSet_lv4
         lvQty = itemQty_lv4
      } else if (currentTier == 4) {
         lvTarget = itemSet_lv5
         lvQty = itemQty_lv5
      } else {
         lvTarget = itemSet_lv6
         lvQty = itemQty_lv6
      }

      if (!hasRewardSlot(lvTarget, lvQty)) {
         cm.sendOk("Check for an available space on your inventory before retrieving a prize.")
      } else {
         int rnd = Math.floor(Math.random() * lvTarget.length).intValue()

         for (int i = 0; i < tickets.length; i++) {
            cm.gainItem(4001009 + i, (short) (-1 * tickets[i]))
         }
         cm.gainItem(coinId, (short) (-1 * coins))

         cm.gainItem(lvTarget[rnd], (short) lvQty[rnd])
      }
   }

   def hasRewardSlot(int[] lvTarget, int[] lvQty) {
      for (int i = 0; i < lvTarget.length; i++) {
         if (!cm.canHold(lvTarget[i], lvQty[i])) {
            return false
         }
      }

      return true
   }
}

NPC1052014 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1052014(cm: cm))
   }
   return (NPC1052014) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }