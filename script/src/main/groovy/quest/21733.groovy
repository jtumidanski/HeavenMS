package quest


import scripting.quest.QuestActionManager

class Quest21733 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            qm.sendNext("Aran, Lith have been caught off guard. We are under attack! Get here ASAP.")
         } else {
            qm.forceStartQuest()
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            qm.sendNext("Aran, thank you very much! Somehow the Puppeteer managed to bypass the security of Lith Harbor. He was trying to seek revenge because of the other day. Luckily, you came by. Nicely done!")
         } else if (status == 1) {
            qm.sendNext("I will teach you the #rPolearm Mastery#k skill, to reward your actions here. You will be able to improve your accuracy and the overall mastery of your polearm arts.")
         } else if (status == 2) {
            qm.gainExp(8000)
            qm.teachSkill(21100000, (byte) 0, (byte) 20, -1) // polearm mastery

            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }
}

Quest21733 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21733(qm: qm))
   }
   return (Quest21733) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}