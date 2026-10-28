package com.kasperstudios.khwrapper;

/**
 * Platform-agnostic interface for command execution context.
 */
public interface ClientCommandContext {
    /**
     * Send a success feedback message to the command executor.
     */
    void sendFeedback(String message);

    /**
     * Send an error message to the command executor.
     */
    void sendError(String message);

    /**
     * Get raw command arguments.
     */
    String[] getArguments();
}
