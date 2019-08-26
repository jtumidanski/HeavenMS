package quest


import scripting.quest.QuestActionManager

class Quest4647 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            if (qm.haveItem(5460000)) {
               qm.completeQuest()
               qm.teachSkill(8, (byte) 1, (byte) 1, -1)
               qm.gainItem(5460000, (short) -1, false)
               qm.sendOk("You got the Pet Snack! Thanks! You can use these to feed multiple pets at once!")
            } else {
               qm.sendOk("Get me the Pet Snack! It can be found in a very big shop....")
            }
         } else if (status == 1) {
            qm.dispose()
         }
      }
   }
}

Quest4647 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest4647(qm: qm))
   }
   return (Quest4647) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}