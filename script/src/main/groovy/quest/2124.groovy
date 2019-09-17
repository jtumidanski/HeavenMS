package quest


import scripting.quest.QuestActionManager

class Quest2124 {
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
            if (!qm.haveItem(4031619, 1)) {
               qm.sendOk("Please bring me the box with the supplies that lies with #b#p2012019##k...")
            } else {
               qm.gainItem(4031619, (short) -1)
               qm.sendOk("Oh, you brought #p2012019#'s box! Thank you.")
               qm.forceCompleteQuest()
            }
         } else if (status == 1) {
            qm.dispose()
         }
      }
   }
}

Quest2124 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2124(qm: qm))
   }
   return (Quest2124) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}