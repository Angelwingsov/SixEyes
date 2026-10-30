package com.sixeyes.client.render.main.exceptions;

import com.sixeyes.client.render.main.ChromaRenderer;

public class ExceptionPrinter {
    private static final String separateLine = "-----------------------------------";

    public static void printAndExit(ChromaException exception) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("\n");
        stringBuilder.append(separateLine).append("\n");
        stringBuilder.append("Renderer error occurred!\n");
        stringBuilder.append(separateLine).append("\n");
        stringBuilder.append("Whats wrong?\n\n");
        stringBuilder.append(exception.getDescription()).append("\n");
        stringBuilder.append(separateLine).append("\n");
        stringBuilder.append("Details:\n\n");
        stringBuilder.append(exception.getDetails()).append("\n");
        stringBuilder.append(separateLine).append("\n");
        stringBuilder.append("Possible reasons:\n\n");
        for (int i = 0; i < exception.getReasons().length; i++)
            stringBuilder.append(i + 1).append(".").append(exception.getReasons()[i]).append("\n");
        stringBuilder.append(separateLine).append("\n");
        stringBuilder.append("Possible solutions:\n\n");
        for (int i = 0; i < exception.getSolutions().length; i++)
            stringBuilder.append(i + 1).append(".").append(exception.getSolutions()[i]).append("\n");
        stringBuilder.append(separateLine);

        ChromaRenderer.logger.error(stringBuilder.toString(), exception);
        throw new RuntimeException("ChromaRenderer error: " + exception.getDescription(), exception);
    }
}
