package net.server.processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import client.MapleCharacter;
import client.MapleClient;
import client.database.administrator.CharacterAdministrator;
import client.database.administrator.GuildAdministrator;
import client.database.administrator.NoteAdministrator;
import client.database.data.GuildData;
import client.database.provider.CharacterProvider;
import client.database.provider.GuildProvider;
import constants.ServerConstants;
import net.server.PlayerStorage;
import net.server.Server;
import net.server.coordinator.MapleInviteCoordinator;
import net.server.coordinator.MapleMatchCheckerCoordinator;
import net.server.guild.MapleGuild;
import net.server.guild.MapleGuildCharacter;
import net.server.guild.MapleGuildResponse;
import net.server.guild.MapleGuildSummary;
import net.server.world.World;
import tools.DatabaseConnection;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.PacketInput;
import tools.packet.guild.GuildCapacityChange;
import tools.packet.guild.GuildDisband;
import tools.packet.guild.GuildEmblemChange;
import tools.packet.guild.GuildInvite;
import tools.packet.guild.GuildMarkChanged;
import tools.packet.guild.GuildMemberChangeRank;
import tools.packet.guild.GuildMemberLeft;
import tools.packet.guild.GuildMemberLevelJobUpdate;
import tools.packet.guild.GuildMemberOnline;
import tools.packet.guild.GuildNameChange;
import tools.packet.guild.GuildNotice;
import tools.packet.guild.GuildRankTitleChange;
import tools.packet.guild.NewGuildMember;
import tools.packet.guild.ShowGuildInfo;
import tools.packet.guild.ShowGuildRanks;
import tools.packet.guild.UpdateGuildPoints;
import tools.packet.message.MultiChat;
import tools.packet.statusinfo.GetGuildPointMessage;

public class MapleGuildProcessor {
   private static MapleGuildProcessor ourInstance = new MapleGuildProcessor();

   public static MapleGuildProcessor getInstance() {
      return ourInstance;
   }

   private MapleGuildProcessor() {
   }

   public int createGuild(int leaderId, String name) {
      Optional<Integer> result = DatabaseConnection.getInstance().withConnectionResult(connection -> {
         if (GuildProvider.getInstance().getByName(connection, name) != -1) {
            return 0;
         }

         GuildAdministrator.getInstance().createGuild(connection, leaderId, name);
         int guildId = GuildProvider.getInstance().getByLeader(connection, leaderId);
         CharacterAdministrator.getInstance().updateGuild(connection, leaderId, guildId);
         return guildId;
      });
      return result.orElse(0);
   }

   public MapleGuildResponse sendInvitation(MapleClient c, String targetName) {
      Optional<MapleCharacter> mc = c.getChannelServer().getPlayerStorage().getCharacterByName(targetName);
      if (mc.isEmpty()) {
         return MapleGuildResponse.NOT_IN_CHANNEL;
      }
      if (mc.get().getGuildId() > 0) {
         return MapleGuildResponse.ALREADY_IN_GUILD;
      }

      MapleCharacter sender = c.getPlayer();
      if (MapleInviteCoordinator.createInvite(MapleInviteCoordinator.InviteType.GUILD, sender, sender.getGuildId(), mc.get().getId())) {
         PacketCreator.announce(mc.get(), new GuildInvite(sender.getGuildId(), sender.getName()));
         return null;
      } else {
         return MapleGuildResponse.MANAGING_INVITE;
      }
   }

   public boolean answerInvitation(int targetId, String targetName, int guildId, boolean answer) {
      MapleInviteCoordinator.MapleInviteResult res = MapleInviteCoordinator.answerInvite(MapleInviteCoordinator.InviteType.GUILD, targetId, guildId, answer);

      MapleGuildResponse mgr;
      MapleCharacter sender = res.from;
      switch (res.result) {
         case ACCEPTED:
            return true;
         case DENIED:
            mgr = MapleGuildResponse.DENIED_INVITE;
            break;
         default:
            mgr = MapleGuildResponse.NOT_FOUND_INVITE;
      }

      if (sender != null) {
         sender.announce(mgr.getPacket(targetName));
      }
      return false;
   }

   public Set<MapleCharacter> getEligiblePlayersForGuild(MapleCharacter guildLeader) {
      Set<MapleCharacter> guildMembers = new HashSet<>();
      guildMembers.add(guildLeader);

      MapleMatchCheckerCoordinator mmce = guildLeader.getWorldServer().getMatchCheckerCoordinator();
      for (MapleCharacter chr : guildLeader.getMap().getAllPlayers()) {
         if (chr.getParty() == null && chr.getGuild().isEmpty() && mmce.getMatchConfirmationLeaderid(chr.getId()) == -1) {
            guildMembers.add(chr);
         }
      }

      return guildMembers;
   }

