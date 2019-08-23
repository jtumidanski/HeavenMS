package quest


import scripting.quest.QuestActionManager

class Quest2186 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (!qm.isQuestCompleted(2186)) {
         if (qm.haveItem(4031853)) {
            if (qm.canHold(2030019)) {
               qm.gainItem(4031853, (short) -1)
               qm.gainExp(1700)
               qm.gainItem(2030019, (short) 10)

               qm.sendOk("Geez, you found my glasses! Thank you, thank you so much. Now I'm able to see everything again!")
               qm.forceCompleteQuest()
            } else {
               qm.sendOk("I need you to have an USE slot available to reward you properly!")
            }
         } else if (qm.haveItem(4031854) || qm.haveItem(4031855)) {
            //When I figure out how to make a completance with just a pickup xD
            if (qm.canHold(2030019)) {
               if (qm.haveItem(4031854)) {
                  qm.gainItem(4031854, (short) -1)
               } else {
                  qm.gainItem(4031855, (short) -1)
               }

               qm.gainExp(1000)
               qm.gainItem(2030019, (short) 5)

               qm.sendOk("Hm, those aren't my glasses... But alas, I'll take it anyway. Thanks.")
               qm.forceCompleteQuest()
            } else {
               qm.sendOk("I need you to have an USE slot available to reward you properly!")
            }
         }
      }

      qm.dispose()
   }
}

Quest2186 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2186(qm: qm))
   }
   return (Quest2186) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}