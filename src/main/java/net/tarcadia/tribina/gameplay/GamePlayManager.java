package net.tarcadia.tribina.gameplay;

import org.jetbrains.annotations.NotNull;

public class GamePlayManager {

    private static final GamePlayManager INSTANCE = new GamePlayManager();

    private GamePlayManager() {}

    public static GamePlayManager getGamePlayManager() {
        return INSTANCE;
    }

    public void init() {

    }

    public synchronized void register(@NotNull GamePlay gamePlay) {

    }

    public synchronized void unregister(@NotNull GamePlay gamePlay) {

    }

}
