package server.gachapon;

/**
 * @author Ronan - parsed MapleSEA loots
 * <p>
 * MapleSEA-like loots thanks to AyumiLove, src: https://ayumilovemaple.wordpress.com/maplestory-gachapon-guide/
 */

public class ElNath extends GachaponItems {

   @Override
   public int[] getCommonItems() {
      return new int[]{

            /* Scroll */
            2041012, 2048003, 2043800, 2043301, 2040301, 2043101, 2043201, 2043001, 2044301, 2043801, 2044201, 2043701, 2044502,
            2041011, 2041014, 2044602, 2043302, 2043202, 2043002, 2048005, 2044402, 2044302, 2043802, 2044102, 2044202, 2043702,
            2044812,

            /* Useable drop */
            2000004, 2000005,

            /* Common equipment */
            1402010, 1032003, 1442013, 1432009, 1302022, 1302029, 1322021, 1302026, 1442017, 1322023, 1102011, 1032008, 1322026,
            1442016, 1312000, 1032007, 1322025, 1322027, 1032020, 1442015, 1432017, 1302027, 1302049, 1372006, 1032022, 1032021,
            1372004, 1332020, 1322007, 1032006, 1302028, 1322003, 1302007, 1092030, 1302021, 1322024, 1322012, 1032005, 1322022,
            1032013, 1302025, 1302013, 1032017, 1032002, 1032001, 1302017, 1432018, 1442012, 1302000, 1032000, 1102013, 1442022,
            1372005, 1442021, 1032009, 1302016,

            /* Warrior equipment */
            1442003, 1312007, 1402008, 1312008, 1412008, 1442009, 1302004, 1312006, 1442016, 1402012, 1302003, 1312005, 1432002,
            1432001, 1302008, 1040030, 1402015, 1322015, 1432006, 1322002, 1302010, 1322017, 1402003, 1402006, 1322000, 1422001,
            1442001, 1422004, 1412004, 1322009, 1322011, 1442000, 1412005, 1402002, 1432004, 1442010, 1422008, 1442007, 1422009,
            1322019, 1412003, 1412007, 1302009, 1412000, 1322014, 1402001, 1402007, 1432005,

            /* Magician equipment */
            1382001, 1372007, 1382010, 1382007, 1372000, 1372003, 1382011, 1382006, 1382000,

            /* Bowman equipment */
            1452004, 1452000, 1452010, 1452015, 1452014, 1462012, 1462010, 1452017, 1462000, 1452008, 1452006, 1462006, 1452007,
            1452002, 1402001,

            /* Thief equipment */
            1472006, 1472010, 1332022, 1332011, 1472015, 1472016, 1472023, 1472028, 1472022, 1472011, 1472026, 1332024, 1332009,
            1472017, 1472013, 1472029, 1472021, 1332015, 1332031, 1332023, 1332004, 1472000, 1332019, 1472027, 1332018, 1472007,
            1332012, 1332016, 1472024, 1332017, 1332003, 1472012, 1472014, 1472005, 1472018, 1472001,

            /* Pirate equipment */
            1072294, 1492009

      };
   }

   @Override
   public int[] getUncommonItems() {
      return new int[]{2022439, 2040804, 2040805, 2340000};
   }

   @Override
   public int[] getRareItems() {
      return new int[]{2043803, 1102085};
   }

}
