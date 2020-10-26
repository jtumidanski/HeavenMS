package npc
import tools.I18nMessage


import client.inventory.Item
import constants.MapleInventoryType
import config.YamlConfig
import net.server.world.MaplePartyCharacter
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2042002 {
   NPCConversationManager cm
   int status = 0
   int select = -1

   int rnk = -1
   int n1 = 50 //???
   int n2 = 40 //??? ???
   int n3 = 7 //35
   int n4 = 10 //40
   int n5 = 20 //50

   int cpqMap = 980000000
   int cpqMinLvl = 30
   int cpqMaxLvl = 50
   int cpqMinAmt = 2
   int cpqMaxAmt = 6

   boolean refineRocks = true     // enables moon rock, star rock
   boolean refineCrystals = true  // enables common crystals
   boolean refineSpecials = true  // enables lithium, special crystals
   double feeMultiplier = 7.0

   def start() {
      status = -1

      if (!YamlConfig.config.server.USE_CPQ) {
         if (YamlConfig.config.server.USE_ENABLE_CUSTOM_NPC_SCRIPT) {
            status = 0
            action((byte) 1, (byte) 0, 4)
         } else {
            cm.sendOk(I18nMessage.from("2042002_CARNIVAL_UNAVAILABLE"))

            cm.dispose()
         }

         return
      }
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (status >= 0 && mode == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (cm.getPlayer().getMapId() == 980000010) {
            if (status == 0) {
               cm.sendNext(I18nMessage.from("2042002_HOPE_YOU_HAD_FUN"))

            } else if (status > 0) {
               cm.warp(980000000, 0)
               cm.dispose()
            }
         } else if (cm.getChar().getMap().isCPQLoserMap()) {
            if (status == 0) {
               if (cm.getChar().getParty() != null) {
                  String shiu = ""
                  if (cm.getPlayer().getFestivalPoints() >= 300) {
                     shiu += "#rA#k"
                     cm.sendOk("Unfortunately, you either drew or lost the battle despite your excellent performance. Victory can be yours next time! \r\n\r\n#bYour result: " + shiu)
                     rnk = 10
                  } else if (cm.getPlayer().getFestivalPoints() >= 100) {
                     shiu += "#rB#k"
                     rnk = 20
                     cm.sendOk("Unfortunately, you either drew or lost the battle, even with your ultimate performance. Just a little bit, and the victory could have been yours! \r\n\r\n#bYour result: " + shiu)
                  } else if (cm.getPlayer().getFestivalPoints() >= 50) {
                     shiu += "#rC#k"
                     rnk = 30
                     cm.sendOk("Unfortunately, you either drew or lost the battle. Victory is for those who strive. I see your efforts, so victory is not far from your reach. Keep it up!\r\n\r\n#bYour result: " + shiu)
                  } else {
                     shiu += "#rD#k"
                     rnk = 40
                     cm.sendOk("Unfortunately, you either equalized or lost the battle, and your performance clearly reflects on it. I expect more from you next time. \r\n\r\n#bYour result: " + shiu)
                  }
               } else {
                  cm.warp(980000000, 0)
                  cm.dispose()
               }
            } else if (status == 1) {
               switch (rnk) {
                  case 10:
                     cm.warp(980000000, 0)
                     cm.gainExp(17500)
                     cm.dispose()
                     break
                  case 20:
                     cm.warp(980000000, 0)
                     cm.gainExp(1200)
                     cm.dispose()
                     break
                  case 30:
                     cm.warp(980000000, 0)
                     cm.gainExp(5000)
                     cm.dispose()
                     break
                  case 40:
                     cm.warp(980000000, 0)
                     cm.gainExp(2500)
                     cm.dispose()
                     break
                  default:
                     cm.warp(980000000, 0)
                     cm.dispose()
                     break
               }
            }
         } else if (cm.getChar().getMap().isCPQWinnerMap()) {
            if (status == 0) {
               if (cm.getChar().getParty() != null) {
                  String shi = ""
                  if (cm.getPlayer().getFestivalPoints() >= 300) {
                     shi += "#rA#k"
                     rnk = 1
                     cm.sendOk("Congratulations on your victory!!! What a performance! The opposite group could not do anything! I hope the same good work next time! \r\n\r\n#bYour result: " + shi)
                  } else if (cm.getPlayer().getFestivalPoints() >= 100) {
                     shi += "#rB#k"
                     rnk = 2
                     cm.sendOk("Congratulations on your victory! That was awesome! You did a good job against the opposing group! Just a little longer, and you'll definitely get an A next time! \r\n\r\n#bYour result: " + shi)
                  } else if (cm.getPlayer().getFestivalPoints() >= 50) {
                     shi += "#rC#k"
                     rnk = 3
                     cm.sendOk("Congratulations on your victory. You did some things here and there, but that can not be considered a good victory. I expect more from you next time. \r\n\r\n#bYour result: " + shi)
                  } else {
                     shi += "#rD#k"
                     rnk = 4
                     cm.sendOk("Congratulations on your victory, though your performance did not quite reflect that. Be more active in your next participation in the Monster Carnival! \r\n\r\n#bYour result: " + shi)
                  }
               } else {
                  cm.warp(980000000, 0)
                  cm.dispose()
               }
            } else if (status == 1) {
               switch (rnk) {
                  case 1:
                     cm.warp(980000000, 0)
                     cm.gainExp(50000)
                     cm.dispose()
                     break
                  case 2:
                     cm.warp(980000000, 0)
                     cm.gainExp(25500)
                     cm.dispose()
                     break
                  case 3:
                     cm.warp(980000000, 0)
                     cm.gainExp(21000)
                     cm.dispose()
                     break
                  case 4:
                     cm.warp(980000000, 0)
                     cm.gainExp(19505)
                     cm.dispose()
                     break
                  default:
                     cm.warp(980000000, 0)
                     cm.dispose()
                     break
               }
            }
         } else if (cm.getMapId() == cpqMap) {   // only CPQ1
            if (status == 0) {
               if (cm.getParty() == null) {
                  status = 10
                  cm.sendOk(I18nMessage.from("2042002_MUST_BE_IN_A_PARTY"))

               } else if (!cm.isLeader()) {
                  status = 10
                  cm.sendOk(I18nMessage.from("2042002_LEADER_MUST_START"))

               } else {
                  MaplePartyCharacter[] party = cm.getParty().orElseThrow().getMembers()
                  int inMap = cm.partyMembersInMap()
                  int lvlOk = 0
                  int isOutMap = 0
                  for (int i = 0; i < party.size(); i++) {
                     if (party[i].getLevel() >= cpqMinLvl && party[i].getLevel() <= cpqMaxLvl) {
                        lvlOk++

                        if (!party[i].inMap(cpqMap)) {
                           isOutMap++
                        }
                     }
                  }

                  if (party >= 1) {
                     status = 10
                     cm.sendOk(I18nMessage.from("2042002_NOT_ENOUGH_PEOPLE").with(cpqMinAmt, cpqMaxAmt))

                  } else if (lvlOk != inMap) {
                     status = 10
                     cm.sendOk(I18nMessage.from("2042002_LEVEL_RANGE").with(cpqMinLvl, cpqMaxLvl))

                  } else if (isOutMap > 0) {
                     status = 10
                     cm.sendOk(I18nMessage.from("2042002_PARTY_MEMBERS_NOT_IN_MAP"))

                  } else {
                     if (!cm.sendCPQMapLists()) {
                        cm.sendOk(I18nMessage.from("2042002_ALL_FIELDS_IN_USE"))

                        cm.dispose()
                     }
                  }
               }
            } else if (status == 1) {
               if (cm.fieldTaken(selection)) {
                  if (cm.fieldLobbied(selection)) {
                     cm.challengeParty(selection)
                     cm.dispose()
                  } else {
                     cm.sendOk(I18nMessage.from("2042002_ROOM_IS_FULL"))

                     cm.dispose()
                  }
               } else {
                  MaplePartyCharacter[] party = cm.getParty().orElseThrow().getMembers()
                  if ((selection >= 0 && selection <= 3) && party.size() < 1) {
                     cm.sendOk(I18nMessage.from("2042002_NEED_AT_LEAST_2_PLAYERS"))

                  } else if ((selection >= 4 && selection <= 5) && party.size() < 1) {
                     cm.sendOk(I18nMessage.from("2042002_NEED_AT_LEAST_3_PLAYERS"))

                  } else {
                     cm.cpqLobby(selection)
                  }
                  cm.dispose()
               }
            } else if (status == 11) {
               cm.dispose()
            }
         } else {
            if (status == 0) {
               String talk = "What would you like to do? If you have never participate in the Monster Carnival, you will need to know a few things before participating! \r\n#b#L0# Go to the Monster Carnival 1.#l \r\n#L3# Go to the Monster Carnival 2.#l \r\n#L1# Learn about the Monster Carnival.#l\r\n#L2# Trade #t4001129#.#l"
               if (YamlConfig.config.server.USE_ENABLE_CUSTOM_NPC_SCRIPT) {
                  talk += "\r\n#L4# ... Can I just refine my ores?#l"
               }
               cm.sendSimple(talk)
            } else if (status == 1) {
               if (selection == 0) {
                  if ((cm.getLevel() > 29 && cm.getLevel() < 51) || cm.getPlayer().isGM()) {
                     cm.getChar().saveLocation("MONSTER_CARNIVAL")
                     cm.warp(980000000, 0)
                     cm.dispose()
                  } else if (cm.getLevel() < 30) {
                     cm.sendOk(I18nMessage.from("2042002_MUST_BE_AT_LEAST_LEVEL_30"))

                     cm.dispose()
                  } else {
                     cm.sendOk(I18nMessage.from("2042002_SORRY_LEVEL_RANGE"))

                     cm.dispose()
                  }
               } else if (selection == 1) {
                  status = 60
                  cm.sendSimple(I18nMessage.from("2042002_WHAT_WOULD_YOU_LIKE_TO_DO"))

               } else if (selection == 2) {
                  cm.sendSimple(I18nMessage.from("2042002_REMEMBER").with(n1, n2))

               } else if (selection == 3) {
                  cm.getChar().saveLocation("MONSTER_CARNIVAL")
                  cm.warp(980030000, 0)
                  cm.dispose()
               } else if (selection == 4) {
                  String selStr = "Very well, instead I offer a steadfast #bore refining#k service for you, taxing #r" + ((feeMultiplier * 100) | 0) + "%#k over the usual fee to synthesize them. What will you do?#b"

                  String[] options = ["Refine mineral ores", "Refine jewel ores"]
                  if (refineCrystals) {
                     options << "Refine crystal ores"
                  }
                  if (refineRocks) {
                     options << "Refine plates/jewels"
                  }

                  for (int i = 0; i < options.length; i++) {
                     selStr += "\r\n#L" + i + "# " + options[i] + "#l"
                  }

                  cm.sendSimple(selStr)

                  status = 76
               }
            } else if (status == 2) {
               select = selection
               if (select == 0) {
                  if (cm.haveItem(4001129, n1) && cm.canHold(4001129)) {
                     cm.gainItem(1122007, (short) 1)
                     cm.gainItem(4001129, (short) -n1)
                     cm.dispose()
                  } else {
                     cm.sendOk(I18nMessage.from("2042002_MISSING_SOMETHING_OR_EQUIP_FULL"))

                     cm.dispose()
                  }
               } else if (select == 1) {
                  if (cm.haveItem(4001129, n2) && cm.canHold(2041211)) {
                     cm.gainItem(2041211, (short) 1)
                     cm.gainItem(4001129, (short) -n2)
                     cm.dispose()
                  } else {
                     cm.sendOk(I18nMessage.from("2042002_MISSING_SOMETHING_OR_USE_FULL"))

                     cm.dispose()
                  }
               } else if (select == 2) {//S2 Warrior 26 S3 Magician 6 S4 Bowman 6 S5 Thief 8
                  status = 10
                  cm.sendSimple(I18nMessage.from("2042002_MAKE_SURE").with(n3, n3, n4, n4, n5, n5, n3, n3, n4, n4, n5, n5))

               } else if (select == 3) {
                  status = 20
                  cm.sendSimple(I18nMessage.from("2042002_SELECT_THE_WEAPON_TO_TRADE").with(n3, n3, n4, n4, n5, n5))

               } else if (select == 4) {
                  status = 30
                  cm.sendSimple(I18nMessage.from("2042002_SELECT_THE_WEAPON_TO_TRADE_2").with(n3, n4, n5, n3, n4, n5))

               } else if (select == 5) {
                  status = 40
                  cm.sendSimple(I18nMessage.from("2042002_SELECT_THE_WEAPON_TO_TRADE_3").with(n3, n4, n5, n3, n4, n4, n5, n5))

               } else if (select == 6) {
                  status = 50 //pirate rewards
                  cm.sendSimple(I18nMessage.from("2042002_SELECT_THE_WEAPON_TO_TRADE_4").with(n3, n4, n5, n3, n4, n5))

               }
            } else if (status == 11) {
               if (selection == 12) {
                  cm.sendSimple(I18nMessage.from("2042002_SELECT_THE_WEAPON_TO_TRADE_5").with(n3, n3, n4, n4, n5, n5, n3, n3, n4, n4, n5, n5))

               } else {
                  int[] item = [1302004, 1402006, 1302009, 1402007, 1302010, 1402003, 1312006, 1412004, 1312007, 1412005, 1312008, 1412003]
                  int[] cost = [n3, n3, n4, n4, n5, n5, n3, n3, n4, n4, n5]
                  if (cm.haveItem(4001129, cost[selection]) && cm.canHold(item[selection])) {
                     cm.gainItem(item[selection], (short) 1)
                     cm.gainItem(4001129, (short) -cost[selection])
                     cm.dispose()
                  } else {
                     cm.sendOk(I18nMessage.from("2042002_NOT_ENOUGH_OR_INVENTORY_IS_FULL"))

                     cm.dispose()
                  }
               }
            } else if (status == 12) {
               if (selection == 12) {
                  status = 10
                  cm.sendSimple(I18nMessage.from("2042002_MAKE_SURE_2").with(n3, n3, n4, n4, n5, n5, n3, n3, n4, n4, n5, n5))

               } else {
                  int[] item = [1322015, 1422008, 1322016, 1422007, 1322017, 1422005, 1432003, 1442003, 1432005, 1442009, 1442005, 1432004]
                  int[] cost = [n3, n3, n4, n4, n5, n5, n3, n3, n4, n4, n5, n5]
                  if (cm.haveItem(4001129, cost[selection]) && cm.canHold(item[selection])) {
                     cm.gainItem(item[selection], (short) 1)
                     cm.gainItem(4001129, (short) -cost[selection])
                     cm.dispose()
                  } else {
                     cm.sendOk(I18nMessage.from("2042002_NOT_ENOUGH_OR_INVENTORY_IS_FULL"))

                     cm.dispose()
                  }
               }
            } else if (status == 21) {
               int[] item = [1372001, 1382018, 1372012, 1382019, 1382001, 1372007]
               int[] cost = [n3, n3, n4, n4, n5, n5]
               if (cm.haveItem(4001129, cost[selection]) && cm.canHold(item[selection])) {
                  cm.gainItem(item[selection], (short) 1)
                  cm.gainItem(4001129, (short) -cost[selection])
                  cm.dispose()
               } else {
                  cm.sendOk(I18nMessage.from("2042002_NOT_ENOUGH_OR_INVENTORY_IS_FULL"))

                  cm.dispose()
               }
            } else if (status == 31) {
               int[] item = [1452006, 1452007, 1452008, 1462005, 1462006, 1462007]
               int[] cost = [n3, n4, n5, n3, n4, n5]
               if (cm.haveItem(4001129, cost[selection]) && cm.canHold(item[selection])) {
                  cm.gainItem(item[selection], (short) 1)
                  cm.gainItem(4001129, (short) -cost[selection])
                  cm.dispose()
               } else {
                  cm.sendOk(I18nMessage.from("2042002_NOT_ENOUGH_OR_INVENTORY_IS_FULL"))

                  cm.dispose()
               }
            } else if (status == 41) {
               int[] item = [1472013, 1472017, 1472021, 1332014, 1332031, 1332011, 1332016, 1332003]
               int[] cost = [n3, n4, n5, n3, n4, n4, n5, n5]
               if (cm.haveItem(4001129, cost[selection]) && cm.canHold(item[selection])) {
                  cm.gainItem(item[selection], (short) 1)
                  cm.gainItem(4001129, (short) -cost[selection])
                  cm.dispose()
               } else {
                  cm.sendOk(I18nMessage.from("2042002_NOT_ENOUGH_OR_INVENTORY_IS_FULL"))

                  cm.dispose()
               }
            } else if (status == 51) {
               int[] item = [1482005, 1482006, 1482007, 1492005, 1492006, 1492007]
               int[] cost = [n3, n4, n5, n3, n4, n5]
               if (cm.haveItem(4001129, cost[selection]) && cm.canHold(item[selection])) {
                  cm.gainItem(item[selection], (short) 1)
                  cm.gainItem(4001129, (short) -cost[selection])
                  cm.dispose()
               } else {
                  cm.sendOk(I18nMessage.from("2042002_NOT_ENOUGH_OR_INVENTORY_IS_FULL"))

                  cm.dispose()
               }
            } else if (status == 61) {
               select = selection
               if (selection == 0) {
                  cm.sendNext(I18nMessage.from("2042002_HELLO"))

               } else if (selection == 1) {
                  cm.sendNext(I18nMessage.from("2042002_MONSTER_CARNIVAL_INFO"))

               } else if (selection == 2) {
                  cm.sendNext(I18nMessage.from("2042002_EASY_RIGHT"))

               } else {
                  cm.dispose()
               }
            } else if (status == 62) {
               if (select == 0) {
                  cm.sendNext(I18nMessage.from("2042002_WHAT_IS_IT"))

               } else if (select == 1) {
                  cm.sendNext(I18nMessage.from("2042002_HOW_TO"))

               } else if (select == 2) {
                  cm.sendNext(I18nMessage.from("2042002_COMMANDS"))

               }
            } else if (status == 63) {
               if (select == 0) {
                  cm.sendNext(I18nMessage.from("2042002_I_KNOW_IT_IS_TOO_DANGEROUS"))

               } else if (select == 1) {
                  cm.sendNext(I18nMessage.from("2042002_3_WAYS_TO_DISTRACT"))

               } else if (select == 2) {
                  cm.sendNext(I18nMessage.from("2042002_SUMMONING"))

               }
            } else if (status == 64) {
               if (select == 0) {
                  cm.sendNext(I18nMessage.from("2042002_OF_COURSE"))

                  cm.dispose()
               } else if (select == 1) {
                  cm.sendNext(I18nMessage.from("2042002_PLEASE_REMEMBER"))

               } else if (select == 2) {
                  cm.sendNext(I18nMessage.from("2042002_ABILITY"))

               }
            } else if (status == 65) {
               if (select == 1) {
                  cm.sendNext(I18nMessage.from("2042002_DO_NOT_LOSE_EXP_WHEN_DEAD"))

                  cm.dispose()
               } else if (select == 2) {
                  cm.sendNext(I18nMessage.from("2042002_PROTECTOR"))

               }
            } else if (status == 66) {
               cm.sendNext(I18nMessage.from("2042002_CANNOT_USE_ITEMS"))

               cm.dispose()
            } else if (status == 77) {
               boolean allDone

               if (selection == 0) {
                  allDone = refineItems(0) // minerals
               } else if (selection == 1) {
                  allDone = refineItems(1) // jewels
               } else if (selection == 2 && refineCrystals) {
                  allDone = refineItems(2) // crystals
               } else if (selection == 2 && !refineCrystals || selection == 3) {
                  allDone = refineRockItems() // moon/star rock
               }

               if (allDone) {
                  cm.sendOk(I18nMessage.from("2042002_DONE"))

               } else {
                  cm.sendOk(I18nMessage.from("2042002_DONE_BUT_LACK_ETC_SPACE"))

               }
               cm.dispose()
            }
         }
      }
   }

   def getRefineFee(int fee) {
      return ((feeMultiplier * fee) | 0)
   }

   def isRefineTarget(refineType, refineItemId) {
      if (refineType == 0) { //mineral refine
         return refineItemId >= 4010000 && refineItemId <= 4010007 && !(refineItemId == 4010007 && !refineSpecials)
      } else if (refineType == 1) { //jewel refine
         return refineItemId >= 4020000 && refineItemId <= 4020008 && !(refineItemId == 4020008 && !refineSpecials)
      } else if (refineType == 2) { //crystal refine
         return refineItemId >= 4004000 && refineItemId <= 4004004 && !(refineItemId == 4004004 && !refineSpecials)
      }

      return false
   }

   static def getRockRefineTarget(refineItemId) {
      if (refineItemId >= 4011000 && refineItemId <= 4011006) {
         return 0
      } else if (refineItemId >= 4021000 && refineItemId <= 4021008) {
         return 1
      }

      return -1
   }

   def refineItems(refineType) {
      boolean allDone = true

      int[][] refineFees = [[300, 300, 300, 500, 500, 500, 800, 270], [500, 500, 500, 500, 500, 500, 500, 1000, 3000], [5000, 5000, 5000, 5000, 1000000]]
      Map<Integer, Integer> itemCount = [:]

      Iterator<Item> iter = cm.getPlayer().getInventory(MapleInventoryType.ETC).iterator()
      while (iter.hasNext()) {
         Item it = iter.next()
         int itemId = it.id()

         if (isRefineTarget(refineType, itemId)) {
            if (itemCount.containsKey(itemId)) {
               itemCount.put(itemId, itemCount.get(itemId) + it.quantity())
            } else {
               itemCount.put(itemId, it.quantity())
            }
         }
      }

      for (Integer itemId in itemCount.keySet()) {
         int itemQuantity = itemCount.get(itemId)

         int refineQty = ((itemQuantity / 10) | 0)
         if (refineQty <= 0) {
            continue
         }

         while (true) {
            itemQuantity = refineQty * 10

            int fee = getRefineFee(refineFees[refineType][(itemId % 100) | 0] * refineQty)
            if (cm.canHold(itemId + 1000, refineQty, itemId, itemQuantity) && cm.getMeso() >= fee) {
               cm.gainMeso(-fee)
               cm.gainItem(itemId, (short) -itemQuantity)
               cm.gainItem(itemId + (itemId != 4010007 ? 1000 : 1001), (short) refineQty)

               break
            } else if (refineQty <= 1) {
               allDone = false
               break
            } else {
               refineQty--
            }
         }
      }

      return allDone
   }

   def refineRockItems() {
      boolean allDone = true
      int[][] minItems = [[0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0]]
      int[] minRocks = [2147483647, 2147483647]

      int[] rockItems = [4011007, 4021009]
      int[] rockFees = [10000, 15000]

      Iterator<Item> iter = cm.getPlayer().getInventory(MapleInventoryType.ETC).iterator()
      while (iter.hasNext()) {
         Item it = iter.next()
         int itemId = it.id()
         int rockRefine = getRockRefineTarget(itemId)
         if (rockRefine >= 0) {
            int rockItem = ((itemId % 100) | 0)
            int itemQuantity = it.quantity()

            minItems[rockRefine][rockItem] += itemQuantity
         }
      }

      for (int i = 0; i < minRocks.length; i++) {
         for (int j = 0; j < minItems[i].length; j++) {
            if (minRocks[i] > minItems[i][j]) {
               minRocks[i] = minItems[i][j]
            }
         }
         if (minRocks[i] <= 0 || minRocks[i] == 2147483647) {
            continue
         }

         int refineQty = minRocks[i]
         while (true) {
            int fee = getRefineFee(rockFees[i] * refineQty)
            if (cm.canHold(rockItems[i], refineQty) && cm.getMeso() >= fee) {
               cm.gainMeso(-fee)

               int j
               if (i == 0) {
                  for (j = 4011000; j < 4011007; j++) {
                     cm.gainItem(j, (short) -refineQty)
                  }
                  cm.gainItem(j, (short) refineQty)
               } else {
                  for (j = 4021000; j < 4021009; j++) {
                     cm.gainItem(j, (short) -refineQty)
                  }
                  cm.gainItem(j, (short) refineQty)
               }

               break
            } else if (refineQty <= 1) {
               allDone = false
               break
            } else {
               refineQty--
            }
         }
      }

      return allDone
   }
}

NPC2042002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2042002(cm: cm))
   }
   return (NPC2042002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }