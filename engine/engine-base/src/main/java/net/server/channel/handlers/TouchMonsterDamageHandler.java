package net.server.channel.handlers;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import net.server.channel.packet.AttackPacket;
import net.server.channel.packet.reader.DamageReader;
import net.server.channel.packet.PacketReaderFactory;
import tools.data.input.SeekableLittleEndianAccessor;

public final class TouchMonsterDamageHandler extends AbstractDealDamageHandler<AttackPacket> {
   @Override
   public Class<DamageReader> getReaderClass() {
      return DamageReader.class;
   }

   @Override
   public void handlePacket(SeekableLittleEndianAccessor accessor, MapleClient client) {
      DamageReader damageReader = (DamageReader) PacketReaderFactory.getInstance().get(getReaderClass());
      handlePacket(damageReader.read(accessor, client.getPlayer(), false, false), client);
   }

   @Override
   public final void handlePacket(AttackPacket attackPacket, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr.getEnergyBar() == 15000 || chr.getBuffedValue(MapleBuffStat.BODY_PRESSURE) != null) {
         applyAttack(attackPacket, c.getPlayer(), 1);
      }
   }
}
