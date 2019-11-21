package quest

import config.YamlConfig
import scripting.quest.QuestActionManager

class Quest21101 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode == 0 && type == 0) {
         status -= 2
      } else if (mode != 1) {
         if (mode == 0) {
            qm.sendNext("#b(You need to think about this for a second...)#k")
         }
         qm.dispose()
         return
      }
      if (status == 0) {
         qm.sendYesNo("#b(Are you certain that you were the hero that wielded the #p1201001#? Yes, you're sure. You better grab the #p1201001# really tightly. Surely it will react to you.)#k")
      } else if (status == 1) {
         if (qm.getPlayer().getJob().getId() == 2000) {
            if (!qm.canHold(1142129)) {
               qm.sendOk("Wow, your #bequip#k inventory is full. You need to make at least 1 empty slot to complete this quest.")
               qm.dispose()
               return
            }
            qm.gainItem(1142129, true)

            qm.changeJobById(2100)
            qm.resetStats()

            if (YamlConfig.config.server.USE_FULL_ARAN_SKILLSET) {
               qm.teachSkill(21000000, (byte) 0, (byte) 10, -1)   //combo ability
               qm.teachSkill(21001003, (byte) 0, (byte) 20, -1)   //polearm booster
            }

            qm.completeQuest()

            //qm.getPlayer().changeSkillLevel(SkillFactory.getSkill(20009000), 0, -1);
            //qm.getPlayer().changeSkillLevel(SkillFactory.getSkill(20009000), 1, 0);
            //qm.showInfo("You have acquired the Pig's Weakness skill.");
            qm.sendNextPrev("#b(You might be starting to remember something...)#k", (byte) 3)
         }
      } else if (status == 2) {
         //qm.warp(914090100, 0);
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21101 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21101(qm: qm))
   }
   return (Quest21101) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}