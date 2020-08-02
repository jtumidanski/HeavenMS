package npc

import config.YamlConfig
import constants.game.GameConstants
import net.server.world.MapleParty
import scripting.npc.NPCConversationManager
import tools.MessageBroadcaster
import tools.ServerNoticeType
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2091005 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   boolean disabled = false
   int[] belts = [1132000, 1132001, 1132002, 1132003, 1132004]
   int[] belt_level = [25, 35, 45, 60, 75]
   boolean[] belt_on_inventory
   int[] belt_points

   int selectedMenu = -1
   int dojoWarp = 0

   def start() {
      if (disabled) {
         cm.sendOk(I18nMessage.from("2091005_DOJO_CLOSED"))

         cm.dispose()
         return
      }

      belt_points = YamlConfig.config.server.USE_FAST_DOJO_UPGRADE ? [10, 90, 200, 460, 850] : [200, 1800, 4000, 9200, 17000]

      belt_on_inventory = []
      for (int i = 0; i < belts.length; i++) {
         belt_on_inventory << cm.haveItemWithId(belts[i], true)
      }
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.getPlayer().setDojoStage(dojoWarp)
         cm.dispose()
      } else {
         if (mode == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         }

         if (status == 0) {
            if (isRestingSpot(cm.getPlayer().getMap().getId())) {
               String text = "I'm surprised you made it this far! But it won't be easy from here on out. You still want the challenge?\r\n\r\n#b#L0#I want to continue#l\r\n#L1#I want to leave#l\r\n"
               if (!GameConstants.isDojoPartyArea(cm.getPlayer().getMapId())) {
                  text += "#L2#I want to record my score up to this point#l"
               }
               cm.sendSimple(text)
            } else if (cm.getPlayer().getLevel() >= 25) {
               if (cm.getPlayer().getMap().getId() == 925020001) {
                  cm.sendSimple(I18nMessage.from("2091005_WANT_TO_CHALLENGE"))

               } else {
                  cm.sendYesNo(I18nMessage.from("2091005_GIVING_UP_LONG"))

               }
            } else {
               cm.sendOk(I18nMessage.from("2091005_MOCKING_MASTER"))

               cm.dispose()
            }
         } else {
            if (cm.getPlayer().getMap().getId() == 925020001) {
               if (mode >= 0) {
                  if (status == 1) {
                     selectedMenu = selection
                  }
                  if (selectedMenu == 0) { //I want to challenge him alone.
                     if (!cm.getPlayer().hasEntered("dojang_Msg") && !cm.getPlayer().getFinishedDojoTutorial()) {
                        //kind of hackish...
                        if (status == 1) {
                           cm.sendYesNo(I18nMessage.from("2091005_IS_THIS_YOUR_FIRST_TIME"))

                        } else if (status == 2) {
                           if (mode == 0) {
                              cm.sendNext(I18nMessage.from("2091005_GO_BACK_HOME"))

                              cm.dispose()
                           } else {
                              int avDojo = cm.getClient().getChannelServer().ingressDojo(true, 0)

                              if (avDojo < 0) {
                                 if (avDojo == -1) {
                                    cm.sendOk(I18nMessage.from("2091005_ALL_DOJOS_USED"))

                                 } else {
                                    cm.sendOk(I18nMessage.from("2091005_PARTY_ALREADY_USING_DOJO"))

                                 }
                              } else {
                                 cm.getClient().getChannelServer().getMapFactory().getMap(925020010 + avDojo).resetMapObjects()

                                 cm.resetDojoEnergy()
                                 cm.warp(925020010 + avDojo, 0)
                              }

                              cm.dispose()
                           }
                        }
                     } else if (cm.getPlayer().getDojoStage() > 0) {
                        dojoWarp = cm.getPlayer().getDojoStage()
                        cm.getPlayer().setDojoStage(0)
                        int stageWarp = ((dojoWarp / 6) | 0) * 5
                        cm.sendYesNo(I18nMessage.from("2091005_LAST_TIME").with(stageWarp))

                     } else {
                        int avDojo = cm.getClient().getChannelServer().ingressDojo(false, dojoWarp)

                        if (avDojo < 0) {
                           if (avDojo == -1) {
                              cm.sendOk(I18nMessage.from("2091005_ALL_DOJOS_USED"))

                           } else {
                              cm.sendOk(I18nMessage.from("2091005_PARTY_ALREADY_USING_DOJO"))

                           }

                           cm.getPlayer().setDojoStage(dojoWarp)
                        } else {
                           int warpDojoMap = 925020000 + (dojoWarp + 1) * 100 + avDojo
                           cm.getClient().getChannelServer().resetDojoMap(warpDojoMap)

                           cm.resetDojoEnergy()
                           cm.warp(warpDojoMap, 0)
                        }

                        cm.dispose()
                     }
                  } else if (selectedMenu == 1) { //I want to challenge him with a party.
                     Optional<MapleParty> party = cm.getPlayer().getParty()
                     if (party.isEmpty()) {
                        cm.sendNext(I18nMessage.from("2091005_WHERE_DO_YOU_THINK"))

                        cm.dispose()
                        return
                     }

                     if (party.get().getLeader().getId() != cm.getPlayer().getId()) {
                        cm.sendNext(I18nMessage.from("2091005_WHERE_DO_YOU_THINK"))

                        cm.dispose()
                     }

                     //else if (party.getMembers().size() == 1) {
                     //    cm.sendNext(I18nMessage.from("2091005_ONE_MAN_PARTY"))

                     //}

                     else if (!isBetween(party.get(), 30)) {
                        cm.sendNext(I18nMessage.from("2091005_LEVEL_RANGE_TOO_BROAD"))

                        cm.dispose()
                     } else {
                        int avDojo = cm.getClient().getChannelServer().ingressDojo(true, cm.getParty().get(), 0)

                        if (avDojo < 0) {
                           if (avDojo == -1) {
                              cm.sendOk(I18nMessage.from("2091005_ALL_DOJOS_USED"))

                           } else {
                              cm.sendOk(I18nMessage.from("2091005_PARTY_ALREADY_USING_DOJO"))

                           }
                        } else {
                           cm.getClient().getChannelServer().resetDojoMap(925030100 + avDojo)

                           cm.resetPartyDojoEnergy()
                           cm.warpParty(925030100 + avDojo)
                        }

                        cm.dispose()
                     }

                  } else if (selectedMenu == 2) { //I want to receive a belt.
                     if (!cm.canHold(belts[0])) {
                        cm.sendNext(I18nMessage.from("2091005_EQUIP_INVENTORY_ROOM_NEEDED"))

                        cm.dispose()
                        return
                     }
                     if (mode < 1) {
                        cm.dispose()
                        return
                     }
                     if (status == 1) {
                        String selStr = "You have #b" + cm.getPlayer().getDojoPoints() + "#k training points. Master prefers those with great talent. If you obtain more points than the average, you can receive a belt depending on your score.\r\n"
                        for (int i = 0; i < belts.length; i++) {
                           if (belt_on_inventory[i]) {
                              selStr += "\r\n#L" + i + "##i" + belts[i] + "# #t" + belts[i] + "# (Already on inventory)"
                           } else {
                              selStr += "\r\n#L" + i + "##i" + belts[i] + "# #t" + belts[i] + "#"
                           }
                        }
                        cm.sendSimple(selStr)
                     } else if (status == 2) {
                        int belt = belts[selection]
                        int level = belt_level[selection]
                        int points = belt_points[selection]

                        int oldBelt = (selection > 0) ? belts[selection - 1] : -1
                        boolean hasOldBelt = (oldBelt == -1 || cm.haveItemWithId(oldBelt, false))

                        if (selection > 0 && !belt_on_inventory[selection - 1]) {
                           sendBeltRequirements(belt, oldBelt, hasOldBelt, level, points)
                        } else if (cm.getPlayer().getDojoPoints() >= points) {
                           if (selection > 0 && !hasOldBelt) {
                              sendBeltRequirements(belt, oldBelt, hasOldBelt, level, points)
                           } else if (cm.getPlayer().getLevel() > level) {
                              if (selection > 0) {
                                 cm.gainItem(oldBelt, (short) -1)
                              }
                              cm.gainItem(belt, (short) 1)
                              cm.getPlayer().setDojoPoints(cm.getPlayer().getDojoPoints() - points)
                              cm.sendNext(I18nMessage.from("2091005_BELT_AWARD").with(belt, belt))

                           } else {
                              sendBeltRequirements(belt, oldBelt, hasOldBelt, level, points)
                           }
                        } else {
                           sendBeltRequirements(belt, oldBelt, hasOldBelt, level, points)
                        }

                        cm.dispose()
                     }
                  } else if (selectedMenu == 3) { //I want to reset my training points.
                     if (status == 1) {
                        cm.sendYesNo(I18nMessage.from("2091005_RESET_INFO"))

                     } else if (status == 2) {
                        if (mode == 0) {
                           cm.sendNext(I18nMessage.from("2091005_GATHER_YOURSELF"))

                        } else {
                           cm.getPlayer().setDojoPoints(0)
                           cm.sendNext(I18nMessage.from("2091005_RESET_SUCCESS"))

                        }
                        cm.dispose()
                     }
                  } else if (selectedMenu == 4) { //I want to receive a medal.
                     if (status == 1 && cm.getPlayer().getVanquisherStage() <= 0) {
                        cm.sendYesNo(I18nMessage.from("2091005_ATTEMPT_THE_MEDAL"))

                     } else if (status == 2 || cm.getPlayer().getVanquisherStage() > 0) {
                        if (mode == 0) {
                           cm.sendNext(I18nMessage.from("2091005_THAT_IS_FINE"))

                        } else {
                           if (cm.getPlayer().getDojoStage() > 37) {
                              cm.sendNext(I18nMessage.from("2091005_COMPLETED_ALL_MEDAL_CHALLENGES"))

                           } else if (cm.getPlayer().getVanquisherKills() < 100 && cm.getPlayer().getVanquisherStage() > 0) {
                              cm.sendNext(I18nMessage.from("2091005_STILL_NEED").with((100 - cm.getPlayer().getVanquisherKills())))

                           } else if (cm.getPlayer().getVanquisherStage() <= 0) {
                              cm.getPlayer().setVanquisherStage(1)
                           } else {
                              cm.sendNext(I18nMessage.from("2091005_HAVE_OBTAINED"))

                              cm.gainItem(1142033 + cm.getPlayer().getVanquisherStage(), (short) 1)
                              cm.getPlayer().setVanquisherStage(cm.c.getPlayer().getVanquisherStage() + 1)
                              cm.getPlayer().setVanquisherKills(0)
                           }
                        }

                        cm.dispose()
                     } else {
                        cm.dispose()
                     }
                  } else if (selectedMenu == 5) { //What is a Mu Lung Dojo?
                     cm.sendNext(I18nMessage.from("2091005_OUR_MASTER"))

                     cm.dispose()
                  }
               } else {
                  cm.dispose()
               }
            } else if (isRestingSpot(cm.getPlayer().getMap().getId())) {
               if (selectedMenu == -1) {
                  selectedMenu = selection
               }

               if (selectedMenu == 0) {
                  boolean hasParty = (cm.getParty().isPresent())

                  boolean firstEnter = false
                  int avDojo = cm.getClient().getChannelServer().lookupPartyDojo(cm.getParty().orElse(null))
                  if (avDojo < 0) {
                     if (hasParty) {
                        if (!cm.isPartyLeader()) {
                           cm.sendOk(I18nMessage.from("2091005_NOT_THE_LEADER"))

                           cm.dispose()
                           return
                        }

                        if (!isBetween(cm.getParty().get(), 35)) {
                           cm.sendOk(I18nMessage.from("2091005_LEVEL_RANGE"))

                           cm.dispose()
                           return
                        }
                     }

                     avDojo = cm.getClient().getChannelServer().ingressDojo(hasParty, cm.getParty().get(), Math.floor((cm.getPlayer().getMap().getId()) / 100) % 100)
                     firstEnter = true
                  }

                  if (avDojo < 0) {
                     if (avDojo == -1) {
                        cm.sendOk(I18nMessage.from("2091005_ALL_DOJOS_USED"))

                     } else {
                        cm.sendOk(I18nMessage.from("2091005_ALREADY_REGISTERED"))

                     }
                  } else {
                     int baseStg = hasParty ? 925030000 : 925020000
                     int nextStg = Math.floor((cm.getPlayer().getMap().getId() + 100) / 100) % 100

                     int dojoWarpMap = baseStg + (nextStg * 100) + avDojo
                     if (firstEnter) {
                        cm.getClient().getChannelServer().resetDojoMap(dojoWarpMap)
                     }

                     //non-leader party members can progress whilst having the record saved if they don't command to enter the next stage
                     cm.getPlayer().setDojoStage(0)

                     if (!hasParty || !cm.isLeader()) {
                        cm.warp(dojoWarpMap, 0)
                     } else {
                        cm.warpParty(dojoWarpMap, 0)
                     }
                  }

                  cm.dispose()
               } else if (selectedMenu == 1) { //I want to leave
                  if (status == 1) {
                     cm.sendYesNo(I18nMessage.from("2091005_GIVING_UP"))

                  } else {
                     if (mode == 1) {
                        cm.warp(925020002, "st00")
                     }
                     cm.dispose()
                  }
               } else if (selectedMenu == 2) { //I want to record my score up to this point
                  if (status == 1) {
                     cm.sendYesNo(I18nMessage.from("2091005_DO_YOU_WANT_TO_RECORD"))

                  } else {
                     if (mode == 0) {
                        cm.sendNext(I18nMessage.from("2091005_GOOD_LUCK"))

                     } else if (cm.getPlayer().getDojoStage() == Math.floor(cm.getMapId() / 100) % 100) {
                        cm.sendOk(I18nMessage.from("2091005_ALREADY_RECORDED"))

                     } else {
                        cm.sendNext(I18nMessage.from("2091005_RECORD_SUCCESS"))

                        cm.getPlayer().setDojoStage(Math.floor(cm.getMapId() / 100) % 100)
                     }
                     cm.dispose()
                  }
               }
            } else {
               if (mode == 0) {
                  cm.sendNext(I18nMessage.from("2091005_STOP_CHANGING_YOUR_MIND"))

               } else if (mode == 1) {
                  int dojoMapId = cm.getPlayer().getMap().getId()

                  cm.warp(925020002, 0)
                  MessageBroadcaster.getInstance().sendServerNotice(cm.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("MAKE_UP_YOUR_MIND"))

                  cm.getClient().getChannelServer().freeDojoSectionIfEmpty(dojoMapId)
               }
               cm.dispose()
            }
         }
      }
   }


   def sendBeltRequirements(int belt, int oldBelt, boolean hasOldBelt, int level, int points) {
      String beltReqStr = (oldBelt != -1) ? " you must have the #i" + oldBelt + "# belt in your inventory," : ""

      String pointsLeftStr = (points - cm.getPlayer().getDojoPoints() > 0) ? " you need #r" + (points - cm.getPlayer().getDojoPoints()) + "#k more training points" : ""
      String beltLeftStr = (!hasOldBelt) ? " you must have the needed belt unequipped and available in your EQP inventory" : ""
      String conjStr = (pointsLeftStr.length() > 0 && beltLeftStr.length() > 0) ? " and" : ""

      cm.sendNext(I18nMessage.from("2091005_IN_ORDER_TO").with(belt, belt, beltReqStr, level, points))

   }

   static def isRestingSpot(int id) {
      return (Math.floor(id / 100).intValue() % 100) % 6 == 0 && id != 925020001
   }

   def isBetween(MapleParty party, int range) {
      int lowest = cm.getPlayer().getLevel()
      int highest = lowest
      for (int x = 0; x < party.getMembers().size(); x++) {
         int lvl = party.getMembers()[x].getLevel()
         if (lvl > highest) {
            highest = lvl
         } else if (lvl < lowest) {
            lowest = lvl
         }
      }
      return (highest - lowest) <= range
   }
}

NPC2091005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2091005(cm: cm))
   }
   return (NPC2091005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }