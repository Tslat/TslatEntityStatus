package net.tslat.tes.api;

import net.minecraft.resources.Identifier;

/**
 * Static class for storing relevant texture locations for TES.
 */
public final class TESTextures {
    public static final Identifier SPRITES_ATLAS = Identifier.withDefaultNamespace("textures/atlas/gui.png");

    public static final Identifier ENTITY_ICON_FRAME = TESConstants.id("entity_icon_frame");

    public static final Identifier BAR_EMPTY = TESConstants.id("bar/empty");
    public static final Identifier BAR_OVERLAY_SEGMENTS = TESConstants.id("bar/overlay_segments");

    public static final Identifier ENTITY_TYPE_AQUATIC = TESConstants.id("entity_type/aquatic");
    public static final Identifier ENTITY_TYPE_ARTHROPOD = TESConstants.id("entity_type/arthropod");
    public static final Identifier ENTITY_TYPE_ILLAGER = TESConstants.id("entity_type/illager");
    public static final Identifier ENTITY_TYPE_UNDEAD = TESConstants.id("entity_type/undead");

    public static final Identifier PROPERTY_FIRE_IMMUNE = TESConstants.id("property/fire_immune");
    public static final Identifier PROPERTY_MELEE = TESConstants.id("property/melee");
    public static final Identifier PROPERTY_RANGED = TESConstants.id("property/ranged");
    public static final Identifier PROPERTY_STORAGE = TESConstants.id("property/storage");

    public static final Identifier STAT_ARMOUR = TESConstants.id("stat/armour");
    public static final Identifier STAT_TOUGHNESS = TESConstants.id("stat/toughness");
    public static final Identifier STAT_MELEE_DAMAGE = TESConstants.id("stat/melee_damage");

}
