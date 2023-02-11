package com.notverygoodatthis.omegareborn;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class OmegaReborn extends JavaPlugin implements Listener {
    //Pre-defined variables used later, such as the main HashMap, Omega Prefix and the OmegaItemType enumerator
    public static HashMap<String, Integer> lifeMap = new HashMap<>();
    public static final String OMEGA_PREFIX = "§l§b";
    List<Player> omegaGappledPlayers = new ArrayList<>();
    public enum OmegaItemType {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS,
        SWORD,
        AXE,
        BOW,
        PICKAXE_FORTUNE,
        APPLE,
        SHARD,
        LIFE
    }

    @Override
    public void onEnable() {
        //Registers the event listeners
        Bukkit.getPluginManager().registerEvents(this, this);
        //Loads all the players and their lives
        List<String> playerList = (List<String>) getConfig().getList("players");
        List<Integer> lifeList = (List<Integer>) getConfig().getList("lives");
        for(String s : playerList) {
            //Loads all of the player names and lives
            lifeMap.put(s, lifeList.get(playerList.indexOf(s)));
            //Logs the lives and players
            getLogger().info(String.format("%s has %d lives.", s, lifeList.get(playerList.indexOf(s))));
        }
        //Registers the recipes and commands
        registerRecipes();
        registerCommands();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        //When a player joins, if they're new they're greeted by a chat message.
        if(!lifeMap.keySet().contains(e.getPlayer().getName())) {
            lifeMap.put(e.getPlayer().getName(), 5);
            e.getPlayer().sendMessage(String.format("%sWelcome to the Omega SMP! You have five lives which you lose upon dying to " +
                    "a player. Lose all five and you're banned. Have fun!", OMEGA_PREFIX));
        }
        //And then we update everyone's tablist
        for(Player p : getServer().getOnlinePlayers()) {
            OmegaPlayer omegaPlayer = new OmegaPlayer(p);
            omegaPlayer.updateTablist();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if(e.getEntity().getKiller() instanceof Player) {
            OmegaPlayer player = new OmegaPlayer(e.getEntity());
            player.setLives(player.getLives() - 1);
            if(player.getLives() < 1) {
                player.omegaBan();
            }
        }
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if(item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(getOmegaItem(OmegaItemType.APPLE, 1).getItemMeta().getDisplayName())) {
            if(!omegaGappledPlayers.contains(e.getPlayer())) {
                omegaGappledPlayers.add(e.getPlayer());
                OmegaPlayer player = new OmegaPlayer(e.getPlayer());
                player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 60, 5));
                player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 60, 3));
                player.getPlayer().sendMessage("§l§bYou've been buffed from eating an Omega apple. You can eat another one in five minutes.");
                Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                    @Override
                    public void run() {
                        omegaGappledPlayers.remove(e.getPlayer());
                        player.getPlayer().sendMessage("§l§bYou can now eat another Omega apple.");
                    }
                }, 20L * 300);
            } else {
                e.getPlayer().sendMessage("§l§bYour Omega apple cooldown is still active. Default god apple effects applied.");
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if(item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(getOmegaItem(OmegaItemType.LIFE, 1).getItemMeta().getDisplayName())) {
            if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                OmegaPlayer p = new OmegaPlayer(e.getPlayer());
                if(p.setLives(p.getLives() + 1) == OmegaPlayer.LifeOutcome.SUCCESS) {
                    p.getPlayer().sendMessage(String.format("%sYou've applied one life, bringing you up to %d", OMEGA_PREFIX, p.getLives()));
                    getLogger().info(String.format("%s%s has applied a life item, bringing their life count up to %d", OMEGA_PREFIX, p.getPlayer().getName(), p.getLives()));
                    p.getPlayer().getInventory().getItemInMainHand().setAmount(item.getAmount() - 1);
                } else {
                    p.getPlayer().sendMessage(String.format("%sYou already have the maximum amount of lives.", OMEGA_PREFIX));
                    getLogger().info(String.format("%s%s has failed to apply a life item.", OMEGA_PREFIX, p.getPlayer().getName()));
                }
            }
        }
    }

    public static ItemStack getOmegaItem(OmegaItemType type, int amount) {
        ItemStack opItem = new ItemStack(Material.AIR, amount);
        switch (type) {
            case HELMET:
                opItem.setType(Material.NETHERITE_HELMET);
                opItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 6);
                opItem.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
                opItem.addUnsafeEnchantment(Enchantment.MENDING, 2);
                opItem.addUnsafeEnchantment(Enchantment.OXYGEN, 5);
                opItem.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
                break;
            case CHESTPLATE:
                opItem.setType(Material.NETHERITE_CHESTPLATE);
                opItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 6);
                opItem.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
                opItem.addUnsafeEnchantment(Enchantment.MENDING, 2);
                break;
            case LEGGINGS:
                opItem.setType(Material.NETHERITE_LEGGINGS);
                opItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 6);
                opItem.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
                opItem.addUnsafeEnchantment(Enchantment.SWIFT_SNEAK, 5);
                opItem.addUnsafeEnchantment(Enchantment.MENDING, 2);
                break;
            case BOOTS:
                opItem.setType(Material.NETHERITE_BOOTS);
                opItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 6);
                opItem.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
                opItem.addUnsafeEnchantment(Enchantment.MENDING, 2);
                opItem.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 6);
                opItem.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 5);
                break;
            case SWORD:
                opItem.setType(Material.NETHERITE_SWORD);
                opItem.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 6);
                opItem.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 5);
                opItem.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 5);
                opItem.addUnsafeEnchantment(Enchantment.DURABILITY, 4);
                opItem.addUnsafeEnchantment(Enchantment.SWEEPING_EDGE, 4);
                opItem.addUnsafeEnchantment(Enchantment.MENDING, 2);
                break;
            case AXE:
                opItem.setType(Material.NETHERITE_AXE);
                opItem.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 6);
                opItem.addUnsafeEnchantment(Enchantment.DIG_SPEED, 10);
                opItem.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 5);
                opItem.addUnsafeEnchantment(Enchantment.MENDING, 2);
                break;
            case BOW:
                opItem.setType(Material.BOW);
                opItem.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 8);
                opItem.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
                opItem.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 5);
                opItem.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 4);
                opItem.addUnsafeEnchantment(Enchantment.MENDING, 2);
                break;
            case PICKAXE_FORTUNE:
                opItem.setType(Material.NETHERITE_PICKAXE);
                opItem.addUnsafeEnchantment(Enchantment.DIG_SPEED, 7);
                opItem.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
                opItem.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 5);
                opItem.addUnsafeEnchantment(Enchantment.MENDING, 2);
                break;
            case APPLE:
                opItem.setType(Material.ENCHANTED_GOLDEN_APPLE);
                ItemMeta meta = opItem.getItemMeta();
                meta.setDisplayName("§l§bO M E G A    A P P L E");
                opItem.setItemMeta(meta);
                break;
            case SHARD:
                opItem.setType(Material.FIREWORK_STAR);
                ItemMeta shardMeta = opItem.getItemMeta();
                shardMeta.setDisplayName("§l§bOmega Shard");
                opItem.setItemMeta(shardMeta);
                break;
            case LIFE:
                opItem.setType(Material.POPPED_CHORUS_FRUIT);
                ItemMeta lifeMeta = opItem.getItemMeta();
                lifeMeta.setDisplayName("§l§bLife");
                opItem.setItemMeta(lifeMeta);
                break;
        }
        return opItem;
    }

    public ShapedRecipe omegaHelmetRecipe() {
        ItemStack helmet = getOmegaItem(OmegaItemType.HELMET, 1);
        NamespacedKey key = new NamespacedKey(this, "netherite_helmet");
        ShapedRecipe rec = new ShapedRecipe(key, helmet);
        rec.shape("SSS", "SHS", "SSS");
        rec.setIngredient('S', new RecipeChoice.ExactChoice(getOmegaItem(OmegaItemType.SHARD, 1)));
        rec.setIngredient('H', Material.NETHERITE_HELMET);
        return rec;
    }

    public ShapedRecipe omegaChestplateRecipe() {
        ItemStack chestplate = getOmegaItem(OmegaItemType.CHESTPLATE, 1);
        NamespacedKey key = new NamespacedKey(this, "netherite_chestplate");
        ShapedRecipe rec = new ShapedRecipe(key, chestplate);
        rec.shape("SSS", "SHS", "SSS");
        rec.setIngredient('S', new RecipeChoice.ExactChoice(getOmegaItem(OmegaItemType.SHARD, 1)));
        rec.setIngredient('H', Material.NETHERITE_CHESTPLATE);
        return rec;
    }

    public ShapedRecipe omegaLeggingsRecipe() {
        ItemStack leggings = getOmegaItem(OmegaItemType.LEGGINGS, 1);
        NamespacedKey key = new NamespacedKey(this, "netherite_leggings");
        ShapedRecipe rec = new ShapedRecipe(key, leggings);
        rec.shape("SSS", "SHS", "SSS");
        rec.setIngredient('S', new RecipeChoice.ExactChoice(getOmegaItem(OmegaItemType.SHARD, 1)));
        rec.setIngredient('H', Material.NETHERITE_LEGGINGS);
        return rec;
    }

    public ShapedRecipe omegaBootsRecipe() {
        ItemStack boots = getOmegaItem(OmegaItemType.BOOTS, 1);
        NamespacedKey key = new NamespacedKey(this, "netherite_boots");
        ShapedRecipe rec = new ShapedRecipe(key, boots);
        rec.shape("SSS", "SHS", "SSS");
        rec.setIngredient('S', new RecipeChoice.ExactChoice(getOmegaItem(OmegaItemType.SHARD, 1)));
        rec.setIngredient('H', Material.NETHERITE_BOOTS);
        return rec;
    }

    public ShapedRecipe omegaSwordRecipe() {
        ItemStack sword = getOmegaItem(OmegaItemType.SWORD, 1);
        NamespacedKey key = new NamespacedKey(this, "netherite_sword");
        ShapedRecipe rec = new ShapedRecipe(key, sword);
        rec.shape("SSS", "SHS", "SSS");
        rec.setIngredient('S', new RecipeChoice.ExactChoice(getOmegaItem(OmegaItemType.SHARD, 1)));
        rec.setIngredient('H', Material.NETHERITE_SWORD);
        return rec;
    }

    public ShapedRecipe omegaAxeRecipe() {
        ItemStack axe = getOmegaItem(OmegaItemType.AXE, 1);
        NamespacedKey key = new NamespacedKey(this, "netherite_axe");
        ShapedRecipe rec = new ShapedRecipe(key, axe);
        rec.shape("SSS", "SHS", "SSS");
        rec.setIngredient('S', new RecipeChoice.ExactChoice(getOmegaItem(OmegaItemType.SHARD, 1)));
        rec.setIngredient('H', Material.NETHERITE_AXE);
        return rec;
    }

    public ShapedRecipe omegaBowRecipe() {
        ItemStack bow = getOmegaItem(OmegaItemType.BOW, 1);
        NamespacedKey key = new NamespacedKey(this, "bow");
        ShapedRecipe rec = new ShapedRecipe(key, bow);
        rec.shape("SSS", "SHS", "SSS");
        rec.setIngredient('S', new RecipeChoice.ExactChoice(getOmegaItem(OmegaItemType.SHARD, 1)));
        rec.setIngredient('H', Material.BOW);
        return rec;
    }

    public ShapedRecipe omegaPickRecipe() {
        ItemStack fortune = getOmegaItem(OmegaItemType.PICKAXE_FORTUNE, 1);
        NamespacedKey key = new NamespacedKey(this, "netherite_pickaxe");
        ShapedRecipe rec = new ShapedRecipe(key, fortune);
        rec.shape("SSS", "SHS", "SSS");
        rec.setIngredient('S', new RecipeChoice.ExactChoice(getOmegaItem(OmegaItemType.SHARD, 1)));
        rec.setIngredient('H', Material.NETHERITE_PICKAXE);
        return rec;
    }

    public ShapedRecipe omegaAppleRecipe() {
        ItemStack apple = getOmegaItem(OmegaItemType.APPLE, 1);
        NamespacedKey key = new NamespacedKey(this, "apple");
        ShapedRecipe rec = new ShapedRecipe(key, apple);
        rec.shape("SSS", "SHS", "SSS");
        rec.setIngredient('S', new RecipeChoice.ExactChoice(getOmegaItem(OmegaItemType.SHARD, 1)));
        rec.setIngredient('H', Material.ENCHANTED_GOLDEN_APPLE);
        return rec;
    }

    void registerRecipes() {
        Bukkit.addRecipe(omegaAppleRecipe());
        Bukkit.addRecipe(omegaHelmetRecipe());
        Bukkit.addRecipe(omegaAxeRecipe());
        Bukkit.addRecipe(omegaBowRecipe());
        Bukkit.addRecipe(omegaBootsRecipe());
        Bukkit.addRecipe(omegaChestplateRecipe());
        Bukkit.addRecipe(omegaLeggingsRecipe());
        Bukkit.addRecipe(omegaPickRecipe());
        Bukkit.addRecipe(omegaSwordRecipe());
    }

    void removeRecipes() {
        Bukkit.removeRecipe(new NamespacedKey(this, "netherite_helmet"));
        Bukkit.removeRecipe(new NamespacedKey(this, "netherite_chestplate"));
        Bukkit.removeRecipe(new NamespacedKey(this, "netherite_leggings"));
        Bukkit.removeRecipe(new NamespacedKey(this, "netherite_boots"));
        Bukkit.removeRecipe(new NamespacedKey(this, "netherite_sword"));
        Bukkit.removeRecipe(new NamespacedKey(this, "netherite_axe"));
        Bukkit.removeRecipe(new NamespacedKey(this, "netherite_pickaxe"));
        Bukkit.removeRecipe(new NamespacedKey(this, "bow"));
        Bukkit.removeRecipe(new NamespacedKey(this, "apple"));
    }

    void registerCommands() {
        getCommand("omegaitem").setExecutor(new OmegaItemCommand());
        getCommand("omegawithdraw").setExecutor(new OmegaWithdrawCommand());
        getCommand("omegaset").setExecutor(new OmegaSetCommand());
    }

    @Override
    public void onDisable() {
        getConfig().set("players", new ArrayList<String>(lifeMap.keySet()));
        getConfig().set("lives", new ArrayList<Integer>(lifeMap.values()));
        saveConfig();
        saveDefaultConfig();
        removeRecipes();
    }
}
