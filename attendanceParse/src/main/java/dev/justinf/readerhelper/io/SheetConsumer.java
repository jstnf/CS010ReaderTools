package dev.justinf.readerhelper.io;

import java.io.File;

public abstract class SheetConsumer {

    protected File file;

    public SheetConsumer(String filePath) {
        file = new File(filePath);
    }

    public abstract void consume();
}