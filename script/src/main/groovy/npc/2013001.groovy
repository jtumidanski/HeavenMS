package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Chamberlain Eak
	Map(s): 		Orbis - Tower of Goddess
	Description: 	Orbis PQ
*/


class NPC2013001 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (cm.getPlayer().getMapId() == 920011200) { //exit
            cm.warp(200080101)
            cm.dispose()
            return
         }
         if (!cm.isEventLeader()) {
            if (cm.getPlayer().getMapId() == 920010000) {
               cm.warp(920010000, 2)
               cm.dispose()
               return
            }

            cm.sendOk(I18nMessage.from("2013001_LEADER_ONLY"))
            cm.dispose()
            return
         }

         EventInstanceManager eim = cm.getEventInstance()

         switch (cm.getPlayer().getMapId()) {
            case 920010000:
               if (eim.getIntProperty("statusStg0") != 1) {
                  eim.warpEventTeamToMapSpawnPoint(920010000, 2)
                  eim.giveEventPlayersExp(3500)
                  clearStage(0, eim)

                  cm.sendNext(I18nMessage.from("2013001_PLEASE_SAVE_MINERVA"))
               } else {
                  cm.warp(920010000, 2)
               }
               cm.dispose()
               break
            case 920010100:
               if (isStatueComplete()) {
                  if (eim.getIntProperty("statusStg7") == -1) {
                     eim.warpEventTeam(920010800)
                  } else if (eim.getIntProperty("statusStg8") == -1) {
                     cm.sendOk(I18nMessage.from("2013001_DROP_IT_AT_THE_BASE"))
                  } else {
                     cm.sendOk(I18nMessage.from("2013001_THANK_YOU"))
                  }
               } else {
                  cm.sendOk(I18nMessage.from("2013001_RETRIEVE_THE_FINAL_PIECE"))
               }
               break
            case 920010200: //walkway
               if (!cm.haveItem(4001050, 30)) {
                  cm.sendOk(I18nMessage.from("2013001_GATHER_THE_STATUE_PIECES"))
               } else {
                  cm.sendOk(I18nMessage.from("2013001_HERE_IS_THE_1ST_STATUE_PIECE"))
                  cm.removeAll(4001050)
                  cm.gainItem(4001044, (short) 1) //first piece
                  eim.giveEventPlayersExp(3500)
                  clearStage(1, eim)
               }
               break
            case 920010300: //storage
               if (eim.getIntProperty("statusStg2") != 1) {
                  if (cm.getMap().countMonsters() == 0 && cm.getMap().countItems() == 0) {
                     if (cm.canHold(4001045)) {
                        cm.sendOk(I18nMessage.from("2013001_2ND_STATUE_PIECE"))
                        cm.gainItem(4001045, (short) 1)
                        eim.giveEventPlayersExp(3500)
                        clearStage(2, eim)
                        eim.setProperty("statusStg2", "1")
                     } else {
                        cm.sendOk(I18nMessage.from("2013001_NEED_INVENTORY_SPACE"))
                     }
                  } else {
                     cm.sendOk(I18nMessage.from("2013001_2ND_PIECE_HIDDEN"))
                  }
               } else {
                  cm.sendOk(I18nMessage.from("2013001_GO_FIND_THE_OTHER_PIECES"))
               }

               break
            case 920010400: //lobby
               if (eim.getIntProperty("statusStg3") == -1) {
                  cm.sendOk(I18nMessage.from("2013001_PLACE_IT_ON_THE_MUSIC_PLAYER"))
               } else if (eim.getIntProperty("statusStg3") == 0) {
                  cm.getMap().getReactorByName("stone3").forceHitReactor((byte) 1)
                  cm.sendOk(I18nMessage.from("2013001_RETRIEVE_THE_STATUE_PART_FROM_BOX"))
                  eim.giveEventPlayersExp(3500)
                  clearStage(3, eim)
                  eim.setProperty("statusStg3", "2")

               } else {
                  cm.sendOk(I18nMessage.from("2013001_THANK_YOU_SO_MUCH"))
               }
               break
            case 920010500: //sealed
               if (eim.getIntProperty("statusStg4") == -1) {
                  int total = 3
                  for (def i = 0; i < 2; i++) {
                     long rnd = Math.round(Math.random() * total)
                     total -= rnd

                     eim.setProperty("stage4_" + i, (int) rnd)
                  }
                  eim.setProperty("stage4_2", "" + total)

                  eim.setProperty("statusStg4", "0")
               }
               if (eim.getIntProperty("statusStg4") == 0) {
                  int[] players = []
                  int total = 0
                  for (def i = 0; i < 3; i++) {
                     int z = cm.getMap().getNumPlayersInArea(i)
                     players << z
                     total += z
                  }
                  if (total != 3) {
                     cm.sendOk(I18nMessage.from("2013001_EXACTLY_3_PLAYERS"))
                  } else {
                     int num_correct = 0
                     for (def i = 0; i < 3; i++) {
                        if (eim.getProperty("stage4_" + i) == "" + players[i]) {
                           num_correct++
                        }
                     }
                     if (num_correct == 3) {
                        cm.sendOk(I18nMessage.from("2013001_RIGHT_COMBINATION_BOX_APPEARED"))
                        cm.getMap().getReactorByName("stone4").forceHitReactor((byte) 1)
                        eim.giveEventPlayersExp(3500)
                        clearStage(4, eim)
                     } else {
                        eim.showWrongEffect()
                        if (num_correct > 0) {
                           cm.sendOk(I18nMessage.from("2013001_ONE_PLATFORM"))
                        } else {
                           cm.sendOk(I18nMessage.from("2013001_ALL_PLATFORMS"))
                        }
                     }
                  }
               } else {
                  cm.sendOk(I18nMessage.from("2013001_WELL_DONE"))
               }
               cm.dispose()
               break
            case 920010600: //lounge
               if (eim.getIntProperty("statusStg5") == -1) {
                  if (!cm.haveItem(4001052, 40)) {
                     cm.sendOk(I18nMessage.from("2013001_40_STATUE_PIECES"))
                  } else {
                     cm.sendOk(I18nMessage.from("2013001_5TH_STATUE_PIECE"))
                     cm.removeAll(4001052)
                     cm.gainItem(4001048, (short) 1) //fifth piece
                     eim.giveEventPlayersExp(3500)
                     clearStage(5, eim)
                     eim.setIntProperty("statusStg5", 1)
                  }
               } else {
                  cm.sendOk(I18nMessage.from("2013001_GO_SEARCH_OTHER_ROOMS"))
               }
               break
            case 920010700: //on the way up
               if (eim.getIntProperty("statusStg6") == -1) {
                  double rnd1 = Math.floor(Math.random() * 5)

                  double rnd2 = Math.floor(Math.random() * 5)
                  while (rnd2 == rnd1) {
                     rnd2 = Math.floor(Math.random() * 5)
                  }

                  if (rnd1 > rnd2) {
                     rnd1 = rnd1 ^ rnd2
                     rnd2 = rnd1 ^ rnd2
                     rnd1 = rnd1 ^ rnd2
                  }

                  String comb = ""
                  for (def i = 0; i < rnd1; i++) {
                     comb += "0"
                  }
                  comb += "1"
                  for (def i = rnd1 + 1; i < rnd2; i++) {
                     comb += "0"
                  }
                  comb += "1"
                  for (def i = rnd2 + 1; i < 5; i++) {
                     comb += "0"
                  }

                  eim.setProperty("stage6_c", "" + comb)

                  eim.setProperty("statusStg6", "0")
               }

               String comb = eim.getProperty("stage6_c")

               if (eim.getIntProperty("statusStg6") == 0) {
                  String react = ""
                  int total = 0
                  for (def i = 1; i <= 5; i++) {
                     if (cm.getMap().getReactorByName("" + i).getState() > 0) {
                        react += "1"
                        total += 1
                     } else {
                        react += "0"
                     }
                  }

                  if (total != 2) {
                     cm.sendOk(I18nMessage.from("2013001_2_LEVERS"))
                  } else {
                     int num_correct = 0
                     int psh_correct = 0
                     for (def i = 0; i < 5; i++) {
                        if (react.charAt(i) == comb.charAt(i)) {
                           num_correct++
                           if (react.charAt(i) == ('1' as char)) {
                              psh_correct++
                           }
                        }
                     }
                     if (num_correct == 5) {
                        cm.sendOk(I18nMessage.from("2013001_RIGHT_COMBINATION"))
                        cm.getMap().getReactorByName("stone6").forceHitReactor((byte) 1)
                        eim.giveEventPlayersExp(3500)
                        clearStage(6, eim)
                     } else {
                        eim.showWrongEffect()
                        if (psh_correct >= 1) {
                           cm.sendOk(I18nMessage.from("2013001_ONE_LEVEL_CORRECT"))
                        } else {
                           cm.sendOk(I18nMessage.from("2013001_BOTH_LEVER_WRONG"))
                        }
                     }
                  }
               } else {
                  cm.sendOk(I18nMessage.from("2013001_NICELY_DONE"))
               }
               break
            case 920010800:
               cm.sendNext(I18nMessage.from("2013001_DEFEAT_PAPA_PIXIE"))
               break
            case 920010900:
               if (eim.getProperty("statusStg8") == "1") {
                  cm.sendNext(I18nMessage.from("2013001_JAIL_OF_THE_TOWER"))
               } else {
                  cm.sendNext(I18nMessage.from("2013001_GO_UP_THE_LADDER"))
               }
               break
            case 920011000:
               if (cm.getMap().countMonsters() > 0) {
                  cm.sendNext(I18nMessage.from("2013001_ELIMINATE_MONSTERS_TO_GAIN_ACCESS"))
               } else {
                  cm.warp(920011100, "st00")
               }
               break
         }
         cm.dispose()
      }
   }

   def isStatueComplete() {
      for (def i = 1; i <= 6; i++) {
         if (cm.getMap().getReactorByName("scar" + i).getState() < 1) {
            return false
         }
      }

      return true
   }

   static def clearStage(int stage, EventInstanceManager eim) {
      eim.setProperty("statusStg" + stage, "1")
      eim.showClearEffect(true)
   }

   def clear() {
      cm.showEffect("quest/party/clear")
      cm.playSound("Party1/Clear")
   }
}

NPC2013001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2013001(cm: cm))
   }
   return (NPC2013001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }