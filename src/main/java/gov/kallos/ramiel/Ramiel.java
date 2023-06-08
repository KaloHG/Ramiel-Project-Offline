package gov.kallos.ramiel;

import net.fabricmc.api.ModInitializer;

public class Ramiel implements ModInitializer {

    public static Ramiel INSTANCE;

    public static Ramiel getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new Ramiel();
        }
        return INSTANCE;
    }

    @Override
    public void onInitialize() {

    }
}
