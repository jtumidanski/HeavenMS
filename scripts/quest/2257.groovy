package quest


import scripting.quest.QuestActionManager

class Quest2257 {
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
            qm.sendNext("Hey there, do you want a ride to #r#m261000000##k? Oh a request from #b#p2101013##k?")
         } else {
            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }
}

Quest2257 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2257(qm: qm))
   }
   return (Quest2257) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}