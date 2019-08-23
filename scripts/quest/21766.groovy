package quest


import scripting.quest.QuestActionManager

class Quest21766 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (status == 0) {
         qm.sendNext("Hey! Can you do me a favor? #p20000# seems a bit strange these days...")
      } else if (status == 1) {
         qm.sendNext("He used to scowl and whine about his arthritis until just recently, but he''s suddenly become all happy and smiley!!")
      } else if (status == 2) {
         qm.sendNext("I have a feeling there is a secret behind that wooden box. Could you stealthily look into the wooden box next to #p20000#?")
      } else if (status == 3) {
         qm.sendNext("You know where #p20000# is, right? He's to the right. Just keep going until you see where Vikin is, then head down past the hanging shark and octopus, and you''ll see John. The box should be right next to him.")
      } else {
         qm.forceStartQuest()
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.gainExp(200)
      qm.forceCompleteQuest()
      qm.dispose()
   }
}

Quest21766 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21766(qm: qm))
   }
   return (Quest21766) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}