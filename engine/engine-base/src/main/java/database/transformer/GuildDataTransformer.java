package database.transformer;

import client.database.data.GuildData;
import entity.Guild;
import transformer.SqlTransformer;

public class GuildDataTransformer implements SqlTransformer<GuildData, Guild> {
   @Override
   public GuildData transform(Guild guild) {
      return new GuildData(guild.getName(), guild.getGp(), guild.getLogo(), guild.getLogoColor(),
            guild.getLogoBackground(), guild.getLogoBackgroundColor(), guild.getCapacity(),
            guild.getRank1Title(), guild.getRank2Title(), guild.getRank3Title(), guild.getRank4Title(),
            guild.getRank5Title(), guild.getLeader(), guild.getNotice(), guild.getSignature(), guild.getAllianceId());
   }
}
