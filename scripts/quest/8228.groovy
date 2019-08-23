package quest


import scripting.quest.QuestActionManager

class Quest8228 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk("Come on, the city really needs you cooperating on this one!")
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline("Hm, that's no good. I can't seem to make these Hyper Glyphs work, dang it. ... Ah, yea, the outsider! He may know the language this paper is written on. Let Elpam try to read this, maybe he knows something.")
      } else if (status == 1) {
         if (qm.canHold(4032032, 1)) {
            qm.gainItem(4032032, (short) 1)
            qm.sendOk("Very well, I'm counting on you on this one.")
            qm.forceStartQuest()
         } else {
            qm.sendOk("Hey. There's no slot on your ETC.")
         }

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
         if (qm.haveItem(4032032, 1)) {
            qm.sendOk("Hello, native of this world. So you have a message that needs translation? My people back in Versal is known for mastering many foreign languages, this one may very well be some we know. Please stand by...")
            qm.gainItem(4032032, (short) -1)
            qm.forceCompleteQuest()
         } else {
            qm.sendOk("I'm afraid you don't have the letter you claimed to have with you.")
         }

         qm.dispose()
      }
   }
}

Quest8228 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest8228(qm: qm))
   }
   return (Quest8228) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}