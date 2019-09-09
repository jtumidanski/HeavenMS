package quest


import scripting.quest.QuestActionManager

class Quest20000 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode > 0) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            qm.sendNext("Ah, you've come... this is exhilarating. I am grateful for your decision to become a Cygnus Knight. I have been waiting a long time for someone like you. Someone that is courageous enough to face the Black Mage and not flinch...")
         } else if (status == 1) {
            qm.sendNext("The battle against the evil nature of the Black Mage who wants to swallow up Maple World as a whole, the cunning nature of his disciples, and the physical battle against the crazy monsters will await you. There will also come a time where even you may turn against yourself into an enemy and torment you ...")
         } else if (status == 2) {
            qm.sendOk("But I won't worry about that. I am confident that you will be able to fight through all that and protect Maple World from the Black Mage. Of course, you'll have to become a bit stronger than you are right now, right?")
         } else if (status == 3) {
            qm.gainItem(1142065, (short) 1) // Noblesse Medal * 1
            qm.gainExp(20) //gain 20 exp!!
            qm.forceStartQuest()
            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20000 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20000(qm: qm))
   }
   return (Quest20000) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}