   public void displayGuildRanks(MapleClient c, int npcid) {
      DatabaseConnection.getInstance().withConnection(connection -> c.announce(PacketCreator.create(new ShowGuildRanks(npcid, GuildProvider.getInstance().getGuildRankData(connection)))));
   }

   public int getIncreaseGuildCost(int size) {
      int cost = ServerConstants.EXPAND_GUILD_BASE_COST + Math.max(0, (size - 15) / 5) * ServerConstants.EXPAND_GUILD_TIER_COST;

      if (size > 30) {
         return Math.min(ServerConstants.EXPAND_GUILD_MAX_COST, Math.max(cost, 5000000));
      } else {
         return cost;
      }
   }

   protected Stream<MapleCharacter> getGuildMemberStream(MapleGuild guild) {
      PlayerStorage ps = Server.getInstance().getWorld(guild.getWorldId()).getPlayerStorage();
      return guild.getMembers().stream()
            .map(mapleGuildCharacter -> ps.getCharacterById(mapleGuildCharacter.getId()))
            .flatMap(Optional::stream);
   }

   protected Stream<MapleCharacter> getLoggedInMemberStream(MapleGuild guild) {
      return getGuildMemberStream(guild).filter(MapleCharacter::isLoggedinWorld);
   }

   public void broadcastInfoChanged(MapleGuild guild) {
      getLoggedInMemberStream(guild).forEach(character -> PacketCreator.announce(character, new ShowGuildInfo(character)));
   }

   public void broadcastNameChanged(MapleGuild guild) {
      getLoggedInMemberStream(guild).forEach(character -> character.getMap().broadcastMessage(character, new GuildNameChange(character.getId(), guild.getName())));
   }

   public void broadcastEmblemChanged(MapleGuild guild) {
      getLoggedInMemberStream(guild).forEach(character -> character.getMap().broadcastMessage(character, new GuildMarkChanged(character.getId(), guild.getLogoBG(), guild.getLogoBGColor(), guild.getLogo(), guild.getLogoColor())));
   }

