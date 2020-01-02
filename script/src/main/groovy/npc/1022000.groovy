package npc

import constants.game.GameConstants
import scripting.npc.NPCConversationManager
import server.life.MaplePlayerNPC

class NPC1022000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def action = ["1stJob": false, "2ndjob": false, "3thJobI": false, "3thJobC": false]
   int job = 110

   boolean spawnPlayerNpc = false
   int spawnPlayerNpcFee = 7000000
   int jobType = 1

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
            cm.sendNext("Do you want to become a #rwarrior#k? You need to meet some criteria in order to do so.#b You should be at least in level 10, and at least " + cm.getFirstJobStatRequirement(jobType) + "#k. Let's see...")
         } else if (cm.getLevel() >= 30 && cm.getJobId() == 100) {
            action["2ndJob"] = true
            if (cm.haveItem(4031012)) {
               cm.sendNext("Oh... you came back safe! I knew you'd breeze through. I'll admit, you are a strong, formidable Warrior! Alright, I'll make you an even stronger Warrior than you already are. But before that, you need to choose one of the three paths that you'll be given. It isn't going to be easy, so if you have and questions, feel free to ask.")
            } else if (cm.haveItem(4031008)) {
               cm.sendOk("Go and see the #b#p1072000##k.")
               cm.dispose()
            } else {
               cm.sendNext("The progress you have made is astonishing.")
            }
         } else if (action["3thJobI"] || (cm.getPlayer().gotPartyQuestItem("JB3") && cm.getLevel() >= 70 && (cm.getJobId() % 10 == 0 && (cm.getJobId() / 100) == 1 && !cm.getPlayer().gotPartyQuestItem("JBP")))) {
            action["3thJobI"] = true
            cm.sendNext("I was waiting for you. Few days ago, I heard about you from #b#p2020008##k in Ossyria. Well... I'd like to test your strength. There is a secret passage near the ant tunnel. Nobody but you can go into that passage. If you go into the passage, you will meat my the other self. Beat him and bring #b#t4031059##k to me.")
         } else if (cm.getPlayer().gotPartyQuestItem("JBP") && !cm.haveItem(4031059)) {
            cm.sendNext("Please, bring me the #b#t4031059##k.")
            cm.dispose()
         } else if (cm.haveItem(4031059) && cm.getPlayer().gotPartyQuestItem("JBP")) {
            action["3thJobC"] = true
            cm.sendNext("Wow... You beat my the other self and brought #b#t4031059##k to me. Good! this surely proves your strength. In terms of strength, you are ready to advance to 3th job. As I promised, I will give #b#t4031057##k to you. Give this necklace to #b#p2020008##k in Ossyria and you will be able to take second test of 3rd job advancement. Good Luck~")
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
            if (mode != 1 || status == 7 && type != 1 || (action["1stJob"] && status == 4) || (cm.haveItem(4031008) && status == 2) || (action["3thJob"] && status == 1)) {
               if (mode == 0 && status == 2 && type == 1) {
                  cm.sendOk("Make up your mind and visit me again.")
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
               cm.sendOk("Train a bit more until you reach the base requirements and I can show you the way of the #rWarrior#k.")
               cm.dispose()
            }
         } else if (status == 1) {
            if (cm.canHold(1302077)) {
               if (cm.getJobId() == 0) {
                  cm.changeJobById(100)
                  cm.gainItem(1302077, (short) 1)
                  cm.resetStats()
               }
               cm.sendNext("From here on out, you are going to the Warrior path. This is not an easy job, but if you have discipline and confidence in your own body and skills, you will overcome any difficulties in your path. Go, young Warrior!")
            } else {
               cm.sendNext("Make some room in your inventory and talk back to me.")
               cm.dispose()
            }
         } else if (status == 2) {
            cm.sendNextPrev("You've gotten much stronger now. Plus every single one of your inventories have added slots. A whole row, to be exact. Go see for it yourself. I just gave you a little bit of #bSP#k. When you open up the #bSkill#k menu on the lower left corner of the screen, there are skills you can learn by using SP's. One warning, though: You can't raise it all together all at once. There are also skills you can acquire only after having learned a couple of skills first.")
         } else if (status == 3) {
            cm.sendNextPrev("Now a reminder. Once you have chosen, you cannot change up your mind and try to pick another path. Go now, and live as a proud Warrior.")
         } else {
            cm.dispose()
         }
      } else if (action["2ndJob"]) {
         if (status == 0) {
            if (cm.haveItem(4031012)) {
               cm.sendSimple("Alright, when you have made your decision, click on [I'll choose my occupation] at the bottom.#b\r\n#L0#Please explain to me what being the Fighter is all about.\r\n#L1#Please explain to me what being the Page is all about.\r\n#L2#Please explain to me what being the Spearman is all about.\r\n#L3#I'll choose my occupation!")
            } else {
               cm.sendNext("Good decision. You look strong, but I need to see if you really are strong enough to pass the test, it's not a difficult test, so you'll do just fine. Here, take my letter first... make sure you don't lose it!")
               if (!cm.isQuestStarted(100003)) {
                  cm.startQuest(100003)
               }
            }
         } else if (status == 1) {
            if (!cm.haveItem(4031012)) {
               if (cm.canHold(4031008)) {
                  if (!cm.haveItem(4031008)) {
                     cm.gainItem(4031008, (short) 1)
                  }
                  cm.sendNextPrev("Please get this letter to #b#p1072000##k who's around #b#m102020300##k near Perion. He is taking care of the job of an instructor in place of me. Give him the letter and he'll test you in place of me. Best of luck to you.")
               } else {
                  cm.sendNext("Please, make some space in your inventory.")
                  cm.dispose()
               }
            } else {
               if (selection < 3) {
                  if (selection == 0) {    //fighter
                     cm.sendNext("Warriors that master #rSwords or Axes#k.\r\n\r\n#rFighters#k get #bRage#k, which boosts your party's weapon attack by 10. During 2nd job this is strongly appreciated, as it is free (except for -10 wep def, which is not going to impact the damage you take much at all), takes no Use slots and increases each party member's damage (except Magicians) by several hundreds. The other classes can give themselves a weapon attack boost as well, but need items to do so. #rFighters#k also get #bPower Guard#k, reducing touch damage by 40% and deals it back to the monster. This is the main reason why #rFighters#k are considered soloers is because this reduces pot costs immensely.")
                  } else if (selection == 1) {    //page
                     cm.sendNext("Warriors that master #rSwords or Maces/Blunt weapons#k.\r\n\r\n#rPages#k get #bThreaten#k, a skill that lowers the enemies' weapon defense and weapon attack by 20; this is mostly used to lower damage dealt to you. Pages also get #bPower Guard#k, reducing touch damage by 40% and deals it back to the monster. This is one of the main reason why #bPages/WKs#k are considered solo players, that's because this reduces pot costs immensely. Of course, constant KB and #bIce Charge#k helps also to the soloing factor.")
                  } else {    //spear man
                     cm.sendNext("Warriors that master #rSpears or Polearms#k.\r\n\r\n#rSpearmen#k get #bHyper Body#k, which boosts your max HP/MP and that of your party by 60% when maxed. This skill is particularly useful for helping partied Thieves, Archers, and Magicians to survive more hits from enemies and/or PQ bosses. They also get #bIron Will#k which gives +20 wep def and +20 mag def for 300 sec. It is basically a nerfed Bless with 100 seconds more duration but gives no accuracy or avoidability bonus. Even with this skill maxed, it isn't even close to being in the same league as Power Guard and is why Spearmen/Dark Knights are not considered a soloing class.")
                  }

                  status -= 2
               } else {
                  cm.sendSimple("Now... have you made up your mind? Please choose the job you'd like to select for your 2nd job advancement. #b\r\n#L0#Fighter\r\n#L1#Page\r\n#L2#Spearman")
               }
            }
         } else if (status == 2) {
            if (cm.haveItem(4031008)) {
               cm.dispose()
               return
            }
            job += selection * 10
            cm.sendYesNo("So you want to make the second job advancement as the " + (job == 110 ? "#bFighter#k" : job == 120 ? "#bPage#k" : "#bSpearman#k") + "? You know you won't be able to choose a different job for the 2nd job advancement once you make your decision here, right? Are you sure about this?")
         } else if (status == 3) {
            if (cm.haveItem(4031012)) {
               cm.gainItem(4031012, (short) -1)
            }
            cm.completeQuest(100005)

            if (job == 110) {
               cm.sendNext("Alright, you have now become the #bFighter#k. A fighter strives to become the strongest of the strong, and never stops fighting. Don't ever lose that will to fight, and push forward 24/7. I'll help you become even stronger than you already are.")
            } else if (job == 120) {
               cm.sendNext("Alright, you have now become a #bPage#k! Pages have high intelligence and bravery, which I hope you'll employ throughout your journey to the right path. I'll help you become much stronger than you already are.")
            } else {
               cm.sendNext("Alright, you have now become the #bSpearman#k. The Spearman use the power of darkness to take out the enemies, always in shadows... Please believe in yourself and your awesome power as you go in your journey. I'll help you become much stronger than you are right now.")
            }
            if (cm.getJobId() != job) {
               cm.changeJobById(job)
            }
         } else if (status == 4) {
            cm.sendNextPrev("I have just given you a book that gives you the list of skills you can acquire as a " + (job == 110 ? "fighter" : job == 120 ? "page" : "spearman") + ". Also your etc inventory has expanded by adding another row to it. Your max HP and MP have increased, too. Go check and see for it yourself.")
         } else if (status == 5) {
            cm.sendNextPrev("I have also given you a little bit of #bSP#k. Open the #bSkill Menu#k located at the bottom left corner. you'll be able to boost up the newer acquired 2nd level skills. A word of warning, though. You can't boost them up all at once. Some of the skills are only available after you have learned other skills. Make sure yo remember that.")
         } else if (status == 6) {
            cm.sendNextPrev((job == 110 ? "Fighter" : job == 120 ? "Page" : "Spearman") + " need to be strong. But remember that you can't abuse that power and use it on a weakling. Please use your enormous power the right way, because... for you to use that the right way, that is much harden than just getting stronger. Please find me after you have advanced much further. I'll be waiting for you.")
         }
      } else if (action["3thJobI"]) {
         if (status == 0) {
            if (cm.getPlayer().gotPartyQuestItem("JB3")) {
               cm.getPlayer().removePartyQuestItem("JB3")
               cm.getPlayer().setPartyQuestItemObtained("JBP")
            }
            cm.sendNextPrev("My the other self is quite strong. He uses many special skills and you should fight with him 1 on 1. However, people cannot stay long in the secret passage, so it is important to beat him ASAP. Well... Good luck I will look forward to you bringing #b#t4031059##k to me.")
         }
      } else if (action["3thJobC"]) {
         cm.getPlayer().removePartyQuestItem("JBP")
         cm.gainItem(4031059, (short) -1)
         cm.gainItem(4031057, (short) 1)
         cm.dispose()
      }
   }
}

NPC1022000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1022000(cm: cm))
   }
   return (NPC1022000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }