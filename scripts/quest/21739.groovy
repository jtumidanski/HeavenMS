package quest


import scripting.quest.QuestActionManager

class Quest21739 {
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
            qm.sendNext("So, have you defeated the giant? Oh, a Black Wing agent undercover? And he GOT THE SEAL STONE OF ORBIS?! Oh, no. That's horrible! We need to develop countermeasures as soon as possible! Tell the informant on Lith about the situation.")
         } else {
            qm.gainExp(29500)
            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }
}

Quest21739 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21739(qm: qm))
   }
   return (Quest21739) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}