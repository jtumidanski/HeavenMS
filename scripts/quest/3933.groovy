package quest


import scripting.quest.QuestActionManager

class Quest3933 {
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
            qm.sendNext("I didn't think you would be this strong. I feel like you have what it takes to become a member of the Sand Bandits. The most important aspect of being a member is power, and I think you have that. I also... want to test you one more time, just to make sure you're the right one. What do you think? Can you handle it?")
         } else if (status == 1) {
            qm.sendAcceptDecline("To truly see your strength, I'll have to face you myself. Don't worry, I'll summon my other self to face off against you. Are you ready?")
         } else if (status == 2) {
            qm.sendNext("Good, I like your confidence.")
         } else {
            if (qm.getWarpMap(926000000).getCharacters().size() > 0) {
               qm.sendOk("There is someone currently in this map, come back later.")
            } else {
               qm.warp(926000000)
               qm.forceStartQuest()
            }

            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest3933 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3933(qm: qm))
   }
   return (Quest3933) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}