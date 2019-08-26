package quest


import scripting.quest.QuestActionManager

class Quest29903 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (qm.forceStartQuest()) {
         qm.showInfoText("You have earned the <Master Adventurer> title. You can receive a Medal from NPC Dalair.")
      }
      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         qm.dispose()
      } else {
         if (status == 0) {
            qm.sendNext("Congratulations on earning your honorable #b<Master Adventurer>#k title. I wish you the best of luck in your future endeavors! Keep up the good work.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n #v1142110:# #t1142110# 1")
         } else if (status == 1) {
            if (qm.canHold(1142110)) {
               qm.gainItem(1142110)
               qm.forceCompleteQuest()
               qm.dispose()
            } else {
               qm.sendNext("Please make room in your inventory")
            }//NOT GMS LIKE
         } else if (status == 2) {
            qm.dispose()
         }
      }
   }
}

Quest29903 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest29903(qm: qm))
   }
   return (Quest29903) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}