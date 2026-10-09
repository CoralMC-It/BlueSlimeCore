package com.github.sirblobman.api.language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import it.coralmc.tongue.lib.api.Key;
import it.coralmc.tongue.lib.api.component.TongueComponents;
import it.coralmc.tongue.lib.bukkit.BukkitTongue;
import it.coralmc.tongue.lib.bukkit.BukkitTongueService;
import com.github.sirblobman.api.shaded.adventure.text.Component;
import com.github.sirblobman.api.shaded.adventure.text.TextReplacementConfig;
import com.github.sirblobman.api.shaded.adventure.text.serializer.gson.GsonComponentSerializer;

/** Viewer-local configuration references; JSON bridges the two Adventure class spaces without losing events. */
final class TongueLanguage {
    private static final Pattern REFERENCE = Pattern.compile("%tongue_([a-zA-Z0-9-]+):([^%|]+)%");
    private final BukkitTongue tongue;
    private final java.util.Map<net.kyori.adventure.text.Component, Component> components = new java.util.concurrent.ConcurrentHashMap<>();

    TongueLanguage(Plugin plugin) {
        this.tongue = BukkitTongue.create(plugin, plugin.getName().toLowerCase(Locale.ROOT));
    }

    Locale locale(CommandSender viewer) {
        return viewer == null ? it.coralmc.tongue.lib.api.TongueSettings.getConsoleLocale() : this.tongue.getLocale(viewer);
    }

    void reload() {
        this.tongue.reload();
        this.components.clear();
    }

    void close() {
        this.tongue.disable();
        this.components.clear();
    }

    Component resolve(CommandSender viewer, Component configured) {
        return configured.replaceText(TextReplacementConfig.builder().match(REFERENCE).replacement((match, builder) -> {
            BukkitTongue namespace = BukkitTongueService.REGISTERED.get(match.group(1));
            if (namespace == null) return builder;
            return shaded(namespace.translate(locale(viewer), Key.of(match.group(1), match.group(2))));
        }).build());
    }

    String resolveRaw(CommandSender viewer, String configured) {
        java.util.regex.Matcher matcher = REFERENCE.matcher(configured);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            BukkitTongue namespace = BukkitTongueService.REGISTERED.get(matcher.group(1));
            if (namespace == null) continue;
            net.kyori.adventure.text.Component component = namespace.translate(locale(viewer), Key.of(matcher.group(1), matcher.group(2)));
            String replacement = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().serialize(component);
            matcher.appendReplacement(result, java.util.regex.Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    List<Component> lines(CommandSender viewer, List<Component> configured) {
        List<Component> result = new ArrayList<>();
        for (Component line : configured) {
            Component resolved = resolve(viewer, line);
            String json = GsonComponentSerializer.gson().serialize(resolved);
            net.kyori.adventure.text.Component unshaded = net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().deserialize(json);
            for (net.kyori.adventure.text.Component split : TongueComponents.lines(unshaded)) result.add(shaded(split));
        }
        return result;
    }

    Component entityType(Player viewer, EntityType type) {
        BukkitTongue combat = BukkitTongueService.REGISTERED.get("combatlogx");
        return shaded(combat.component(viewer, Key.of("combatlogx", "entity." + type.name().toLowerCase(Locale.ROOT))));
    }

    private Component shaded(net.kyori.adventure.text.Component original) {
        return this.components.computeIfAbsent(original, component -> {
            String json = net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().serialize(component);
            return GsonComponentSerializer.gson().deserialize(json);
        });
    }
}
