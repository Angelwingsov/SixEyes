package com.ferra13671.TextureUtils.utils;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.util.function.Consumer;


public class Utils {

    public static ByteBuffer readStream(InputStream stream) throws IOException {
        ReadableByteChannel rbChannel = Channels.newChannel(stream);
        if (rbChannel instanceof SeekableByteChannel) {
            SeekableByteChannel sbChannel = (SeekableByteChannel) rbChannel;
            return readChannel(rbChannel, (int) sbChannel.size() + 1);
        } else
            return readChannel(rbChannel, 8192);
    }

    public static ByteBuffer readChannel(ReadableByteChannel channel, int bufSize) throws IOException {
        ByteBuffer byteBuffer = MemoryUtil.memAlloc(bufSize);

        try {
            while(channel.read(byteBuffer) != -1) {
                if (!byteBuffer.hasRemaining())
                    byteBuffer = MemoryUtil.memRealloc(byteBuffer, byteBuffer.capacity() * 2);
            }

            return byteBuffer;
        } catch (IOException var4) {
            MemoryUtil.memFree(byteBuffer);
            throw var4;
        }
    }

    public static void tryGenerate(ByteBuffer buffer, InputStream stream, Consumer<MemoryStack> consumer) throws IOException {
        try {
            MemoryStack memoryStack = MemoryStack.stackPush();
            try {
                consumer.accept(memoryStack);
            } catch (Throwable var9) {
                if (memoryStack != null) {
                    try {
                        memoryStack.close();
                    } catch (Throwable var8) {
                        var9.addSuppressed(var8);
                    }
                }

                throw var9;
            }

            if (memoryStack != null) {
                memoryStack.close();
            }
        } finally {
            MemoryUtil.memFree(buffer);
            if (stream != null)
                stream.close();
        }
    }

    public static byte getRed(int rgba) {
        return (byte) ((rgba >> 16) & 0xFF);
    }

    public static byte getGreen(int rgba) {
        return (byte) ((rgba >> 8) & 0xFF);
    }

    public static byte getBlue(int rgba) {
        return (byte) (rgba & 0xFF);
    }

    public static byte getAlpha(int rgba) {
        return (byte) ((rgba >> 24) & 0xFF);
    }
}
