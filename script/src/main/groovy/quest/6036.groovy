package quest


import scripting.quest.QuestActionManager

class Quest6036 {
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
            qm.sendNext("Bothering me again? What's it?")
         } else if (status == 1) {
            if (qm.haveItem(4031980, 1)) {
               qm.sendNext("You crafted a #b#t4031980##k?! How comes, how did you do it?? ... Well, that can't be helped, I guess. The student surpassed the teacher! Youth sure do wonders to one's perception capabilities.\r\n\r\nYou are now ready to take the last step on mastering the Maker skill, contemplate it at it's finest form!")
            } else {
               qm.sendNext("... Please step aside, I can't finish this work if I'm being distracted at every moment.")
               qm.dispose()
            }
         } else if (status == 2) {
            qm.forceCompleteQuest()
            qm.gainItem(4031980, (short) -1)
            int skillId = Math.floor(qm.getPlayer().getJob().getId() / 1000).intValue() * 10000000 + 1007
            qm.teachSkill(skillId, (byte) 3, (byte) 3, -1)
            qm.gainExp(300000)
            qm.dispose()
         }
      }
   }
}

Quest6036 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest6036(qm: qm))
   }
   return (Quest6036) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}