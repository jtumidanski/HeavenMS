package quest


import scripting.quest.QuestActionManager

class Quest2214 {
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
            int hourDay = qm.getHourOfDay()
            if (!(hourDay >= 17 && hourDay < 20)) {
               qm.sendNext("(Hmm, I'm searching the trash can but can't find the #t4031894# JM was talking about, maybe it's not time yet...)")
               qm.dispose()
               return
            }

            if (!qm.canHold(4031894, 1)) {
               qm.sendNext("(Eh, I can't hold the #t4031894# right now, I need an ETC slot available.)")
               qm.dispose()
               return
            }

            qm.sendNext("(Ah, there is a crumbled note here... Hm, it contains details about some scheme that is about to happen, that must be what #r#p1052002##k was talking about.)")
            qm.gainItem(4031894, (short) 1)
            qm.gainExp(20000)
            qm.forceCompleteQuest()

            qm.dispose()
         }
      }
   }
}

Quest2214 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2214(qm: qm))
   }
   return (Quest2214) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}