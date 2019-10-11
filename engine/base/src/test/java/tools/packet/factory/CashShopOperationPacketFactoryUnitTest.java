package tools.packet.factory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.testng.Assert;

import net.opcodes.SendOpcode;
import tools.data.input.ByteArrayByteStream;
import tools.data.input.GenericLittleEndianAccessor;
import tools.data.input.LittleEndianAccessor;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.cashshop.CashShopOperationSubOp;
import tools.packet.cashshop.operation.ShowWishList;
import tools.packet.cashshop.operation.ShowWishListUpdate;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MaplePacketLittleEndianWriter.class, CashShopOperationPacketFactory.class, Handler.class})
public class CashShopOperationPacketFactoryUnitTest {

   private CashShopOperationPacketFactory cashShopOperationPacketFactory;

   @Before
   public void setup() {
      cashShopOperationPacketFactory = PowerMockito.spy(CashShopOperationPacketFactory.getInstance());
      PowerMockito.mockStatic(CashShopOperationPacketFactory.class);
      Mockito.when(CashShopOperationPacketFactory.getInstance()).thenReturn(cashShopOperationPacketFactory);

      MaplePacketLittleEndianWriter writer = Mockito.spy(MaplePacketLittleEndianWriter.class);
      PowerMockito.mockStatic(MaplePacketLittleEndianWriter.class);
//      Handler registry = Mockito.spy(Handler.class);
//      PowerMockito.mockStatic(Handler.class);
//      Mockito.doReturn(writer).when(registry).newWriter(MaplePacketLittleEndianWriter.DEFAULT_SIZE);
   }

   @Test
   public void showWishList_update() {
      //Setup
      List<Integer> sns = Arrays.asList(1, 2, 3);
      ShowWishListUpdate input = new ShowWishListUpdate(sns, true);

      //Do
      byte[] packet = CashShopOperationPacketFactory.getInstance().create(input);

      //Assert
      LittleEndianAccessor accessor = new GenericLittleEndianAccessor(new ByteArrayByteStream(packet));
      Assert.assertEquals(accessor.readShort(), SendOpcode.CASHSHOP_OPERATION.getValue());
      Assert.assertEquals(accessor.readByte(), CashShopOperationSubOp.SHOW_WISHLIST_UPDATE.getValue());
      sns.forEach(sn -> Assert.assertEquals(accessor.readInt(), (int) sn));
      IntStream.range(0, 10 - sns.size()).forEach(i -> Assert.assertEquals(accessor.readInt(), 0));
   }

   @Test
   public void showWishList_fullUpdate() {
      //Setup
      List<Integer> sns = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
      ShowWishListUpdate input = new ShowWishListUpdate(sns, true);

      //Do
      byte[] packet = CashShopOperationPacketFactory.getInstance().create(input);

      //Assert
      LittleEndianAccessor accessor = new GenericLittleEndianAccessor(new ByteArrayByteStream(packet));
      Assert.assertEquals(accessor.readShort(), SendOpcode.CASHSHOP_OPERATION.getValue());
      Assert.assertEquals(accessor.readByte(), CashShopOperationSubOp.SHOW_WISHLIST_UPDATE.getValue());
      sns.forEach(sn -> Assert.assertEquals(accessor.readInt(), (int) sn));
      IntStream.range(0, 10 - sns.size()).forEach(i -> Assert.assertEquals(accessor.readInt(), 0));
   }

   @Test
   public void showWishList_emptyUpdate() {
      //Setup
      List<Integer> sns = Collections.emptyList();
      ShowWishListUpdate input = new ShowWishListUpdate(sns, true);

      //Do
      byte[] packet = CashShopOperationPacketFactory.getInstance().create(input);

      //Assert
      LittleEndianAccessor accessor = new GenericLittleEndianAccessor(new ByteArrayByteStream(packet));
      Assert.assertEquals(accessor.readShort(), SendOpcode.CASHSHOP_OPERATION.getValue());
      Assert.assertEquals(accessor.readByte(), CashShopOperationSubOp.SHOW_WISHLIST_UPDATE.getValue());
      IntStream.range(0, 10 - sns.size()).forEach(i -> Assert.assertEquals(accessor.readInt(), 0));
   }

   @Test
   public void showWishList_noUpdate() {
      //Setup
      List<Integer> sns = Arrays.asList(1, 2, 3);
      ShowWishList input = new ShowWishList(sns, false);

      //Do
      byte[] packet = CashShopOperationPacketFactory.getInstance().create(input);

      //Assert
      LittleEndianAccessor accessor = new GenericLittleEndianAccessor(new ByteArrayByteStream(packet));
      Assert.assertEquals(accessor.readShort(), SendOpcode.CASHSHOP_OPERATION.getValue());
      Assert.assertEquals(accessor.readByte(), CashShopOperationSubOp.SHOW_WISHLIST.getValue());
      sns.forEach(sn -> Assert.assertEquals(accessor.readInt(), (int) sn));
      IntStream.range(0, 10 - sns.size()).forEach(i -> Assert.assertEquals(accessor.readInt(), 0));
   }
}
