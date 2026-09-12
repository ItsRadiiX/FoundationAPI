package net.astralounge.foundationapi.paper.itemmanager.definitions;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;

import java.util.UUID;

public final class AttributeModifierDefinition {
    public Attribute attribute;
    public String name;      // Exposed name of the modifier
    public double amount;
    public AttributeModifier.Operation operation;
    public UUID id;          // Optional fixed UUID
    public EquipmentSlotGroup slotGroup; // or slot, depending on API version
}
