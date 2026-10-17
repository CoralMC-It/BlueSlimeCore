package com.github.sirblobman.api.language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import it.coralmc.tongue.lib.api.Key;
import it.coralmc.tongue.lib.api.TongueSettings;
import it.coralmc.tongue.lib.api.component.TongueComponents;
import it.coralmc.tongue.lib.bukkit.BukkitTongue;
import it.coralmc.tongue.lib.bukkit.BukkitTongueService;
import com.github.sirblobman.api.shaded.adventure.text.Component;
import com.github.sirblobman.api.shaded.adventure.text.TextReplacementConfig;
import com.github.sirblobman.api.shaded.adventure.text.serializer.gson.GsonComponentSerializer;

/**
 * Optional Tongue bridge: only instantiated when the Tongue plugin is installed, so no Tongue class is loaded otherwise.
 * Resolves {@code %tongue_namespace:key%} configuration references per viewer; JSON bridges the shaded and unshaded
 * Adventure class spaces without losing events.
 */
final class TongueLanguage {
    private static final Pattern REFERENCE = Pattern.compile("%tongue_([a-zA-Z0-9-]+):([^%|]+)%");

    private final String namespace;
    private final @Nullable BukkitTongue tongue;
    private final Map<net.kyori.adventure.text.Component, Component> components = new ConcurrentHashMap<>();

    TongueLanguage(@NotNull Plugin plugin) {
        this.namespace = plugin.getName().toLowerCase(Locale.ROOT);
        // Only plugins that bundle a Tongue catalog own a namespace; others just resolve references and locales.
        boolean bundled = plugin.getResource("tongue/" + this.namespace + "_en.properties") != null
                || plugin.getResource("tongue/" + this.namespace + ".properties") != null;
        this.tongue = bundled ? BukkitTongue.create(plugin, this.namespace) : null;
    }

    @NotNull Locale locale(@Nullable CommandSender viewer) {
        if (viewer == null || viewer instanceof ConsoleCommandSender) {
            return TongueSettings.getConsoleLocale();
        }

        return TongueSettings.getLocale(viewer.getName());
    }

    void reload() {
        if (this.tongue != null) {
            this.tongue.reload();
        }

        this.components.clear();
    }

    void close() {
        if (this.tongue != null) {
            this.tongue.disable();
        }

        this.components.clear();
    }

    @NotNull Component resolve(@Nullable CommandSender viewer, @NotNull Component configured) {
        return configured.replaceText(TextReplacementConfig.builder().match(REFERENCE).replacement((match, builder) -> {
            BukkitTongue owner = BukkitTongueService.REGISTERED.get(match.group(1));
            if (owner == null) {
                return builder;
            }

            return shaded(owner.translate(locale(viewer), Key.of(match.group(1), match.group(2))));
        }).build());
    }

    @NotNull String resolveRaw(@Nullable CommandSender viewer, @NotNull String configured) {
        if (!configured.contains("%tongue_")) {
            return configured;
        }

        Matcher matcher = REFERENCE.matcher(configured);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            BukkitTongue owner = BukkitTongueService.REGISTERED.get(matcher.group(1));
            if (owner == null) {
                continue;
            }

            net.kyori.adventure.text.Component component = owner.translate(locale(viewer),
                    Key.of(matcher.group(1), matcher.group(2)));
            String replacement = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().serialize(component);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(result);
        return result.toString();
    }

    @NotNull List<Component> lines(@Nullable CommandSender viewer, @NotNull List<Component> configured) {
        List<Component> result = new ArrayList<>(configured.size());
        for (Component line : configured) {
            Component resolved = resolve(viewer, line);
            if (resolved.equals(line)) {
                // No reference: keep the configured line exactly as before.
                result.add(line);
                continue;
            }

            String json = GsonComponentSerializer.gson().serialize(resolved);
            net.kyori.adventure.text.Component unshaded =
                    net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().deserialize(json);
            for (net.kyori.adventure.text.Component split : TongueComponents.lines(unshaded)) {
                result.add(shaded(split));
            }
        }

        return result;
    }

    /**
     * @return the entity type name from this plugin's own namespace ({@code entity.<type>}), or null if the plugin
     * has no Tongue catalog or the key is missing.
     */
    @Nullable Component entityType(@NotNull Player viewer, @NotNull EntityType type) {
        if (this.tongue == null) {
            return null;
        }

        Locale locale = locale(viewer);
        Key key = Key.of(this.namespace, "entity." + type.name().toLowerCase(Locale.ROOT));
        if (key.value().equals(this.tongue.plain(locale, key))) {
            // Tongue formats a missing key to the key itself.
            return null;
        }

        return shaded(this.tongue.translate(locale, key));
    }

    private @NotNull Component shaded(@NotNull net.kyori.adventure.text.Component original) {
        return this.components.computeIfAbsent(original, component -> {
            String json = net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().serialize(component);
            return GsonComponentSerializer.gson().deserialize(json);
        });
    }
}
