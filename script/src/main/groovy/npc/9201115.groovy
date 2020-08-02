package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

import java.awt.*

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201115 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      EventInstanceManager eim = cm.getEventInstance()
      if (eim != null && eim.getIntProperty("glpq6") == 3) {
         cm.sendOk(I18nMessage.from("9201115_WELL_PLAYED"))
         cm.dispose()
         return
      }

      if (!cm.isEventLeader()) {
         cm.sendNext(I18nMessage.from("9201115_LEADER_MUST_TALK"))
         cm.dispose()
         return
      }

      if (mode == 1) {
         status++
      } else {
         status--
      }

      if (eim != null) {
         if (eim.getIntProperty("glpq6") == 0) {
            if (status == 0) {
               cm.sendNext(I18nMessage.from("9201115_WELCOME"))
            } else if (status == 1) {
               cm.sendNext(I18nMessage.from("9201115_TONIGHT_WE_FEAST"))
            } else if (status == 2) {
               cm.sendNext(I18nMessage.from("9201115_ESCORT"))
               MessageBroadcaster.getInstance().sendMapServerNotice(cm.getPlayer().getMap(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("MASTER_GUARDIANS_APPROACH"))
               for (int i = 0; i < 10; i++) {
                  eim.getMonster(9400594).ifPresent({ mob -> cm.getMap().spawnMonsterOnGroundBelow(mob, new Point(-1337 + (Math.random() * 1337).intValue(), 276)) })
               }
               for (int i = 0; i < 20; i++) {
                  eim.getMonster(9400582).ifPresent({ mob -> cm.getMap().spawnMonsterOnGroundBelow(mob, new Point(-1337 + (Math.random() * 1337).intValue(), 276)) })
               }
               eim.setIntProperty("glpq6", 1)
               cm.dispose()
            }
         } else if (eim.getIntProperty("glpq6") == 1) {
            if (cm.getMap().countMonsters() == 0) {
               if (status == 0) {
                  cm.sendOk(I18nMessage.from("9201115_WHAT_IS_THIS"))
               } else if (status == 1) {
                  cm.sendNext(I18nMessage.from("9201115_NO_MATTER"))
                  MessageBroadcaster.getInstance().sendMapServerNotice(cm.getPlayer().getMap(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("TWISTED_MASTERS_APPROACH"))

                  //Margana
                  eim.getMonster(9400590).ifPresent({ mob -> cm.getMap().spawnMonsterOnGroundBelow(mob, new Point(-22, 1)) })

                  //Red Nirg
                  eim.getMonster(9400591).ifPresent({ mob -> cm.getMap().spawnMonsterOnGroundBelow(mob, new Point(-22, 276)) })

                  //Hsalf
                  eim.getMonster(9400593).ifPresent({ mob -> cm.getMap().spawnMonsterOnGroundBelow(mob, new Point(496, 276)) })

                  //Rellik
                  eim.getMonster(9400592).ifPresent({ mob -> cm.getMap().spawnMonsterOnGroundBelow(mob, new Point(-496, 276)) })

                  eim.setIntProperty("glpq6", 2)
                  cm.dispose()
               }
            } else {
               cm.sendOk(I18nMessage.from("9201115_PAY_NO_ATTENTION"))
               cm.dispose()
            }
         } else if (eim.getIntProperty("glpq6") == 2) {
            if (cm.getMap().countMonsters() == 0) {
               cm.sendOk(I18nMessage.from("9201115_CANNOT_BE_HAPPENING"))
               MessageBroadcaster.getInstance().sendMapServerNotice(cm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, I18nMessage.from("9201115_NEXT_STAGE_OPENED"))
               eim.setIntProperty("glpq6", 3)

               eim.showClearEffect(true)
               eim.giveEventPlayersStageReward(6)

               eim.clearPQ()
               cm.dispose()
            } else {
               cm.sendOk(I18nMessage.from("9201115_PAY_NO_ATTENTION_TWISTED_MASTERS"))
               cm.dispose()
            }
         } else {
            cm.sendOk(I18nMessage.from("9201115_WELL_PLAYED"))
            cm.dispose()
         }
      } else {
         cm.dispose()
      }
   }
}

NPC9201115 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201115(cm: cm))
   }
   return (NPC9201115) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }