package quest


import scripting.quest.QuestActionManager

class Quest2321 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk("You don't seem to follow instructions well. Come see me when you are ready.")
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline("Now you'll be able to penetrate the spiny vine barrier of Mushroom Forest, but before that, #bMinister of Home Affairs#k wants to have a word with you. Please go see him immediately.")
      }
      if (status == 1) {
         qm.forceStartQuest()
         qm.sendOk("Good luck.")
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendOk("I have been keeping up on your fabulour work. I am aware that you have successfully created the #bKiller Mushroom Spores#k, which penetrates through the unpenetrable barrier of the forest. Congratulations!")
      }
      if (status == 1) {
         qm.gainExp(2500)
         qm.sendOk("The problem now is to figure out how to enter the castle.")
         qm.forceCompleteQuest()
         qm.dispose()
      }
   }
}

Quest2321 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2321(qm: qm))
   }
   return (Quest2321) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}