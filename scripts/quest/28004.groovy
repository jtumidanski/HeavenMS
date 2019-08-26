package quest


import scripting.quest.QuestActionManager

class Quest28004 {
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
            if (qm.getPlayer().getLevel() > 50) {
               qm.forceCompleteQuest()
               qm.dispose()
               return
            }

            qm.sendNext("Okay... so here's our plan to defeat Scrooge and his dastardly plans. The Force of the Spirit I gave you is an item packed with mana. It's an item you'll definitely use at the map I am about to send you. In order to do that, you'll have to bring your party members with you as well. You should bring your party members here or form one right now!")
         } else if (status == 1) {
            qm.sendAcceptDecline("Would you like to move forward?")
         } else if (status == 2) {
            int level = qm.getPlayer().getLevel()

            qm.warp(level <= 30 ? 889100000 : (level <= 40 ? 889100010 : 889100020))
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest28004 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest28004(qm: qm))
   }
   return (Quest28004) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}