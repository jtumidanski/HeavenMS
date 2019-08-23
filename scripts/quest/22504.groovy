package quest


import scripting.quest.QuestActionManager

class Quest22504 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == 0 && type == 0) {
         status--
      } else if (mode == -1) {
         qm.dispose()
         return
      } else {
         status++
      }
      if (status == 0) {
         qm.sendNext("Ugh. This isn't going to work. I need something else. No plants. No meat. What, you have no idea? But you're the master, and you're older than me, too. You must know what'd be good for me!")
      } else if (status == 1) {
         qm.sendNextPrev("#bBut I don't. It's not like age has anything to do with this...", (byte) 2)
      } else if (status == 2) {
         qm.sendAcceptDecline("Since you're older, you must be more experienced in the world, too. Makes sense that you'd know more than me. Oh, fine. I'll ask someone who's even older than you, master!")
      } else if (status == 3) {
         if (mode == 0) {
            qm.sendNext("No use trying to find an answer to this on my own. I'd better look for #bsomeone older and wiser than master#k!")
         } else {
            qm.forceStartQuest()
            qm.sendNext("#b#b(You already asked Dad once, but you don't have any better ideas. Time to ask him again!)")
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest22504 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest22504(qm: qm))
   }
   return (Quest22504) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}