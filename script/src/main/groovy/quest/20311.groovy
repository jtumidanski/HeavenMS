package quest

import client.MapleJob
import scripting.quest.QuestActionManager

class Quest20311 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (status == 1 && mode == 0) {
            qm.sendNext("Come back when you are ready.")
            qm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            qm.sendNext("The jewel you brought back from the Master of Disguise is Shinsoo's Teardrop. It is the crystallization of Shinsoo's powers. If the Black Magician gets his hands on this, then this spells doom for all of us.")
         } else if (status == 1) {
            qm.sendYesNo("For your effort in preventing a potentially serious disaster, the Empress has decided to present you with a new title. Are you ready to accept it?")
         } else if (status == 2) {
            int nPSP = (qm.getPlayer().getLevel() - 70) * 3
            if (qm.getPlayer().getRemainingSp() > nPSP) {
               qm.sendNext("You still have way too much #bSP#k with you. You can't earn a new title like that, I strongly urge you to use more SP on your 1st and 2nd level skills.")
            } else {
               if (!qm.canHold(1142068)) {
                  qm.sendNext("If you wish to receive the medal befitting the title, you may want to make some room in your equipment inventory.")
               } else {
                  qm.completeQuest()
                  qm.gainItem(1142068, (short) 1)
                  qm.getPlayer().changeJob(MapleJob.DAWN_WARRIOR_3)
                  qm.sendOk("#h #, as of this moment, you are an Advanced Knight. From this moment on, you shall carry yourself with dignity and respect befitting your new title, an Advanced Knight of Cygnus Knights. May your glory continue to shine as bright as this moment.")
               }
            }
         } else if (status == 3) {
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20311 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20311(qm: qm))
   }
   return (Quest20311) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}