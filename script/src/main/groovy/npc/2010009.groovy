package npc

import client.MapleCharacter
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2010009 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int choice
   String guildName

   int allianceCost = 2000000
   int increaseCost = 1000000
   int allianceLimit = 5

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 1) {
         status++
      } else {
         cm.dispose()
         return
      }
      if (status == 0) {
         if (cm.getPlayer().getGuildId() < 1 || cm.getPlayer().getGuildRank() != 1) {
            cm.sendNext(I18nMessage.from("2010009_HELLO_NON_GUILD_LEADER"))
            cm.dispose()
            return
         }

         cm.sendSimple(I18nMessage.from("2010009_HELLO_GUILD_LEADER"))
      } else if (status == 1) {
         choice = selection
         if (selection == 0) {
            cm.sendNext(I18nMessage.from("2010009_UNION_DEFINITION"))
            cm.dispose()
         } else if (selection == 1) {
            cm.sendNext(I18nMessage.from("2010009_HOW_TO"))
            cm.dispose()
         } else if (selection == 2) {
            if (!cm.isLeader()) {
               cm.sendNext(I18nMessage.from("2010009_PARTY_LEADER_MUST_TALK"))
               cm.dispose()
               return
            }
            if (cm.getPlayer().getGuild().get().getAllianceId() > 0) {
               cm.sendOk(I18nMessage.from("2010009_GUILD_CANNOT_BE_PART_OF_ANOTHER_UNION"))
               cm.dispose()
               return
            }

            cm.sendYesNo(I18nMessage.from("2010009_UNION_CREATION_FEE").with(allianceCost))
         } else if (selection == 3) {
            if (cm.getPlayer().getMGC() == null) {
               cm.sendOk(I18nMessage.from("2010009_MUST_OWN_A_UNION_TO_EXPAND_ONE"))
               cm.dispose()
               return
            }

            int rank = cm.getPlayer().getMGC().getAllianceRank()
            if (rank == 1) {
               cm.sendYesNo(I18nMessage.from("2010009_UNION_EXPANSION_COST").with(increaseCost))
            } else {
               cm.sendNext(I18nMessage.from("2010009_MUST_BE_UNION_LEADER_TO_EXPAND"))
               cm.dispose()
            }
         } else if (selection == 4) {
            if (cm.getPlayer().getMGC() == null) {
               cm.sendOk(I18nMessage.from("2010009_MUST_OWN_A_UNION_TO_DISBAND_ONE"))
               cm.dispose()
               return
            }

            int rank = cm.getPlayer().getMGC().getAllianceRank()
            if (rank == 1) {
               cm.sendYesNo(I18nMessage.from("2010009_UNION_DISBAND_CONFIRMATION"))
            } else {
               cm.sendNext(I18nMessage.from("2010009_MUST_BE_UNION_LEADER_TO_DISBAND"))
               cm.dispose()
            }
         }
      } else if (status == 2) {
         if (choice == 2) {
            if (cm.getMeso() < allianceCost) {
               cm.sendOk(I18nMessage.from("2010009_NOT_ENOUGH_MESOS"))
               cm.dispose()
               return
            }
            cm.sendGetText(I18nMessage.from("2010009_UNION_NAME_INPUT"))
         } else if (choice == 3) {
            if (cm.getAllianceCapacity() == allianceLimit) {
               cm.sendOk(I18nMessage.from("2010009_UNION_AT_CAPACITY"))
               cm.dispose()
               return
            }
            if (cm.getMeso() < increaseCost) {
               cm.sendOk(I18nMessage.from("2010009_NOT_ENOUGH_MESOS"))
               cm.dispose()
               return
            }

            cm.upgradeAlliance()
            cm.gainMeso(-increaseCost)
            cm.sendOk(I18nMessage.from("2010009_UNION_EXPANSION_SUCCESS"))
            cm.dispose()
         } else if (choice == 4) {
            if (cm.getPlayer().getGuild() == null || cm.getPlayer().getGuild().get().getAllianceId() <= 0) {
               cm.sendNext(I18nMessage.from("2010009_CANNOT_DISBAND_NON_EXISTENT_UNION"))
               cm.dispose()
            } else {
               cm.disbandAlliance(cm.getClient(), cm.getPlayer().getGuild().get().getAllianceId())
               cm.sendOk(I18nMessage.from("2010009_UNION_DISBAND_SUCCESS"))
               cm.dispose()
            }
         }
      } else if (status == 3) {
         guildName = cm.getText()
         cm.sendYesNo(I18nMessage.from("2010009_UNION_NAME_CONFIRMATION").with(guildName))
      } else if (status == 4) {
         if (!cm.canBeUsedAllianceName(guildName)) {
            cm.sendNext(I18nMessage.from("2010009_UNION_NAME_UNAVAILABLE")) //Not real text
            status = 1
            choice = 2
         } else {
            if (cm.createAlliance(guildName) == null) {
               cm.sendOk(I18nMessage.from("2010009_PARTY_COMPOSITION_ISSUE"))
            } else {
               cm.gainMeso(-allianceCost)
               cm.sendOk(I18nMessage.from("2010009_UNION_CREATION_SUCCESS"))
            }
            cm.dispose()
         }
      }
   }
}

NPC2010009 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2010009(cm: cm))
   }
   return (NPC2010009) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }