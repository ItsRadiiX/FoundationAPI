package nl.bryansuk.foundationapi.common.playerinfo.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import nl.bryansuk.foundationapi.common.playerinfo.PlayerInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class PlayerLanguageModel extends PlayerDataModel {
    private String currentLocale;

    public PlayerLanguageModel(@JsonProperty("currentLocale") String currentLocale) {
        this.currentLocale = currentLocale;
    }

    public Locale getCurrentLocale() {
        return Locale.of(currentLocale);
    }

    public void setCurrentLocale(Locale newCurrentLocale) {
        String oldLocale = currentLocale;
        currentLocale = newCurrentLocale.getLanguage();
    }

    public static @NotNull PlayerLanguageModel getFromPlayerInfo(PlayerInfo playerInfo) {
        return playerInfo.getDataOrDefault(addDefaultToPlayerInfo(playerInfo), PlayerLanguageModel.class);
    }

    public static @NotNull PlayerLanguageModel defaultModel() {
        return new PlayerLanguageModel(Locale.ENGLISH.getLanguage());
    }

    private static PlayerLanguageModel addDefaultToPlayerInfo(PlayerInfo playerInfo) {
        PlayerLanguageModel languageModel = defaultModel();
        playerInfo.setData(languageModel);
        return languageModel;
    }
}
