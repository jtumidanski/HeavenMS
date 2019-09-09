package quest


import scripting.quest.QuestActionManager

class Quest22502 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == 0 && type == 0) {
         status--
      } else if (mode == -1) {
         qm.dispose()
         return
      } else {
         status++
      }
      if (status == 0) {
         qm.sendAcceptDecline("Wouldn't a lizard enjoy a #b#t4032452##k, like a cow? There are a lot of #bHaystacks#k nearby, so try feeding it that.")
      } else if (status == 1) {
         if (mode == 0) {
            qm.sendNext("Hm, you never know unless you try. That lizard is big enough to be on Maple's Believe It Or Not. It might eat hay.")
         } else {
            qm.forceStartQuest()
            qm.showInfo("UI/tutorial/evan/12/0")
         }
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest22502 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest22502(qm: qm))
   }
   return (Quest22502) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}