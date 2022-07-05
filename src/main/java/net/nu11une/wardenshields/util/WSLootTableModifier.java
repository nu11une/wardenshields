package net.nu11une.wardenshields.util;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.nu11une.wardenshields.register.WSItems;

public class WSLootTableModifier {
    public static void registerWSLootPools() {
        LootTableEvents.MODIFY.register(((resourceManager, lootManager, id, tableBuilder, source) -> {
            if(EntityType.WARDEN.getLootTableId().equals(id)){
                LootPool.Builder pool = LootPool.builder().with(ItemEntry.builder(WSItems.ANCIENT_BONE_SHARD)).rolls(ConstantLootNumberProvider.create(2));
                tableBuilder.pool(pool);
            }
            if(Blocks.SCULK_SHRIEKER.getLootTableId().equals(id)){
                LootPool.Builder pool = LootPool.builder().with(ItemEntry.builder(WSItems.ANCIENT_BONE_SHARD)).conditionally(RandomChanceLootCondition.builder(0.3F));
                tableBuilder.pool(pool);
            }
        }));
    }
}
