package quest


import scripting.quest.QuestActionManager

class Quest6030 {
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
            qm.sendNext("I am to teach you about the fundamentals of Alchemy.")
         } else if (status == 1) {
            qm.sendNextPrev("While science is good to take a look on the thoughtful side of the elements that compounds the items, it alone is not nearly enough to devise an item.")
         } else if (status == 2) {
            qm.sendNextPrev("In fact, to be able to 'tell the pieces' to become a whole, how should it be done? The rustic ways of the blacksmithing winds up dumbing down some latent potentials of the items.")
         } else if (status == 3) {
            qm.sendNextPrev("Alchemy can be employed for this task. Cleanly and swiftly, #rit merges the parts that forms an item with almost no drawbacks#k, making out the most of the process with almost no scrap, if done right. It takes a while to master it, but once it is done, everything will run out neatly.")
         } else if (status == 4) {
            qm.sendNextPrev("And remember this: the maxima of #bExchange#k, the area of the fundamentals of Alchemy where the total amount of the material does not change, is that no item can be created from nothing. Understood?")
         } else if (status == 5) {
            qm.gainMeso(-10000)

            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }
}

Quest6030 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest6030(qm: qm))
   }
   return (Quest6030) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}