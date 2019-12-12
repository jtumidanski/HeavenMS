package npc

import client.MapleJob
import client.MonsterBook
import client.inventory.MapleInventoryType
import config.YamlConfig
import constants.game.GameConstants
import scripting.npc.NPCConversationManager
import server.MapleItemInformationProvider
import server.life.MapleLifeFactory

import java.math.RoundingMode

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/

class ScrollGenerator {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int curItemQty

   int curItemSel

   List<List<List<Integer>>> jobWeaponRestricted = [[[2043000, 2043100, 2044000, 2044100, 2043200, 2044200]], [[2043000, 2043100, 2044000, 2044100], [2043000, 2043200, 2044000, 2044200], [2044300, 2044400]], [[2043700, 2043800], [2043700, 2043800], [2043700, 2043800]], [[2044500], [2044600]], [[2044700], [2043300]], [[2044800], [2044900]]]
   List<Integer> aranWeaponRestricted = [jobWeaponRestricted[1][2][1]]

   List<Integer> tier1Scrolls = []
   List<Integer> tier2Scrolls = [2040000, 2040400, 2040500, 2040600, 2040700, 2040800, 2040900]
   List<Integer> tier3Scrolls = [2048000, 2049200, 2041000, 2041100, 2041300, 2040100, 2040200, 2040300]

   List<List<String>> typeTierScrolls = [["PAD", "MAD"], ["STR", "DEX", "INT", "LUK", "ACC", "EVA", "Speed", "Jump"], ["PDD", "MDD", "MHP", "MMP"]]

   List<Integer> sgItems = [4003004, 4003005, 4001006, 4006000, 4006001, 4030012]
   List<Double> sgToBucket = [100, 50, 37.5, 37.5, 37.5, 200]
   int mesoToBucket = 2800000

   List<Integer> sgAppliedItems = [0, 0, 0, 0, 0, 0]
   int sgAppliedMeso = 0

