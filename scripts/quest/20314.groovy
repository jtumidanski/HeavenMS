package quest

import client.MapleJob
import scripting.quest.QuestActionManager

class Quest20314 {
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
            qm.sendNext("The jewel you brought back from the Master of Disguise is Shinsoo's Teardrop. It is the crystalization of Shinsoo's powers. If the Black Mage gets his hands on this, then this spells doom for all of us.")
         } else if (status == 1) {
            qm.sendYesNo("The Empress thought highly of your accomplishment and granted you a new title. Would you like to receive it?")
         } else if (status == 2) {
            int nPSP = (qm.getPlayer().getLevel() - 70) * 3
            if (qm.getPlayer().getRemainingSp() > nPSP) {
               qm.sendNext("You still have way too much #bSP#k with you. You can't earn a new title like that, I strongly urge you to use more SP on your 1st and 2nd level skills.")
               qm.dispose()
            } else {
               if (!qm.canHold(1142068)) {
                  qm.sendNext("If you wish to receive the medal befitting the title, you may want to make some room in your equipment inventory.")
                  qm.dispose()
               } else {
                  qm.gainItem(1142068, (short) 1)
                  qm.getPlayer().changeJob(MapleJob.NIGHTWALKER3)
                  qm.sendOk("#h #, from here on out, you are an Advanced Knight of Cygnus Knights. The title comes with a newfound broad view on everything. You may encounter temptations here and there, but I want you to keep your faith and beliefs intact and do not succumb to the darkness.")
                  qm.completeQuest()
                  qm.dispose()
               }
            }

         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20314 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20314(qm: qm))
   }
   return (Quest20314) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}