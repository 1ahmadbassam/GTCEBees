package gtcebees;

import forestry.api.recipes.ICentrifugeRecipe;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.core.items.ItemFluidContainerForestry;
import forestry.core.utils.OreDictUtil;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gtcebees.Items.GTCombs;
import gtcebees.Recipes.ForestryMachineRecipes;
import gtcebees.Recipes.GTMachineRecipes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collections;

@Mod.EventBusSubscriber(modid = GTCEBees.MODID)
public class CommonProxy {
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(GTCombs.combItem);
        OreDictionary.registerOre(OreDictUtil.BEE_COMB, GTCombs.combItem);
        OreDictionary.registerOre(OreDictUtil.BEE_COMB, new ItemStack(GTCombs.combItem, 1, OreDictionary.WILDCARD_VALUE));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        ForestryMachineRecipes.init();
    }

    public void preInit() {
    }

    public void postInit() {
        if (GTCEBeesConfig.Recipes.GenerateCentrifugeRecipes)
            for (ICentrifugeRecipe recipe : RecipeManagers.centrifugeManager.recipes()) {
                SimpleRecipeBuilder builder = RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder();
                builder.inputs(recipe.getInput().copy());
                for (ItemStack stack : recipe.getAllProducts().keySet()) {
                    builder.chancedOutput(stack.copy(), (int) (recipe.getAllProducts().get(stack) * (float) Recipe.getMaxChancedValue()), 1000);
                }
                builder.EUt(5);
                builder.duration(128);
                builder.buildAndRegister();
            }

        if (GTCEBeesConfig.Recipes.GenerateExtractorRecipes)
            for (ISqueezerRecipe recipe : RecipeManagers.squeezerManager.recipes()) {
                if (recipe.getResources().size() != 1 || recipe.getResources().get(0).getItem() instanceof ItemFluidContainerForestry)
                    continue;
                if (RecipeMaps.FLUID_EXTRACTION_RECIPES.findRecipe(Integer.MAX_VALUE, recipe.getResources(), Collections.emptyList(), Integer.MAX_VALUE) != null)
                    continue;
                SimpleRecipeBuilder builder = RecipeMaps.FLUID_EXTRACTION_RECIPES.recipeBuilder();
                builder.inputs(recipe.getResources().get(0).copy());
                if (!recipe.getRemnants().isEmpty())
                    builder.chancedOutput(recipe.getRemnants().copy(), (int) (recipe.getRemnantsChance() * (float) Recipe.getMaxChancedValue()), 1000);
                recipe.getFluidOutput();
                builder.fluidOutputs(recipe.getFluidOutput());
                builder.EUt(5);
                builder.duration(128);
                builder.buildAndRegister();
            }

        GTMachineRecipes.postInit();
    }
}