   double sgBuckets = 0.0
   double sgBookBuckets = 0.0
   double sgItemBuckets = 0.0

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
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            cm.sendNext("This is the MapleTV Scroll Generator broadcast. Place your supplies or mesos earned throughout your adventure to redeem a prize! You can place #bany amount of supplies#k, however take note that placing #rdifferent supplies#k with #rbigger shots of any of them#k will improve the reward possibilities!")
         } else if (status == 1) {
            String sendStr

            //print("Book: " + sgBookBuckets + " Item: " + sgItemBuckets)

            if (sgItemBuckets > 0.0) {
               sendStr = "With the items you have currently placed, you have #r" + sgBuckets + "#k buckets (#r" + (sgItemBuckets < 1.0 ? BigDecimal.valueOf(sgItemBuckets).setScale(2, RoundingMode.HALF_UP).doubleValue() : Math.floor(sgItemBuckets)) + "#k supply buckets) for claiming a prize. Place supplies:"
            } else {
               sendStr = "You have placed no supplies yet. Place supplies:"
            }

            String listStr = ""
            int i
            for (i = 0; i < sgItems.size(); i++) {
               listStr += "#b#L" + i + "##t" + sgItems.get(i) + "##k"
               if (sgAppliedItems.get(i) > 0) {
                  listStr += " - " + sgAppliedItems.get(i)
               }
               listStr += "#l\r\n"
            }

            listStr += "#b#L" + i + "#Mesos#k"
            if (sgAppliedMeso > 0) {
               listStr += " - " + sgAppliedMeso
            }
            listStr += "#l\r\n"

            cm.sendSimple(sendStr + "\r\n\r\n" + listStr + "#r#L" + (sgItems.size() + 2) + "#Retrieve a prize!#l#k\r\n")
         } else if (status == 2) {
            if (selection == (sgItems.size() + 2)) {
               if (sgItemBuckets < 1.0) {
                  cm.sendPrev("You have set not enough supplies. Insert at least one bucket of #bsupplies#k to claim a prize.")
               } else {
                  generateRandomScroll()
                  cm.dispose()
               }
            } else {
               String tickSel

               if (selection < sgItems.size()) {
                  tickSel = "of #b#t" + sgItems.get(selection) + "##k"
                  curItemQty = cm.getItemQuantity(sgItems.get(selection))
               } else {
                  tickSel = "#bmesos#k"
                  curItemQty = cm.getMeso()
               }

               curItemSel = selection
               if (curItemQty > 0) {
                  cm.sendGetText("How many " + tickSel + " do you want to provide? (#r" + curItemQty + "#k available)#k")
               } else {
                  cm.sendPrev("You have got #rnone#k " + tickSel + " to provide for Scroll Generation. Click '#rBack#k' to return to the main interface.")
               }
            }
         } else if (status == 3) {
            String text = cm.getText()

            try {
               int placedQty = Integer.valueOf(text)
               if (placedQty < 0) {
                  throw new Exception()
               }

               if (placedQty > curItemQty) {
                  cm.sendPrev("You cannot insert the given amount of #r" + (curItemSel < sgItems.size() ? "#t" + sgItems[curItemSel] + "#" : "mesos") + "#k (#r" + curItemQty + "#k available). Click '#rBack#k' to return to the main interface.")
               } else {
                  if (curItemSel < sgItems.size()) {
                     sgApplyItem(curItemSel, placedQty)
                  } else {
                     sgApplyMeso(placedQty)
                  }

                  cm.sendPrev("Operation succeeded. Click '#rBack#k' to return to the main interface.")
               }
            } catch (Exception ignored) {
               cm.sendPrev("You must enter a positive number of supplies to insert. Click '#rBack#k' to return to the main interface.")
            }

            status = 2
         } else {
            cm.dispose()
         }
      }
   }

   def getJobTierScrolls() {
      List<Integer> scrolls = []

      MapleJob job = cm.getPlayer().getJob()
      int jobWeaponRestrictedIndex = Math.floor(cm.getPlayer().getJobStyle().getId() / 100).intValue()
      List<List<Integer>> jobScrolls = jobWeaponRestricted[jobWeaponRestrictedIndex]

      int jobBranch = GameConstants.getJobBranch(job)
      if (jobBranch >= 2) {
         int jobScrollIndex = (Math.floor((job.getId() / 10) % 10) - 1).intValue()
         scrolls.addAll(jobScrolls.get(jobScrollIndex))
      } else {
         for (int i = 0; i < jobScrolls.size(); i++) {
            scrolls.addAll(jobScrolls.get(i))
         }
      }

      return scrolls
   }

   def getScrollTypePool(rewardTier) {
      List<Integer> scrolls = []
      switch (rewardTier) {
         case 1:
            if (cm.getPlayer().isAran()) {
               scrolls.addAll(aranWeaponRestricted)
            } else {
               scrolls.addAll(getJobTierScrolls())
            }

            scrolls.addAll(tier1Scrolls)
            break
         case 2:
            scrolls.addAll(tier2Scrolls)
            break
         default:
            scrolls.addAll(tier3Scrolls)
      }

      return scrolls
   }

   def getScrollTier(scrollStats) {
      for (int i = 0; i < typeTierScrolls.size(); i++) {
         for (int j = 0; j < typeTierScrolls.get(i).size(); j++) {
            if (scrollStats.get(typeTierScrolls.get(i).get(j)) > 0) {
               return i + 1
            }
         }
      }

      return 4
   }

   def getScrollSuccessTier(Map<String, Integer> scrollStats) {
      int prop = scrollStats.get("success")

      if (prop > 90) {
         return 3
      } else if (prop < 50) {
         return YamlConfig.config.server.SCROLL_CHANCE_ROLLS > 2 ? 2 : 1
      } else {
         return YamlConfig.config.server.SCROLL_CHANCE_ROLLS > 2 ? 1 : 2
      }
   }

   def getAvailableScrollsPool(List<Integer> baseScrolls, int rewardTier, int successTier) {
      List<Integer> scrolls = []
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance()

      for (int i = 0; i < baseScrolls.size(); i++) {
         for (int j = 0; j < 100; j++) {
            int scrollid = baseScrolls[i] + j
            Map<String, Integer> scrollStats = ii.getEquipStats(scrollid)
            if (scrollStats != null && ii.getScrollReqs(scrollid).isEmpty()) {
               int scrollTier = getScrollTier(scrollStats)
               if (scrollTier == rewardTier && successTier == getScrollSuccessTier(scrollStats)) {
                  scrolls.add(scrollid)
               }
            }
         }
      }

      return scrolls
   }

