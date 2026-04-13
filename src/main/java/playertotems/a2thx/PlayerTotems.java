package playertotems.a2thx;

import org.bukkit.event.Listener;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
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

    public boolean isPotionEnabled(String type) {
        String cleanType = type.toLowerCase().replace(" ", "").replace("strenght", "strength");
        return getConfig().getBoolean("potion-settings." + cleanType + ".enabled", true);
    }

    public class onJoin implements Listener {
        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            Files playerfile = new Files("playerdata/" + player.getName().toLowerCase() + ".yml");
            playerfile.addDefault("CurrentGlobalTier", 0);
            playerfile.addDefault("TotemTiers/StrenghtTier", 0);
            playerfile.addDefault("TotemTiers/HealthTier", 0);
            playerfile.addDefault("TotemTiers/FireResistanceTier", 0);
            playerfile.addDefault("TotemTiers/HasteTier", 0);
            playerfile.save();
        }

        public static Files getPlayerFile(Player player) {
            Files playerfile = new Files("playerdata/" + player.getName().toLowerCase() + ".yml");
            return playerfile;
        }
    }

    public static class Files {
        private final static PlayerTotems instance = PlayerTotems.getInstance();
        private File file;
        private FileConfiguration config;
        private String filename;

        public Files(String filename) {
            this.filename = filename;
            this.load();
        }

        private void load() {
            this.file = new File(instance.getDataFolder(), this.filename);

            if (!instance.getDataFolder().exists()) {
                instance.getDataFolder().mkdirs();
            }

            if (!this.file.exists()) {
                try {
                    this.file.getParentFile().mkdirs();

                    if (instance.getResource(this.filename) != null) {
                        instance.saveResource(this.filename, false);
                    } else {
                        this.file.createNewFile();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.config = YamlConfiguration.loadConfiguration(this.file);
                this.config.options().parseComments(true);
                this.config.options().copyDefaults(true);
            }

            this.config = YamlConfiguration.loadConfiguration(this.file);
            this.config.options().parseComments(true);
        }

        public void set(String path, Object value) {
            this.config.set(path, value);
        }

        public void addDefault(String path, Object value) {
            this.config.addDefault(path, value);
        }

        public FileConfiguration getFile() {
            return this.config;
        }

        public void save() {
            try {
                this.config.save(this.file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        getServer().getPluginManager().registerEvents(new OnDeathListener(), this);
        getServer().getPluginManager().registerEvents(new OnDropListener(), this);
        getCommand("gethead").setExecutor(new getHead());
        getCommand("destroy").setExecutor(new Destroy());
        getCommand("research").setExecutor(new Research.OpenResearchMenu());
        getCommand("bypasstime").setExecutor(new BypassTime());
        getServer().getPluginManager().registerEvents(new OnPlace(), this);
        getServer().getPluginManager().registerEvents(new OnCraftListener(), this);
        getServer().getPluginManager().registerEvents(new onWardenDeath(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new Research.ResearchGui.GuiListener(), this);
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

    public static class tier2totems {
        public static ItemStack T2strengthtotem;
        public static ItemStack T2healthtotem;
        public static ItemStack T2fireresistancetotem;
        public static ItemStack T2hastetotem;

        public static void register() {
            T2strengthtotem = new ItemStack(Material.TOTEM_OF_UNDYING);
            T2healthtotem = new ItemStack(Material.TOTEM_OF_UNDYING);
            T2fireresistancetotem = new ItemStack(Material.TOTEM_OF_UNDYING);
            T2hastetotem = new ItemStack(Material.TOTEM_OF_UNDYING);

            ItemMeta strengthmeta = T2strengthtotem.getItemMeta();
            ItemMeta healthmeta = T2healthtotem.getItemMeta();
            ItemMeta fireresistancemeta = T2fireresistancetotem.getItemMeta();
            ItemMeta hastemeta = T2hastetotem.getItemMeta();

            strengthmeta.setDisplayName(getInstance().getTotemColor("strength") + "Tier 2 Strength Totem");
            healthmeta.setDisplayName(getInstance().getTotemColor("health") + "Tier 2 Health Totem");
            fireresistancemeta
                    .setDisplayName(getInstance().getTotemColor("fire resistance") + "Tier 2 Fire Resistance Totem");
            hastemeta.setDisplayName(getInstance().getTotemColor("haste") + "Tier 2 Haste Totem");

            strengthmeta.getPersistentDataContainer().set(keys.STRENGTH_TOTEM_TIER2, PersistentDataType.STRING,
                    "StrengthTotemT2");
            healthmeta.getPersistentDataContainer().set(keys.HEALTH_TOTEM_TIER2, PersistentDataType.STRING,
                    "HealthTotemT2");
            fireresistancemeta.getPersistentDataContainer().set(keys.FIRE_RESISTANCE_TOTEM_TIER2,
                    PersistentDataType.STRING, "FireResistanceTotemT2");
            hastemeta.getPersistentDataContainer().set(keys.HASTE_TOTEM_TIER2, PersistentDataType.STRING,
                    "HasteTotemT2");

            T2strengthtotem.setItemMeta(strengthmeta);
            T2healthtotem.setItemMeta(healthmeta);
            T2fireresistancetotem.setItemMeta(fireresistancemeta);
            T2hastetotem.setItemMeta(hastemeta);
        }
    }

    public ChatColor getTotemColor(String type) {
        String cleanType = type.toLowerCase().replace(" ", "").replace("strenght", "strength");
        String colorName = getConfig().getString("totem-colors." + cleanType, "WHITE");
        try {
            return ChatColor.valueOf(colorName.toUpperCase());
        } catch (Exception e) {
            return ChatColor.WHITE;
        }
    }

    // Research Tree
    public static class Research {
        public static ItemStack TiersButton;

        public static Set<UUID> bypassPlayers = new HashSet<>();

        public static int getPlayedMinutes(Player player) {
            return player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60;
        }

        public static boolean canResearch(Player player, String type, int tier) {
            boolean isBypassing = bypassPlayers.contains(player.getUniqueId());

            String path;
            if (type.equalsIgnoreCase("global")) {
                path = "research.tier" + tier + ".global";
            } else {
                path = "research.tier" + tier + "." + type.toLowerCase().replace(" ", "");
            }

            int reqMinutes = PlayerTotems.getInstance().getConfig().getInt(path + ".playtime", 0);
            int reqXP = PlayerTotems.getInstance().getConfig().getInt(path + ".xp", 0);

            boolean hasTime = isBypassing || getPlayedMinutes(player) >= reqMinutes;
            boolean hasXP = player.getLevel() >= reqXP;

            return hasTime && hasXP;
        }

        public static int getRequiredXP(String type, int tier) {
            String path;
            if (type.equalsIgnoreCase("global")) {
                path = "research.tier" + tier + ".global.xp";
            } else {
                path = "research.tier" + tier + "." + type.toLowerCase().replace(" ", "") + ".xp";
            }
            return PlayerTotems.getInstance().getConfig().getInt(path);
        }

        public static int getRequiredMinutes(String type, int tier) {
            String path;
            if (type.equalsIgnoreCase("global")) {
                path = "research.tier" + tier + ".global.playtime";
            } else {
                path = "research.tier" + tier + "." + type.toLowerCase().replace(" ", "") + ".playtime";
            }
            return PlayerTotems.getInstance().getConfig().getInt(path);
        }

        public static String getTotemTierPath(Player player, String type) {
            type = type.toLowerCase().replace(" ", "");
            switch (type) {
                case "strength":
                case "strenght":
                    return "TotemTiers/StrenghtTier";
                case "health":
                    return "TotemTiers/HealthTier";
                case "fireresistance":
                    return "TotemTiers/FireResistanceTier";
                case "haste":
                    return "TotemTiers/HasteTier";
                case "invisibility":
                    return "TotemTiers/InvisibilityTier";
                case "speed":
                    return "TotemTiers/SpeedTier";
                default:
                    return "CurrentGlobalTier";
            }
        }

        public static int getTier(Player player, String type) {
            FileConfiguration config = onJoin.getPlayerFile(player).getFile();
            return config.getInt(getTotemTierPath(player, type));
        }

        public static class OpenResearchMenu implements CommandExecutor {
            @Override
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (!(sender instanceof Player))
                    return false;
                Player player = (Player) sender;
                Inventory inventory = Bukkit.createInventory(null, 27,
                        ChatColor.GREEN + "" + ChatColor.BOLD + "Research Tree");

                TiersButton = new ItemStack(Material.HEAVY_CORE);
                ItemMeta tiersButtonMeta = TiersButton.getItemMeta();
                tiersButtonMeta.setDisplayName(ChatColor.DARK_PURPLE + "Tiers");
                TiersButton.setItemMeta(tiersButtonMeta);
                ItemStack invisibility = Recipes.invisibilityPotion.clone();
                ItemMeta invisMeta = invisibility.getItemMeta();
                invisMeta.setDisplayName(ChatColor.GREEN + "Invisibility Tiers");
                if (!getInstance().getConfig().getBoolean("potion-settings.invisibility.enabled", true)) {
                    invisMeta.setLore(Arrays.asList(ChatColor.RED + "Coming soon!"));
                } else {
                    invisMeta.setLore(null);
                }
                invisMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                invisibility.setItemMeta(invisMeta);

                ItemStack speed = Recipes.speedPotion.clone();
                ItemMeta speedMeta = speed.getItemMeta();
                speedMeta.setDisplayName(ChatColor.GREEN + "Speed Tiers");
                if (!getInstance().getConfig().getBoolean("potion-settings.speed.enabled", true)) {
                    speedMeta.setLore(Arrays.asList(ChatColor.RED + "Coming soon!"));
                } else {
                    speedMeta.setLore(null);
                }
                speedMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                speed.setItemMeta(speedMeta);

                ItemStack strength = Recipes.strengthDisplay.clone();
                ItemMeta strengthMeta = strength.getItemMeta();
                strengthMeta.setDisplayName(ChatColor.GREEN + "Strength Tiers");
                if (!getInstance().isPotionEnabled("strength")) {
                    strengthMeta.setLore(Arrays.asList(ChatColor.RED + "Coming soon!"));
                } else {
                    strengthMeta.setLore(null);
                }
                strengthMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                strength.setItemMeta(strengthMeta);

                ItemStack health = Recipes.healthDisplay.clone();
                ItemMeta healthMeta = health.getItemMeta();
                healthMeta.setDisplayName(ChatColor.GREEN + "Health Tiers");
                if (!getInstance().isPotionEnabled("health")) {
                    healthMeta.setLore(Arrays.asList(ChatColor.RED + "Coming soon!"));
                } else {
                    healthMeta.setLore(null);
                }
                healthMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                health.setItemMeta(healthMeta);

                ItemStack haste = Recipes.hasteDisplay.clone();
                ItemMeta hasteMeta = haste.getItemMeta();
                hasteMeta.setDisplayName(ChatColor.GREEN + "Haste Tiers");
                if (!getInstance().isPotionEnabled("haste")) {
                    hasteMeta.setLore(Arrays.asList(ChatColor.RED + "Coming soon!"));
                } else {
                    hasteMeta.setLore(null);
                }
                hasteMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                haste.setItemMeta(hasteMeta);

                ItemStack fireResistance = Recipes.fireResDisplay.clone();
                ItemMeta fireResMeta = fireResistance.getItemMeta();
                fireResMeta.setDisplayName(ChatColor.GREEN + "Fire Resistance Tiers");
                if (!getInstance().isPotionEnabled("fire resistance")) {
                    fireResMeta.setLore(Arrays.asList(ChatColor.RED + "Coming soon!"));
                } else {
                    fireResMeta.setLore(null);
                }
                fireResMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                fireResistance.setItemMeta(fireResMeta);

                inventory.setItem(10, invisibility);
                inventory.setItem(11, speed);
                inventory.setItem(12, strength);
                inventory.setItem(13, TiersButton);
                inventory.setItem(14, health);
                inventory.setItem(15, haste);
                inventory.setItem(16, fireResistance);
                player.openInventory(inventory);
                player.setMetadata("openedmenu", new FixedMetadataValue(getInstance(), "research tree"));
                return false;
            }
        }

        public static class PotionMenus {
            private static void openGenericPotionMenu(Player player, String type, ItemStack baseIcon, String title) {
                Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.BLACK + "" + ChatColor.BOLD + title);

                int current = getTier(player, type);
                int global = getTier(player, "global");
                boolean isEnabled = getInstance().isPotionEnabled(type);

                ItemStack lockedGlass = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                ItemStack unlockingGlass = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
                ItemStack unlockedGlass = new ItemStack(Material.LIME_STAINED_GLASS_PANE);

                // Background
                Material bgMaterial = Material.RED_STAINED_GLASS_PANE;
                if (current >= 2)
                    bgMaterial = Material.LIME_STAINED_GLASS_PANE;

                for (int i = 0; i < 9; i++)
                    inventory.setItem(i, new ItemStack(bgMaterial));
                for (int i = 18; i < 27; i++)
                    inventory.setItem(i, new ItemStack(bgMaterial));

                if (current == 1) {
                    for (int i = 0; i < 4; i++)
                        inventory.setItem(i, unlockedGlass);
                    inventory.setItem(4, unlockingGlass);
                    for (int i = 18; i < 22; i++)
                        inventory.setItem(i, unlockedGlass);
                    inventory.setItem(22, unlockingGlass);
                }

                if (current == 2) {
                    // Top row
                    for (int i = 0; i < 7; i++)
                        inventory.setItem(i, unlockedGlass);
                    inventory.setItem(7, unlockingGlass);
                    inventory.setItem(8, unlockingGlass);
                    // Bottom row
                    for (int i = 18; i < 25; i++)
                        inventory.setItem(i, unlockedGlass);
                    inventory.setItem(25, unlockingGlass);
                    inventory.setItem(26, unlockingGlass);
                }

                if (current == 3) {
                    for (int i = 0; i < 9; i++)
                        inventory.setItem(i, unlockedGlass);
                    for (int i = 18; i < 27; i++)
                        inventory.setItem(i, unlockedGlass);
                }

                String displayType = type.substring(0, 1).toUpperCase() + type.substring(1);

                // Tier 1 (Slot 10)
                ItemStack icon1 = baseIcon.clone();
                ItemMeta meta1 = icon1.getItemMeta();
                if (!isEnabled) {
                    meta1.setDisplayName(ChatColor.RED + displayType + " Tier 1");
                    meta1.setLore(Arrays.asList(ChatColor.RED + "Coming soon!"));
                } else if (current >= 1) {
                    meta1.setDisplayName(ChatColor.GREEN + displayType + " Tier 1");
                    meta1.setLore(Arrays.asList(ChatColor.GRAY + "Unlocked"));
                } else if (global >= 1 && canResearch(player, type, 1)) {
                    meta1.setDisplayName(ChatColor.YELLOW + displayType + " Tier 1");
                    meta1.setLore(Arrays.asList(ChatColor.YELLOW + "Click to unlock!",
                            ChatColor.GRAY + "Cost: " + getRequiredXP(type, 1) + " levels"));
                } else {
                    meta1.setDisplayName(ChatColor.RED + displayType + " Tier 1");
                    String req = global < 1 ? "Requires Global Tier 1"
                            : "Requires " + getRequiredMinutes(type, 1) + "m playtime & " + getRequiredXP(type, 1)
                                    + " levels";
                    meta1.setLore(Arrays.asList(ChatColor.RED + "LOCKED", ChatColor.GRAY + req));
                }
                meta1.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                icon1.setItemMeta(meta1);
                inventory.setItem(10, icon1);

                // Tier 2 (Slot 13)
                ItemStack icon2 = baseIcon.clone();
                ItemMeta meta2 = icon2.getItemMeta();
                if (!isEnabled) {
                    meta2.setDisplayName(ChatColor.RED + displayType + " Tier 2");
                    meta2.setLore(Arrays.asList(ChatColor.RED + "Coming soon!"));
                } else if (current >= 2) {
                    meta2.setDisplayName(ChatColor.GREEN + displayType + " Tier 2");
                    meta2.setLore(Arrays.asList(ChatColor.GRAY + "Unlocked"));
                } else if (current == 1 && global >= 2 && canResearch(player, type, 2)) {
                    meta2.setDisplayName(ChatColor.YELLOW + displayType + " Tier 2");
                    meta2.setLore(Arrays.asList(ChatColor.YELLOW + "Click to unlock!",
                            ChatColor.GRAY + "Cost: " + getRequiredXP(type, 2) + " levels"));
                } else {
                    meta2.setDisplayName(ChatColor.RED + displayType + " Tier 2");
                    String req = current < 1 ? "Requires Tier 1"
                            : (global < 2 ? "Requires Global Tier 2"
                                    : "Requires " + getRequiredMinutes(type, 2) + "m playtime & "
                                            + getRequiredXP(type, 2) + " levels");
                    meta2.setLore(Arrays.asList(ChatColor.RED + "LOCKED", ChatColor.GRAY + req));
                }
                meta2.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                icon2.setItemMeta(meta2);
                inventory.setItem(13, icon2);

                // Tier 3 (Slot 16)
                ItemStack icon3 = baseIcon.clone();
                ItemMeta meta3 = icon3.getItemMeta();
                if (!isEnabled) {
                    meta3.setDisplayName(ChatColor.RED + displayType + " Tier 3");
                    meta3.setLore(Arrays.asList(ChatColor.RED + "Coming soon!"));
                } else if (current >= 3) {
                    meta3.setDisplayName(ChatColor.GREEN + displayType + " Tier 3");
                    meta3.setLore(Arrays.asList(ChatColor.GRAY + "Unlocked"));
                } else if (current == 2 && global >= 3 && canResearch(player, type, 3)) {
                    meta3.setDisplayName(ChatColor.YELLOW + displayType + " Tier 3");
                    meta3.setLore(Arrays.asList(ChatColor.YELLOW + "Click to unlock!",
                            ChatColor.GRAY + "Cost: " + getRequiredXP(type, 3) + " levels"));
                } else {
                    meta3.setDisplayName(ChatColor.RED + displayType + " Tier 3");
                    String req = current < 2 ? "Requires Tier 2"
                            : (global < 3 ? "Requires Global Tier 3"
                                    : "Requires " + getRequiredMinutes(type, 3) + "m playtime & "
                                            + getRequiredXP(type, 3) + " levels");
                    meta3.setLore(Arrays.asList(ChatColor.RED + "LOCKED", ChatColor.GRAY + req));
                }
                meta3.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                icon3.setItemMeta(meta3);
                inventory.setItem(16, icon3);

                player.openInventory(inventory);
                player.setMetadata("openedmenu", new FixedMetadataValue(getInstance(), type.toLowerCase() + " menu"));
            }

            public static void openTiersMenu(Player player) {
                Inventory inventory = Bukkit.createInventory(null, 27,
                        ChatColor.BLACK + "" + ChatColor.BOLD + "Research Tiers");

                ItemStack lockedGlass = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                ItemStack unlockingGlass = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
                ItemStack unlockedGlass = new ItemStack(Material.LIME_STAINED_GLASS_PANE);

                int currentTier = getTier(player, "global");
                Material bgMaterial = Material.RED_STAINED_GLASS_PANE;
                if (currentTier >= 2)
                    bgMaterial = Material.LIME_STAINED_GLASS_PANE;

                for (int i = 0; i < 9; i++)
                    inventory.setItem(i, new ItemStack(bgMaterial));
                for (int i = 18; i < 27; i++)
                    inventory.setItem(i, new ItemStack(bgMaterial));

                if (currentTier == 1) {
                    for (int i = 0; i < 4; i++)
                        inventory.setItem(i, unlockedGlass);
                    inventory.setItem(4, unlockingGlass);
                    for (int i = 18; i < 22; i++)
                        inventory.setItem(i, unlockedGlass);
                    inventory.setItem(22, unlockingGlass);
                }

                if (currentTier == 2) {
                    for (int i = 0; i < 7; i++)
                        inventory.setItem(i, unlockedGlass);
                    inventory.setItem(7, unlockingGlass);
                    inventory.setItem(8, unlockingGlass);
                    for (int i = 18; i < 25; i++)
                        inventory.setItem(i, unlockedGlass);
                    inventory.setItem(25, unlockingGlass);
                    inventory.setItem(26, unlockingGlass);
                }

                if (currentTier == 3) {
                    for (int i = 0; i < 9; i++)
                        inventory.setItem(i, unlockedGlass);
                    for (int i = 18; i < 27; i++)
                        inventory.setItem(i, unlockedGlass);
                }

                ItemStack icon = new ItemStack(Material.HEAVY_CORE);
                ItemMeta iconMeta = icon.getItemMeta();
                // Tier 1 (Slot 10)
                if (currentTier >= 1) {
                    iconMeta.setDisplayName(ChatColor.GREEN + "Tier 1");
                    iconMeta.setLore(Arrays.asList(ChatColor.GRAY + "Unlocked"));
                } else if (canResearch(player, "global", 1)) {
                    iconMeta.setDisplayName(ChatColor.YELLOW + "Tier 1");
                    iconMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to unlock!",
                            ChatColor.GRAY + "Cost: " + getRequiredXP("global", 1) + " levels"));
                } else {
                    iconMeta.setDisplayName(ChatColor.RED + "Tier 1");
                    iconMeta.setLore(Arrays.asList(ChatColor.RED + "LOCKED",
                            ChatColor.GRAY + "Requires " + getRequiredMinutes("global", 1) + "m playtime & "
                                    + getRequiredXP("global", 1) + " levels"));
                }
                icon.setItemMeta(iconMeta);
                inventory.setItem(10, icon);

                // Tier 2 (Slot 13)
                if (currentTier >= 2) {
                    iconMeta.setDisplayName(ChatColor.GREEN + "Tier 2");
                    iconMeta.setLore(Arrays.asList(ChatColor.GRAY + "Unlocked"));
                } else if (currentTier == 1 && canResearch(player, "global", 2)) {
                    iconMeta.setDisplayName(ChatColor.YELLOW + "Tier 2");
                    iconMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to unlock!",
                            ChatColor.GRAY + "Cost: " + getRequiredXP("global", 2) + " levels"));
                } else {
                    iconMeta.setDisplayName(ChatColor.RED + "Tier 2");
                    String req = currentTier < 1 ? "Requires Tier 1"
                            : "Requires " + getRequiredMinutes("global", 2) + "m playtime & "
                                    + getRequiredXP("global", 2) + " levels";
                    iconMeta.setLore(Arrays.asList(ChatColor.RED + "LOCKED", ChatColor.GRAY + req));
                }
                icon.setItemMeta(iconMeta);
                inventory.setItem(13, icon);

                // Tier 3 (Slot 16)
                if (currentTier >= 3) {
                    iconMeta.setDisplayName(ChatColor.GREEN + "Tier 3");
                    iconMeta.setLore(Arrays.asList(ChatColor.GRAY + "Unlocked"));
                } else if (currentTier == 2 && canResearch(player, "global", 3)) {
                    iconMeta.setDisplayName(ChatColor.YELLOW + "Tier 3");
                    iconMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to unlock!",
                            ChatColor.GRAY + "Cost: " + getRequiredXP("global", 3) + " levels"));
                } else {
                    iconMeta.setDisplayName(ChatColor.RED + "Tier 3");
                    String req = currentTier < 2 ? "Requires Tier 2"
                            : "Requires " + getRequiredMinutes("global", 3) + "m playtime & "
                                    + getRequiredXP("global", 3) + " levels";
                    iconMeta.setLore(Arrays.asList(ChatColor.RED + "LOCKED", ChatColor.GRAY + req));
                }
                icon.setItemMeta(iconMeta);
                inventory.setItem(16, icon);

                player.openInventory(inventory);
                player.setMetadata("openedmenu", new FixedMetadataValue(getInstance(), "tiers"));
            }

            public static void openInvisibilityMenu(Player player) {
                openGenericPotionMenu(player, "invisibility", Recipes.invisibilityDisplay, "Invisibility research");
            }

            public static void openSpeedMenu(Player player) {
                openGenericPotionMenu(player, "speed", Recipes.speedDisplay, "Speed research");
            }

            public static void openStrengthMenu(Player player) {
                openGenericPotionMenu(player, "strength", Recipes.strengthDisplay, "Strength research");
            }

            public static void openHealthMenu(Player player) {
                openGenericPotionMenu(player, "health", Recipes.healthDisplay, "Health research");
            }

            public static void openHasteMenu(Player player) {
                openGenericPotionMenu(player, "haste", Recipes.hasteDisplay, "Haste research");
            }

            public static void openFireResistanceMenu(Player player) {
                openGenericPotionMenu(player, "fire resistance", Recipes.fireResDisplay,
                        "Fire Resistance research");
            }
        }

        public static class ResearchGui {
            public static class GuiListener implements Listener {
                @EventHandler
                public void onClick(InventoryClickEvent e) {
                    Player player = (Player) e.getWhoClicked();
                    if (player.hasMetadata("openedmenu")) {
                        e.setCancelled(true);

                        String menu = player.getMetadata("openedmenu").get(0).asString();

                        if (menu.equals("research tree")) {
                            switch (e.getSlot()) {
                                case 10:
                                    PotionMenus.openInvisibilityMenu(player);
                                    break;
                                case 11:
                                    PotionMenus.openSpeedMenu(player);
                                    break;
                                case 12:
                                    PotionMenus.openStrengthMenu(player);
                                    break;
                                case 13:
                                    PotionMenus.openTiersMenu(player);
                                    break;
                                case 14:
                                    PotionMenus.openHealthMenu(player);
                                    break;
                                case 15:
                                    PotionMenus.openHasteMenu(player);
                                    break;
                                case 16:
                                    PotionMenus.openFireResistanceMenu(player);
                                    break;
                            }
                        }

                        if (menu.equals("tiers")) {
                            int current = getTier(player, "global");
                            switch (e.getSlot()) {
                                case 10: // Tier 1
                                    if (current < 1 && canResearch(player, "global", 1)) {
                                        player.setLevel(player.getLevel() - getRequiredXP("global", 1));
                                        Files f = onJoin.getPlayerFile(player);
                                        f.set("CurrentGlobalTier", 1);
                                        f.save();
                                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                        PotionMenus.openTiersMenu(player);
                                    } else {
                                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                                    }
                                    break;
                                case 13: // Tier 2
                                    if (current == 1 && canResearch(player, "global", 2)) {
                                        player.setLevel(player.getLevel() - getRequiredXP("global", 2));
                                        Files f = onJoin.getPlayerFile(player);
                                        f.set("CurrentGlobalTier", 2);
                                        f.save();
                                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                        PotionMenus.openTiersMenu(player);
                                    } else {
                                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                                    }
                                    break;
                                case 16: // Tier 3
                                    if (current == 2 && canResearch(player, "global", 3)) {
                                        player.setLevel(player.getLevel() - getRequiredXP("global", 3));
                                        Files f = onJoin.getPlayerFile(player);
                                        f.set("CurrentGlobalTier", 3);
                                        f.save();
                                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                        PotionMenus.openTiersMenu(player);
                                    } else {
                                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                                    }
                                    break;
                            }
                        } else if (menu.endsWith(" menu")) {
                            String type = menu.replace(" menu", "");
                            if (!getInstance().isPotionEnabled(type)) {
                                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                                return;
                            }
                            int current = getTier(player, type);
                            int globalTier = getTier(player, "global");
                            int slot = e.getSlot();
                            int targetTier = 0;
                            if (slot == 10)
                                targetTier = 1;
                            else if (slot == 13)
                                targetTier = 2;
                            else if (slot == 16)
                                targetTier = 3;

                            if (targetTier > 0 && current == (targetTier - 1) && globalTier >= targetTier
                                    && canResearch(player, type, targetTier)) {
                                player.setLevel(player.getLevel() - getRequiredXP(type, targetTier));
                                Files f = onJoin.getPlayerFile(player);
                                String configPath = getTotemTierPath(player, type);

                                f.set(configPath, targetTier);
                                f.save();
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                switch (type.toLowerCase()) {
                                    case "strength":
                                        PotionMenus.openStrengthMenu(player);
                                        break;
                                    case "health":
                                        PotionMenus.openHealthMenu(player);
                                        break;
                                    case "haste":
                                        PotionMenus.openHasteMenu(player);
                                        break;
                                    case "fire resistance":
                                        PotionMenus.openFireResistanceMenu(player);
                                        break;
                                    case "speed":
                                        PotionMenus.openSpeedMenu(player);
                                        break;
                                    case "invisibility":
                                        PotionMenus.openInvisibilityMenu(player);
                                        break;
                                }
                            } else if (targetTier > 0) {
                                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                            }
                        }
                    }
                }

                @EventHandler
                public void onDrag(InventoryDragEvent e) {
                    Player player = (Player) e.getWhoClicked();
                    if (player.hasMetadata("openedmenu")) {
                        e.setCancelled(true);
                    }
                }

                @EventHandler
                public void onClose(InventoryCloseEvent e) {
                    Player player = (Player) e.getPlayer();
                    if (player.hasMetadata("openedmenu")) {
                        player.removeMetadata("openedmenu", getInstance());
                    }
                }
            }
        }
    }

    public final class Recipes {
        public static ItemStack invisibilityPotion;
        public static ItemStack speedPotion;
        public static ItemStack strengthPotion;
        public static ItemStack healthPotion;
        public static ItemStack fireResistancePotion;
        public static ItemStack hastePotion;
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
        public static ItemStack strengthDisplay;
        public static ItemStack healthDisplay;
        public static ItemStack hasteDisplay;
        public static ItemStack fireResDisplay;
        public static ItemStack speedDisplay;
        public static ItemStack invisibilityDisplay;

        public static void register() {
            // heart of the warden
            heartOfTheWarden = new ItemStack(Material.NETHER_STAR);
            ItemMeta heartOfTheWardenMeta = heartOfTheWarden.getItemMeta();
            heartOfTheWardenMeta.setDisplayName(ChatColor.DARK_PURPLE + "Heart of the Warden");
            heartOfTheWardenMeta.getPersistentDataContainer().set(keys.HEART_OF_THE_WARDEN, PersistentDataType.STRING,
                    "HeartOfTheWarden");
            heartOfTheWardenMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            heartOfTheWardenMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            heartOfTheWarden.setItemMeta(heartOfTheWardenMeta);

            // POTIONS

            // invisibility
            invisibilityPotion = new ItemStack(Material.POTION, 1);
            invisibilityDisplay = new ItemStack(Material.POTION, 1);
            PotionMeta invisibilityMeta = (PotionMeta) invisibilityDisplay.getItemMeta();
            invisibilityMeta.setLore(Arrays.asList(ChatColor.RED + "Comming soon!"));
            invisibilityMeta.setBasePotionType(PotionType.INVISIBILITY);
            invisibilityMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            invisibilityDisplay.setItemMeta(invisibilityMeta);

            // speed
            speedPotion = new ItemStack(Material.POTION, 1);
            speedDisplay = new ItemStack(Material.POTION, 1);
            PotionMeta speedMeta = (PotionMeta) speedDisplay.getItemMeta();
            speedMeta.setLore(Arrays.asList(ChatColor.RED + "Comming soon!"));
            speedMeta.setBasePotionType(PotionType.SWIFTNESS);
            speedMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            speedDisplay.setItemMeta(speedMeta);

            // haste
            hastePotion = new ItemStack(Material.POTION, 1);
            PotionMeta hasteMeta = (PotionMeta) hastePotion.getItemMeta();
            hasteMeta.setBasePotionType(PotionType.LEAPING);
            hasteMeta.setDisplayName("Haste Potion");
            hastePotion.setItemMeta(hasteMeta);

            hasteDisplay = hastePotion.clone();
            ItemMeta hasteDisplayMeta = hasteDisplay.getItemMeta();
            hasteDisplayMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            hasteDisplay.setItemMeta(hasteDisplayMeta);

            // health
            healthPotion = new ItemStack(Material.POTION, 1);
            PotionMeta healthReqMeta = (PotionMeta) healthPotion.getItemMeta();
            healthReqMeta.setBasePotionType(PotionType.HEALING);
            healthPotion.setItemMeta(healthReqMeta);

            healthDisplay = healthPotion.clone();
            ItemMeta healthDisplayMeta = healthDisplay.getItemMeta();
            healthDisplayMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            healthDisplay.setItemMeta(healthDisplayMeta);

            // strength
            strengthPotion = new ItemStack(Material.POTION, 1);
            PotionMeta strengthReqMeta = (PotionMeta) strengthPotion.getItemMeta();
            strengthReqMeta.setBasePotionType(PotionType.STRENGTH);
            strengthPotion.setItemMeta(strengthReqMeta);

            strengthDisplay = strengthPotion.clone();
            ItemMeta strengthDisplayMeta = strengthDisplay.getItemMeta();
            strengthDisplayMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            strengthDisplay.setItemMeta(strengthDisplayMeta);

            // fire resistance
            fireResistancePotion = new ItemStack(Material.POTION, 1);
            PotionMeta fireReqMeta = (PotionMeta) fireResistancePotion.getItemMeta();
            fireReqMeta.setBasePotionType(PotionType.FIRE_RESISTANCE);
            fireResistancePotion.setItemMeta(fireReqMeta);

            fireResDisplay = fireResistancePotion.clone();
            ItemMeta fireDisplayMeta = fireResDisplay.getItemMeta();
            fireDisplayMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            fireResDisplay.setItemMeta(fireDisplayMeta);

            // TOTEMS T1

            StrenghtTotem = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta StrenghtTotemMeta = StrenghtTotem.getItemMeta();
            StrenghtTotemMeta.setDisplayName(getInstance().getTotemColor("strength") + "Strenght Totem");
            StrenghtTotemMeta.getPersistentDataContainer().set(keys.STRENGHT_TOTEM, PersistentDataType.STRING,
                    "StrenghtTotem");
            StrenghtTotemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            StrenghtTotemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            StrenghtTotem.setItemMeta(StrenghtTotemMeta);
            HealthTotem = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta HealthTotemMeta = HealthTotem.getItemMeta();
            HealthTotemMeta.setDisplayName(getInstance().getTotemColor("health") + "Health Totem");
            HealthTotemMeta.getPersistentDataContainer().set(keys.HEALTH_TOTEM, PersistentDataType.STRING,
                    "HealthTotem");
            HealthTotemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            HealthTotemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            HealthTotem.setItemMeta(HealthTotemMeta);
            FireResistanceTotem = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta FireResistanceTotemMeta = FireResistanceTotem.getItemMeta();
            FireResistanceTotemMeta
                    .setDisplayName(getInstance().getTotemColor("fire resistance") + "Fire Resistance Totem");
            FireResistanceTotemMeta.getPersistentDataContainer().set(keys.FIRE_RESISTANCE_TOTEM,
                    PersistentDataType.STRING, "FireResistanceTotem");
            FireResistanceTotemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            FireResistanceTotemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            FireResistanceTotem.setItemMeta(FireResistanceTotemMeta);
            HasteTotem = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta HasteTotemMeta = HasteTotem.getItemMeta();
            HasteTotemMeta.setDisplayName(getInstance().getTotemColor("haste") + "Haste Totem");
            HasteTotemMeta.getPersistentDataContainer().set(keys.HASTE_TOTEM, PersistentDataType.STRING, "HasteTotem");
            HasteTotemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            HasteTotemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            HasteTotem.setItemMeta(HasteTotemMeta);
            // TIER 2
            StrenghtTotemT2 = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta StrenghtTotemT2Meta = StrenghtTotemT2.getItemMeta();
            StrenghtTotemT2Meta.setDisplayName(getInstance().getTotemColor("strength") + "Strenght Totem T2");
            StrenghtTotemT2Meta.getPersistentDataContainer().set(keys.STRENGTH_TOTEM_TIER2, PersistentDataType.STRING,
                    "StrenghtTotemT2");
            StrenghtTotemT2Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            StrenghtTotemT2Meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            StrenghtTotemT2.setItemMeta(StrenghtTotemT2Meta);

            HealthTotemT2 = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta HealthTotemT2Meta = HealthTotemT2.getItemMeta();
            HealthTotemT2Meta.setDisplayName(getInstance().getTotemColor("health") + "Health Totem T2");
            HealthTotemT2Meta.getPersistentDataContainer().set(keys.HEALTH_TOTEM_TIER2, PersistentDataType.STRING,
                    "HealthTotemT2");
            HealthTotemT2Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            HealthTotemT2Meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            HealthTotemT2.setItemMeta(HealthTotemT2Meta);

            FireResistanceTotemT2 = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta FireResistanceTotemT2Meta = FireResistanceTotemT2.getItemMeta();
            FireResistanceTotemT2Meta
                    .setDisplayName(getInstance().getTotemColor("fire resistance") + "Fire Resistance Totem T2");
            FireResistanceTotemT2Meta.getPersistentDataContainer().set(keys.FIRE_RESISTANCE_TOTEM_TIER2,
                    PersistentDataType.STRING, "FireResistanceTotemT2");
            FireResistanceTotemT2Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            FireResistanceTotemT2Meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            FireResistanceTotemT2.setItemMeta(FireResistanceTotemT2Meta);

            HasteTotemT2 = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta HasteTotemT2Meta = HasteTotemT2.getItemMeta();
            HasteTotemT2Meta.setDisplayName(getInstance().getTotemColor("haste") + "Haste Totem T2");
            HasteTotemT2Meta.getPersistentDataContainer().set(keys.HASTE_TOTEM_TIER2, PersistentDataType.STRING,
                    "HasteTotemT2");
            HasteTotemT2Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            HasteTotemT2Meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            HasteTotemT2.setItemMeta(HasteTotemT2Meta);

            StrenghtTotemT3 = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta StrenghtTotemT3Meta = StrenghtTotemT3.getItemMeta();
            StrenghtTotemT3Meta.setDisplayName(getInstance().getTotemColor("strength") + "Strenght Totem T3");
            StrenghtTotemT3Meta.getPersistentDataContainer().set(keys.STRENGTH_TOTEM_TIER3, PersistentDataType.STRING,
                    "StrenghtTotemT3");
            StrenghtTotemT3Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            StrenghtTotemT3Meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            StrenghtTotemT3.setItemMeta(StrenghtTotemT3Meta);

            HealthTotemT3 = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta HealthTotemT3Meta = HealthTotemT3.getItemMeta();
            HealthTotemT3Meta.setDisplayName(getInstance().getTotemColor("health") + "Health Totem T3");
            HealthTotemT3Meta.getPersistentDataContainer().set(keys.HEALTH_TOTEM_TIER3, PersistentDataType.STRING,
                    "HealthTotemT3");
            HealthTotemT3Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            HealthTotemT3Meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            HealthTotemT3.setItemMeta(HealthTotemT3Meta);

            FireResistanceTotemT3 = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta FireResistanceTotemT3Meta = FireResistanceTotemT3.getItemMeta();
            FireResistanceTotemT3Meta
                    .setDisplayName(getInstance().getTotemColor("fire resistance") + "Fire Resistance Totem T3");
            FireResistanceTotemT3Meta.getPersistentDataContainer().set(keys.FIRE_RESISTANCE_TOTEM_TIER3,
                    PersistentDataType.STRING, "FireResistanceTotemT3");
            FireResistanceTotemT3Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            FireResistanceTotemT3Meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            FireResistanceTotemT3.setItemMeta(FireResistanceTotemT3Meta);

            HasteTotemT3 = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta HasteTotemT3Meta = HasteTotemT3.getItemMeta();
            HasteTotemT3Meta.setDisplayName(getInstance().getTotemColor("haste") + "Haste Totem T3");
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
            HealthTotemT2Recipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
            HealthTotemT2Recipe.setIngredient('N', Material.NETHERITE_INGOT);
            HealthTotemT2Recipe.setIngredient('P', new RecipeChoice.ExactChoice(healthPotion));

            StrenghtTotemT2Recipe.shape(" P ",
                    "NTN",
                    " P ");
            StrenghtTotemT2Recipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
            StrenghtTotemT2Recipe.setIngredient('N', Material.NETHERITE_INGOT);
            StrenghtTotemT2Recipe.setIngredient('P', new RecipeChoice.ExactChoice(strengthPotion));

            FireResistanceTotemT2Recipe.shape(" P ",
                    "NTN",
                    " P ");
            FireResistanceTotemT2Recipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
            FireResistanceTotemT2Recipe.setIngredient('N', Material.NETHERITE_INGOT);
            FireResistanceTotemT2Recipe.setIngredient('P', new RecipeChoice.ExactChoice(fireResistancePotion));

            HasteTotemT2Recipe.shape(" P ",
                    "NTN",
                    " P ");
            HasteTotemT2Recipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
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
            HealthTotemT3Recipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
            HealthTotemT3Recipe.setIngredient('N', Material.NETHERITE_INGOT);
            HealthTotemT3Recipe.setIngredient('S', new RecipeChoice.ExactChoice(heartOfTheWarden));
            HealthTotemT3Recipe.setIngredient('P', Material.POTION);

            StrenghtTotemT3Recipe.shape(" S ",
                    "NTN",
                    " H ");
            StrenghtTotemT3Recipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
            StrenghtTotemT3Recipe.setIngredient('N', Material.NETHERITE_INGOT);
            StrenghtTotemT3Recipe.setIngredient('S', new RecipeChoice.ExactChoice(heartOfTheWarden));
            StrenghtTotemT3Recipe.setIngredient('H', new RecipeChoice.ExactChoice(heartOfTheWarden));

            FireResistanceTotemT3Recipe.shape(" S ",
                    "NTN",
                    " M ");
            FireResistanceTotemT3Recipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
            FireResistanceTotemT3Recipe.setIngredient('N', Material.NETHERITE_INGOT);
            FireResistanceTotemT3Recipe.setIngredient('S', new RecipeChoice.ExactChoice(heartOfTheWarden));
            FireResistanceTotemT3Recipe.setIngredient('M', Material.MAGMA_CREAM);

            HasteTotemT3Recipe.shape(" S ",
                    "NTN",
                    " G ");
            HasteTotemT3Recipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
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

    public class BypassTime implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player))
                return false;
            Player player = (Player) sender;
            if (!player.hasPermission("playertotems.bypasstime")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }
            UUID uuid = player.getUniqueId();
            if (Research.bypassPlayers.contains(uuid)) {
                Research.bypassPlayers.remove(uuid);
                player.sendMessage(ChatColor.YELLOW + "Time bypass " + ChatColor.RED + "DISABLED");
            } else {
                Research.bypassPlayers.add(uuid);
                player.sendMessage(ChatColor.YELLOW + "Time bypass " + ChatColor.GREEN + "ENABLED");
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
            Player player = (Player) e.getViewers().get(0);

            // Workaround for Spigot's shape recipe matcher prioritizing identical base
            // materials incorrectly
            ItemStack[] matrix = e.getInventory().getMatrix();
            boolean hasT1Strength = false, hasT1Health = false, hasT1Fire = false, hasT1Haste = false;
            boolean hasT2Strength = false, hasT2Health = false, hasT2Fire = false, hasT2Haste = false;
            boolean hasStrengthPotion = false, hasHealingPotion = false, hasFirePotion = false;
            boolean hasGoldBlock = false, hasGoldPickaxe = false, hasMagmaCream = false;

            for (ItemStack item : matrix) {
                if (item == null)
                    continue;
                if (item.getType() == Material.TOTEM_OF_UNDYING && item.hasItemMeta()) {
                    PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
                    if (pdc.has(keys.STRENGHT_TOTEM, PersistentDataType.STRING))
                        hasT1Strength = true;
                    else if (pdc.has(keys.HEALTH_TOTEM, PersistentDataType.STRING))
                        hasT1Health = true;
                    else if (pdc.has(keys.FIRE_RESISTANCE_TOTEM, PersistentDataType.STRING))
                        hasT1Fire = true;
                    else if (pdc.has(keys.HASTE_TOTEM, PersistentDataType.STRING))
                        hasT1Haste = true;
                    else if (pdc.has(keys.STRENGTH_TOTEM_TIER2, PersistentDataType.STRING))
                        hasT2Strength = true;
                    else if (pdc.has(keys.HEALTH_TOTEM_TIER2, PersistentDataType.STRING))
                        hasT2Health = true;
                    else if (pdc.has(keys.FIRE_RESISTANCE_TOTEM_TIER2, PersistentDataType.STRING))
                        hasT2Fire = true;
                    else if (pdc.has(keys.HASTE_TOTEM_TIER2, PersistentDataType.STRING))
                        hasT2Haste = true;
                }
                if (item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION
                        || item.getType() == Material.LINGERING_POTION) {
                    if (item.hasItemMeta() && item.getItemMeta() instanceof PotionMeta) {
                        PotionType type = ((PotionMeta) item.getItemMeta()).getBasePotionType();
                        if (type == PotionType.STRENGTH)
                            hasStrengthPotion = true;
                        else if (type == PotionType.HEALING)
                            hasHealingPotion = true;
                        else if (type == PotionType.FIRE_RESISTANCE)
                            hasFirePotion = true;
                    }
                }
                if (item.getType() == Material.GOLD_BLOCK)
                    hasGoldBlock = true;
                if (item.getType() == Material.GOLDEN_PICKAXE)
                    hasGoldPickaxe = true;
                if (item.getType() == Material.MAGMA_CREAM)
                    hasMagmaCream = true;
            }

            boolean isT1 = resultMeta.getPersistentDataContainer().has(keys.STRENGHT_TOTEM, PersistentDataType.STRING)
                    ||
                    resultMeta.getPersistentDataContainer().has(keys.HEALTH_TOTEM, PersistentDataType.STRING) ||
                    resultMeta.getPersistentDataContainer().has(keys.FIRE_RESISTANCE_TOTEM, PersistentDataType.STRING)
                    ||
                    resultMeta.getPersistentDataContainer().has(keys.HASTE_TOTEM, PersistentDataType.STRING);

            boolean isT2 = resultMeta.getPersistentDataContainer().has(keys.STRENGTH_TOTEM_TIER2,
                    PersistentDataType.STRING) ||
                    resultMeta.getPersistentDataContainer().has(keys.HEALTH_TOTEM_TIER2, PersistentDataType.STRING) ||
                    resultMeta.getPersistentDataContainer().has(keys.FIRE_RESISTANCE_TOTEM_TIER2,
                            PersistentDataType.STRING)
                    ||
                    resultMeta.getPersistentDataContainer().has(keys.HASTE_TOTEM_TIER2, PersistentDataType.STRING);

            boolean isT3 = resultMeta.getPersistentDataContainer().has(keys.STRENGTH_TOTEM_TIER3,
                    PersistentDataType.STRING) ||
                    resultMeta.getPersistentDataContainer().has(keys.HEALTH_TOTEM_TIER3, PersistentDataType.STRING) ||
                    resultMeta.getPersistentDataContainer().has(keys.FIRE_RESISTANCE_TOTEM_TIER3,
                            PersistentDataType.STRING)
                    ||
                    resultMeta.getPersistentDataContainer().has(keys.HASTE_TOTEM_TIER3, PersistentDataType.STRING);

            if (isT1) {
                if (hasStrengthPotion)
                    result = Recipes.StrenghtTotem.clone();
                else if (hasHealingPotion)
                    result = Recipes.HealthTotem.clone();
                else if (hasFirePotion)
                    result = Recipes.FireResistanceTotem.clone();
                else if (hasGoldPickaxe)
                    result = Recipes.HasteTotem.clone();
                resultMeta = result.getItemMeta();
                e.getInventory().setResult(result);
            } else if (isT2) {
                if (hasT1Strength || hasStrengthPotion)
                    result = Recipes.StrenghtTotemT2.clone();
                else if (hasT1Health || hasHealingPotion)
                    result = Recipes.HealthTotemT2.clone();
                else if (hasT1Fire || hasFirePotion)
                    result = Recipes.FireResistanceTotemT2.clone();
                else if (hasT1Haste || hasGoldBlock)
                    result = Recipes.HasteTotemT2.clone();
                resultMeta = result.getItemMeta();
                e.getInventory().setResult(result);
            } else if (isT3) {
                if (hasT2Strength || (hasT2Strength && hasStrengthPotion))
                    result = Recipes.StrenghtTotemT3.clone();
                else if (hasT2Health || (hasT2Health && hasHealingPotion))
                    result = Recipes.HealthTotemT3.clone();
                else if (hasT2Fire || hasMagmaCream)
                    result = Recipes.FireResistanceTotemT3.clone();
                else if (hasT2Haste || hasGoldBlock)
                    result = Recipes.HasteTotemT3.clone();
                resultMeta = result.getItemMeta();
                e.getInventory().setResult(result);
            }

            String researchType = "";
            int requiredTier = 0;

            if (resultMeta.getPersistentDataContainer().has(keys.STRENGHT_TOTEM, PersistentDataType.STRING)) {
                researchType = "strength";
                requiredTier = 1;
            } else if (resultMeta.getPersistentDataContainer().has(keys.HEALTH_TOTEM, PersistentDataType.STRING)) {
                researchType = "health";
                requiredTier = 1;
            } else if (resultMeta.getPersistentDataContainer().has(keys.FIRE_RESISTANCE_TOTEM,
                    PersistentDataType.STRING)) {
                researchType = "fire resistance";
                requiredTier = 1;
            } else if (resultMeta.getPersistentDataContainer().has(keys.HASTE_TOTEM, PersistentDataType.STRING)) {
                researchType = "haste";
                requiredTier = 1;
            } else if (resultMeta.getPersistentDataContainer().has(keys.STRENGTH_TOTEM_TIER2,
                    PersistentDataType.STRING)) {
                researchType = "strength";
                requiredTier = 2;
            } else if (resultMeta.getPersistentDataContainer().has(keys.HEALTH_TOTEM_TIER2,
                    PersistentDataType.STRING)) {
                researchType = "health";
                requiredTier = 2;
            } else if (resultMeta.getPersistentDataContainer().has(keys.FIRE_RESISTANCE_TOTEM_TIER2,
                    PersistentDataType.STRING)) {
                researchType = "fire resistance";
                requiredTier = 2;
            } else if (resultMeta.getPersistentDataContainer().has(keys.HASTE_TOTEM_TIER2, PersistentDataType.STRING)) {
                researchType = "haste";
                requiredTier = 2;
            } else if (resultMeta.getPersistentDataContainer().has(keys.STRENGTH_TOTEM_TIER3,
                    PersistentDataType.STRING)) {
                researchType = "strength";
                requiredTier = 3;
            } else if (resultMeta.getPersistentDataContainer().has(keys.HEALTH_TOTEM_TIER3,
                    PersistentDataType.STRING)) {
                researchType = "health";
                requiredTier = 3;
            } else if (resultMeta.getPersistentDataContainer().has(keys.FIRE_RESISTANCE_TOTEM_TIER3,
                    PersistentDataType.STRING)) {
                researchType = "fire resistance";
                requiredTier = 3;
            } else if (resultMeta.getPersistentDataContainer().has(keys.HASTE_TOTEM_TIER3, PersistentDataType.STRING)) {
                researchType = "haste";
                requiredTier = 3;
            }
            NamespacedKey ingredientKeyToMatch = null;
            PotionType requiredPotionType = null;

            if (requiredTier == 2) {
                switch (researchType) {
                    case "strength":
                        ingredientKeyToMatch = keys.STRENGHT_TOTEM;
                        requiredPotionType = PotionType.STRENGTH;
                        break;
                    case "health":
                        ingredientKeyToMatch = keys.HEALTH_TOTEM;
                        requiredPotionType = PotionType.HEALING;
                        break;
                    case "fire resistance":
                        ingredientKeyToMatch = keys.FIRE_RESISTANCE_TOTEM;
                        requiredPotionType = PotionType.FIRE_RESISTANCE;
                        break;
                    case "haste":
                        ingredientKeyToMatch = keys.HASTE_TOTEM;
                        break;
                }
            } else if (requiredTier == 3) {
                switch (researchType) {
                    case "strength":
                        ingredientKeyToMatch = keys.STRENGTH_TOTEM_TIER2;
                        break;
                    case "health":
                        ingredientKeyToMatch = keys.HEALTH_TOTEM_TIER2;
                        requiredPotionType = PotionType.HEALING;
                        break;
                    case "fire resistance":
                        ingredientKeyToMatch = keys.FIRE_RESISTANCE_TOTEM_TIER2;
                        break;
                    case "haste":
                        ingredientKeyToMatch = keys.HASTE_TOTEM_TIER2;
                        break;
                }
            }
            if (ingredientKeyToMatch != null) {
                boolean hasCorrectTotem = false;
                boolean hasCorrectPotion = true;
                int potionsFound = 0;
                String inheritedName = null;

                for (ItemStack item : e.getInventory().getMatrix()) {
                    if (item == null)
                        continue;

                    if (item.getType() == Material.TOTEM_OF_UNDYING) {
                        if (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer()
                                .has(ingredientKeyToMatch, PersistentDataType.STRING)) {
                            hasCorrectTotem = true;
                            inheritedName = item.getItemMeta().getPersistentDataContainer().get(keys.PLAYER_HEAD,
                                    PersistentDataType.STRING);
                        }
                    } else if ((item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION
                            || item.getType() == Material.LINGERING_POTION) && requiredPotionType != null) {
                        potionsFound++;
                        PotionMeta pMeta = (PotionMeta) item.getItemMeta();
                        PotionType type = pMeta.getBasePotionType();

                        if (type != requiredPotionType && !type.name().contains(requiredPotionType.name())) {
                            hasCorrectPotion = false;
                        }
                    }
                }

                if (requiredPotionType != null && potionsFound == 0)
                    hasCorrectPotion = false;

                if (!hasCorrectTotem) {
                    player.sendMessage(ChatColor.RED + "[Debug] Missing required " + researchType.toUpperCase()
                            + " totem from previous tier!");
                    e.getInventory().setResult(null);
                    return;
                }
                if (!hasCorrectPotion) {
                    player.sendMessage(ChatColor.RED + "[Debug] Invalid or missing potion type! Needs: "
                            + requiredPotionType.name());
                    e.getInventory().setResult(null);
                    return;
                }
                if (inheritedName != null) {
                    resultMeta.getPersistentDataContainer().set(keys.PLAYER_HEAD, PersistentDataType.STRING,
                            inheritedName);
                }
            }
            if (requiredTier > 0 && Research.getTier(player, researchType) < requiredTier) {
                player.sendMessage(ChatColor.RED + "[Debug] You haven't researched how to craft this tier yet!");
                e.getInventory().setResult(null);
                return;
            }

            String baseName = "";
            String tierSuffix = "";
            if (requiredTier == 2)
                tierSuffix = " T2";
            else if (requiredTier == 3)
                tierSuffix = " T3";

            if (resultMeta.getPersistentDataContainer().has(keys.HEALTH_TOTEM, PersistentDataType.STRING) ||
                    resultMeta.getPersistentDataContainer().has(keys.HEALTH_TOTEM_TIER2, PersistentDataType.STRING) ||
                    resultMeta.getPersistentDataContainer().has(keys.HEALTH_TOTEM_TIER3, PersistentDataType.STRING)) {
                baseName = "Health Totem" + tierSuffix;
            } else if (resultMeta.getPersistentDataContainer().has(keys.STRENGHT_TOTEM, PersistentDataType.STRING) ||
                    resultMeta.getPersistentDataContainer().has(keys.STRENGTH_TOTEM_TIER2, PersistentDataType.STRING) ||
                    resultMeta.getPersistentDataContainer().has(keys.STRENGTH_TOTEM_TIER3, PersistentDataType.STRING)) {
                baseName = "Strenght Totem" + tierSuffix;
            } else if (resultMeta.getPersistentDataContainer().has(keys.FIRE_RESISTANCE_TOTEM,
                    PersistentDataType.STRING) ||
                    resultMeta.getPersistentDataContainer().has(keys.FIRE_RESISTANCE_TOTEM_TIER2,
                            PersistentDataType.STRING)
                    ||
                    resultMeta.getPersistentDataContainer().has(keys.FIRE_RESISTANCE_TOTEM_TIER3,
                            PersistentDataType.STRING)) {
                baseName = "Fire Resistance Totem" + tierSuffix;
            } else if (resultMeta.getPersistentDataContainer().has(keys.HASTE_TOTEM, PersistentDataType.STRING) ||
                    resultMeta.getPersistentDataContainer().has(keys.HASTE_TOTEM_TIER2, PersistentDataType.STRING) ||
                    resultMeta.getPersistentDataContainer().has(keys.HASTE_TOTEM_TIER3, PersistentDataType.STRING)) {
                baseName = "Haste Totem" + tierSuffix;
            }

            if (!baseName.isEmpty()) {
                String finalPlayerName = resultMeta.getPersistentDataContainer().get(keys.PLAYER_HEAD,
                        PersistentDataType.STRING);

                for (ItemStack item : e.getInventory().getMatrix()) {
                    if (item != null && item.getType() == Material.PLAYER_HEAD && item.hasItemMeta()) {
                        ItemMeta headMeta = item.getItemMeta();
                        String headOwner = headMeta.getPersistentDataContainer().get(keys.PLAYER_HEAD,
                                PersistentDataType.STRING);

                        if (headOwner == null && headMeta instanceof SkullMeta) {
                            SkullMeta skull = (SkullMeta) headMeta;
                            if (skull.getOwningPlayer() != null) {
                                headOwner = skull.getOwningPlayer().getName();
                            }
                        }

                        if (headOwner != null) {
                            finalPlayerName = headOwner;
                            break;
                        }
                    }
                }

                if (finalPlayerName != null) {
                    ChatColor color = getInstance()
                            .getTotemColor(researchType.isEmpty() ? baseName : researchType);
                    resultMeta.setDisplayName(ChatColor.YELLOW + finalPlayerName + "'s " + color + baseName);
                    resultMeta.getPersistentDataContainer().set(keys.PLAYER_HEAD, PersistentDataType.STRING,
                            finalPlayerName);
                    result.setItemMeta(resultMeta);
                    e.getInventory().setResult(result);
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

    private static boolean isSpecialItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta())
            return false;
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(keys.PLAYER_HEAD, PersistentDataType.STRING) ||
                meta.getPersistentDataContainer().has(keys.HEALTH_TOTEM, PersistentDataType.STRING) ||
                meta.getPersistentDataContainer().has(keys.STRENGHT_TOTEM, PersistentDataType.STRING) ||
                meta.getPersistentDataContainer().has(keys.FIRE_RESISTANCE_TOTEM, PersistentDataType.STRING) ||
                meta.getPersistentDataContainer().has(keys.HASTE_TOTEM, PersistentDataType.STRING)
                || meta.getPersistentDataContainer().has(keys.HEALTH_TOTEM_TIER2, PersistentDataType.STRING)
                || meta.getPersistentDataContainer().has(keys.STRENGTH_TOTEM_TIER2, PersistentDataType.STRING)
                || meta.getPersistentDataContainer().has(keys.FIRE_RESISTANCE_TOTEM_TIER2, PersistentDataType.STRING)
                || meta.getPersistentDataContainer().has(keys.HASTE_TOTEM_TIER2, PersistentDataType.STRING)
                || meta.getPersistentDataContainer().has(keys.HEALTH_TOTEM_TIER3, PersistentDataType.STRING)
                || meta.getPersistentDataContainer().has(keys.STRENGTH_TOTEM_TIER3, PersistentDataType.STRING)
                || meta.getPersistentDataContainer().has(keys.FIRE_RESISTANCE_TOTEM_TIER3, PersistentDataType.STRING)
                || meta.getPersistentDataContainer().has(keys.HASTE_TOTEM_TIER3, PersistentDataType.STRING);
    }

    public static class OnItemUse implements Listener {
        @EventHandler
        public void onUse(PlayerInteractEvent e) {
            if (e.getItem() == null)
                return;
            ItemStack item = e.getItem();
            ItemMeta meta = item.getItemMeta();
            Player player = e.getPlayer();
            if (meta == null)
                return;
            if (meta.getPersistentDataContainer().has(keys.HEALTH_TOTEM, PersistentDataType.STRING)) {
                e.setCancelled(true);
            } else if (meta.getPersistentDataContainer().has(keys.STRENGHT_TOTEM, PersistentDataType.STRING)) {
                e.setCancelled(true);
            } else if (meta.getPersistentDataContainer().has(keys.FIRE_RESISTANCE_TOTEM, PersistentDataType.STRING)) {
                e.setCancelled(true);
            } else if (meta.getPersistentDataContainer().has(keys.HASTE_TOTEM, PersistentDataType.STRING)) {
                e.setCancelled(true);
            } else if (meta.getPersistentDataContainer().has(keys.HEALTH_TOTEM_TIER2, PersistentDataType.STRING)) {

            } else if (meta.getPersistentDataContainer().has(keys.STRENGTH_TOTEM_TIER2, PersistentDataType.STRING)) {

            } else if (meta.getPersistentDataContainer().has(keys.FIRE_RESISTANCE_TOTEM_TIER2,
                    PersistentDataType.STRING)) {

            } else if (meta.getPersistentDataContainer().has(keys.HASTE_TOTEM_TIER2, PersistentDataType.STRING)) {

            } else if (meta.getPersistentDataContainer().has(keys.HEALTH_TOTEM_TIER3, PersistentDataType.STRING)) {

            } else if (meta.getPersistentDataContainer().has(keys.STRENGTH_TOTEM_TIER3, PersistentDataType.STRING)) {

            } else if (meta.getPersistentDataContainer().has(keys.FIRE_RESISTANCE_TOTEM_TIER3,
                    PersistentDataType.STRING)) {

            } else if (meta.getPersistentDataContainer().has(keys.HASTE_TOTEM_TIER3, PersistentDataType.STRING)) {

            }
        }
    }

    public static class OnTotemPop implements Listener {
        @EventHandler
        public void onTotemUse(EntityResurrectEvent e) {
            if (!(e.getEntity() instanceof Player player))
                return;

            ItemStack main = player.getInventory().getItemInMainHand();
            ItemStack off = player.getInventory().getItemInOffHand();

            ItemStack item = null;

            if (main != null && main.getType() == Material.TOTEM_OF_UNDYING && PlayerTotems.isSpecialItem(main)) {
                item = main;
            } else if (off != null && off.getType() == Material.TOTEM_OF_UNDYING && PlayerTotems.isSpecialItem(off)) {
                item = off;
            }

            if (item == null)
                return;
        }
    }
}