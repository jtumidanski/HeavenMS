package quest


import scripting.quest.QuestActionManager

class Quest21729 {
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
            qm.sendNext("Okay, you should not return to #bTru#k for further details on your next steps. ... Oh wait!! I remembered something. See the #rMysterious Statue#k over there? That statue has it's origins unknown, and there's something scribbled onto it that resembles something big, it probably is the password for the cave? #rGet the password there#k, it may help you on your journey.")
         } else if (status == 1) {
            qm.forceStartQuest()
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21729 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21729(qm: qm))
   }
   return (Quest21729) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}