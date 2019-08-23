package quest


import scripting.quest.QuestActionManager

class Quest10940 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode > 0) {
            status++
         } else {
            qm.dispose()
         }
         if (status == 0) {
            qm.sendAcceptDecline("Hello, #h0#. Welcome to Maple World. It's currently event season, and we're welcome new characters with a gift. Would you like your gift now?")
         } else if (status == 1) {
            qm.forceStartQuest()
            qm.forceCompleteQuest()
            qm.gainItem(2430191, (short) 1, true)
            qm.sendOk("Open your inventory and double-click on it! These gifts will make you look stylish. Oh, one more thing! You'll get another gift at level 30. Good luck!")
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest10940 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest10940(qm: qm))
   }
   return (Quest10940) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}