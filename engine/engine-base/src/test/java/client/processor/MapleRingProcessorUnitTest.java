package client.processor;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import database.administrator.InventoryEquipmentAdministrator;
import database.administrator.RingAdministrator;
import client.inventory.manipulator.MapleCashIdGenerator;
import server.MapleItemInformationProvider;
import database.DatabaseConnection;
import tools.DatabaseTestBase;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DatabaseConnection.class, RingAdministrator.class, MapleCashIdGenerator.class, InventoryEquipmentAdministrator.class, MapleItemInformationProvider.class})
public class MapleRingProcessorUnitTest extends DatabaseTestBase {
   MapleRingProcessor mapleRingProcessor;

   @Mock
   protected RingAdministrator ringAdministrator;

   @Mock
   protected MapleCashIdGenerator mapleCashidGenerator;

   @Mock
   protected InventoryEquipmentAdministrator inventoryEquipmentAdministrator;

   @Override
   @Before
   public void setup() throws SQLException {
      super.setup();

      PowerMockito.mockStatic(RingAdministrator.class);
      Mockito.when(RingAdministrator.getInstance()).thenReturn(ringAdministrator);

      PowerMockito.mockStatic(MapleCashIdGenerator.class);
      Mockito.when(MapleCashIdGenerator.getInstance()).thenReturn(mapleCashidGenerator);

      PowerMockito.mockStatic(InventoryEquipmentAdministrator.class);
      Mockito.when(InventoryEquipmentAdministrator.getInstance()).thenReturn(inventoryEquipmentAdministrator);

      mapleRingProcessor = new MapleRingProcessor();
   }

   @Test
   public void removeRing_nullInput() {
//      //Setup
//
//      //Do
//      mapleRingProcessor.removeRing(null);
//
//      //Assert
//      Mockito.verify(ringAdministrator, Mockito.never()).deleteRing(Mockito.eq(connection), Mockito.anyInt(), Mockito.anyInt());
//      Mockito.verify(mapleCashidGenerator, Mockito.never()).freeCashId(Mockito.anyInt());
//      Mockito.verify(inventoryEquipmentAdministrator, Mockito.never()).updateRing(Mockito.eq(connection), Mockito.anyInt(), Mockito.anyInt());
   }

   @Test
   public void removeRing_validInput() {
//      //Setup
//      int ringId1 = 1;
//      int ringId2 = 2;
//      int partnerId = 3;
//      int itemId = 4;
//      String partnerName = "partner";
//      Ring mapleRing = new Ring(ringId1, ringId2, partnerId, itemId, partnerName);
//
//      //Do
//      mapleRingProcessor.removeRing(mapleRing);
//
//      //Assert
//      Mockito.verify(ringAdministrator, Mockito.times(1)).deleteRing(Mockito.eq(connection), Mockito.eq(ringId1), Mockito.eq(ringId2));
//      Mockito.verify(mapleCashidGenerator, Mockito.times(1)).freeCashId(Mockito.eq(ringId1));
//      Mockito.verify(mapleCashidGenerator, Mockito.times(1)).freeCashId(Mockito.eq(ringId2));
//      Mockito.verify(inventoryEquipmentAdministrator, Mockito.times(1)).updateRing(Mockito.eq(connection), Mockito.eq(ringId1), Mockito.eq(ringId2));
   }
}
