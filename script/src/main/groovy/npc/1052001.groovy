package npc

import constants.game.GameConstants
import scripting.npc.NPCConversationManager
import server.life.MaplePlayerNPC
import tools.I18nMessage
import tools.SimpleMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1052001 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def action = ["1stJob": false, "2ndjob": false, "3thJobI": false, "3thJobC": false]
   int job = 410
   boolean spawnPlayerNpc = false
   int spawnPlayerNpcFee = 7000000
   int jobType = 4

   def start() {
      if ((cm.getJobId() / 100).intValue() == jobType && cm.canSpawnPlayerNpc(GameConstants.getHallOfFameMapId(cm.getJob()))) {
         spawnPlayerNpc = true

         String sendStr = I18nMessage.from("1052001_WALKED_A_LONG_WAY").to(cm.getClient()).evaluate()
         if (spawnPlayerNpcFee > 0) {
            sendStr += I18nMessage.from("1052001_FEE").with(cm.numberWithCommas(spawnPlayerNpcFee)).to(cm.getClient()).evaluate()
         }

         cm.sendYesNo(SimpleMessage.from(sendStr))
      } else {
         if (cm.getJobId() == 0) {
            action["1stJob"] = true
            cm.sendNext(I18nMessage.from("1052001_WANT_TO_BE_A_THIEF").with(cm.getFirstJobStatRequirement(jobType)))
         } else if (cm.getLevel() >= 30 && cm.getJobId() == 400) {
            action["2ndJob"] = true
            if (cm.haveItem(4031012)) {
               cm.sendNext(I18nMessage.from("1052001_YOU_HAVE_DONE_WELL"))
            } else if (cm.haveItem(4031011)) {
               cm.sendOk(I18nMessage.from("1052001_GO_AND_SEE"))
               cm.dispose()
            } else {
               cm.sendNext(I18nMessage.from("1052001_ASTONISHING_PROGRESS"))
            }
         } else if (action["3thJobI"] || (cm.getPlayer().gotPartyQuestItem("JB3") && cm.getLevel() >= 70 && cm.getJobId() % 10 == 0 && (cm.getJobId() / 100).intValue() == 4 && !cm.getPlayer().gotPartyQuestItem("JBP"))) {
            action["3thJobI"] = true
            cm.sendNext(I18nMessage.from("1052001_A_FEW_DAYS_AGO"))
         } else if (cm.getPlayer().gotPartyQuestItem("JBP") && !cm.haveItem(4031059)) {
            cm.sendNext(I18nMessage.from("1052001_BRING_ME"))
            cm.dispose()
         } else if (cm.haveItem(4031059) && cm.getPlayer().gotPartyQuestItem("JBP")) {
            action["3thJobC"] = true
            cm.sendNext(I18nMessage.from("1052001_DEFEATED_CLONE"))
         } else if (cm.isQuestStarted(6141)) {
            cm.warp(910300000, 3)
         } else {
            cm.sendOk(I18nMessage.from("1052001_CHOSEN_WISELY"))
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
                  cm.sendOk(I18nMessage.from("1052001_NOT_ENOUGH_MESOS"))
                  cm.dispose()
                  return
               }

               if (MaplePlayerNPC.spawnPlayerNPC(GameConstants.getHallOfFameMapId(cm.getJob()), cm.getPlayer())) {
                  cm.sendOk(I18nMessage.from("1052001_THERE_YOU_GO"))
                  cm.gainMeso(-spawnPlayerNpcFee)
               } else {
                  cm.sendOk(I18nMessage.from("1052001_CURRENTLY_FULL"))
               }
            }

            cm.dispose()
            return
         } else {
            if (mode != 1 || status == 7 && type != 1 || (action["1stJob"] && status == 4) || (cm.haveItem(4031008) && status == 2) || (action["3thJobI"] && status == 1)) {
               if (mode == 0 && status == 2 && type == 1) {
                  cm.sendOk(I18nMessage.from("1052001_NO_OTHER_CHOICE"))
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
               cm.sendYesNo(I18nMessage.from("1052001_WANNA_BE_A_ROGUE"))
            } else {
               cm.sendOk(I18nMessage.from("1052001_TRAIN_A_BIT_MORE"))
               cm.dispose()
            }
         } else if (status == 1) {
            if (cm.canHold(2070000) && cm.canHoldAll([1472061, 1332063])) {
               if (cm.getJobId() == 0) {
                  cm.changeJobById(400)
                  cm.gainItem(2070015, (short) 500)
                  cm.gainItem(1472061, (short) 1)
                  cm.gainItem(1332063, (short) 1)
                  cm.resetStats()
               }
               cm.sendNext(I18nMessage.from("1052001_GIVE_YOU_SOME_OF_MY_ABILITIES"))
            } else {
               cm.sendNext(I18nMessage.from("1052001_MAKE_ROOM_IN_INVENTORY"))
               cm.dispose()
            }
         } else if (status == 2) {
            cm.sendNextPrev(I18nMessage.from("1052001_STRONGER_NOW"))
         } else if (status == 3) {
            cm.sendNextPrev(I18nMessage.from("1052001_CANNOT_CHANGE"))
         } else {
            cm.dispose()
         }
      } else if (action["2ndJob"]) {
         if (status == 0) {
            if (cm.haveItem(4031012)) {
               cm.sendSimple(I18nMessage.from("1052001_PATH_INFO"))
            } else {
               cm.sendNext(I18nMessage.from("1052001_GOOD_DECISION"))
               if (!cm.isQuestStarted(100009)) {
                  cm.startQuest(100009)
               }
            }
         } else if (status == 1) {
            if (!cm.haveItem(4031012)) {
               if (cm.canHold(4031011)) {
                  if (!cm.haveItem(4031011)) {
                     cm.gainItem(4031011, (short) 1)
                  }
                  cm.sendNextPrev(I18nMessage.from("1052001_PLEASE_GET_THIS_LETTER_TO"))
               } else {
                  cm.sendNext(I18nMessage.from("1052001_MAKE_SPACE_IN_INVENTORY"))
                  cm.dispose()
               }
            } else {
               if (selection < 3) {
                  if (selection == 0) {    //assassin
                     cm.sendNext(I18nMessage.from("1052001_ASSASSIN_INFO"))
                  } else if (selection == 1) {    //bandit
                     cm.sendNext(I18nMessage.from("1052001_BANDIT_INFO"))
                  }

                  status -= 2
               } else {
                  cm.sendSimple(I18nMessage.from("1052001_CHOOSE_PATH"))
               }
            }
         } else if (status == 2) {
            if (cm.haveItem(4031011)) {
               cm.dispose()
               return
            }
            job += selection * 10
            cm.sendYesNo(I18nMessage.from("1052001_CONFIRM").with(job == 410 ? "#bAssassin#k" : "#bBandit#k"))
         } else if (status == 3) {
            if (cm.haveItem(4031012)) {
               cm.gainItem(4031012, (short) -1)
            }
            cm.completeQuest(100011)

            if (job == 410) {
               cm.sendNext(I18nMessage.from("1052001_ASSASSIN_CHOSEN"))
            } else {
               cm.sendNext(I18nMessage.from("1052001_BANDIT_CHOSEN"))
            }

            if (cm.getJobId() != job) {
               cm.changeJobById(job)
            }
         } else if (status == 4) {
            cm.sendNextPrev(I18nMessage.from("1052001_BOOK_OF_SKILLS").with(job == 410 ? "assassin" : "bandit"))
         } else if (status == 5) {
            cm.sendNextPrev(I18nMessage.from("1052001_GIVEN_SP"))
         } else if (status == 6) {
            cm.sendNextPrev((job == 410 ? "Assassin" : "Bandit") + " need to be strong. But remember that you can't abuse that power and use it on a weakling. Please use your enormous power the right way, because... for you to use that the right way, that is much harden than just getting stronger. Please find me after you have advanced much further. I'll be waiting for you.")
         }
      } else if (action["3thJobI"]) {
         if (status == 0) {
            if (cm.getPlayer().gotPartyQuestItem("JB3")) {
               cm.getPlayer().removePartyQuestItem("JB3")
               cm.getPlayer().setPartyQuestItemObtained("JBP")
            }
            cm.sendNextPrev(I18nMessage.from("1052001_CLONE"))
         }
      } else if (action["3thJobC"]) {
         cm.getPlayer().removePartyQuestItem("JBP")
         cm.gainItem(4031059, (short) -1)
         cm.gainItem(4031057, (short) 1)
         cm.dispose()
      }
   }
}

NPC1052001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1052001(cm: cm))
   }
   return (NPC1052001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }