package npc

import net.server.processor.MapleGuildProcessor
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2010007 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendSimple(I18nMessage.from("2010007_WHAT_WOULD_YOU_LIKE_TO_DO"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 1) {
            sel = selection
            if (selection == 0) {
               if (cm.getPlayer().getGuildId() > 0) {
                  cm.sendOk(I18nMessage.from("2010007_CANNOT_CREATE_GUILD_WHEN_IN_ONE"))
                  cm.dispose()
               } else {
                  cm.sendYesNo(I18nMessage.from("2010007_COST_CONFIRMATION"))
               }
            } else if (selection == 1) {
               if (cm.getPlayer().getGuildId() < 1 || cm.getPlayer().getGuildRank() != 1) {
                  cm.sendOk(I18nMessage.from("2010007_MUST_BE_LEADER_TO_DISBAND"))
                  cm.dispose()
               } else {
                  cm.sendYesNo(I18nMessage.from("2010007_DISBAND_CONFIRMATION"))
               }
            } else if (selection == 2) {
               if (cm.getPlayer().getGuildId() < 1 || cm.getPlayer().getGuildRank() != 1) {
                  cm.sendOk(I18nMessage.from("2010007_MUST_BE_LEADER_TO_INCREASE_CAPACITY"))
                  cm.dispose()
               } else {
                  cm.sendYesNo(I18nMessage.from("2010007_CAPACITY_INCREASE_CONFIRMATION").with(MapleGuildProcessor.getInstance().getIncreaseGuildCost(cm.getPlayer().getGuild().get().getCapacity())))
               }
            }
         } else if (status == 2) {
            if (sel == 0 && cm.getPlayer().getGuildId() <= 0) {
               cm.getPlayer().genericGuildMessage(1)
               cm.dispose()
            } else if (cm.getPlayer().getGuildId() > 0 && cm.getPlayer().getGuildRank() == 1) {
               if (sel == 1) {
                  cm.getPlayer().disbandGuild()
                  cm.dispose()
               } else if (sel == 2) {
                  MapleGuildProcessor.getInstance().increaseGuildCapacity(cm.getPlayer())
                  cm.dispose()
               }
            }
         }
      }
   }
}

NPC2010007 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2010007(cm: cm))
   }
   return (NPC2010007) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }