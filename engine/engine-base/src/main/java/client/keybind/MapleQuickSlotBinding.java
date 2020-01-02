package client.keybind;

public class MapleQuickSlotBinding {
   public static final int QUICK_SLOT_SIZE = 8;

   public static final byte[] DEFAULT_QUICK_SLOTS = {
         0x2A, 0x52, 0x47, 0x49, 0x1D, 0x53, 0x4F, 0x51
   };

   public byte[] quickSlotKeyMapped;

   public MapleQuickSlotBinding(byte[] keys) {
      if (keys.length != QUICK_SLOT_SIZE) {
         throw new IllegalArgumentException(String.format("keys size should be %d", QUICK_SLOT_SIZE));
      }

      this.quickSlotKeyMapped = keys.clone();
   }

   public byte[] getQuickSlotKeyMapped() {
      return quickSlotKeyMapped;
   }
}
