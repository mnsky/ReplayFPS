package com.igrium.replayfps.core.recording;

import com.igrium.replayfps.core.channel.ChannelHandler;
import com.igrium.replayfps.core.playback.UnserializedFrame;
import com.igrium.replayfps.core.util.AnimationUtils;
import com.igrium.replayfps.core.util.NoHeaderException;
import com.igrium.replayfps.core.util.TimecodeProvider;
import com.mojang.logging.LogUtils;
import com.replaymod.recording.packet.PacketListener;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Captures and saves frames to a file.
 */
public class ClientCapRecorder implements Closeable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int AUTOSAVE_INTERVAL = 512;

    private final BufferedOutputStream out;
    private final ClientCapWriter writer;

    private final PacketListener packetListener;

    @Nullable
    private ClientCapHeader header;

    public ClientCapRecorder(OutputStream out, PacketListener packetListener) {
        this.out = new BufferedOutputStream(out);
        this.writer = new ClientCapWriter(out);
        this.packetListener = packetListener;
    }

    @Nullable
    public ClientCapHeader getHeader() {
        return header;
    }

    /**
     * Write the file header.
     *
     * @param header Header to write.
     * @throws IllegalStateException If the header has already been written.
     */
    public void writeHeader(ClientCapHeader header) throws IllegalStateException {
        if (this.header != null) {
            throw new IllegalStateException("Header has already been written.");
        }
        this.header = header;
        try {
            header.writeHeader(out);
            out.flush();
        } catch (IOException e) {
            LOGGER.error("Error writing clientcap header. Recording will be aborted.", e);
            this.error = e;
        }
    }

    /* FRAME CAPTURE */

    /**
     * Capture a frame.
     *
     * @return The frame.
     */
    public UnserializedFrame captureFrame(MinecraftClient client) {
        assertHeaderWritten();
        Object[] values = new Object[header.numChannels()];
        int i = 0;
        for (ChannelHandler<?> handler : header.getChannels()) {
            values[i] = handler.capture(client);
            i++;
        }
        return new UnserializedFrame(header, values);
    }

    private int framesSinceLastSave;

    protected void writeFrame(MinecraftClient client) throws IOException {
        assertHeaderWritten();
        var frame = captureFrame(client);
        writer.writeFrame(frame);
        framesSinceLastSave++;
        if (framesSinceLastSave > AUTOSAVE_INTERVAL) {
            out.flush();
            framesSinceLastSave = 0;
        }
    }

    /* RECORDING */

    private boolean isRecording;

    public final boolean isRecording() {
        return isRecording;
    }

    public void startRecording() throws IllegalStateException {
        if (isRecording) throw new IllegalStateException("We are already recording.");
        isRecording = true;
    }

    @Nullable
    private Exception error;

    public boolean hasErrored() {
        return error != null;
    }

    /**
     * Called every frame wile capturing.
     */
    public void tick(MinecraftClient client) {
        if (header == null || !isRecording) return;
        if (hasErrored()) return;

        // We can't use Util.getMeasuringTimeMillis because packetListener.getStartTime returns in terms of global unix time.
        if (((TimecodeProvider) packetListener).getServerWasPaused()) {
            return;
        }

        long timeRecording = System.currentTimeMillis() - ((TimecodeProvider) packetListener).getStartTime();
        long timestamp = timeRecording - ((TimecodeProvider) packetListener).getTimePassedWhilePaused();

        int currentFrame = AnimationUtils.countFrames((int) timestamp, header.getFramerate(), header.getFramerateBase());
        // It doesn't matter if this is negative because we're only using it for a for loop.
        int framesToCapture = currentFrame - writer.getWrittenFrames();

        if (framesToCapture > 100) {
            LOGGER.warn("{} frames have been captured on this tick. This might be a mistake.", framesToCapture);
        }

        if (framesToCapture < 0) {
            LOGGER.warn("More frames have been captured than the current timestamp suggests. ({} > {})",
                    writer.getWrittenFrames(), currentFrame);
        }

        for (int i = 0; i < framesToCapture; i++) {
            try {
                writeFrame(client);
            } catch (Exception e) {
                LOGGER.error("Error capturing frame {}. Capture will be aborted.\n{}",
                        writer.getWrittenFrames(), e);
                this.error = e;
                return;
            }
        }
    }

    private void assertHeaderWritten() throws NoHeaderException {
        if (header == null)
            throw new NoHeaderException("Header has not been written.");
    }

    @Override
    public void close() throws IOException {
        out.flush();
        out.close();
    }
}
