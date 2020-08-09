package tools.packet.cashshop.operation;

import java.util.List;

import client.inventory.Item;
import net.opcodes.SendOpcode;
import tools.Pair;
import tools.packet.PacketInput;

public record ShowCouponRedeemSuccess(int accountId, int maplePoints, int mesos,
                                      List<Item> cashItems,
                                      List<Pair<Integer, Integer>> items) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CASH_SHOP_OPERATION;
   }
}