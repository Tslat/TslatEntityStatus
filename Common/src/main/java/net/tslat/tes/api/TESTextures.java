package net.tslat.tes.api;

import net.minecraft.resources.ResourceLocation;

/**
 * Static class for storing relevant texture locations for TES.
 */
public final class TESTextures {
    public static final ResourceLocation SPRITES_ATLAS = ResourceLocation.withDefaultNamespace("textures/atlas/gui.png");

    public static final ResourceLocation ENTITY_ICON_FRAME = TESConstants.id("entity_icon_frame");

    public static final ResourceLocation BAR_EMPTY = TESConstants.id("bar/empty");
    public static final ResourceLocation BAR_OVERLAY_SEGMENTS = TESConstants.id("bar/overlay_segments");

    public static final ResourceLocation ENTITY_TYPE_AQUATIC = TESConstants.id("entity_type/aquatic");
    public static final ResourceLocation ENTITY_TYPE_ARTHROPOD = TESConstants.id("entity_type/arthropod");
    public static final ResourceLocation ENTITY_TYPE_ILLAGER = TESConstants.id("entity_type/illager");
    public static final ResourceLocation ENTITY_TYPE_UNDEAD = TESConstants.id("entity_type/undead");

    public static final ResourceLocation PROPERTY_FIRE_IMMUNE = TESConstants.id("property/fire_immune");
    public static final ResourceLocation PROPERTY_MELEE = TESConstants.id("property/melee");
    public static final ResourceLocation PROPERTY_RANGED = TESConstants.id("property/ranged");
    public static final ResourceLocation PROPERTY_STORAGE = TESConstants.id("property/storage");

    public static final ResourceLocation STAT_ARMOUR = TESConstants.id("stat/armour");
    public static final ResourceLocation STAT_TOUGHNESS = TESConstants.id("stat/toughness");
    public static final ResourceLocation STAT_MELEE_DAMAGE = TESConstants.id("stat/melee_damage");

}
