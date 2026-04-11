package playertotems.a2thx;

import org.bukkit.event.Listener;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class PlayerTotems extends JavaPlugin {
    public class keys {
        public static final NamespacedKey STRENGHT_TOTEM = new NamespacedKey(PlayerTotems.getInstance(),
                "StrenghtTotem");
        public static final NamespacedKey HEALTH_TOTEM = new NamespacedKey(PlayerTotems.getInstance(), "HealthTotem");
        public static final NamespacedKey FIRE_RESISTANCE_TOTEM = new NamespacedKey(PlayerTotems.getInstance(),
                "FireResistanceTotem");
        public static final NamespacedKey HASTE_TOTEM = new NamespacedKey(PlayerTotems.getInstance(), "HasteTotem");
        public static final NamespacedKey PLAYER_HEAD = new NamespacedKey(PlayerTotems.getInstance(), "PlayerHead");
        public static final NamespacedKey STRENGTH_TOTEM_TIER2 = new NamespacedKey(PlayerTotems.getInstance(),
                "StrengthTotemT2");
        public static final NamespacedKey HEALTH_TOTEM_TIER2 = new NamespacedKey(PlayerTotems.getInstance(),
                "HealthTotemT2");
        public static final NamespacedKey FIRE_RESISTANCE_TOTEM_TIER2 = new NamespacedKey(PlayerTotems.getInstance(),
                "FireResistanceTotemT2");
        public static final NamespacedKey HASTE_TOTEM_TIER2 = new NamespacedKey(PlayerTotems.getInstance(),
                "HasteTotemT2");
        public static final NamespacedKey STRENGTH_TOTEM_TIER3 = new NamespacedKey(PlayerTotems.getInstance(),
                "StrengthTotemT3");
        public static final NamespacedKey HEALTH_TOTEM_TIER3 = new NamespacedKey(PlayerTotems.getInstance(),
                "HealthTotemT3");
        public static final NamespacedKey FIRE_RESISTANCE_TOTEM_TIER3 = new NamespacedKey(PlayerTotems.getInstance(),
                "FireResistanceTotemT3");
        public static final NamespacedKey HASTE_TOTEM_TIER3 = new NamespacedKey(PlayerTotems.getInstance(),
                "HasteTotemT3");

        public static final NamespacedKey HEART_OF_THE_WARDEN = new NamespacedKey(PlayerTotems.getInstance(),
                "HeartOfTheWarden");
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new OnDeathListener(), this);
        getServer().getPluginManager().registerEvents(new OnDropListener(), this);
        getCommand("gethead").setExecutor(new getHead());
        getCommand("destroy").setExecutor(new Destroy());
        getServer().getPluginManager().registerEvents(new OnPlace(), this);
        getServer().getPluginManager().registerEvents(new OnCraftListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getLogger().info("PlayerTotems has been enabled!");
        Recipes.register();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    boolean hasHealthTotem = false;
                    int hasteCount = 0;
                    for (ItemStack item : player.getInventory().getContents()) {
                        if (item == null || !item.hasItemMeta())
                            continue;
                        ItemMeta meta = item.getItemMeta();
                        if (meta.getPersistentDataContainer().has(keys.STRENGHT_TOTEM, PersistentDataType.STRING)) {
                            player.addPotionEffect(
                                    new PotionEffect(PotionEffectType.STRENGTH, 200, 0, true, false, true));
                        }
                        if (meta.getPersistentDataContainer().has(keys.HEALTH_TOTEM, PersistentDataType.STRING)) {
                            hasHealthTotem = true;
                        }
                        if (meta.getPersistentDataContainer().has(keys.FIRE_RESISTANCE_TOTEM,
                                PersistentDataType.STRING)) {
                            player.addPotionEffect(
                                    new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 200, 0, true, false, true));
                        }
                        if (meta.getPersistentDataContainer().has(keys.HASTE_TOTEM, PersistentDataType.STRING)) {
                            hasteCount++;
                        }
                    }
                    if (hasHealthTotem) {
                        player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(24.0);
                    } else {
                        player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20.0);
                    }
                    if (hasteCount > 0) {
                        int amplifier = hasteCount >= 2 ? 1 : 0;
                        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 200, amplifier, true,
                                false, true));
                    }
                }
            }
        }.runTaskTimer(this, 40L, 40L);
    }

    @Override
    public void onDisable() {
        getLogger().info("PlayerTotems has been disabled!");
    }

    public static PlayerTotems getInstance() {
        return getPlugin(PlayerTotems.class);
    }

    public final class Recipes {
        public static ItemStack heartOfTheWarden;
        public static ItemStack StrenghtTotem;
        public static ItemStack HealthTotem;
        public static ItemStack FireResistanceTotem;
        public static ItemStack HasteTotem;
        public static ItemStack StrenghtTotemT2;
        public static ItemStack HealthTotemT2;
        public static ItemStack FireResistanceTotemT2;
        public static ItemStack HasteTotemT2;
        public static ItemStack StrenghtTotemT3;
        public static ItemStack HealthTotemT3;
        public static ItemStack FireResistanceTotemT3;
        public static ItemStack HasteTotemT3;

        public static void register() {
            heartOfTheWarden = new ItemStack(Material.NETHER_STAR);
            ItemMeta heartOfTheWardenMeta = heartOfTheWarden.getItemMeta();
            heartOfTheWardenMeta.setDisplayName(ChatColor.DARK_PURPLE + "Heart of the Warden");
            heartOfTheWardenMeta.getPersistentDataContainer().set(keys.HEART_OF_THE_WARDEN, PersistentDataType.STRING,
                    "HeartOfTheWarden");
            heartOfTheWardenMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            heartOfTheWardenMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            heartOfTheWarden.setItemMeta(heartOfTheWardenMeta);

            StrenghtTotem = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta StrenghtTotemMeta = StrenghtTotem.getItemMeta();
            StrenghtTotemMeta.setDisplayName(ChatColor.YELLOW + "Strenght Totem");
            StrenghtTotemMeta.getPersistentDataContainer().set(keys.STRENGHT_TOTEM, PersistentDataType.STRING,
                    "StrenghtTotem");
            StrenghtTotemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            StrenghtTotemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            StrenghtTotem.setItemMeta(StrenghtTotemMeta);
            ItemStack healthPotion = new ItemStack(Material.POTION, 1);
            PotionMeta healthMeta = (PotionMeta) healthPotion.getItemMeta();
            healthMeta.setBasePotionType(PotionType.HEALING);
            healthPotion.setItemMeta(healthMeta);
            ItemStack strengthPotion = new ItemStack(Material.POTION, 1);
            PotionMeta strengthMeta = (PotionMeta) strengthPotion.getItemMeta();
            strengthMeta.setBasePotionType(PotionType.STRENGTH);
            strengthPotion.setItemMeta(strengthMeta);
            ItemStack fireResistancePotion = new ItemStack(Material.POTION, 1);
            PotionMeta fireResMeta = (PotionMeta) fireResistancePotion.getItemMeta();
            fireResMeta.setBasePotionType(PotionType.FIRE_RESISTANCE);
            fireResistancePotion.setItemMeta(fireResMeta);

            HealthTotem = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta HealthTotemMeta = HealthTotem.getItemMeta();
            HealthTotemMeta.setDisplayName(ChatColor.YELLOW + "Health Totem");
            HealthTotemMeta.getPersistentDataContainer().set(keys.HEALTH_TOTEM, PersistentDataType.STRING,
                    "HealthTotem");
            HealthTotemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            HealthTotemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            HealthTotem.setItemMeta(HealthTotemMeta);
            FireResistanceTotem = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta FireResistanceTotemMeta = FireResistanceTotem.getItemMeta();
            FireResistanceTotemMeta.setDisplayName(ChatColor.RED + "Fire Resistance Totem");
            FireResistanceTotemMeta.getPersistentDataContainer().set(keys.FIRE_RESISTANCE_TOTEM,
                    PersistentDataType.STRING, "FireResistanceTotem");
            FireResistanceTotemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            FireResistanceTotemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            FireResistanceTotem.setItemMeta(FireResistanceTotemMeta);
            HasteTotem = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta HasteTotemMeta = HasteTotem.getItemMeta();
            HasteTotemMeta.setDisplayName(ChatColor.YELLOW + "Haste Totem");
            HasteTotemMeta.getPersistentDataContainer().set(keys.HASTE_TOTEM, PersistentDataType.STRING, "HasteTotem");
            HasteTotemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            HasteTotemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            HasteTotem.setItemMeta(HasteTotemMeta);
            // TIER 2
            StrenghtTotemT2 = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta StrenghtTotemT2Meta = StrenghtTotemT2.getItemMeta();
            StrenghtTotemT2Meta.setDisplayName(ChatColor.YELLOW + "Strenght Totem T2");
            StrenghtTotemT2Meta.getPersistentDataContainer().set(keys.STRENGTH_TOTEM_TIER2, PersistentDataType.STRING,
                    "StrenghtTotemT2");
            StrenghtTotemT2Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            StrenghtTotemT2Meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            StrenghtTotemT2.setItemMeta(StrenghtTotemT2Meta);

            HealthTotemT2 = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta HealthTotemT2Meta = HealthTotemT2.getItemMeta();
            HealthTotemT2Meta.setDisplayName(ChatColor.YELLOW + "Health Totem T2");
            HealthTotemT2Meta.getPersistentDataContainer().set(keys.HEALTH_TOTEM_TIER2, PersistentDataType.STRING,
                    "HealthTotemT2");
            HealthTotemT2Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            HealthTotemT2Meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            HealthTotemT2.setItemMeta(HealthTotemT2Meta);

            FireResistanceTotemT2 = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta FireResistanceTotemT2Meta = FireResistanceTotemT2.getItemMeta();
            FireResistanceTotemT2Meta.setDisplayName(ChatColor.YELLOW + "Fire Resistance Totem T2");
            FireResistanceTotemT2Meta.getPersistentDataContainer().set(keys.FIRE_RESISTANCE_TOTEM_TIER2,
                    PersistentDataType.STRING, "FireResistanceTotemT2");
            FireResistanceTotemT2Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            FireResistanceTotemT2Meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            FireResistanceTotemT2.setItemMeta(FireResistanceTotemT2Meta);

            HasteTotemT2 = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta HasteTotemT2Meta = HasteTotemT2.getItemMeta();
            HasteTotemT2Meta.setDisplayName(ChatColor.YELLOW + "Haste Totem T2");
            HasteTotemT2Meta.getPersistentDataContainer().set(keys.HASTE_TOTEM_TIER2, PersistentDataType.STRING,
                    "HasteTotemT2");
            HasteTotemT2Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            HasteTotemT2Meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            HasteTotemT2.setItemMeta(HasteTotemT2Meta);

            StrenghtTotemT3 = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta StrenghtTotemT3Meta = StrenghtTotemT3.getItemMeta();
            StrenghtTotemT3Meta.setDisplayName(ChatColor.YELLOW + "Strenght Totem T3");
            StrenghtTotemT3Meta.getPersistentDataContainer().set(keys.STRENGTH_TOTEM_TIER3, PersistentDataType.STRING,
                    "StrenghtTotemT3");
            StrenghtTotemT3Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            StrenghtTotemT3Meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            StrenghtTotemT3.setItemMeta(StrenghtTotemT3Meta);

            HealthTotemT3 = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta HealthTotemT3Meta = HealthTotemT3.getItemMeta();
            HealthTotemT3Meta.setDisplayName(ChatColor.YELLOW + "Health Totem T3");
            HealthTotemT3Meta.getPersistentDataContainer().set(keys.HEALTH_TOTEM_TIER3, PersistentDataType.STRING,
                    "HealthTotemT3");
            HealthTotemT3Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            HealthTotemT3Meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            HealthTotemT3.setItemMeta(HealthTotemT3Meta);

            FireResistanceTotemT3 = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta FireResistanceTotemT3Meta = FireResistanceTotemT3.getItemMeta();
            FireResistanceTotemT3Meta.setDisplayName(ChatColor.YELLOW + "Fire Resistance Totem T3");
            FireResistanceTotemT3Meta.getPersistentDataContainer().set(keys.FIRE_RESISTANCE_TOTEM_TIER3,
                    PersistentDataType.STRING, "FireResistanceTotemT3");
            FireResistanceTotemT3Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            FireResistanceTotemT3Meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            FireResistanceTotemT3.setItemMeta(FireResistanceTotemT3Meta);

            HasteTotemT3 = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta HasteTotemT3Meta = HasteTotemT3.getItemMeta();
            HasteTotemT3Meta.setDisplayName(ChatColor.YELLOW + "Haste Totem T3");
            HasteTotemT3Meta.getPersistentDataContainer().set(keys.HASTE_TOTEM_TIER3, PersistentDataType.STRING,
                    "HasteTotemT3");
            HasteTotemT3Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            HasteTotemT3Meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            HasteTotemT3.setItemMeta(HasteTotemT3Meta);

            ShapedRecipe HealthTotemRecipe = new ShapedRecipe(keys.HEALTH_TOTEM, HealthTotem);
            ShapedRecipe StrenghtTotemRecipe = new ShapedRecipe(keys.STRENGHT_TOTEM, StrenghtTotem);
            ShapedRecipe FireResistanceTotemRecipe = new ShapedRecipe(keys.FIRE_RESISTANCE_TOTEM, FireResistanceTotem);
            ShapedRecipe HasteTotemRecipe = new ShapedRecipe(keys.HASTE_TOTEM, HasteTotem);
            HealthTotemRecipe.shape(" H ",
                    "NTN",
                    " P ");
            HealthTotemRecipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
            HealthTotemRecipe.setIngredient('N', Material.NETHERITE_INGOT);
            HealthTotemRecipe.setIngredient('H', Material.PLAYER_HEAD);
            HealthTotemRecipe.setIngredient('P', new RecipeChoice.ExactChoice(healthPotion));
            StrenghtTotemRecipe.shape(" H ",
                    "NTN",
                    " P ");
            StrenghtTotemRecipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
            StrenghtTotemRecipe.setIngredient('N', Material.NETHERITE_INGOT);
            StrenghtTotemRecipe.setIngredient('H', Material.PLAYER_HEAD);
            StrenghtTotemRecipe.setIngredient('P', new RecipeChoice.ExactChoice(strengthPotion));

            FireResistanceTotemRecipe.shape(" H ",
                    "NTN",
                    " P ");
            FireResistanceTotemRecipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
            FireResistanceTotemRecipe.setIngredient('N', Material.NETHERITE_INGOT);
            FireResistanceTotemRecipe.setIngredient('H', Material.PLAYER_HEAD);
            FireResistanceTotemRecipe.setIngredient('P', new RecipeChoice.ExactChoice(fireResistancePotion));

            HasteTotemRecipe.shape(" H ",
                    "NTN",
                    " G ");
            HasteTotemRecipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
            HasteTotemRecipe.setIngredient('N', Material.NETHERITE_INGOT);
            HasteTotemRecipe.setIngredient('H', Material.PLAYER_HEAD);
            HasteTotemRecipe.setIngredient('G', Material.GOLDEN_PICKAXE);
            // Tier 2 recipies
            ShapedRecipe HealthTotemT2Recipe = new ShapedRecipe(keys.HEALTH_TOTEM_TIER2, HealthTotemT2);
            ShapedRecipe StrenghtTotemT2Recipe = new ShapedRecipe(keys.STRENGTH_TOTEM_TIER2, StrenghtTotemT2);
            ShapedRecipe FireResistanceTotemT2Recipe = new ShapedRecipe(keys.FIRE_RESISTANCE_TOTEM_TIER2,
                    FireResistanceTotemT2);
            ShapedRecipe HasteTotemT2Recipe = new ShapedRecipe(keys.HASTE_TOTEM_TIER2, HasteTotemT2);

            HealthTotemT2Recipe.shape(" P ",
                    "NTN",
                    " P ");
            HealthTotemT2Recipe.setIngredient('N', Material.NETHERITE_INGOT);
            HealthTotemT2Recipe.setIngredient('P', new RecipeChoice.ExactChoice(healthPotion));
            StrenghtTotemT2Recipe.shape(" P ",
                    "NTN",
                    " P ");
            StrenghtTotemT2Recipe.setIngredient('N', Material.NETHERITE_INGOT);
            StrenghtTotemT2Recipe.setIngredient('P', new RecipeChoice.ExactChoice(strengthPotion));
            FireResistanceTotemT2Recipe.shape(" P ",
                    "NTN",
                    " P ");
            FireResistanceTotemT2Recipe.setIngredient('N', Material.NETHERITE_INGOT);
            FireResistanceTotemT2Recipe.setIngredient('P', new RecipeChoice.ExactChoice(fireResistancePotion));
            HasteTotemT2Recipe.shape(" P ",
                    "NTN",
                    " P ");
            HasteTotemT2Recipe.setIngredient('T', new RecipeChoice.ExactChoice(HasteTotem));
            HasteTotemT2Recipe.setIngredient('N', Material.NETHERITE_INGOT);
            HasteTotemT2Recipe.setIngredient('P', Material.GOLD_BLOCK);

            // Tier 3 recipies
            ShapedRecipe HealthTotemT3Recipe = new ShapedRecipe(keys.HEALTH_TOTEM_TIER3, HealthTotemT3);
            ShapedRecipe StrenghtTotemT3Recipe = new ShapedRecipe(keys.STRENGTH_TOTEM_TIER3, StrenghtTotemT3);
            ShapedRecipe FireResistanceTotemT3Recipe = new ShapedRecipe(keys.FIRE_RESISTANCE_TOTEM_TIER3,
                    FireResistanceTotemT3);
            ShapedRecipe HasteTotemT3Recipe = new ShapedRecipe(keys.HASTE_TOTEM_TIER3, HasteTotemT3);

            HealthTotemT3Recipe.shape(" S ",
                    "NTN",
                    " P ");
            HealthTotemT3Recipe.setIngredient('T', new RecipeChoice.ExactChoice(HealthTotemT2));
            HealthTotemT3Recipe.setIngredient('N', Material.NETHERITE_INGOT);
            HealthTotemT3Recipe.setIngredient('S', new RecipeChoice.ExactChoice(heartOfTheWarden));
            HealthTotemT3Recipe.setIngredient('P', new RecipeChoice.ExactChoice(healthPotion));

            StrenghtTotemT3Recipe.shape(" S ",
                    "NTN",
                    " H ");
            StrenghtTotemT3Recipe.setIngredient('T', new RecipeChoice.ExactChoice(StrenghtTotemT2));
            StrenghtTotemT3Recipe.setIngredient('N', Material.NETHERITE_INGOT);
            StrenghtTotemT3Recipe.setIngredient('S', new RecipeChoice.ExactChoice(heartOfTheWarden));
            StrenghtTotemT3Recipe.setIngredient('H', new RecipeChoice.ExactChoice(heartOfTheWarden));

            FireResistanceTotemT3Recipe.shape(" S ",
                    "NTN",
                    " M ");
            FireResistanceTotemT3Recipe.setIngredient('T', new RecipeChoice.ExactChoice(FireResistanceTotemT2));
            FireResistanceTotemT3Recipe.setIngredient('N', Material.NETHERITE_INGOT);
            FireResistanceTotemT3Recipe.setIngredient('S', new RecipeChoice.ExactChoice(heartOfTheWarden));
            FireResistanceTotemT3Recipe.setIngredient('M', Material.MAGMA_CREAM);

            HasteTotemT3Recipe.shape(" S ",
                    "NTN",
                    " G ");
            HasteTotemT3Recipe.setIngredient('T', new RecipeChoice.ExactChoice(HasteTotemT2));
            HasteTotemT3Recipe.setIngredient('N', Material.NETHERITE_INGOT);
            HasteTotemT3Recipe.setIngredient('S', new RecipeChoice.ExactChoice(heartOfTheWarden));
            HasteTotemT3Recipe.setIngredient('G', Material.GOLD_BLOCK);
            // T1
            Bukkit.addRecipe(HealthTotemT2Recipe);
            Bukkit.addRecipe(StrenghtTotemT2Recipe);
            Bukkit.addRecipe(FireResistanceTotemT2Recipe);
            Bukkit.addRecipe(HasteTotemT2Recipe);
            // T2
            Bukkit.addRecipe(HealthTotemRecipe);
            Bukkit.addRecipe(StrenghtTotemRecipe);
            Bukkit.addRecipe(FireResistanceTotemRecipe);
            Bukkit.addRecipe(HasteTotemRecipe);
            // T3
            Bukkit.addRecipe(HealthTotemT3Recipe);
            Bukkit.addRecipe(StrenghtTotemT3Recipe);
            Bukkit.addRecipe(FireResistanceTotemT3Recipe);
            Bukkit.addRecipe(HasteTotemT3Recipe);
        }
    }

    public class OnDeathListener implements Listener {
        @EventHandler
        public void onDeath(PlayerDeathEvent e) {
            Player victum = e.getEntity();
            Player killer = victum.getKiller();
            if (killer == null) {
                return;
            }
            ItemStack victumHead = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta meta = victumHead.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + victum.getName() + "'s head");
            meta.getPersistentDataContainer().set(keys.PLAYER_HEAD, PersistentDataType.STRING, victum.getName());
            meta.setLore(List
                    .of(ChatColor.RED
                            + "The head will be destroyed on death or when its taken out of your inventory \n WARNING: THE PLAYER WHO YOU KILLED NOW HAS YOUR LOCATION \n you can either destroy the head or get hunted down by him"));
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.getPersistentDataContainer().set(keys.PLAYER_HEAD, PersistentDataType.STRING, victum.getName());
            SkullMeta skullMeta = (SkullMeta) meta;
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(victum.getName()));
            victumHead.setItemMeta(skullMeta);
            if (killer.getInventory().firstEmpty() == -1) {
                killer.getWorld().dropItem(killer.getLocation(), victumHead);
            } else {
                killer.getInventory().addItem(victumHead);
            }
            ItemStack[] contents = victum.getInventory().getContents();
            for (int i = 0; i < contents.length; i++) {
                if (isSpecialItem(contents[i])) {
                    victum.getInventory().setItem(i, null);
                }
            }
        }
    }

    public class onWardenDeath implements Listener {
        @EventHandler
        public void onWardenDeath(EntityDeathEvent e) {
            if (e.getEntity() instanceof Warden) {
                e.getDrops().add(Recipes.heartOfTheWarden);
            }
        }
    }

    public class getHead implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player && sender.hasPermission("playertotems.gethead")) {
                Player player = (Player) sender;
                if (args.length == 0) {
                    player.sendMessage(ChatColor.RED + "You need to specify a player name");
                    return true;
                }
                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                ItemMeta meta = head.getItemMeta();
                meta.setDisplayName(ChatColor.YELLOW + args[0] + "'s head");
                meta.getPersistentDataContainer().set(keys.PLAYER_HEAD, PersistentDataType.STRING, args[0]);
                meta.setLore(List
                        .of(ChatColor.RED
                                + "The head will be destroyed on death or when its taken out of your inventory \n WARNING: THE PLAYER WHO YOU KILLED NOW HAS YOUR LOCATION \n you can either destroy the head or get hunted down by him"));
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.getPersistentDataContainer().set(keys.PLAYER_HEAD, PersistentDataType.STRING, args[0]);
                SkullMeta skullMeta = (SkullMeta) meta;
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(args[0]));
                head.setItemMeta(skullMeta);
                player.getInventory().addItem(head);
            }
            return true;
        }
    }

    public class Destroy implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
                    player.sendMessage(ChatColor.RED
                            + "You can't destroy this item");
                    return true;
                }
                ItemMeta meta = item.getItemMeta();
                if (isSpecialItem(item)) {
                    player.getInventory().removeItem(item);
                } else {
                    player.sendMessage(ChatColor.RED
                            + "You can't destroy this item");
                }
            }
            return true;
        }
    }

    public class OnDropListener implements Listener {
        @EventHandler
        public void onDrop(PlayerDropItemEvent e) {
            ItemStack item = e.getItemDrop().getItemStack();
            if (isSpecialItem(item)) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.RED + "This item cant leave your inventory! " + ChatColor.YELLOW
                        + "Do " + ChatColor.BLUE + "/destroy" + ChatColor.YELLOW + " while holding it to destroy it");
            }
        }
    }

    public class OnPlace implements Listener {
        @EventHandler
        public void onPlace(PlayerInteractEvent e) {
            ItemStack item = e.getItem();
            if (isSpecialItem(item)) {
                if (item.getType() == Material.PLAYER_HEAD) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(ChatColor.YELLOW + "You can't place this head. " + ChatColor.YELLOW
                            + "Do " + ChatColor.BLUE + "/destroy" + ChatColor.YELLOW
                            + " while holding it to destroy the head");
                }
            }
        }
    }

    public class OnCraftListener implements Listener {
        @EventHandler
        public void onCraft(PrepareItemCraftEvent e) {
            ItemStack result = e.getInventory().getResult();
            if (result == null || !result.hasItemMeta())
                return;

            ItemMeta resultMeta = result.getItemMeta();
            String baseName = "";

            if (resultMeta.getPersistentDataContainer().has(keys.HEALTH_TOTEM, PersistentDataType.STRING)) {
                baseName = "Health Totem";
            } else if (resultMeta.getPersistentDataContainer().has(keys.STRENGHT_TOTEM, PersistentDataType.STRING)) {
                baseName = "Strenght Totem";
            } else if (resultMeta.getPersistentDataContainer().has(keys.FIRE_RESISTANCE_TOTEM,
                    PersistentDataType.STRING)) {
                baseName = "Fire Resistance Totem";
            } else if (resultMeta.getPersistentDataContainer().has(keys.HASTE_TOTEM,
                    PersistentDataType.STRING)) {
                baseName = "Haste Totem";
            }

            if (!baseName.isEmpty()) {
                for (ItemStack item : e.getInventory().getMatrix()) {
                    if (item != null && item.getType() == Material.PLAYER_HEAD && item.hasItemMeta()) {
                        ItemMeta headMeta = item.getItemMeta();
                        String playerName = headMeta.getPersistentDataContainer().get(keys.PLAYER_HEAD,
                                PersistentDataType.STRING);

                        if (playerName == null && headMeta instanceof SkullMeta) {
                            SkullMeta skull = (SkullMeta) headMeta;
                            if (skull.getOwningPlayer() != null) {
                                playerName = skull.getOwningPlayer().getName();
                            }
                        }

                        if (playerName != null) {
                            resultMeta.setDisplayName(ChatColor.YELLOW + playerName + "'s " + baseName);
                            resultMeta.getPersistentDataContainer().set(keys.PLAYER_HEAD, PersistentDataType.STRING,
                                    playerName);
                            result.setItemMeta(resultMeta);
                            e.getInventory().setResult(result);
                            break;
                        }
                    }
                }
            }
        }
    }

    public class InventoryListener implements Listener {
        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            ItemStack clickedItem = e.getCurrentItem();
            ItemStack cursorItem = e.getCursor();

            if (isSpecialItem(clickedItem) || isSpecialItem(cursorItem)) {
                Inventory clickedInventory = e.getClickedInventory();
                Inventory topInventory = e.getView().getTopInventory();

                if (clickedInventory == null)
                    return;
                if (topInventory.getType() != InventoryType.PLAYER
                        && topInventory.getType() != InventoryType.CRAFTING
                        && topInventory.getType() != InventoryType.WORKBENCH) {
                    if (e.isShiftClick() && (clickedInventory.getType() == InventoryType.PLAYER
                            || clickedInventory.getType() == InventoryType.CRAFTING)) {
                        e.setCancelled(true);
                        e.getWhoClicked().sendMessage(ChatColor.RED + "This item cannot leave your inventory!");
                        return;
                    }
                    if (clickedInventory == topInventory) {
                        if (isSpecialItem(cursorItem)) {
                            e.setCancelled(true);
                            e.getWhoClicked().sendMessage(ChatColor.RED + "This item cannot leave your inventory!");
                            return;
                        }
                    }
                }
                if (clickedInventory != null && clickedInventory.getType() != InventoryType.PLAYER
                        && clickedInventory.getType() != InventoryType.CRAFTING
                        && clickedInventory.getType() != InventoryType.WORKBENCH) {
                    if (e.getAction() == InventoryAction.HOTBAR_SWAP
                            || e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
                        Player p = (Player) e.getWhoClicked();
                        ItemStack hotbarItem = p.getInventory().getItem(e.getHotbarButton());
                        if (isSpecialItem(hotbarItem)) {
                            e.setCancelled(true);
                            e.getWhoClicked().sendMessage(ChatColor.RED + "This item cannot leave your inventory!");
                            return;
                        }
                    }
                }
            }

            Material bundleMat = Material.getMaterial("BUNDLE");
            if (bundleMat != null) {
                if (clickedItem != null && clickedItem.getType() == bundleMat && isSpecialItem(cursorItem)) {
                    e.setCancelled(true);
                    e.getWhoClicked().sendMessage(ChatColor.RED + "You cannot put this item in a bundle!");
                } else if (cursorItem != null && cursorItem.getType() == bundleMat && isSpecialItem(clickedItem)) {
                    e.setCancelled(true);
                    e.getWhoClicked().sendMessage(ChatColor.RED + "You cannot put this item in a bundle!");
                }
            }
        }

        @EventHandler
        public void onInventoryDrag(InventoryDragEvent e) {
            if (isSpecialItem(e.getOldCursor())) {
                for (int slot : e.getRawSlots()) {
                    if (slot < e.getView().getTopInventory().getSize()) {
                        Inventory top = e.getView().getTopInventory();
                        if (top.getType() != InventoryType.PLAYER && top.getType() != InventoryType.CRAFTING
                                && top.getType() != InventoryType.WORKBENCH) {
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean isSpecialItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta())
            return false;
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(keys.PLAYER_HEAD, PersistentDataType.STRING) ||
                meta.getPersistentDataContainer().has(keys.HEALTH_TOTEM, PersistentDataType.STRING) ||
                meta.getPersistentDataContainer().has(keys.STRENGHT_TOTEM, PersistentDataType.STRING) ||
                meta.getPersistentDataContainer().has(keys.FIRE_RESISTANCE_TOTEM, PersistentDataType.STRING) ||
                meta.getPersistentDataContainer().has(keys.HASTE_TOTEM, PersistentDataType.STRING) ||
                meta.getPersistentDataContainer().has(keys.HEART_OF_THE_WARDEN, PersistentDataType.STRING);
    }
}