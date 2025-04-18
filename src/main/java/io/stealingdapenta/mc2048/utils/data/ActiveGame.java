package io.stealingdapenta.mc2048.utils.data;

import static io.stealingdapenta.mc2048.config.ConfigKey.PLAYER_ITEM_LORE_AVERAGE_SCORE;
import static io.stealingdapenta.mc2048.config.ConfigKey.PLAYER_ITEM_LORE_CURRENT_PLAYTIME;
import static io.stealingdapenta.mc2048.config.ConfigKey.PLAYER_ITEM_LORE_CURRENT_SCORE;
import static io.stealingdapenta.mc2048.config.ConfigKey.PLAYER_ITEM_LORE_GAMES_PLAYED;
import static io.stealingdapenta.mc2048.config.ConfigKey.PLAYER_ITEM_LORE_HIGH_SCORE;
import static io.stealingdapenta.mc2048.config.ConfigKey.PLAYER_ITEM_LORE_TOTAL_PLAYTIME;
import static io.stealingdapenta.mc2048.config.ConfigKey.PLAYER_ITEM_SLOT;
import static io.stealingdapenta.mc2048.config.ConfigKey.UNDO_BUTTON_USAGES;
import static io.stealingdapenta.mc2048.utils.FileManager.FILE_MANAGER;
import static io.stealingdapenta.mc2048.utils.InventoryUtil.getPlayerSkullItem;

