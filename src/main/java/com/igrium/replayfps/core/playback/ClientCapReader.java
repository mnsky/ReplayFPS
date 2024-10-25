package com.igrium.replayfps.core.playback;

import com.google.common.io.CountingInputStream;
import com.igrium.replayfps.core.channel.ChannelHandler;
import com.igrium.replayfps.core.recording.ClientCapHeader;
import com.igrium.replayfps.core.util.NoHeaderException;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.channels.Channels;

/**
 * Reads a ClientCap file.
 */
public class ClientCapReader implements Closeable {
    private int headerLength;
    private int frameLength;
    private final RandomAccessFile file;
    private int playhead;
    private boolean endOfFile;
    private ClientCapHeader header;

    /**
     * Create a ClientCap reader.
     *
     * @param stream An input stream containing the file contents. The entire file
     *               will be read and stored in a temp directory, where it can be
     *               randomly accessed.
     * @throws IOException If an IO exception occurs reading the file.
     */
    public ClientCapReader(InputStream stream) throws IOException {
        File tempFile = File.createTempFile("client", ".ccap");
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile))) {
            stream.transferTo(out);
        }
        tempFile.deleteOnExit();
        this.file = new RandomAccessFile(tempFile, "r");
    }

    @Nullable
    public final ClientCapHeader getHeader() {
        return header;
    }

    /**
     * Get the position of the playhead.
     *
     * @return Index of the frame that will be read on next call to {@link #readFrame()}.
     */
    public int getPlayhead() {
        return playhead;
    }

    /**
     * Read the header of this file.
     *
     * @throws IOException           If an IO exception occurs.
     * @throws IllegalStateException If the header has already been read.
     */
    public synchronized void readHeader() throws IOException, IllegalStateException {
        if (header != null) {
            throw new IllegalStateException("The header has already been read!");
        }

        CountingInputStream counter = new CountingInputStream(Channels.newInputStream(file.getChannel()));
        header = new ClientCapHeader();
        header.readHeader(counter);
        frameLength = header.calculateFrameLength();
        headerLength = (int) counter.getCount();
    }

    /**
     * Read the current frame and advance the playhead by 1.
     *
     * @return An array of all channels and their parsed values. If we've reached
     * the end of the file, this array is empty.
     * @throws IOException       If an IO exception occurs while reading the file.
     * @throws NoHeaderException If the header has not been read.
     */
    public synchronized UnserializedFrame readFrame() throws IOException, NoHeaderException {
        assertHeaderRead();
        if (endOfFile) return new UnserializedFrame(header);

        var channels = new Object[header.numChannels()];
        try {
            int i = 0;
            for (ChannelHandler<?> handler : header.getChannels()) {
                channels[i] = handler.getChannelType().read(file);
                i++;
            }
        } catch (EOFException e) {
            endOfFile = true;
            return new UnserializedFrame(header);
        }

        playhead += 1;
        return new UnserializedFrame(header, channels);

    }

    /**
     * Get the byte offset of a given frame in the file.
     *
     * @param frame Frame index.
     * @return Byte offset of the beginning of the frame.
     * @throws NoHeaderException If the header has not been read (required for frame
     *                           length.)
     */
    public long getFrameOffset(int frame) throws NoHeaderException {
        assertHeaderRead();
        return ((long) frame) * frameLength + headerLength;
    }

    /**
     * Jump to a specific frame in the file, queuing it for {@link #readFrame()}.
     *
     * @param frame Frame index.
     * @throws NoHeaderException         If the file header has not been read.
     * @throws IndexOutOfBoundsException If frame is less than 0.
     * @throws IOException               If an IO exception occurs seeking within
     *                                   the file.
     */
    public synchronized void seek(int frame) throws NoHeaderException, IndexOutOfBoundsException, IOException {
        assertHeaderRead();
        if (frame == playhead) return;
        if (frame < 0) {
            throw new IndexOutOfBoundsException(frame);
        }

        long offset = getFrameOffset(frame);
        file.seek(offset);
        endOfFile = offset > file.length();

        playhead = frame;
    }

    private void assertHeaderRead() throws NoHeaderException {
        if (header == null) {
            throw new NoHeaderException("The header has not been read!");
        }
    }

    /**
     * Close this reader and the underlying file.
     *
     * @throws IOException If an IO exception is thrown when closing the file.
     */
    @Override
    public synchronized void close() throws IOException {
        file.close();
    }
}
