package quest


import scripting.quest.QuestActionManager

class Quest20400 {
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
            qm.sendNext("Not long ago, we received a distress signal of #bAdvanced Knight #p1103000##k, currently stationed somewhere in #rEl Nath#k. His Your job is to find him, first go talk to #b#p1101002##k and receive further instructions on your mission.")
         } else if (status == 1) {
            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20400 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20400(qm: qm))
   }
   return (Quest20400) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}