package quest


import scripting.quest.QuestActionManager

class Quest21303 {
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
            qm.sendNext("Aaaargh... Yeti's #b#t4032339##k has just been stolen! How frustrating, Yeti worked hard to get it, just to have it stolen by that #rThief Crow#k...", (byte) 9)
         } else if (status == 1) {
            qm.sendNextPrev("Hey, I was just passing by and could not refrain from hearing you just now. I can lend you my strength, where did the thief go?", (byte) 3)
         } else if (status == 2) {
            qm.sendNextPrev("Oh, how nice of you... Thief has passed #rthrough the gate at west#k. Bring back the #b#t4032339##k, Yeti needs it to give to beloved one.", (byte) 9)
         } else if (status == 3) {
            qm.sendNextPrev("Ok, wait there. I will return it back to you in no time!", (byte) 3)
         } else if (status == 4) {
            qm.forceStartQuest()
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21303 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21303(qm: qm))
   }
   return (Quest21303) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}