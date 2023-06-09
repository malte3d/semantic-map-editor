package com.malte3d.suturo.sme.ui.viewmodel.editor.keymap;

import com.jme3.input.InputManager;
import com.jme3.input.controls.Trigger;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An {@link ActionTrigger} can combine multiple {@link Trigger}s to a single trigger.
 */
public class ActionTrigger {

    private final String name;

    private final Map<String, Trigger> triggers;
    private final Map<Trigger, Boolean> triggerPressed;

    /**
     * Creates a new {@link ActionTrigger} with the specified name and triggers.
     *
     * @param name     A unique name for the action trigger
     * @param triggers The triggers to combine
     */
    public ActionTrigger(@NonNull String name, @NonNull Trigger... triggers) {

        this.name = name;
        this.triggers = new HashMap<>();
        this.triggerPressed = new HashMap<>();

        for (Trigger trigger : triggers) {
            this.triggers.put(getMappingName(trigger), trigger);
            triggerPressed.put(trigger, false);
        }
    }

    /**
     * Sets the specified trigger to the specified pressed state.
     *
     * @param name      the name of the trigger
     * @param isPressed whether the trigger is pressed
     */
    public void update(@NonNull String name, boolean isPressed) {
        Trigger trigger = this.triggers.get(name);
        triggerPressed.put(trigger, isPressed);
    }

    /**
     * @return true, when all triggers are pressed
     */
    public boolean isActive() {
        return triggerPressed.values().stream().allMatch(Boolean::booleanValue);
    }

    /**
     * Register the triggers to the specified input manager.
     *
     * @param inputManager the input manager to register the triggers to
     */
    public void register(@NonNull InputManager inputManager) {

        for (Map.Entry<String, Trigger> entry : triggers.entrySet())
            inputManager.addMapping(entry.getKey(), entry.getValue());
    }

    /**
     * Unregister the triggers from the specified input manager.
     *
     * @param inputManager the input manager to unregister the triggers from
     */
    public void unregister(@NonNull InputManager inputManager) {


        for (Trigger trigger : triggers.values()) {

            String mappingName = getMappingName(trigger);

            if (inputManager.hasMapping(mappingName))
                inputManager.deleteMapping(mappingName);
        }
    }

    public Set<String> getTriggers() {
        return triggers.keySet();
    }

    private String getMappingName(Trigger trigger) {
        return name + " " + trigger.getName();
    }

}
