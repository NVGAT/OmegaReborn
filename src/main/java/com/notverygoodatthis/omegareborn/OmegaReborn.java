package com.notverygoodatthis.omegareborn;
import dev.dbassett.skullcreator.SkullCreator;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
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
        LIFE,
        HEAD
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
            lifeMap.put(e.getPlayer().getName(), 3);
            e.getPlayer().sendMessage(String.format("%sWelcome to the Omega SMP! You have five lives which you lose upon dying to " +
                    "a player. Lose all five and you're banned. Have fun!", OMEGA_PREFIX));
        }
        //And then we update everyone's tablist
        for(Player p : getServer().getOnlinePlayers()) {
            OmegaPlayer omegaPlayer = new OmegaPlayer(p);
            omegaPlayer.updateTablist();
        }
    }

    //Player death logic, managed to shrink it down to just a few lines
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
	//If the killer is a player...
        if(e.getEntity().getKiller() instanceof Player) {
	    //We make a new OmegaPlayer object, parsing the current player into the constructor
            OmegaPlayer player = new OmegaPlayer(e.getEntity());
	    //We decrement their lives
            player.setLives(player.getLives() - 1);
            if(player.getLives() < 1) {
		//And if their lives are lower than one we give them an Omega ban. Simple!
                player.omegaBan();
            }
        }
    }

    //OnPlayerConsume event, used for omega apples
    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent e) {
        //First we get the item in the player's main hand
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        //If the item is an omega apple
        if(item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(getOmegaItem(OmegaItemType.APPLE, 1).getItemMeta().getDisplayName())) {
            //And the player currently isn't omega gappled...
            if(!omegaGappledPlayers.contains(e.getPlayer())) {
                //We add the player to the omegaGappledPlayers list
                omegaGappledPlayers.add(e.getPlayer());
                //Then we create a new OmegaPlayer instance, give it potion effects and message it saying that they've eaten an
                //omega apple and that they won't be able to for five minutes
                OmegaPlayer player = new OmegaPlayer(e.getPlayer());
                player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 60, 5));
                player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 60, 3));
                player.getPlayer().sendMessage("§l§bYou've been buffed from eating an Omega apple. You can eat another one in five minutes.");
                Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                    //Then we schedule a task to run in five minutes that will remove the current player from the omegaGappledPlayers
                    //list and tell them that they may eat another omega apple.
                    @Override
                    public void run() {
                        omegaGappledPlayers.remove(e.getPlayer());
                        player.getPlayer().sendMessage("§l§bYou can now eat another Omega apple.");
                    }
                }, 20L * 300);
            } else {
                //If the list contains the current player we notify them that they can't eat another omega apple yet.
                e.getPlayer().sendMessage("§l§bYour Omega apple cooldown is still active. Default god apple effects applied.");
            }
        }
    }

    //OnPlayerInteract event, used for life items
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        //First we get the currently held item
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        //If the item is a life item
        if(item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(getOmegaItem(OmegaItemType.LIFE, 1).getItemMeta().getDisplayName())) {
            //And the player right clicked
            if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                //We cast the current player into an OmegaPlayer object
                OmegaPlayer p = new OmegaPlayer(e.getPlayer());
                //And we try to set their lives.
                if(p.setLives(p.getLives() + 1) == OmegaPlayer.LifeOutcome.SUCCESS) {
                    //If setting the lives succeeds, we notify the player that they've applied a life item and log it into the
                    //server console. We also take away one life item.
                    p.getPlayer().sendMessage(String.format("%sYou've applied one life, bringing you up to %d", OMEGA_PREFIX, p.getLives()));
                    getLogger().info(String.format("%s%s has applied a life item, bringing their life count up to %d", OMEGA_PREFIX, p.getPlayer().getName(), p.getLives()));
                    p.getPlayer().getInventory().getItemInMainHand().setAmount(item.getAmount() - 1);
                } else {
                    //However, if setting the lives fails we notify the player that they've failed to apply a life item and log
                    //it into the server console
                    p.getPlayer().sendMessage(String.format("%sYou already have the maximum amount of lives.", OMEGA_PREFIX));
                    getLogger().info(String.format("%s%s has failed to apply a life item.", OMEGA_PREFIX, p.getPlayer().getName()));
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if(e.getEntity() instanceof Creeper) {
            e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), new ItemStack(Material.GUNPOWDER, 5));
        }
    }

    public static ItemStack getOmegaItem(OmegaItemType type, int amount) {
        //Basic getter for any omega item, makes things miles easier down the road. It uses the OmegaItemType enumerator to
        //determine which item the user wants to get
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
            case HEAD:
                opItem = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ2MDdhZThhNmY5Mzc0MmU4ZWIxNmEwZjg2MjY1OWUzMDg3NjEwMTlhMzk3NzIyYzFhZmU4NGIxNzlkMWZhMiJ9fX0=");
                ItemMeta headMeta = opItem.getItemMeta();
                headMeta.setDisplayName(String.format("%sRevival head", OMEGA_PREFIX));
                opItem.setItemMeta(headMeta);
                break;
        }
        return opItem;
    }

    //region recipes
    //Basic recipes for the Omega items
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

    public ShapedRecipe revivalHeadRecipe() {
        ItemStack head = getOmegaItem(OmegaItemType.HEAD, 1);
        NamespacedKey key = new NamespacedKey(this, "player_head");
        ShapedRecipe rec = new ShapedRecipe(key, head);
        rec.shape("SSS", "SES", "SSS");
        rec.setIngredient('S', new RecipeChoice.ExactChoice(getOmegaItem(OmegaItemType.SHARD, 1)));
        rec.setIngredient('E', Material.ELYTRA);
        return rec;
    }
    public ShapedRecipe lifeItemRecipe() {
        ItemStack life = getOmegaItem(OmegaItemType.LIFE, 1);
        NamespacedKey key = new NamespacedKey(this, "firework_star");
        ShapedRecipe rec = new ShapedRecipe(key, life);
        rec.shape("TRT", "RER", "TRT");
        rec.setIngredient('R', Material.RECOVERY_COMPASS);
        rec.setIngredient('T', Material.TOTEM_OF_UNDYING);
        rec.setIngredient('E', Material.ELYTRA);
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
        Bukkit.addRecipe(revivalHeadRecipe());
        Bukkit.addRecipe(lifeItemRecipe());
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
        Bukkit.removeRecipe(new NamespacedKey(this, "player_head"));
        Bukkit.removeRecipe(new NamespacedKey(this, "firework_star"));
    }
    //endregion

    void registerCommands() {
        //Registers all the commands on startup
        getCommand("omegaitem").setExecutor(new OmegaItemCommand());
        getCommand("omegawithdraw").setExecutor(new OmegaWithdrawCommand());
        getCommand("omegaset").setExecutor(new OmegaSetCommand());
        getCommand("omegarevive").setExecutor(new OmegaReviveCommand());
    }

    @Override
    public void onDisable() {
        //While disabling the plugin, we save all of the lives into their respected lists in the config
        getConfig().set("players", new ArrayList<String>(lifeMap.keySet()));
        getConfig().set("lives", new ArrayList<Integer>(lifeMap.values()));
        saveConfig();
        saveDefaultConfig();
        //And we remove the recipes. This isn't necessary but it makes things easier while using plugins such as PlugManX.
        removeRecipes();
    }
}
