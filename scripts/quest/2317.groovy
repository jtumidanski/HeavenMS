package quest


import scripting.quest.QuestActionManager

class Quest2317 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk("Breaking through the barrier will require the Poison Mushroom Cap. Talk to me when you change your mind.")
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline("Ah! If I am not mistaken, I saw the #bKiller Mushroom Spores#k way back when I was a kid in a book. Now I remember... it's made out of extracts of powerful poisons from Poison Mushrooms, which means you'll need some Poison Mushroom Caps. If you can get me those, I think I'll be able to make it.")
      }
      if (status == 1) {
         qm.forceStartQuest()
         qm.sendOk("Please defeat #bPoison Mushrooms#k and bring back #b100 Poison Mushroom Caps#k in return.")
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
         qm.sendOk("Have you gathered up the 100 Poison Mushroom Caps like I asked you to get?")
      }
      if (status == 1) {
         qm.gainExp(13500)
         qm.gainItem(4000500, (short) -100)
         qm.sendOk("I am amazed that you were able to gather up these 100 Poison Mushroom Caps, which is considered a difficult feat. I think I'll be able to make #bKiller Mushroom Spores#k our of these.")
         qm.forceCompleteQuest()
         qm.dispose()
      }
   }
}

Quest2317 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2317(qm: qm))
   }
   return (Quest2317) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}