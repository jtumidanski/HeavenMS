package quest

import client.MapleJob
import scripting.quest.QuestActionManager

class Quest20201 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (status == 0 && mode == 0) {
            qm.sendNext("I guess you are not ready to tackle on the responsibilities of an official knight.")
            qm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            qm.sendYesNo("So you brought all of #t4032096#... Okay, I believe that your are now qualified to become an official knight. Do you want to become one?")
         } else if (status == 1) {
            if (qm.getPlayer().getJob().getId() == 1100 && qm.getPlayer().getRemainingSp() > ((qm.getPlayer().getLevel() - 30) * 3)) {
               qm.sendNext("You have too much #bSP#k with you. Use some more on the 1st-level skill.")
               qm.dispose()
            } else {
               if (qm.getPlayer().getJob().getId() != 1110) {
                  if (!qm.canHold(1142067)) {
                     qm.sendNext("If you wish to receive the medal befitting the title, you may want to make some room in your equipment inventory.")
                     qm.dispose()
                     return
                  }
                  qm.gainItem(4032096, (short) -30)
                  qm.gainItem(1142067, (short) 1)
                  qm.getPlayer().changeJob(MapleJob.DAWN_WARRIOR_2)
                  qm.completeQuest()
               }
               qm.sendNext("You are a Knight-in-Training no more. You are now an official knight of the Cygnus Knights.")
            }
         } else if (status == 2) {
            qm.sendNextPrev("I have given you some #bSP#k. I have also given you a number of skills for a Dawn Warrior that's only available to knights, so I want you to work on it and hopefully cultivate it as much as your soul.")
         } else if (status == 3) {
            qm.sendPrev("Now that you are officially a Cygnus Knight, act like one so you will keep the Empress's name up high.")
         } else if (status == 4) {
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20201 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20201(qm: qm))
   }
   return (Quest20201) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}