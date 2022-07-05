package net.nu11une.wardenshields.common;

import com.github.crimsondawn45.fabricshieldlib.lib.object.FabricShieldItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.nu11une.wardenshields.util.WSToolTip;

import java.util.List;

public class WSShieldItem extends FabricShieldItem {
    public WSShieldItem(Settings settings, int cooldownTicks, int enchantability, Item... repairItems) {
        super(settings, cooldownTicks, enchantability, repairItems);
    }

    @Override
    public void appendShieldTooltip(ItemStack stack, List<Text> tooltip, TooltipContext context) {
        tooltip.add(WSToolTip.SONIC_DEFENSE);
    }
}
