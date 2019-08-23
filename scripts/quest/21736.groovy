package quest


import scripting.quest.QuestActionManager

class Quest21736 {
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
            qm.sendNext("Long time no see! You've leveled up a lot since the last time we met. You must be training really hard. Always hard-working. I'm not surprised. It's exactly what a hero like you would do. I'm sure Lilin will be happy to hear about your progress.")
         } else if (status == 1) {
            qm.sendNextPrev("Anyway, enough small talk. I realized that it might be more effective to search for information in places outside Victoria Island as well, so I've begun investigating in Ossyria. I began with #bOrbis#k and immediately hit the jackpot.")
         } else if (status == 2) {
            qm.sendNextPrev("It seems like something strange is happening in Orbis in Ossyria. It's a bit different from when we were dealing with the puppeteer, but my instincts tell me it has to do with the Black Wings. Please head over to Orbis.")
         } else if (status == 3) {
            qm.sendAcceptDecline("#bLisa the Fairy#k in Orbis should know a thing or two. Go see Lisa first, she knows someone that knows the whereabouts of the sealing stone. That person #rwill require a password from you#k, when requested use the #bThere's something strange going on in Orbis....#k keyword to talk to her. Understood?")
         } else {
            qm.forceStartQuest()
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21736 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21736(qm: qm))
   }
   return (Quest21736) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}