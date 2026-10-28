package com.kasperstudios.khwrapper;

/**
 * Handle to a platform-specific keybinding.
 */
public interface IKeyBindingHandle {
    /**
     * @return True if the key is currently held down.
     */
    boolean isPressed();

    /**
     * @return True if the key was pressed since the last check.
     */
    boolean wasPressed();
}
