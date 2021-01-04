package com.crittermap.backcountrynavigator.xe.share;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.IOException;
import java.io.InputStream;

public class SvgDecoder implements ResourceDecoder<InputStream, SVG> {
    @Override
    public boolean handles(InputStream source, Options options) {
        return true;
    }

    public Resource<SVG> decode(InputStream source, int width, int height, Options options)
            throws IOException {
        try {
            SVG svg = SVG.getFromInputStream(source);
            svg.setDocumentWidth(128);
            svg.setDocumentHeight(128);
            return new SimpleResource<SVG>(svg);
        } catch (SVGParseException ex) {
            throw new IOException("Cannot load SVG from stream", ex);
        }
    }
}

