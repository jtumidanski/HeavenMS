package database.transformer;

import client.database.data.GuildRankData;
import entity.Guild;
import transformer.SqlTransformer;

public class GuildRankDataTransformer implements SqlTransformer<GuildRankData, Guild> {
   @Override
   public GuildRankData transform(Guild guild) {
      return new GuildRankData(guild.getName(), guild.getGp(), guild.getLogoBackground(), guild.getLogoBackgroundColor(), guild.getLogo(), guild.getLogoColor());
   }
}