   public void increaseGuildCapacity(MapleCharacter character) {
      character.getGuild().ifPresent(guild -> {
         int cost = MapleGuildProcessor.getInstance().getIncreaseGuildCost(guild.getCapacity());

         if (character.getMeso() < cost) {
            MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.POP_UP, "You don't have enough mesos.");
            return;
         }

         if (guild.getCapacity() > 99) {
            MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.POP_UP, "Your guild already reached the maximum capacity of players.");
            return;
         }

         guild.increaseCapacity(5);
         writeToDB(guild, false);

         World world = character.getWorldServer();
         List<Integer> memberIds = getLoggedInMemberStream(guild).map(MapleCharacter::getId).collect(Collectors.toList());
         MasterBroadcaster.getInstance().sendToWorld(world, memberIds, new GuildCapacityChange(guild.getId(), guild.getCapacity()), false, -1);
      });

   }

   public void guildChat(MapleCharacter character, String message) {
      List<Integer> memberIds = character.getGuild().map(guild -> getLoggedInMemberStream(guild).map(MapleCharacter::getId).collect(Collectors.toList())).orElse(new ArrayList<>());
      MasterBroadcaster.getInstance().sendToWorld(character.getWorldServer(), memberIds, new MultiChat(character.getName(), message, 2), false, character.getId());
   }

   public boolean addGuildMember(MapleGuildCharacter guildCharacterToAdd, MapleCharacter characterToAdd) {
      Optional<MapleGuild> guild = characterToAdd.getWorldServer().getGuild(guildCharacterToAdd);
      if (guild.isEmpty()) {
         return false;
      }

      MapleGuild referenceGuild = guild.get();
      if (referenceGuild.getMembers().size() >= referenceGuild.getCapacity()) {
         return false;
      }

      referenceGuild.addGuildMember(guildCharacterToAdd, characterToAdd);

      List<Integer> memberIds = getLoggedInMemberStream(referenceGuild).map(MapleCharacter::getId).collect(Collectors.toList());
      PacketInput packetInput = new NewGuildMember(guildCharacterToAdd.getGuildId(), guildCharacterToAdd.getId(), guildCharacterToAdd.getName(), guildCharacterToAdd.getJobId(), guildCharacterToAdd.getLevel(), guildCharacterToAdd.getGuildRank(), guildCharacterToAdd.isOnline());
      MasterBroadcaster.getInstance().sendToWorld(characterToAdd.getWorldServer(), memberIds, packetInput, false, -1);
      return true;
   }

   public void leaveGuild(MapleCharacter character) {
      World world = character.getWorldServer();
      world.getGuild(character.getMGC()).ifPresent(guild -> {
         guild.leaveGuild(character.getMGC());

         List<Integer> memberIds = getLoggedInMemberStream(guild).map(MapleCharacter::getId).collect(Collectors.toList());
         MasterBroadcaster.getInstance().sendToWorld(world, memberIds, new GuildMemberLeft(character.getGuildId(), character.getId(), character.getName(), false), false, -1);
      });
   }

   public void setGuildNotice(MapleGuild guild, String notice) {
      guild.setGuildNotice(notice);
      writeToDB(guild, false);

      List<Integer> memberIds = getLoggedInMemberStream(guild).map(MapleCharacter::getId).collect(Collectors.toList());
      World world = Server.getInstance().getWorld(guild.getWorldId());
      MasterBroadcaster.getInstance().sendToWorld(world, memberIds, new GuildNotice(guild.getId(), notice), false, -1);
   }

   public void gainGP(MapleGuild guild, int amount) {
      guild.gainGP(amount);
      writeToDB(guild, false);
      MasterBroadcaster.getInstance().sendToGuild(guild, new UpdateGuildPoints(guild.getId(), guild.getGP()));
      MasterBroadcaster.getInstance().sendToGuild(guild, new GetGuildPointMessage(amount));
   }

   public void removeGP(MapleGuild guild, int amount) {
      guild.removeGP(amount);
      writeToDB(guild, false);
      MasterBroadcaster.getInstance().sendToGuild(guild, new UpdateGuildPoints(guild.getId(), guild.getGP()));
   }

   public MapleGuild createGuild(int worldId, int guildId) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> {
         Optional<GuildData> guildData = GuildProvider.getInstance().getGuildDataById(connection, guildId);
         if (guildData.isEmpty()) {
            return null;
         }

         GuildData data = guildData.get();
         MapleGuild guild = new MapleGuild(guildId, worldId, data.name(), data.gp(), data.logo(), data.logoColor(),
               data.logoBackground(), data.logoBackgroundColor(), data.capacity(), data.rankTitles(),
               data.leaderId(), data.notice(), data.signature(), data.allianceId());

         CharacterProvider.getInstance().getGuildCharacterData(connection, guildId)
               .forEach(characterGuildData -> guild.addGuildMember(new MapleGuildCharacter(null,
                     characterGuildData.id(), characterGuildData.level(), characterGuildData.name(), (byte) -1, worldId,
                     characterGuildData.job(), characterGuildData.guildRank(), guildId, false,
                     characterGuildData.allianceRank()), null));
         return guild;
      }).orElse(new MapleGuild(guildId, worldId));
   }

   protected void writeToDB(MapleGuild guild, boolean bDisband) {
      DatabaseConnection.getInstance().withConnection(connection -> {
         if (!bDisband) {
            GuildAdministrator.getInstance().update(connection, guild.getGP(), guild.getLogo(), guild.getLogoColor(),
                  guild.getLogoBG(), guild.getLogoBGColor(), guild.getRankTitles(), guild.getCapacity(),
                  guild.getNotice(), guild.getId());
         } else {
            CharacterAdministrator.getInstance().removeAllCharactersFromGuild(connection, guild.getId());
            GuildAdministrator.getInstance().deleteGuild(connection, guild.getId());
            MasterBroadcaster.getInstance().sendToGuild(guild, new GuildDisband(guild.getId()));
         }
      });
   }

   public void setMemberOnline(MapleCharacter character, boolean online, int channel) {
      Server.getInstance().getGuild(character.getGuildId(), character.getWorld(), character)
            .ifPresent(guild -> MapleGuildProcessor.getInstance().setMemberOnline(guild, character.getId(), online, channel));
   }

   public void setMemberOnline(MapleGuild guild, int characterId, boolean online, int channel) {
      Optional<MapleGuildCharacter> guildCharacterOptional = guild.getMembers().stream().filter(guildCharacter -> guildCharacter.getId() == characterId).findFirst();
      guildCharacterOptional.ifPresent(guildCharacter -> setMemberOnline(guild, guildCharacter, online, channel));
   }

   protected void setMemberOnline(MapleGuild guild, MapleGuildCharacter guildCharacter, boolean online, int channel) {
      boolean bBroadcast = true;
      if (guildCharacter.isOnline() && online) {
         bBroadcast = false;
      }
      guildCharacter.setOnline(online);
      guildCharacter.setChannel(channel);
      if (bBroadcast) {
         MasterBroadcaster.getInstance().sendToGuild(guild, new GuildMemberOnline(guild.getId(), guildCharacter.getId(), online), false, guildCharacter.getId());
      }
   }

   //TODO JDT - seems like a bug to be passing world id to channel
   public void reloadGuildCharacters(int worldId) {
      World world = Server.getInstance().getWorld(worldId);
      world.getPlayerStorage().getAllCharacters().parallelStream()
            .filter(character -> character.getGuild().isPresent())
            .forEach(character -> {
               MapleGuild guild = character.getGuild().get();
               setMemberOnline(guild, character.getMGC(), true, world.getId());
               memberLevelJobUpdate(character.getMGC());
            });
      world.reloadGuildSummary();
   }

   public void memberLevelJobUpdate(MapleGuildCharacter mgc) {
      Server.getInstance().getGuild(mgc.getGuildId()).ifPresent(guild -> memberLevelJobUpdate(guild, mgc));
   }

   protected void memberLevelJobUpdate(MapleGuild guild, MapleGuildCharacter mgc) {
      guild.getMembers().parallelStream()
            .filter(guildCharacter -> guildCharacter.equals(mgc))
            .findFirst()
            .ifPresent(guildCharacter -> {
               guildCharacter.setJobId(mgc.getJobId());
               guildCharacter.setLevel(mgc.getLevel());
               MasterBroadcaster.getInstance().sendToGuild(guild, new GuildMemberLevelJobUpdate(mgc.getGuildId(), mgc.getId(), mgc.getLevel(), mgc.getJobId()));
            });
   }

   protected void ifGuildPresent(int guildId, Consumer<MapleGuild> guildConsumer) {
      Server.getInstance().getGuild(guildId).ifPresent(guildConsumer);
   }

   public void setGuildAllianceId(int guildId, int allianceId) {
      ifGuildPresent(guildId, guild -> setGuildAllianceId(guild, allianceId));
   }

   public void setGuildAllianceId(MapleGuild guild, int allianceId) {
      guild.setAllianceId(allianceId);
      DatabaseConnection.getInstance().withConnection(connection -> GuildAdministrator.getInstance().setAlliance(connection, guild.getId(), allianceId));
   }

   public void changeRank(int guildId, int characterId, int newRank) {
      ifGuildPresent(guildId, guild -> changeRank(guild, characterId, newRank));
   }

   protected void changeRank(MapleGuild guild, int characterId, int newRank) {
      guild.findMember(characterId).ifPresent(guildCharacter -> changeRank(guild, guildCharacter, newRank));
   }

   //TODO JDT clean this up...
   protected void changeRank(MapleGuild guild, MapleGuildCharacter guildCharacter, int newRank) {
      try {
         if (guildCharacter.isOnline()) {
            setGuildAndRank(guildCharacter.getWorld(), guildCharacter.getId(), guild.getId(), newRank);
            guildCharacter.setGuildRank(newRank);
         } else {
            Server.getInstance().getWorld(guildCharacter.getWorld()).setOfflineGuildStatus((short) guild.getId(), (byte) newRank, guildCharacter.getId());
            guildCharacter.setOfflineGuildRank(newRank);
         }
      } catch (Exception re) {
         re.printStackTrace();
         return;
      }

      MasterBroadcaster.getInstance().sendToGuild(guild, new GuildMemberChangeRank(guildCharacter.getGuildId(), guildCharacter.getId(), guildCharacter.getGuildRank()));
   }

   public void expelMember(MapleGuildCharacter initiator, String name, int characterId) {
      ifGuildPresent(initiator.getGuildId(), guild -> expelMember(guild, initiator, name, characterId));
   }

   protected void expelMember(MapleGuild guild, MapleGuildCharacter initiator, String name, int cid) {
      guild.findMember(cid)
            .filter(guildCharacter -> guildCharacter.is(cid))
            .filter(guildCharacter -> initiator.getGuildRank() < guildCharacter.getGuildRank())
            .ifPresentOrElse(guildCharacter -> {
               if (guildCharacter.isOnline()) {
                  setGuildAndRank(guildCharacter.getWorld(), cid, 0, 5);
               } else {
                  DatabaseConnection.getInstance().withConnection(
                        connection -> NoteAdministrator.getInstance().sendNote(connection, guildCharacter.getName(), initiator.getName(), "You have been expelled from the guild.", Byte.parseByte("0")));
                  Server.getInstance().getWorld(guildCharacter.getWorld()).setOfflineGuildStatus((short) 0, (byte) 5, cid);
               }
            }, () -> System.out.println("Unable to find member with name " + name + " and id " + cid));
   }

   public void changeRankTitle(int guildId, String[] ranks) {
      ifGuildPresent(guildId, guild -> {
         guild.changeRankTitle(ranks);
         MasterBroadcaster.getInstance().sendToGuild(guild, new GuildRankTitleChange(guildId, ranks));
         writeToDB(guild, false);
      });
   }

   public void setGuildEmblem(int guildId, short bg, byte bgcolor, short logo, byte logocolor) {
      ifGuildPresent(guildId, guild -> {
         guild.setGuildEmblem(bg, bgcolor, logo, logocolor);
         changeEmblem(guild.getWorldId(), guildId, getLoggedInMemberStream(guild).map(MapleCharacter::getId).collect(Collectors.toList()), new MapleGuildSummary(guild));
         writeToDB(guild, false);
      });
   }

   protected void changeEmblem(int worldId, int gid, List<Integer> affectedPlayers, MapleGuildSummary mgs) {
      World world = Server.getInstance().getWorld(worldId);
      world.updateGuildSummary(gid, mgs);
      MasterBroadcaster.getInstance().sendToWorld(world, affectedPlayers, character ->
            PacketCreator.create(new GuildEmblemChange(gid, mgs.getLogoBG(), mgs.getLogoBGColor(), mgs.getLogo(), mgs.getLogoColor())), true, -1);
      setGuildAndRank(worldId, affectedPlayers, -1, -1, -1);   //respawn player
   }

   public void disbandGuild(int guildId) {
      Server.getInstance().removeGuild(guildId).ifPresent(guild -> {
         if (guild.getAllianceId() > 0) {
            if (!MapleAllianceProcessor.getInstance().removeGuildFromAlliance(guild.getAllianceId(), guild.getId(), guild.getWorldId())) {
               MapleAllianceProcessor.getInstance().disbandAlliance(guild.getAllianceId());
            }
         }
         setGuildAndRank(guild.getWorldId(), getLoggedInMemberStream(guild).map(MapleCharacter::getId).collect(Collectors.toList()), 0, 5, -1);
         writeToDB(guild, true);
      });
   }

   protected void setGuildAndRank(int worldId, List<Integer> cids, int guildid, int rank, int exception) {
      for (int cid : cids) {
         if (cid != exception) {
            setGuildAndRank(worldId, cid, guildid, rank);
         }
      }
   }

   protected void setGuildAndRank(int worldId, int cid, int guildid, int rank) {
      Server.getInstance().getWorld(worldId).getPlayerStorage().getCharacterById(cid).ifPresent(character -> {
         boolean bDifferentGuild;
         if (guildid == -1 && rank == -1) {
            bDifferentGuild = true;
         } else {
            bDifferentGuild = guildid != character.getGuildId();
            character.getMGC().setGuildId(guildid);
            character.getMGC().setGuildRank(rank);

            if (bDifferentGuild) {
               character.getMGC().setAllianceRank(5);
            }

            character.saveGuildStatus();
         }
         if (bDifferentGuild) {
            if (character.isLoggedinWorld()) {
               Server.getInstance().getGuild(guildid).ifPresentOrElse(guild -> {
                  character.getMap().broadcastMessage(character, new GuildNameChange(cid, guild.getName()));
                  character.getMap().broadcastMessage(character, new GuildMarkChanged(cid, guild.getLogoBG(), guild.getLogoBGColor(), guild.getLogo(), guild.getLogoColor()));
               }, () -> character.getMap().broadcastMessage(character, new GuildNameChange(cid, "")));
            }
         }
      });
   }

   protected void removeGuildCharacter(MapleCharacter mc) {
      setMemberOnline(mc, false, (byte) -1);
      if (mc.getMGC().getGuildRank() > 1) {
         leaveGuild(mc);
      } else {
         disbandGuild(mc.getMGC().getGuildId());
      }
   }

   public void removeGuildCharacter(MapleGuildCharacter mgc) {
      if (mgc.getCharacter() != null) {
         removeGuildCharacter(mgc.getCharacter());
      }
   }

   public void resetAllianceGuildPlayersRank(int gId) {
      ifGuildPresent(gId, this::resetAllianceGuildPlayersRank);
   }

   protected void resetAllianceGuildPlayersRank(MapleGuild guild) {
      getLoggedInMemberStream(guild).forEach(character -> character.setAllianceRank(5));
      DatabaseConnection.getInstance().withConnection(connection -> CharacterAdministrator.getInstance().updateAllianceRank(connection, guild.getId(), 5));
   }
}
