package net.nu11une.wardenshields.register;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.nu11une.wardenloot.WardenLoot;
import net.nu11une.wardenloot.register.WLItems;
import net.nu11une.wardenshields.WardenShields;
import net.nu11une.wardenshields.common.WSShieldItem;

public class WSItems {
    public static Item SCULK_SHIELD;
    public static Item ANCIENT_BONE_SHARD;

    public static void registerWSItems(){
        Registry.register(Registry.ITEM, new Identifier(WardenShields.MOD_ID, "sculk_shield"), SCULK_SHIELD);
        Registry.register(Registry.ITEM, new Identifier(WardenShields.MOD_ID, "ancient_bone_shard"), ANCIENT_BONE_SHARD);
    }

    static {
        SCULK_SHIELD = new WSShieldItem(new FabricItemSettings().fireproof().maxDamage(3070).group(WardenLoot.WL_GROUP), 60, 20, WLItems.SCULK_INGOT);
        ANCIENT_BONE_SHARD = new Item(new FabricItemSettings().group(WardenLoot.WL_GROUP));
    }
}
