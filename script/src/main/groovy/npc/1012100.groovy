package npc

import constants.game.GameConstants
import scripting.npc.NPCConversationManager
import server.life.MaplePlayerNPC

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1012100 {
   NPCConversationManager cm
   int status = -1
   int sel = -1
   def action = ["1stJob": false, "2ndjob": false, "3thJobI": false, "3thJobC": false]
   int job = 310
   boolean spawnPlayerNpc = false
   int spawnPlayerNpcFee = 7000000
   int jobType = 3

   def start() {
      if ((cm.getJobId() / 100).intValue() == jobType && cm.canSpawnPlayerNpc(GameConstants.getHallOfFameMapId(cm.getJob()))) {
         spawnPlayerNpc = true

         String sendStr = "You have walked a long way to reach the power, wisdom and courage you hold today, haven't you? What do you say about having right now #ra NPC on the Hall of Fame holding the current image of your character#k? Do you like it?"
         if (spawnPlayerNpcFee > 0) {
            sendStr += " I can do it for you, for the fee of #b " + cm.numberWithCommas(spawnPlayerNpcFee) + " mesos.#k"
         }

         cm.sendYesNo(sendStr)
      } else {
         if (cm.getJobId() == 0) {
            action["1stJob"] = true
            cm.sendNext("So you decided to become a #rbowman#k? There are some standards to meet, y'know... #bYour level should be at least 10, with at least " + cm.getFirstJobStatRequirement(jobType) + "#k. Let's see.")
         } else if (cm.getLevel() >= 30 && cm.getJobId() == 300) {
            action["2ndJob"] = true
            if (cm.haveItem(4031012)) {
               cm.sendNext("Haha...I knew you'd breeze through that test. I'll admit, you are a great bowman. I'll make you much stronger than you're right now. before that, however... you;ll need to choose one of two paths given to you. It'll be a difficult decision for you to make, but... if there's any question to ask, please do so.")
            } else if (cm.haveItem(4031011)) {
               cm.sendOk("Go and see the #b#p1072002##k.")
               cm.dispose()
            } else {
               cm.sendYesNo("Hmmm... you have grown a lot since I last saw you. I don't see the weakling I saw before, and instead, look much more like a bowman now. Well, what do you think? Don't you want to get even more powerful than that? Pass a simple test and I'll do just that for you. Do you want to do it?")
            }
         } else if (action["3thJobI"] || (cm.getPlayer().gotPartyQuestItem("JB3") && cm.getLevel() >= 70 && cm.getJobId() % 10 == 0 && (cm.getJobId() / 100).intValue() == 3 && !cm.getPlayer().gotPartyQuestItem("JBP"))) {
            action["3thJobI"] = true
            cm.sendNext("There you are. A few days ago, #b#p2020010##k of Ossyria talked to me about you. I see that you are interested in making the leap to the amazing world of the third job advancement for archers. To achieve that goal, I will have to test your strength in order to see whether you are worthy of the advancement. There is an opening in the middle of a deep forest in Victoria Island, where it'll lead you to a secret passage. Once inside, you'll face a clone of myself. Your task is to defeat her and bring #b#t4031059##k back with you.")
         } else if (cm.getPlayer().gotPartyQuestItem("JBP") && !cm.haveItem(4031059)) {
            cm.sendNext("Please, bring me the #b#t4031059##k.")
            cm.dispose()
         } else if (cm.haveItem(4031059) && cm.getPlayer().gotPartyQuestItem("JBP")) {
            action["3thJobC"] = true
            cm.sendNext("Nice work. You have defeated my clone and brought #b#t4031059##k back safely. You have now proven yourself worthy of the 3rd job advancement from the physical standpoint. Now you should give this necklace to #b#p2020011##k in Ossyria to take on the second part of the test. Good luck. You'll need it.")
         } else {
            cm.sendOk("You have chosen wisely.")
            cm.dispose()
         }
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode == -1 && selection == -1) {
         cm.dispose()
         return
      } else if (mode == 0 && type != 1) {
         status -= 2
      }

      if (status == -1) {
         start()
         return
      } else {
         if (spawnPlayerNpc) {
            if (mode > 0) {
               if (cm.getMeso() < spawnPlayerNpcFee) {
                  cm.sendOk("Sorry, you don't have enough mesos to purchase your place on the Hall of Fame.")
                  cm.dispose()
                  return
               }

               if (MaplePlayerNPC.spawnPlayerNPC(GameConstants.getHallOfFameMapId(cm.getJob()), cm.getPlayer())) {
                  cm.sendOk("There you go! Hope you will like it.")
                  cm.gainMeso(-spawnPlayerNpcFee)
               } else {
                  cm.sendOk("Sorry, the Hall of Fame is currently full...")
               }
            }

            cm.dispose()
            return
         } else {
            if (mode != 1 || status == 7 && type != 1 || (action["1stJob"] && status == 4) || (cm.haveItem(4031008) && status == 2) || (action["3thJobI"] && status == 1)) {
               if (mode == 0 && status == 2 && type == 1) {
                  cm.sendOk("You know there is no other choice...")
               }
               if (!(mode == 0 && type != 1)) {
                  cm.dispose()
                  return
               }
            }
         }
      }

      if (action["1stJob"]) {
         if (status == 0) {
            if (cm.getLevel() >= 10 && cm.canGetFirstJob(jobType)) {
               cm.sendNextPrev("It is an important and final choice. You will not be able to turn back.")
            } else {
               cm.sendOk("Train a bit more until you reach the base requirements and I can show you the way of the #rBowman#k.")
               cm.dispose()
            }
         } else if (status == 1) {
            if (cm.canHold(1452051) && cm.canHold(2070000)) {
               if (cm.getJobId() == 0) {
                  cm.changeJobById(300)
                  cm.gainItem(1452051, (short) 1)
                  cm.gainItem(2060000, (short) 1000)
                  cm.resetStats()
               }
               cm.sendNext("Alright, from here out, you are a part of us! You'll be living the life of a wanderer at ..., but just be patient as soon, you'll be living the high life. Alright, it ain't much, but I'll give you some of my abilities... HAAAHHH!!!")
            } else {
               cm.sendNext("Make some room in your inventory and talk back to me.")
               cm.dispose()
            }
         } else if (status == 2) {
            cm.sendNextPrev("You've gotten much stronger now. Plus every single one of your inventories have added slots. A whole row, to be exact. Go see for it yourself. I just gave you a little bit of #bSP#k. When you open up the #bSkill#k menu on the lower left corner of the screen, there are skills you can learn by using SP's. One warning, though: You can't raise it all together all at once. There are also skills you can acquire only after having learned a couple of skills first.")
         } else if (status == 3) {
            cm.sendNextPrev("Now a reminder. Once you have chosen, you cannot change up your mind and try to pick another path. Go now, and live as a proud Bowman.")
         } else {
            cm.dispose()
         }
      } else if (action["2ndJob"]) {
         if (status == 0) {
            if (cm.haveItem(4031012)) {
               cm.sendSimple("Alright, when you have made your decision, click on [I'll choose my occupation] at the bottom.#b\r\n#L0#Please explain to me what being the Hunter is all about.\r\n#L1#Please explain to me what being the Crossbowman is all about.\r\n#L3#I'll choose my occupation!")
            } else {
               cm.sendNext("Good decision. You look strong, but I need to see if you really are strong enough to pass the test, it's not a difficult test, so you'll do just fine. Here, take my letter first... make sure you don't lose it!")
               if (!cm.isQuestStarted(100000)) {
                  cm.startQuest(100000)
               }
            }
         } else if (status == 1) {
            if (!cm.haveItem(4031012)) {
               if (cm.canHold(4031010)) {
                  if (!cm.haveItem(4031010)) {
                     cm.gainItem(4031010, (short) 1)
                  }
                  cm.sendNextPrev("Please get this letter to #b#p1072002##k who's around #b#m106010000##k near Henesys. She is taking care of the job of an instructor in place of me. Give her the letter and she'll test you in place of me. Best of luck to you.")
                  cm.dispose()
               } else {
                  cm.sendNext("Please, make some space in your inventory.")
                  cm.dispose()
               }
            } else {
               if (selection < 3) {
                  if (selection == 0) {    //hunter
                     cm.sendNext("Archers that master #rBows#k.\r\n\r\n#bHunters#k have a higher damage/minute output in early levels, with attacks having a faster pace but slightly weaker than Crossbowmans. #bHunters#k get #rArrow Bomb#k, a slightly weaker attack that can cause up to 6 enemies to get stunned.")
                  } else if (selection == 1) {    //crossbowman
                     cm.sendNext("Archers that master #rCrossbows#k.\r\n\r\n#bCrossbowmans'#k attack power grows higher the higher level you are, when compared to Hunters. #bCrossbowmans#k get #rIron Arrow#k, a stronger attack that does not home on enemies but can go through walls.")
                  }

                  status -= 2
               } else {
                  cm.sendSimple("Now... have you made up your mind? Please choose the job you'd like to select for your 2nd job advancement. #b\r\n#L0#Hunter\r\n#L1#Crossbowman")
               }
            }
         } else if (status == 2) {
            job += selection * 10
            cm.sendYesNo("So you want to make the second job advancement as the " + (job == 310 ? "#bHunter#k" : "#bCrossbowman#k") + "? You know you won't be able to choose a different job for the 2nd job advancement once you make your decision here, right?")
         } else if (status == 3) {
            if (cm.haveItem(4031012)) {
               cm.gainItem(4031012, (short) -1)
            }

            cm.sendNext("Alright, you're the " + (job == 310 ? "#bHunter#k" : "#bCrossbowman#k") + " from here on out. " + (job == 310 ? "#bHunter#k" : "#bCrossbowman#k") + "s are the intelligent bunch with incredible vision, able to pierce the arrow through the heart of the monsters with ease... please train yourself each and everyday. I'll help you become even stronger than you already are.")
            if (cm.getJobId() != job) {
               cm.changeJobById(job)
            }
         } else if (status == 4) {
            cm.sendNextPrev("I have just given you a book that gives you the list of skills you can acquire as a " + (job == 310 ? "hunter" : "crossbowman") + ". Also your etc inventory has expanded by adding another row to it. Your max HP and MP have increased, too. Go check and see for it yourself.")
         } else if (status == 5) {
            cm.sendNextPrev("I have also given you a little bit of #bSP#k. Open the #bSkill Menu#k located at the bottom left corner. you'll be able to boost up the newer acquired 2nd level skills. A word of warning, though. You can't boost them up all at once. Some of the skills are only available after you have learned other skills. Make sure yo remember that.")
         } else if (status == 6) {
            cm.sendNextPrev((job == 310 ? "Hunter" : "Crossbowman") + " need to be strong. But remember that you can't abuse that power and use it on a weakling. Please use your enormous power the right way, because... for you to use that the right way, that is much harden than just getting stronger. Please find me after you have advanced much further. I'll be waiting for you.")
         }
      } else if (action["3thJobI"]) {
         if (status == 0) {
            if (cm.getPlayer().gotPartyQuestItem("JB3")) {
               cm.getPlayer().removePartyQuestItem("JB3")
               cm.getPlayer().setPartyQuestItemObtained("JBP")
            }
            cm.sendNextPrev("Since she is a clone of myself, you can expect a tough battle ahead. He uses a number of special attacking skills unlike any you have ever seen, and it is your task to successfully take him one on one. There is a time limit in the secret passage, so it is crucial that you defeat him within the time limit. I wish you the best of luck, and I hope you bring the #b#t4031059##k with you.")
         }
      } else if (action["3thJobC"]) {
         cm.getPlayer().removePartyQuestItem("JBP")
         cm.gainItem(4031059, (short) -1)
         cm.gainItem(4031057, (short) 1)
         cm.dispose()
      }
   }
}

NPC1012100 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1012100(cm: cm))
   }
   return (NPC1012100) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }