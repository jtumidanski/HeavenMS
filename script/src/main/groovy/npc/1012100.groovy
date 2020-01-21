package npc

import constants.game.GameConstants
import scripting.npc.NPCConversationManager
import server.life.MaplePlayerNPC
import tools.I18nMessage

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
         if (spawnPlayerNpcFee > 0) {
            cm.sendYesNo(I18nMessage.from("1012100_HALL_OF_FAME_FEE").with(cm.numberWithCommas(spawnPlayerNpcFee)))
         } else {
            cm.sendYesNo(I18nMessage.from("1012100_HALL_OF_FAME"))
         }
      } else {
         if (cm.getJobId() == 0) {
            action["1stJob"] = true
            cm.sendNext(I18nMessage.from("1012100_1ST_JOB").with(cm.getFirstJobStatRequirement(jobType)))
         } else if (cm.getLevel() >= 30 && cm.getJobId() == 300) {
            action["2ndJob"] = true
            if (cm.haveItem(4031012)) {
               cm.sendNext(I18nMessage.from("1012100_2ND_JOB_BREEZE"))
            } else if (cm.haveItem(4031011)) {
               cm.sendOk(I18nMessage.from("1012100_2ND_JOB_GO_SEE"))
               cm.dispose()
            } else {
               cm.sendYesNo(I18nMessage.from("1012100_2ND_JOB_TEST"))
            }
         } else if (action["3thJobI"] || (cm.getPlayer().gotPartyQuestItem("JB3") && cm.getLevel() >= 70 && cm.getJobId() % 10 == 0 && (cm.getJobId() / 100).intValue() == 3 && !cm.getPlayer().gotPartyQuestItem("JBP"))) {
            action["3thJobI"] = true
            cm.sendNext(I18nMessage.from("1012100_3RD_JOB"))
         } else if (cm.getPlayer().gotPartyQuestItem("JBP") && !cm.haveItem(4031059)) {
            cm.sendNext(I18nMessage.from("1012100_3RD_JOB_BRING"))
            cm.dispose()
         } else if (cm.haveItem(4031059) && cm.getPlayer().gotPartyQuestItem("JBP")) {
            action["3thJobC"] = true
            cm.sendNext(I18nMessage.from("1012100_3RD_JOB_CLONE_DEFEATED"))
         } else {
            cm.sendOk(I18nMessage.from("1012100_3RD_JOB_CHOSEN_WISELY"))
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
                  cm.sendOk(I18nMessage.from("1012100_HALL_OF_FAME_NOT_ENOUGH_MESOS"))
                  cm.dispose()
                  return
               }

               if (MaplePlayerNPC.spawnPlayerNPC(GameConstants.getHallOfFameMapId(cm.getJob()), cm.getPlayer())) {
                  cm.sendOk(I18nMessage.from("1012100_HALL_OF_FAME_SPAWN"))
                  cm.gainMeso(-spawnPlayerNpcFee)
               } else {
                  cm.sendOk(I18nMessage.from("1012100_HALL_OF_FAME_FULL"))
               }
            }

            cm.dispose()
            return
         } else {
            if (mode != 1 || status == 7 && type != 1 || (action["1stJob"] && status == 4) || (cm.haveItem(4031008) && status == 2) || (action["3thJobI"] && status == 1)) {
               if (mode == 0 && status == 2 && type == 1) {
                  cm.sendOk(I18nMessage.from("1012100_NO_OTHER_CHOICE"))
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
               cm.sendNextPrev(I18nMessage.from("1012100_1ST_JOB_NO_TURN_BACK"))
            } else {
               cm.sendOk(I18nMessage.from("1012100_1ST_JOB_TRAIN_MORE"))
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
               cm.sendNext(I18nMessage.from("1012100_ONE_OF_US"))
            } else {
               cm.sendNext(I18nMessage.from("1012100_NEED_INVENTORY_ROOM"))
               cm.dispose()
            }
         } else if (status == 2) {
            cm.sendNextPrev(I18nMessage.from("1012100_STRONGER_NOW"))
         } else if (status == 3) {
            cm.sendNextPrev(I18nMessage.from("1012100_PROUD_BOWMAN"))
         } else {
            cm.dispose()
         }
      } else if (action["2ndJob"]) {
         if (status == 0) {
            if (cm.haveItem(4031012)) {
               cm.sendSimple(I18nMessage.from("1012100_CHOOSE_OCCUPATION"))
            } else {
               cm.sendNext(I18nMessage.from("1012100_TAKE_MY_LETTER"))
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
                  cm.sendNextPrev(I18nMessage.from("1012100_TAKE_LETTER_GOOD_LUCK"))
                  cm.dispose()
               } else {
                  cm.sendNext(I18nMessage.from("1012100_MAKE_INVENTORY_SPACE"))
                  cm.dispose()
               }
            } else {
               if (selection < 3) {
                  if (selection == 0) {    //hunter
                     cm.sendNext(I18nMessage.from("1012100_HUNTER_INFO"))
                  } else if (selection == 1) {    //crossbowman
                     cm.sendNext(I18nMessage.from("1012100_CROSSBOW_INFO"))
                  }
                  status -= 2
               } else {
                  cm.sendSimple(I18nMessage.from("1012100_2ND_JOB_SELECT_PATH"))
               }
            }
         } else if (status == 2) {
            job += selection * 10
            cm.sendYesNo(I18nMessage.from("1012100_2ND_JOB_ARE_YOU_SURE").with(job == 310 ? "#bHunter#k" : "#bCrossbowman#k"))
         } else if (status == 3) {
            if (cm.haveItem(4031012)) {
               cm.gainItem(4031012, (short) -1)
            }

            cm.sendNext(I18nMessage.from("1012100_2ND_JOB_ALRIGHT").with(job == 310 ? "#bHunter#k" : "#bCrossbowman#k", job == 310 ? "#bHunter#k" : "#bCrossbowman#k"))
            if (cm.getJobId() != job) {
               cm.changeJobById(job)
            }
         } else if (status == 4) {
            cm.sendNextPrev(I18nMessage.from("1012100_GIVEN_BOOK").with(job == 310 ? "hunter" : "crossbowman"))
         } else if (status == 5) {
            cm.sendNextPrev(I18nMessage.from("1012100_GIVEN_SP"))
         } else if (status == 6) {
            cm.sendNextPrev(I18nMessage.from("1012100_NEED_TO_BE_STRONG").with(job == 310 ? "Hunter" : "Crossbowman"))
         }
      } else if (action["3thJobI"]) {
         if (status == 0) {
            if (cm.getPlayer().gotPartyQuestItem("JB3")) {
               cm.getPlayer().removePartyQuestItem("JB3")
               cm.getPlayer().setPartyQuestItemObtained("JBP")
            }
            cm.sendNextPrev(I18nMessage.from("1012100_CLONE"))
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