// passive tier buckets...

   def getLevelTier(int level) {
      return Math.floor((level - 1) / 15) + 1
   }

   def getPlayerCardTierPower() {
      Set<Map.Entry<Integer, Integer>> cardset = cm.getPlayer().getMonsterBook().getCardSet()
      List<Integer> countTier = [0, 0, 0, 0, 0, 0, 0, 0, 0]

      for (Iterator<Map.Entry<Integer, Integer>> iterator = cardset.iterator(); iterator.hasNext();) {
         Map.Entry<Integer, Integer> ce = iterator.next()

         int cardid = ce.getKey()
         int ceTier = Math.floor(cardid / 1000) % 10
         countTier.set(ceTier, countTier.get(ceTier) + ce.getValue())

         if (ceTier >= 8) {  // is special card
            int mobLevel = MapleLifeFactory.getMonsterLevel(MapleItemInformationProvider.getInstance().getCardMobId(cardid))
            int mobTier = (getLevelTier(mobLevel) - 1).intValue()

            countTier.set(mobTier, (countTier.get(mobTier) + (ce.getValue() * 1.2)).intValue())
         }
      }

      return countTier
   }

   def calculateMobBookTierBuckets(List<Integer> tierSize, List<Integer> playerCards, int tier) {
      if (tier < 1) {
         return 0.0
      }

      tier-- // started at 1
      double tierHitRate = playerCards[tier] / (tierSize[tier] * 5)
      if (tierHitRate > 0.5) {
         tierHitRate = 0.5
      }

      return tierHitRate * 4
   }

   def calculateMobBookBuckets() {
      MonsterBook book = cm.getPlayer().getMonsterBook()
      double bookLevelMult = 0.9 + (0.1 * book.getBookLevel())

      int playerLevelTier = getLevelTier(cm.getPlayer().getLevel()).intValue()
      if (playerLevelTier > 8) {
         playerLevelTier = 8
      }

      List<Integer> tierSize = MonsterBook.getCardTierSize()
      List<Integer> playerCards = getPlayerCardTierPower()

      double prevBuckets = calculateMobBookTierBuckets(tierSize, playerCards, playerLevelTier - 1)
      double currBuckets = calculateMobBookTierBuckets(tierSize, playerCards, playerLevelTier)

      return (prevBuckets + currBuckets) * bookLevelMult
   }

   def recalcBuckets() {
      sgBookBuckets = calculateMobBookBuckets()
      sgItemBuckets = calculateSuppliesBuckets()

      double buckets = sgBookBuckets + sgItemBuckets
      if (buckets > 6.0) {
         sgBuckets = 6
      } else {
         sgBuckets = Math.floor(buckets)
      }
   }

// variable buckets...

   def sgApplyItem(int idx, int amount) {
      if (sgAppliedItems.get(idx) != amount) {
         sgAppliedItems.set(idx, amount)
         recalcBuckets()
      }
   }

   def sgApplyMeso(int amount) {
      if (sgAppliedMeso != amount) {
         sgAppliedMeso = amount
         recalcBuckets()
      }
   }

   def calculateSuppliesBuckets() {
      double suppliesHitRate = 0.0
      for (int i = 0; i < sgItems.size(); i++) {
         suppliesHitRate += sgAppliedItems.get(i) / sgToBucket.get(i)
      }
      suppliesHitRate *= 2

      suppliesHitRate += (sgAppliedMeso / mesoToBucket)
      return suppliesHitRate
   }

   def calculateScrollTiers() {
      double buckets = sgBuckets
      List<Integer> tiers = [0, 0, 0]
      while (buckets > 0) {
         List<Integer> pool = []
         for (int i = 0; i < tiers.size(); i++) {
            if (tiers[i] < 2) {
               pool.push(i)
            }
         }

         int rnd = pool.get(Math.floor(Math.random() * pool.size()).intValue())

         tiers.set(rnd, tiers.get(rnd) + 1)
         buckets--
      }

      // normalize tiers
      for (int i = 0; i < tiers.size(); i++) {
         tiers[i] = 3 - tiers.get(i)
      }

      return tiers
   }

   def getRandomScroll(List<Integer> tiers) {
      int typeTier = tiers[0], subtypeTier = tiers[1], successTier = tiers[2]
      List<Integer> scrollTypePool = getScrollTypePool(typeTier)
      List<Integer> scrollPool = getAvailableScrollsPool(scrollTypePool, subtypeTier, successTier)

      if (scrollPool.size() > 0) {
         return scrollPool[Math.floor(Math.random() * scrollPool.size())]
      } else {
         return -1
      }
   }

   def performExchange(int sgItemid, int sgCount) {
      if (cm.getMeso() < sgAppliedMeso) {
         return false
      }

      for (int i = 0; i < sgItems.size(); i++) {
         int itemid = sgItems.get(i)
         int count = sgAppliedItems.get(i)
         if (count > 0 && !cm.haveItem(itemid, count)) {
            return false
         }
      }

      cm.gainMeso(-sgAppliedMeso)

      for (int i = 0; i < sgItems.size(); i++) {
         int itemid = sgItems.get(i)
         int count = sgAppliedItems.get(i)
         cm.gainItem(itemid, (short) -count)
      }

      cm.gainItem(sgItemid, (short) sgCount)
      return true
   }

   def generateRandomScroll() {
      if (cm.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
         int itemid = getRandomScroll(calculateScrollTiers())
         if (itemid != -1) {
            if (performExchange(itemid, 1)) {
               cm.sendNext("Transaction accepted! You have received a #r#t" + itemid + "##k.")
            } else {
               cm.sendOk("Oh, it looks like some items are missing... Please double-check provided items in your inventory before trying to exchange.")
            }
         } else {
            cm.sendOk("Sorry for the inconvenience, but it seems there are no scrolls on store right now... Try again later.")
         }
      } else {
         cm.sendOk("Please look out for a slot available on your USE inventory before trying for a scroll.")
      }
   }
}

ScrollGenerator getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new ScrollGenerator(cm: cm))
   }
   return (ScrollGenerator) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }