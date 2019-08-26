package quest


import scripting.quest.QuestActionManager

class Quest20522 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
         return
      } else if (status >= 2 && mode == 0) {
         qm.dispose()
         return
      }

      if (mode == 1) {
         status++
      } else {
         status--
      }

      if (status == 0) {
         qm.sendNext("The riding for Knights are a bit different from the rides available for regular folks. The takes place through a creature that is of the Mimi race that can be found on this island; they are called #bMimianas#k. Instead of riding monsters, the Knights ride Mimiana. There's one thing that you should never, ever forget.")
      } else if (status == 1) {
         qm.sendNextPrev("Dont't think of this as just a form of mount or transportation. These mounts can be your friend, your comrade, your colleague... all of the above. Even a friend close enough to entrust your life! That's why the Knights of Ereve actually grow their own mounts.")
      } else if (status == 2) {
         qm.sendAcceptDecline("Now, here's a Mimiana egg. Are you ready to raise a Mimiana and have it as your traveling companion for the rest of its life?")
      } else if (status == 3) {
         if (!qm.haveItem(4220137) && !qm.canHold(4220137)) {
            qm.sendOk("Make up a room on your ETC tab so I can give you the Mimiana egg.")
            qm.dispose()
            return
         }

         qm.forceStartQuest()
         if (!qm.haveItem(4220137)) {
            qm.gainItem(4220137)
         }
         qm.sendOk("Mimiana's egg can be raised by #bsharing your daily experiences with it#k. Once Mimiana fully grows up, please come see me.")
      } else if (status == 4) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode != 1) {
         qm.dispose()
         return
      }

      status++
      if (status == 0) {
         qm.sendNext("Hey there! How's Mimiana's egg?")
      } else if (status == 1) {   //pretty sure there would need to have an egg EXP condition... Whatever.
         if (!qm.haveItem(4220137)) {
            qm.sendOk("I see, you lost your egg... You need to be more careful when raising a baby Mimiana!")
            return
         }

         qm.forceCompleteQuest()
         qm.gainItem(4220137, (short) -1)
         qm.gainExp(37600)
         qm.sendOk("Oh, were you able to awaken Mimiana Egg? That's amazing... Most knights can't even dream of awakening it in such a short amount of time.")
      } else if (status == 2) {
         qm.dispose()
      }
   }
}

Quest20522 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20522(qm: qm))
   }
   return (Quest20522) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}