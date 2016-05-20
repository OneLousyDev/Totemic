package totemic_commons.pokefenn.item.equipment.weapon;

import static totemic_commons.pokefenn.Totemic.logger;

import java.lang.reflect.Method;

import org.apache.logging.log4j.Level;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import totemic_commons.pokefenn.Totemic;
import totemic_commons.pokefenn.entity.projectile.EntityInvisArrow;
import totemic_commons.pokefenn.lib.Strings;

public class ItemBaykokBow extends ItemBow
{
    private static final Method getArrowMethod = ReflectionHelper.findMethod(ItemBow.class, null, new String[] {"func_185060_a"}, EntityPlayer.class);

    public ItemBaykokBow()
    {
        setUnlocalizedName(Strings.RESOURCE_PREFIX + Strings.BAYKOK_BOW_NAME);
        setCreativeTab(Totemic.tabsTotem);
        setMaxDamage(576);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft)
    {
        //TODO: Make it work properly with all types of arrows
        if(!(entity instanceof EntityPlayer))
            return;

        EntityPlayer player = (EntityPlayer)entity;
        boolean infinity = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.infinity, stack) > 0;
        ItemStack arrow = getArrow(player);

        int chargeTicks = this.getMaxItemUseDuration(stack) - timeLeft;
        chargeTicks = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, world, player, chargeTicks, arrow != null || infinity);
        if(chargeTicks < 0) return;

        if(arrow != null || infinity)
        {
            if (arrow == null)
                arrow = new ItemStack(Items.arrow);

            float charge = func_185059_b(chargeTicks);

            if(charge >= 0.1D)
            {
                boolean flag1 = infinity && arrow.getItem() instanceof ItemArrow; //Forge: Fix consuming custom arrows.

                if(!world.isRemote)
                {
                    //ItemArrow itemarrow = ((ItemArrow)(arrow.getItem() instanceof ItemArrow ? arrow.getItem() : Items.arrow));
                    EntityArrow entityarrow = new EntityInvisArrow(world, player);//itemarrow.makeTippedArrow(world, arrow, player);
                    entityarrow.setDamage(2.5);
                    entityarrow.func_184547_a(player, player.rotationPitch, player.rotationYaw, 0.0F, charge * 3.0F, 1.0F);

                    if(charge == 1.0F)
                        entityarrow.setIsCritical(true);

                    int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.power, stack);
                    if(power > 0)
                        entityarrow.setDamage(entityarrow.getDamage() + power * 0.5D + 0.5D);

                    int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.punch, stack);
                    if(punch > 0)
                        entityarrow.setKnockbackStrength(punch);

                    if(EnchantmentHelper.getEnchantmentLevel(Enchantments.flame, stack) > 0)
                        entityarrow.setFire(100);

                    stack.damageItem(1, player);

                    if(flag1)
                        entityarrow.canBePickedUp = EntityArrow.PickupStatus.CREATIVE_ONLY;

                    world.spawnEntityInWorld(entityarrow);
                }

                world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.entity_arrow_shoot, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + charge * 0.5F);

                if(!flag1)
                {
                    --arrow.stackSize;

                    if(arrow.stackSize == 0)
                        player.inventory.deleteStack(arrow);
                }

                player.addStat(StatList.func_188057_b(this));
            }
        }
    }

    private ItemStack getArrow(EntityPlayer player)
    {
        try
        {
            return (ItemStack)getArrowMethod.invoke(this, player);
        }
        catch(ReflectiveOperationException e)
        {
            logger.catching(Level.ERROR, e);
            return null;
        }
    }

    @Override
    public int getItemEnchantability()
    {
        return 5;
    }

    @Override
    public boolean hasEffect(ItemStack stack)
    {
        return true;
    }
}
