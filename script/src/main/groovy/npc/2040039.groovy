package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2040039 {
   NPCConversationManager cm
   int status = 0
   int sel = -1
   int curMap, stage

   def start() {
      curMap = cm.getMapId()
      stage = Math.floor((curMap - 922010100) / 100).intValue() + 1
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   static def clearStage(int stage, EventInstanceManager eim, int curMap) {
      eim.setProperty(stage + "stageclear", "true")
      eim.showClearEffect(true)

      eim.linkToNextStage(stage, "lpq", curMap)  //opens the portal to the next map
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else if (mode == 0) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }

         EventInstanceManager eim = cm.getPlayer().getEventInstance()

         if (eim.getProperty(stage.toString() + "stageclear") != null) {
            cm.sendNext(I18nMessage.from("2040039_GO_TO_NEXT_STAGE"))
         } else {
            if (eim.isEventLeader(cm.getPlayer())) {
               int state = eim.getIntProperty("statusStg" + stage)

               if (state == -1) {           // preamble
                  cm.sendOk(I18nMessage.from("2040039_WELCOME").with(stage))
                  eim.setProperty("statusStg" + stage, 0)
               } else {                      // check stage completion
                  if (cm.haveItem(4001022, 6)) {
                     cm.sendOk(I18nMessage.from("2040039_GOOD_JOB"))
                     cm.gainItem(4001022, (short) -6)

                     eim.setProperty("statusStg" + stage, 1)
                     clearStage(stage, eim, curMap)
                  } else {
                     cm.sendNext(I18nMessage.from("2040039_SORRY"))
                  }
               }
            } else {
               cm.sendNext(I18nMessage.from("2040039_PARTY_LEADER_MUST_TALK"))
            }
         }

         cm.dispose()
      }
   }
}

NPC2040039 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040039(cm: cm))
   }
   return (NPC2040039) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }