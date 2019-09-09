package quest


import scripting.quest.QuestActionManager

class Quest6410 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

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
            if (qm.getQuestProgress(6410, 0) == 0) {
               qm.sendOk("You must save #r#p2095000##k first!")
               qm.dispose()
            } else {
               qm.sendNext("Again, thank you so much for rescuing me. I don't know how to repay you for all this... both Shulynch and you are the nicest people I have encountered. If you approach the mobs the same way you approached me, they may all end up becoming friends with you, as well. Please never lose the kindness you have in you.")
            }
         } else if (status == 1) {
            qm.sendNext("(Friends with the mobs... never lose the kindness.)\r\n\r\n  #s5221009#    #b#q5221009##k")
         } else if (status == 2) {
            qm.gainExp(1200000)
            qm.teachSkill(5221009, (byte) 0, (byte) 10, -1)

            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }
}

Quest6410 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest6410(qm: qm))
   }
   return (Quest6410) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}