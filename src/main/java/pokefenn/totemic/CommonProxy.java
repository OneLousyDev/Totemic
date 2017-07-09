package pokefenn.totemic;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import pokefenn.totemic.datafix.VanillaIronNugget;
import pokefenn.totemic.entity.animal.EntityBuffalo;
import pokefenn.totemic.entity.boss.EntityBaykok;
import pokefenn.totemic.entity.projectile.EntityInvisArrow;
import pokefenn.totemic.handler.EntityFall;
import pokefenn.totemic.handler.EntitySpawn;
import pokefenn.totemic.handler.EntityUpdate;
import pokefenn.totemic.handler.PlayerInteract;
import pokefenn.totemic.item.ItemBuffaloDrops;
import pokefenn.totemic.item.ItemTotemicItems;
import pokefenn.totemic.lib.Strings;
import pokefenn.totemic.network.GuiHandler;
import pokefenn.totemic.network.NetworkHandler;
import pokefenn.totemic.tileentity.TileTipi;
import pokefenn.totemic.tileentity.music.TileDrum;
import pokefenn.totemic.tileentity.music.TileWindChime;
import pokefenn.totemic.tileentity.totem.TileTotemBase;
import pokefenn.totemic.tileentity.totem.TileTotemPole;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {
        registerEntities();
        registerTileEntities();
    }

    public void init(FMLInitializationEvent event)
    {
        ModContent.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(Totemic.instance, new GuiHandler());
        NetworkHandler.init();
        oreDictionary();
        furnaceRecipes();
        registerEventHandlers();
        registerDataFixers();
    }

    public void postInit(FMLPostInitializationEvent event)
    {

    }

    private void registerEntities()
    {
        EntityRegistry.registerModEntity(new ResourceLocation(Totemic.MOD_ID, Strings.BUFFALO_NAME), EntityBuffalo.class, Strings.RESOURCE_PREFIX + Strings.BUFFALO_NAME, 0, Totemic.instance, 80, 5, true, 0x2A1C12, 0x885F3E);
        EntityRegistry.registerModEntity(new ResourceLocation(Totemic.MOD_ID, Strings.BAYKOK_NAME), EntityBaykok.class, Strings.RESOURCE_PREFIX + Strings.BAYKOK_NAME, 1, Totemic.instance, 80, 3, true, 0xE0E0E0, 0xF8DAD2);
        EntityRegistry.registerModEntity(new ResourceLocation(Totemic.MOD_ID, Strings.INVIS_ARROW_NAME), EntityInvisArrow.class, Strings.RESOURCE_PREFIX + Strings.INVIS_ARROW_NAME, 2, Totemic.instance, 64, 20, true);
    }

    private void registerTileEntities()
    {
        GameRegistry.registerTileEntity(TileTotemBase.class, Strings.RESOURCE_PREFIX + Strings.TOTEM_BASE_NAME);
        GameRegistry.registerTileEntity(TileTotemPole.class, Strings.RESOURCE_PREFIX + Strings.TOTEM_POLE_NAME);
        GameRegistry.registerTileEntity(TileDrum.class, Strings.RESOURCE_PREFIX + Strings.DRUM_NAME);
        GameRegistry.registerTileEntity(TileWindChime.class, Strings.RESOURCE_PREFIX + Strings.WIND_CHIME_NAME);
        GameRegistry.registerTileEntity(TileTipi.class, Strings.RESOURCE_PREFIX + Strings.TIPI_NAME);
    }

    private void oreDictionary()
    {
        OreDictionary.registerOre("treeLeaves", new ItemStack(ModBlocks.cedar_leaves, 1));
        OreDictionary.registerOre("logWood", new ItemStack(ModBlocks.cedar_log, 1, 0));
        OreDictionary.registerOre("plankWood", new ItemStack(ModBlocks.cedar_plank, 1, 0));
        OreDictionary.registerOre("bellsIron", new ItemStack(ModItems.sub_items, 1, ItemTotemicItems.Type.iron_bells.ordinal()));
        OreDictionary.registerOre("listAllmeatraw", new ItemStack(ModItems.buffalo_meat));
        OreDictionary.registerOre("listAllbeefraw", new ItemStack(ModItems.buffalo_meat));
        OreDictionary.registerOre("listAllbuffaloraw", new ItemStack(ModItems.buffalo_meat));
        OreDictionary.registerOre("listAllmeatcooked", new ItemStack(ModItems.cooked_buffalo_meat));
        OreDictionary.registerOre("listAllbeefcooked", new ItemStack(ModItems.cooked_buffalo_meat));
        OreDictionary.registerOre("listAllbuffalocooked", new ItemStack(ModItems.cooked_buffalo_meat));
        OreDictionary.registerOre("hideBuffalo", new ItemStack(ModItems.buffalo_items, 1, ItemBuffaloDrops.Type.hide.ordinal()));
        OreDictionary.registerOre("teethBuffalo", new ItemStack(ModItems.buffalo_items, 1, ItemBuffaloDrops.Type.teeth.ordinal()));
    }

    private void furnaceRecipes()
    {
        GameRegistry.addSmelting(ModBlocks.stripped_cedar_log, new ItemStack(Items.COAL, 1, 1), 0.5F);
        GameRegistry.addSmelting(ModBlocks.cedar_log, new ItemStack(Items.COAL, 1, 1), 0.5F);
        GameRegistry.addSmelting(ModItems.buffalo_meat, new ItemStack(ModItems.cooked_buffalo_meat), 0.35F);
    }

    private void registerDataFixers()
    {
        ModFixs fixes = FMLCommonHandler.instance().getDataFixer().init(Totemic.MOD_ID, 1000);
        fixes.registerFix(FixTypes.ITEM_INSTANCE, new VanillaIronNugget());
    }

    protected void registerEventHandlers()
    {
        MinecraftForge.EVENT_BUS.register(new EntityUpdate());
        MinecraftForge.EVENT_BUS.register(new EntityFall());
        MinecraftForge.EVENT_BUS.register(new PlayerInteract());
        MinecraftForge.EVENT_BUS.register(new EntitySpawn());
    }
}