import io.stealingdapenta.mc2048.config.ConfigKey;
import io.stealingdapenta.mc2048.config.PlayerConfigField;
import io.stealingdapenta.mc2048.utils.ItemBuilder;
import io.stealingdapenta.mc2048.utils.StringUtil;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class ActiveGame {

    private final Player player;
    private final RepeatingUpdateTask relatedTask;
    private Inventory gameWindow;
    private final long gameOpenTime;
    private int score;
    private final int highScore;
    private final int attempts;
    private final long totalPlayTime;
    private final double averageScore;
    private int undoLastMoveCounter;
    private ItemStack[][] lastPosition;
    private int scoreGainedAfterLastMove = 0;
    private boolean lastMoveUndo = false;
    private boolean locked = false;

    public ActiveGame(Player player, RepeatingUpdateTask relatedTask) {
        this.player = player;
        this.relatedTask = relatedTask;
        this.score = 0;
        this.gameOpenTime = System.currentTimeMillis();

        this.highScore = FILE_MANAGER.getIntByKey(player, PlayerConfigField.HIGH_SCORE.getKey());
        this.attempts = FILE_MANAGER.getIntByKey(player, PlayerConfigField.ATTEMPTS.getKey());
        this.totalPlayTime = FILE_MANAGER.getLongByKey(player, PlayerConfigField.PLAYTIME.getKey());
        this.averageScore = FILE_MANAGER.getDoubleByKey(player, PlayerConfigField.AVERAGE_SCORE.getKey());
        this.undoLastMoveCounter = UNDO_BUTTON_USAGES.getIntValue();
    }

    public static String makeSecondsATimestamp(long totalMilliSeconds) {
        int totalSeconds = (int) (totalMilliSeconds / 1000);
        int hours = totalSeconds / 3600;
        int remainder = totalSeconds % 3600;
        int minutes = remainder / 60;
        int seconds = remainder % 60;
        return hours + "h " + minutes + "m " + seconds + "s";
    }

    public RepeatingUpdateTask getRelatedTask() {
        return relatedTask;
    }

    public Inventory getGameWindow() {
        return gameWindow;
    }

    public Player getPlayer() {
        return player;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setGameWindow(Inventory gameWindow) {
        this.gameWindow = gameWindow;
    }

    public void addToScore(int amount) {
        setScore(getScore() + amount);
    }

    public void removeFromScore(int amount) {
        setScore(getScore() - amount);
    }

    public long getMillisecondsSinceStart() {
        return System.currentTimeMillis() - gameOpenTime;
    }

    public String getCurrentPlayTimeFormatted() {
        return makeSecondsATimestamp(getMillisecondsSinceStart());
    }

    public String getTotalPlusCurrentPlayTimeFormatted() {
        return makeSecondsATimestamp(getMillisecondsSinceStart() + getTotalPlayTime());
    }

    public int getHighScore() {
        return highScore;
    }

    public int getAttempts() {
        return attempts;
    }

    public long getTotalPlayTime() {
        return totalPlayTime;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void decrementUndoLastMoveCounter() {
        if (undoLastMoveCounter < 0) {
            return; // infinite undo
        }

        undoLastMoveCounter--;
    }

    public boolean hasNoUndoLastMoveLeft() {
        if (undoLastMoveCounter < 0) {
            return false; // infinite undo
        }

        return undoLastMoveCounter == 0;
    }

    public int getUndoLastMoveCounter() {
        return undoLastMoveCounter;
    }

    public ItemStack[][] getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(ItemStack[][] lastPosition) {
        this.lastPosition = lastPosition;
    }

    public int getScoreGainedAfterLastMove() {
        return scoreGainedAfterLastMove;
    }

    public void setScoreGainedAfterLastMove(int scoreGainedAfterLastMove) {
        this.scoreGainedAfterLastMove = scoreGainedAfterLastMove;
    }

    public void resetGainedAfterLastMove() {
        scoreGainedAfterLastMove = 0;
    }

    public void addToGainedAfterLastMove(int amount) {
        setScoreGainedAfterLastMove(getScoreGainedAfterLastMove() + amount);
    }

    public boolean isLastMoveUndo() {
        return lastMoveUndo;
    }

    public void setLastMoveUndo(boolean lastMoveUndo) {
        this.lastMoveUndo = lastMoveUndo;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLock(boolean locked) {
        this.locked = locked;
    }

    public double calculateNewAverageScore() {
        return (getAttempts() * getAverageScore() + getScore()) / (getAttempts() + 1);
    }

    // Doesn't need a disabled check as the caller method implies its enabled
    public void updateStatisticsItem() {
        getGameWindow().setItem(PLAYER_ITEM_SLOT.getIntValue(), getPlayerStatsHead());
    }

    private ItemStack getPlayerStatsHead() {
        return (new ItemBuilder(getPlayerSkullItem(getPlayer()))).addLore(PLAYER_ITEM_LORE_CURRENT_SCORE.getFormattedValue(StringUtil.formatInt(getScore())))
                                                                 .addLore(PLAYER_ITEM_LORE_CURRENT_PLAYTIME.getFormattedValue(getCurrentPlayTimeFormatted()))
                                                                 .addLore(PLAYER_ITEM_LORE_TOTAL_PLAYTIME.getFormattedValue(getTotalPlusCurrentPlayTimeFormatted()))
                                                                 .addLore(PLAYER_ITEM_LORE_GAMES_PLAYED.getFormattedValue(StringUtil.formatInt(getAttempts())))
                                                                 .addLore(PLAYER_ITEM_LORE_HIGH_SCORE.getFormattedValue(StringUtil.formatInt(getHighScore())))
                                                                 .addLore(PLAYER_ITEM_LORE_AVERAGE_SCORE.getFormattedValue(StringUtil.formatLong(Math.round(getAverageScore()))))
                                                                 .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                                                                 .create();
    }

    public void updateInventoryTitle(int score) {
        Inventory oldInventory = this.gameWindow;

        Inventory newInventory = Bukkit.createInventory(new GameHolder(player), oldInventory.getSize(), LegacyComponentSerializer.legacySection()
                                                                                                                                 .serialize(ConfigKey.GAME_GUI_TITLE.getFormattedValue(StringUtil.formatInt(score))));

        newInventory.setContents(oldInventory.getContents());
        setGameWindow(newInventory);
        player.openInventory(newInventory);
    }
}
