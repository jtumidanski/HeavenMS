package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

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

            cm.sendOk("I only wish to speak to your leader!")
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

                  cm.sendNext("Please save Minerva, She've been trapped in the seal by Papa Pixie, the terror of our tower! He misplaced all of our Minerva Statue's parts and we have to get it all back! Oh pardon me, I am the tower's Chamberlain, Eak. I am Minerva's royal servant.")
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
                     cm.sendOk("Oh! You brought the #t4001055#! Please, drop it at the base of the statue to bring Minerva back!")
                  } else {
                     cm.sendOk("Thank you for saving Minerva! Please, talk to her...")
                  }
               } else {
                  cm.sendOk("Please, save Minerva! Gather the six pieces of her statue and then talk to me to retrieve the final piece!")
               }
               break
            case 920010200: //walkway
               if (!cm.haveItem(4001050, 30)) {
                  cm.sendOk("Gather the 30 Statue Pieces from the monsters in this stage, and please bring them to me so I can put them together!")
               } else {
                  cm.sendOk("You got them all! Here, the 1st statue piece.")
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
                        cm.sendOk("Oh, I've found the 2nd Piece of Statue. Here, take it.")
                        cm.gainItem(4001045, (short) 1)
                        eim.giveEventPlayersExp(3500)
                        clearStage(2, eim)
                        eim.setProperty("statusStg2", "1")
                     } else {
                        cm.sendOk("I've found the 2nd Piece of Statue. Get a slot available on your inventory to take it.")
                     }
                  } else {
                     cm.sendOk("Find the 2nd Piece of Statue that is hidden in this room.")
                  }
               } else {
                  cm.sendOk("Well done. Go find the other statue pieces.")
               }

               break
            case 920010400: //lobby
               if (eim.getIntProperty("statusStg3") == -1) {
                  cm.sendOk("Please, find the LP for the current day of week and place it on the music player.\r\n#v4001056# Sunday\r\n#v4001057# Monday\r\n#v4001058# Tuesday\r\n#v4001059# Wednesday\r\n#v4001060# Thursday\r\n#v4001061# Friday\r\n#v4001062# Saturday\r\n")
               } else if (eim.getIntProperty("statusStg3") == 0) {
                  cm.getMap().getReactorByName("stone3").forceHitReactor((byte) 1)
                  cm.sendOk("Ooh, the music... It sounds so fitting with the ambient. Nicely done, a box has appeared on the field. Retrieve the statue part from it!")
                  eim.giveEventPlayersExp(3500)
                  clearStage(3, eim)
                  eim.setProperty("statusStg3", "2")

               } else {
                  cm.sendOk("Thank you so much!")
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
                     cm.sendOk("There needs to be exactly 3 players on these platforms.")
                  } else {
                     int num_correct = 0
                     for (def i = 0; i < 3; i++) {
                        if (eim.getProperty("stage4_" + i) == "" + players[i]) {
                           num_correct++
                        }
                     }
                     if (num_correct == 3) {
                        cm.sendOk("You found the right combination! A box has appeared on the top of this map, go retrieve the statue piece from it!")
                        cm.getMap().getReactorByName("stone4").forceHitReactor((byte) 1)
                        eim.giveEventPlayersExp(3500)
                        clearStage(4, eim)
                     } else {
                        eim.showWrongEffect()
                        if (num_correct > 0) {
                           cm.sendOk("One of the platforms has the right number of players.")
                        } else {
                           cm.sendOk("All of the platforms have the wrong amount of players.")
                        }
                     }
                  }
               } else {
                  cm.sendOk("Well done! Please, go fetch the other pieces and save Minerva!")
               }
               cm.dispose()
               break
            case 920010600: //lounge
               if (eim.getIntProperty("statusStg5") == -1) {
                  if (!cm.haveItem(4001052, 40)) {
                     cm.sendOk("Gather the 40 Statue Pieces from the monsters in this stage, and please bring them to me so I can put them together!")
                  } else {
                     cm.sendOk("You got them all! Here, the 5th statue piece.")
                     cm.removeAll(4001052)
                     cm.gainItem(4001048, (short) 1) //fifth piece
                     eim.giveEventPlayersExp(3500)
                     clearStage(5, eim)
                     eim.setIntProperty("statusStg5", 1)
                  }
               } else {
                  cm.sendOk("You got them all here. Go search the others rooms of the tower.")
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
                     cm.sendOk("There needs to be exactly 2 levers at the top of the map pushed on.")
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
                        cm.sendOk("You found the right combination! Retrieve the statue piece from inside it!")
                        cm.getMap().getReactorByName("stone6").forceHitReactor((byte) 1)
                        eim.giveEventPlayersExp(3500)
                        clearStage(6, eim)
                     } else {
                        eim.showWrongEffect()
                        if (psh_correct >= 1) {
                           cm.sendOk("One of the pushed levers is correct.")
                        } else {
                           cm.sendOk("Both of the pushed levers are wrong.")
                        }
                     }
                  }
               } else {
                  cm.sendOk("Nicely done!! Go check out the rest of the pieces.")
               }
               break
            case 920010800:
               cm.sendNext("Please, find a way to defeat Papa Pixie! Once you've found the Dark Nependeath by placing seeds, you've found Papa Pixie! Defeat it, and get the Root of Life to save Minerva!!!")
               break
            case 920010900:
               if (eim.getProperty("statusStg8") == "1") {
                  cm.sendNext("This is the jail of the tower. You may find some goodies here, just be sure to clear the puzzles ahead as fast as possible.")
               } else {
                  cm.sendNext("Down there you will not find any statue pieces. Go up the ladder to return to the center tower and search elsewhere. You can come back here to get the goodies that lies down there once you have saved Minerva.")
               }
               break
            case 920011000:
               if (cm.getMap().countMonsters() > 0) {
                  cm.sendNext("This is the hidden room of the tower. After eliminating all monsters on this room, talk to me to gain access to the treasure room, leaving the center tower access behind.")
               } else {
                  cm.warp(920011100)
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