package npc

import constants.game.GameConstants
import scripting.npc.NPCConversationManager
import server.life.MaplePlayerNPC
import tools.I18nMessage

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


         if (spawnPlayerNpcFee > 0) {
            cm.sendYesNo(I18nMessage.from("1022000_HALL_OF_FAME_FEE").with(cm.numberWithCommas(spawnPlayerNpcFee)))
         } else {
            cm.sendYesNo(I18nMessage.from("1022000_HALL_OF_FAME"))
         }
      } else {
         if (cm.getJobId() == 0) {
            action["1stJob"] = true
            cm.sendNext(I18nMessage.from("1022000_DO_YOU_WANT").with(cm.getFirstJobStatRequirement(jobType)))
         } else if (cm.getLevel() >= 30 && cm.getJobId() == 100) {
            action["2ndJob"] = true
            if (cm.haveItem(4031012)) {
               cm.sendNext(I18nMessage.from("1022000_CHOOSE_A_PATH"))
            } else if (cm.haveItem(4031008)) {
               cm.sendOk(I18nMessage.from("1022000_GO_AND_SEE"))
               cm.dispose()
            } else {
               cm.sendNext(I18nMessage.from("1022000_ASTONISHING"))
            }
         } else if (action["3thJobI"] || (cm.getPlayer().gotPartyQuestItem("JB3") && cm.getLevel() >= 70 && (cm.getJobId() % 10 == 0 && (cm.getJobId() / 100) == 1 && !cm.getPlayer().gotPartyQuestItem("JBP")))) {
            action["3thJobI"] = true
            cm.sendNext(I18nMessage.from("1022000_BEAT_CLONE"))
         } else if (cm.getPlayer().gotPartyQuestItem("JBP") && !cm.haveItem(4031059)) {
            cm.sendNext(I18nMessage.from("1022000_BRING"))
            cm.dispose()
         } else if (cm.haveItem(4031059) && cm.getPlayer().gotPartyQuestItem("JBP")) {
            action["3thJobC"] = true
            cm.sendNext(I18nMessage.from("1022000_WOW_BEAT_CLONE"))
         } else {
            cm.sendOk(I18nMessage.from("1022000_CHOSEN_WISELY"))
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
                  cm.sendOk(I18nMessage.from("1022000_HALL_OF_FAME_NOT_ENOUGH_MESOS"))
                  cm.dispose()
                  return
               }

               if (MaplePlayerNPC.spawnPlayerNPC(GameConstants.getHallOfFameMapId(cm.getJob()), cm.getPlayer())) {
                  cm.sendOk(I18nMessage.from("1022000_HALL_OF_FAME_SUCCESS"))
                  cm.gainMeso(-spawnPlayerNpcFee)
               } else {
                  cm.sendOk(I18nMessage.from("1022000_HALL_OF_FAME_FULL"))
               }
            }

            cm.dispose()
            return
         } else {
            if (mode != 1 || status == 7 && type != 1 || (action["1stJob"] && status == 4) || (cm.haveItem(4031008) && status == 2) || (action["3thJob"] && status == 1)) {
               if (mode == 0 && status == 2 && type == 1) {
                  cm.sendOk(I18nMessage.from("1022000_MAKE_UP_YOUR_MIND"))
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
               cm.sendNextPrev(I18nMessage.from("1022000_IMPORTANT_CHOICE"))
            } else {
               cm.sendOk(I18nMessage.from("1022000_TRAIN_MORE"))
               cm.dispose()
            }
         } else if (status == 1) {
            if (cm.canHold(1302077)) {
               if (cm.getJobId() == 0) {
                  cm.changeJobById(100)
                  cm.gainItem(1302077, (short) 1)
                  cm.resetStats()
               }
               cm.sendNext(I18nMessage.from("1022000_GO_YOUNG_WARRIOR"))
            } else {
               cm.sendNext(I18nMessage.from("1022000_MAKE_INVENTORY_ROOM"))
               cm.dispose()
            }
         } else if (status == 2) {
            cm.sendNextPrev(I18nMessage.from("1022000_GOTTEN_STRONGER"))
         } else if (status == 3) {
            cm.sendNextPrev(I18nMessage.from("1022000_REMINDER"))
         } else {
            cm.dispose()
         }
      } else if (action["2ndJob"]) {
         if (status == 0) {
            if (cm.haveItem(4031012)) {
               cm.sendSimple(I18nMessage.from("1022000_CHOOSE_PATH"))
            } else {
               cm.sendNext(I18nMessage.from("1022000_GOOD_DECISION"))
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
                  cm.sendNextPrev(I18nMessage.from("1022000_TAKE_LETTER"))
               } else {
                  cm.sendNext(I18nMessage.from("1022000_MAKE_SPACE"))
                  cm.dispose()
               }
            } else {
               if (selection < 3) {
                  if (selection == 0) {    //fighter
                     cm.sendNext(I18nMessage.from("1022000_FIGHTER_INFO"))
                  } else if (selection == 1) {    //page
                     cm.sendNext(I18nMessage.from("1022000_PAGE_INFO"))
                  } else {    //spear man
                     cm.sendNext(I18nMessage.from("1022000_SPEARMAN_INFO"))
                  }

                  status -= 2
               } else {
                  cm.sendSimple(I18nMessage.from("1022000_CHOOSE_JOB"))
               }
            }
         } else if (status == 2) {
            if (cm.haveItem(4031008)) {
               cm.dispose()
               return
            }
            job += selection * 10
            cm.sendYesNo(
                  I18nMessage.from("1022000_CONFIRMATION").with((job == 110 ? "#bFighter#k" : job == 120 ? "#bPage#k" : "#bSpearman#k")))
         } else if (status == 3) {
            if (cm.haveItem(4031012)) {
               cm.gainItem(4031012, (short) -1)
            }
            cm.completeQuest(100005)

            if (job == 110) {
               cm.sendNext(I18nMessage.from("1022000_FIGHTER_SUCCESS"))
            } else if (job == 120) {
               cm.sendNext(I18nMessage.from("1022000_PAGE_SUCCESS"))
            } else {
               cm.sendNext(I18nMessage.from("1022000_SPEARMAN_SUCCESS"))
            }
            if (cm.getJobId() != job) {
               cm.changeJobById(job)
            }
         } else if (status == 4) {
            cm.sendNextPrev(
                  I18nMessage.from("1022000_BOOK_GIVEN").with((job == 110 ? "fighter" : job == 120 ? "page" : "spearman")))
         } else if (status == 5) {
            cm.sendNextPrev(I18nMessage.from("1022000_SP_GIVEN"))
         } else if (status == 6) {
            cm.sendNextPrev(I18nMessage.from("1022000_BECOME_STRONG").with(job == 110 ? "Fighter" : job == 120 ? "Page" : "Spearman"))
         }
      } else if (action["3thJobI"]) {
         if (status == 0) {
            if (cm.getPlayer().gotPartyQuestItem("JB3")) {
               cm.getPlayer().removePartyQuestItem("JB3")
               cm.getPlayer().setPartyQuestItemObtained("JBP")
            }
            cm.sendNextPrev(I18nMessage.from("1022000_CLONE_INFO"))
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