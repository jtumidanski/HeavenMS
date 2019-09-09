package quest


import scripting.quest.QuestActionManager

class Quest21704 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || mode == 0 && type > 0) {
         qm.dispose()
         return
      }

      if (mode == 1) {
         status++
      } else {
         if (status == 2) {
            qm.dispose()
            return
         }
         status--
      }
      if (status == 0) {
         qm.sendNext("How did the training go? The Penguin Teacher #p1202006# likes to exaggerate and it worried me knowing that he has bouts of Alzheimer's, but I'm sure he helped you. He's been studying the skills of heroes for a very long time.")
      } else if (status == 1) {
         qm.sendNextPrev("#b(You tell her that you were able to remember the Combo Ability skill.)#k", (byte) 2)
      } else if (status == 2) {
         qm.sendNextPrev("That's great! Honestly, though, I think it has less to do with the method of #p1202006#'s training and more to do with your body remembering its old abilities. #bI'm sure your body will remember more skills as you continue to train#k!  \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 500 exp")
      } else if (status == 3) {
         qm.forceCompleteQuest()
         qm.gainExp(500)
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.dispose()
   }
}

Quest21704 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21704(qm: qm))
   }
   return (Quest21704) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}