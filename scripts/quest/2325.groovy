package quest


import scripting.quest.QuestActionManager

class Quest2325 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
         return
      } else if (mode == 0) {
         status--
      } else {
         status++
      }

      if (status == 0) {
         qm.sendNext("I... I am scared... Please... please help me...")
      } else if (status == 1) {
         qm.sendNextPrev("Don't be afriad, #b#p1300005##k sent me here.",  (byte) 2)
      } else if (status == 2) {
         qm.sendOk("What? My brother sent you here? Ahhh... I am safe now. Thank you so much...")
         qm.forceCompleteQuest()
         qm.gainExp(6000)
         qm.dispose()
      }
   }
}

Quest2325 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2325(qm: qm))
   }
   return (Quest2325) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}