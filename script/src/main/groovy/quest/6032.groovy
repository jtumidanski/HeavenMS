package quest


import scripting.quest.QuestActionManager

class Quest6032 {
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
            qm.sendNext("So you've come to attend my class, huh? Right, I'll make it fast.")
         } else if (status == 1) {
            qm.sendNextPrev("I will teach you the actual application of the #bMaker#k method. All you need to do is have an item in mind to make, gather all the ingredients from the receipt and mix them in a #ralchemistic way#k. Easy, isn't it?")
         } else if (status == 2) {
            qm.sendNextPrev("Let's take producing the #bWeight Earrings#k as an example. There is a rather specific #rductility theory#k to generate it, as any other 'unique' items have, the name going around the #rmain physical force#k acting over the thing we are working on: on that case, the #bDuctility Theory of Gravity#k (as it is a 'Weighted Earrings', got it?).")
         } else if (status == 3) {
            qm.sendNextPrev("Ok, now you need to hand me a fee, 10,000 mesos that is, for that information. The collected fee shall be used for acquiring the needed materials for your learning of the fine art of the #bMaker#k.")
         } else if (status == 4) {
            qm.gainMeso(-10000)

            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }
}

Quest6032 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest6032(qm: qm))
   }
   return (Quest6032) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}