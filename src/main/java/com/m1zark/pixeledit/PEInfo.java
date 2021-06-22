package com.m1zark.pixeledit;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class PEInfo {
    public static final String ID = "pixeledit";
    public static final String NAME = "PixelEdit";
    public static final String VERSION = "1.8.0-S7.2";
    public static final String DESCRIPTION = "Edit Pokemon using items(Scrolls) plus a robust Pokemon Builder";

    public static final Text PREFIX = Text.of(TextColors.AQUA, "PixelEdit ", TextColors.GRAY, "\u00bb ", TextColors.DARK_AQUA);
    public static final Text ERROR_PREFIX = Text.of(TextColors.RED, "PixelEdit ", TextColors.GRAY, "(", TextColors.RED, "Error", TextColors.GRAY, ") ", TextColors.DARK_RED);
    public static final Text DEBUG_PREFIX = Text.of(TextColors.AQUA, "PixelEdit ", TextColors.GRAY, "(", TextColors.RED, "Debug", TextColors.GRAY, ") ", TextColors.DARK_AQUA);

    public enum Dependencies {
        Pixelmon("pixelmon", "8.x.x+");

        private String dependency;
        private String version;

        Dependencies(String dependency, String version){
            this.dependency = dependency;
            this.version = version;
        }

        public String getDependency() { return dependency; }

        public String getVersion() {
            return version;
        }
    }

    static void startup(){
        PixelEdit.getInstance().getConsole().ifPresent(console -> console.sendMessages(
                Text.of(TextColors.AQUA, NAME, " v.", TextColors.GREEN, VERSION),
                Text.of(TextColors.GREEN, "Author: ", TextColors.AQUA, "m1zark"),
                Text.EMPTY
        ));
    }

    static boolean dependencyCheck(){
        boolean valid = true;

        for(Dependencies dependency : Dependencies.values()){
            if(!Sponge.getPluginManager().isLoaded(dependency.getDependency())){
                PixelEdit.getInstance().getConsole().ifPresent(console -> console.sendMessages(Text.of(ERROR_PREFIX, Text.of(TextColors.DARK_RED, "==== Missing Dependency ===="))));
                PixelEdit.getInstance().getConsole().ifPresent(console -> console.sendMessages(Text.of(ERROR_PREFIX, Text.of(TextColors.DARK_RED, "  Dependency: ", TextColors.RED, dependency.name()))));
                PixelEdit.getInstance().getConsole().ifPresent(console -> console.sendMessages(Text.of(ERROR_PREFIX, Text.of(TextColors.DARK_RED, "  Version: ", TextColors.RED, dependency.getVersion()))));

                valid = false;
            }
        }
        return valid;
    }
}
