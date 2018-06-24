package org.osgl.util;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2018 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.osgl.Lang.requireNotNull;
import static org.osgl.util.E.*;
import static org.osgl.util.N.*;
import static org.osgl.util.S.requireNotBlank;

import org.osgl.$;
import org.osgl.Lang;
import org.osgl.exception.NotAppliedException;

import java.awt.*;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

/**
 * Image utilities
 *
 * Disclaim: some of the logic in this class comes from
 * https://github.com/playframework/play1/blob/master/framework/src/play/libs/Images.java
 */
public enum Img {
    ;

    private static Processor COPIER = new Processor() {
        @Override
        protected BufferedImage run() {
            return source;
        }
    };

    /**
     * The default image MIME type when not specified
     *
     * The value is `image/png`
     */
    public static final String DEF_MIME_TYPE = "image/png";

    public static final String GIF_MIME_TYPE = "image/gif";

    public static final String PNG_MIME_TYPE = "image/png";

    public static final String JPG_MIME_TYPE = "image/jpeg";

    public static final Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);

    /**
     * Byte array of a tracking pixel image in gif format
     */
    public static final byte[] TRACKING_PIXEL_BYTES = new ProcessorStage(F.TRACKING_PIXEL).toByteArray(GIF_MIME_TYPE);

    /**
     * Base64 string of a tracking pixel image in gif format
     */
    public static final String TRACKING_PIXEL_BASE64 = toBase64(TRACKING_PIXEL_BYTES, GIF_MIME_TYPE);

    /**
     * The direction used to process image
     */
    public enum Direction {
        HORIZONTAL, VERTICAL;

        public boolean isHorizontal() {
            return HORIZONTAL == this;
        }

        public boolean isVertical() {
            return VERTICAL == this;
        }

        public N.WH concatenate(N.Dimension d1, N.Dimension d2) {
            return isHorizontal() ? N.dimension(d1.w() + d2.w(), N.max(d1.h(), d2.h()))
                    : N.dimension(N.max(d1.w(), d2.w()), d1.h() + d2.h());
        }

        public void drawImage(Graphics2D g, BufferedImage source1, BufferedImage source2) {
            g.drawImage(source1, 0, 0, source1.getWidth(), source1.getHeight(), null);
            int x2 = 0, y2 = 0;
            if (isHorizontal()) {
                x2 = source1.getWidth();
            } else {
                y2 = source1.getHeight();
            }
            g.drawImage(source2, x2, y2, source2.getWidth(), source2.getHeight(), null);
        }
    }

    /**
     * Base class for image operator function which provides source width, height, ratio parameters
     * on demand
     */
    public abstract static class Processor<PROCESSOR extends Processor<PROCESSOR, STAGE>, STAGE extends ProcessorStage<STAGE, PROCESSOR>> extends $.Producer<java.awt.image.BufferedImage> {
        /**
         * The source image
         */
        protected BufferedImage source;
        /**
         * The source image width
         */
        protected int sourceWidth;
        /**
         * The source image height
         */
        protected int sourceHeight;
        /**
         * The source image width/height ratio
         */
        protected double sourceRatio;

        /**
         * The target image
         */
        protected BufferedImage target;

        /**
         * The graphics
         */
        protected Graphics2D g;

        private Class<STAGE> stageClass;

        protected Processor() {
            exploreStageClass();
        }

        /**
         * Create a builder for this processor.
         *
         * To provide better fluent coding experience, sub class can overwrite this
         * function to provide specified builder instance instead of a general
         * `ProcessorBuilder` as provided here
         *
         * @return a builder for this processor
         */
        protected final STAGE createStage() {
            return createStage(get());
        }

        /**
         * Create a builder for this processor.
         *
         * To provide better fluent coding experience, sub class can overwrite this
         * function to provide specified builder instance instead of a general
         * `ProcessorBuilder` as provided here
         *
         * @param source the source image
         * @return a builder for this processor
         */
        protected STAGE createStage(BufferedImage source) {
            return null == stageClass ? (STAGE) new ProcessorStage<>(source, (PROCESSOR) this) : $.newInstance(stageClass, source, this).source(source);
        }

        @Override
        public BufferedImage produce() {
            try {
                beforeRun();
                return run();
            } finally {
                if (null != g) {
                    g.dispose();
                }
            }
        }

        protected void beforeRun() {}

        /*
         * Sub class shall implement the image process logic in
         * this method
         *
         * @return the processed image from {@link #source source image}
         */
        protected abstract BufferedImage run();

        /**
         * Set source image. This method will calculate and cache the following
         * parameters about the source image:
         *
         * * {@link #sourceWidth}
         * * {@link #sourceHeight}
         * * {@link #sourceRatio}
         *
         * @param source the source image
         * @return this Processor instance
         */
        public Processor source(BufferedImage source) {
            this.source = requireNotNull(source);
            this.sourceWidth = source.getWidth();
            this.sourceHeight = source.getHeight();
            this.sourceRatio = (double) this.sourceWidth / this.sourceHeight;
            return this;
        }

        public STAGE process(InputStream is) {
            return createStage(read(is));
        }

        public STAGE process(BufferedImage source) {
            return createStage(source);
        }

        /**
         * Get {@link Graphics2D} instance. If it is not created yet
         * then call {@link #createGraphics2D()} to create the instance
         *
         * @return the g instance
         */
        protected Graphics2D g() {
            if (null == g) {
                g = createGraphics2D();
            }
            return g;
        }

        protected Graphics2D cloneSource() {
            g().drawImage(source, 0, 0, null);
            return g;
        }

        /**
         * Create the {@link Graphics2D}. This method will trigger
         * {@link #createTarget()} method if target has not been
         * created yet
         *
         * @return an new Graphics2D
         */
        protected Graphics2D createGraphics2D() {
            if (null == target) {
                createTarget();
            }
            return target.createGraphics();
        }

        /**
         * Create {@link #target} image using source width/height. It will
         * use source color model to check if alpha channel should be be
         * added or not
         */
        protected void createTarget() {
            setTargetSpec(sourceWidth, sourceHeight, source.getColorModel().hasAlpha());
        }

        /**
         * Create {@link #target} image using specified width and height.
         *
         * This method will use source code model it check if alpha channel should be
         * added or not
         *
         * @param w the width of target image
         * @param h the height of target image
         */
        protected void setTargetSpec(int w, int h) {
            setTargetSpec(w, h, source.getColorModel().hasAlpha());
        }

        /**
         * Create {@link #target} image using specified width, height and alpha channel flag
         *
         * @param w                the width of target image
         * @param h                the height of target image
         * @param withAlphaChannel whether it shall be created with alpha channel
         */
        protected void setTargetSpec(int w, int h, boolean withAlphaChannel) {
            target = new BufferedImage(w, h, withAlphaChannel ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        }

        private void exploreStageClass() {
            try {
                List<Type> types = Generics.typeParamImplementations(getClass(), Processor.class);
                if (types.size() > 1) {
                    Type stageType = types.get(1);
                    stageClass = Generics.classOf(stageType);
                }
            } catch (RuntimeException e) {
                stageClass = null;
            }
        }
    }

    public static abstract class Filter<FILTER extends Filter<FILTER, STAGE>, STAGE extends ProcessorStage<STAGE, FILTER>> extends Processor<FILTER, STAGE> {

        @Override
        protected void beforeRun() {
            createTarget();
        }

        @Override
        protected void createTarget() {
            target = source;
        }


    }

    /**
     * The base class that process two image sources and produce result image
     */
    public abstract static class BinarySourceProcessor<PROCESSOR extends BinarySourceProcessor<PROCESSOR, STAGE>, STAGE extends ProcessorStage<STAGE, PROCESSOR>> extends Processor<PROCESSOR, STAGE> {

        /**
         * How to handle two image sources scale mismatch
         */
        public enum ScaleFix {
            /**
             * Scale smaller image to larger one
             */
            SCALE_TO_MAX() {
                @Override
                public int targetScale(int scale1, int scale2) {
                    return N.max(scale1, scale2);
                }
            },

            /**
             * Shrink larger image to smaller one
             */
            SHRINK_TO_MIN() {
                @Override
                public int targetScale(int scale1, int scale2) {
                    return N.min(scale1, scale2);
                }

            },

            /**
             * Do not fix the scale mismatch
             */
            NO_FIX() {
                @Override
                public boolean shouldFix() {
                    return false;
                }
            };

            public int targetScale(int scale1, int scale2) {
                throw unsupport();
            }

            public boolean shouldFix() {
                return true;
            }
        }

        /**
         * The second source image
         */
        protected BufferedImage source2;
        /**
         * The second source image width
         */
        protected int source2Width;
        /**
         * The second source image height
         */
        protected int source2Height;
        /**
         * The second source image width/height ratio
         */
        protected double source2Ratio;

        /**
         * Set second source image. This method will calculate and cache the following
         * parameters about the source image:
         *
         * * {@link #source2Width}
         * * {@link #source2Height}
         * * {@link #source2Ratio}
         *
         * @param source the second source image
         * @return this Processor instance
         */
        public Processor secondSource(BufferedImage source) {
            this.source2 = requireNotNull(source);
            this.source2Width = source.getWidth();
            this.source2Height = source.getHeight();
            this.source2Ratio = (double) this.sourceWidth / this.sourceHeight;
            return this;
        }

    }


    public static class ProcessorStage<STAGE extends ProcessorStage<STAGE, PROCESSOR>, PROCESSOR extends Processor<PROCESSOR, STAGE>> extends _Load<STAGE> {
        /**
         * The image target as a result of processing. Note if {@link #processor} is not provided
         * then it will use {@link #source} directly as the target
         */
        protected volatile BufferedImage target;
        /**
         * The processor that apply a certain logic on source
         * and generates the target
         */
        protected PROCESSOR processor;
        /**
         * Define the compression quality of the target image
         */
        protected float compressionQuality = Float.NaN;

        private ProcessorStage($.Func0<BufferedImage> source) {
            this(source.apply(), (PROCESSOR) COPIER);
        }

        private ProcessorStage(BufferedImage source) {
            this(source, (PROCESSOR) COPIER);
        }

        /**
         * Construct a ProcessorBuilder with an image source provider
         *
         * @param source the function that provides the image source
         */
        public ProcessorStage($.Func0<BufferedImage> source, PROCESSOR processor) {
            this(source.apply(), processor);
        }

        /**
         * Construct a ProcessorBuilder with image source specified
         *
         * @param source the image source to be processed
         */
        public ProcessorStage(BufferedImage source, PROCESSOR processor) {
            super(source);
            this.processor = requireNotNull(processor);
        }

        /**
         * Returns the the target image as a result of processing or source image if no {@link #processor} is provided
         *
         * @return the target image
         */
        @Override
        public BufferedImage get() {
            return target();
        }

        /**
         * Pipeline the target image as an input (source) image to to another processor
         *
         * @param processor the next processor
         * @param <B>       the processor builder type
         * @param <P>       the processor type
         * @return a {@link ProcessBuilder} for the processor specified
         */
        public <B extends ProcessorStage<B, P>, P extends Processor<P, B>> B pipeline(P processor) {
            return processor.createStage(get());
        }

        public <B extends ProcessorStage<B, P>, P extends Processor<P, B>> B pipeline(Class<? extends P> processorClass) {
            return $.newInstance(processorClass).createStage(get());
        }

        public STAGE compressionQuality(float compressionQuality) {
            this.compressionQuality = N.requireAlpha(compressionQuality);
            return me();
        }

        public STAGE source(InputStream is) {
            this.source = read(is);
            return me();
        }

        public STAGE source(BufferedImage source) {
            this.source = requireNotNull(source);
            return me();
        }

        public _Load pipeline() {
            return new _Load(target());
        }

        private BufferedImage target() {
            if (null == target) {
                doJob();
            }
            return target;
        }

        private synchronized void doJob() {
            preTransform();
            target = null == processor ? source : processor.source(source).produce();
        }

        protected void preTransform() {
        }

    }

    public static class _Load<T extends _Load> extends $.Provider<BufferedImage> {

        protected BufferedImage source;
        /**
         * Define the compression quality of the target image
         */
        protected float compressionQuality = Float.NaN;

        private _Load(InputStream is) {
            this.source = read(is);
        }

        private _Load(BufferedImage source) {
            this.source = requireNotNull(source);
        }

        public T compressionQuality(float compressionQuality) {
            this.compressionQuality = N.requireAlpha(compressionQuality);
            return me();
        }

        public void writeTo(String fileName) {
            writeTo(new File(fileName));
        }

        public void writeTo(File file, String mimeType) {
            writeTo(IO.outputStream(file), mimeType);
        }

        public void writeTo(File file) {
            writeTo(IO.outputStream(file), mimeType(file));
        }

        public void writeTo(OutputStream os, String mimeType) {
            ImageWriter writer = ImageIO.getImageWritersByMIMEType(mimeType(mimeType)).next();
            dropAlphaChannelIfJPEG(writer);
            ImageWriteParam params = writer.getDefaultWriteParam();

            if (!Float.isNaN(compressionQuality) && params.canWriteCompressed()) {
                params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                params.setCompressionType(params.getCompressionTypes()[0]);
                params.setCompressionQuality(compressionQuality);
            }

            ImageOutputStream ios = os(os);
            writer.setOutput(ios);
            IIOImage image = new IIOImage(get(), null, null);
            try {
                writer.write(null, image, params);
            } catch (IOException e) {
                throw ioException(e);
            }
            IO.flush(ios);
            writer.dispose();
        }

        public byte[] toByteArray() {
            return toByteArray(DEF_MIME_TYPE);
        }

        public byte[] toByteArray(String mimeType) {
            ByteArrayOutputStream baos = IO.baos();
            writeTo(baos, mimeType(mimeType));
            return baos.toByteArray();
        }

        public String toBase64() {
            return toBase64(DEF_MIME_TYPE);
        }

        public String toBase64(String mimeType) {
            return Img.toBase64(toByteArray(mimeType), mimeType);
        }

        public void dropAlphaChannelIfJPEG(ImageWriter writer) {
            if (writer.getClass().getSimpleName().toUpperCase().contains("JPEG")) {
                BufferedImage src = source;
                BufferedImage convertedImg = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
                convertedImg.getGraphics().drawImage(src, 0, 0, null);
                source = convertedImg;
            }
        }

        @Override
        public BufferedImage get() {
            return source;
        }

        public Resizer.Stage resize() {
            return new Resizer.Stage(source);
        }

        public Resizer.Stage resize(float scale) {
            return new Resizer.Stage(source).scale(scale);
        }

        public Resizer.Stage resize(int w, int h) {
            return new Resizer.Stage(source).dimension(w, h);
        }

        public Resizer.Stage resize($.Tuple<Integer, Integer> dimension) {
            return resize(dimension.left(), dimension.right());
        }

        public Resizer.Stage resize(Dimension dimension) {
            return resize(dimension.width, dimension.height);
        }

        public Cropper.Stage crop() {
            return new Cropper.Stage(source);
        }

        public Cropper.Stage crop(int x1, int y1, int x2, int y2) {
            return new Cropper.Stage(source).from(x1, y1).to(x2, y2);
        }

        public Cropper.Stage crop($.Tuple<Integer, Integer> leftTop, $.Tuple<Integer, Integer> rightBottom) {
            return crop(leftTop._1, leftTop._2, rightBottom._1, rightBottom._2);
        }

        public TextWriter.Stage text(String text) {
            return new TextWriter.Stage(source).text(text);
        }

        public TextWriter.Stage watermark() {
            return new TextWriter.Stage(source);
        }

        public TextWriter.Stage watermark(String text) {
            return new TextWriter.Stage(source).color(Color.LIGHT_GRAY).alpha(0.8f).rotate(-Math.PI / 7).offset(-130, 80).text(text);
        }

        public Blur.Stage blur() {
            return new Blur.Stage(source);
        }

        public Blur.Stage blur(int level) {
            return new Blur.Stage(source).level(level);
        }

        public NoiseMaker.Stage makeNoise() {
            return new NoiseMaker.Stage(source);
        }

        public Flip.Stage flip() {
            return flip(Direction.HORIZONTAL);
        }

        public Flip.Stage flipVertial() {
            return flip(Direction.VERTICAL);
        }

        public Flip.Stage flip(Direction dir) {
            return new Flip.Stage(source).dir(dir);
        }

        public ProcessorStage compress(float compressionQuality) {
            return new ProcessorStage(source).compressionQuality(compressionQuality).pipeline(COPIER);
        }

        public ProcessorStage copy() {
            return new ProcessorStage(source).pipeline(COPIER);
        }

        public ProcessorStage processor(Processor processor) {
            return processor.createStage();
        }

        public Concatenater.Stage appendWith($.Func0<BufferedImage> secondImange) {
            return new Concatenater.Stage(source).with(secondImange);
        }

        public Concatenater.Stage appendTo($.Func0<BufferedImage> firstImage) {
            return appendWith(firstImage).reverse();
        }

        public Concatenater.Stage appendWith(BufferedImage secondImange) {
            return new Concatenater.Stage(source).with(F.source(secondImange));
        }

        public Concatenater.Stage appendTo(BufferedImage firstImage) {
            return appendWith(firstImage).reverse();
        }

        protected T me() {
            return $.cast(this);
        }

    }

    public static _Load source(InputStream is) {
        return new _Load(is);
    }

    public static _Load source(URL url) {
        return new _Load(IO.inputStream(url));
    }

    public static _Load source(File file) {
        return new _Load(IO.inputStream(file));
    }

    public static _Load source($.Func0<BufferedImage> imageProducer) {
        return new ProcessorStage<>(imageProducer).pipeline();
    }

    public static _Load source(BufferedImage image) {
        return new ProcessorStage<>(image).pipeline();
    }

    public static Resizer.Stage resize($.Func0<BufferedImage> imageProvider) {
        return source(imageProvider).resize();
    }

    public static Cropper.Stage crop($.Func0<BufferedImage> imageProvider) {
        return source(imageProvider).crop();
    }

    public static Flip.Stage flip($.Func0<BufferedImage> imageProvider) {
        return source(imageProvider).flip();
    }

    public static Blur.Stage blur($.Func0<BufferedImage> imageProvider) {
        return source(imageProvider).blur();
    }

    public static TextWriter.Stage watermark($.Func0<BufferedImage> imageProvider) {
        return source(imageProvider).watermark();
    }

    public static Concatenater.Stage concat($.Func0<BufferedImage> image1) {
        return new Concatenater.Stage(image1.apply());
    }

    public static Concatenater.Stage concat($.Func0<BufferedImage> image1, $.Func0<BufferedImage> image2) {
        return source(image1).appendWith(image2);
    }

    /**
     * Encode an image to base64 using a data: URI
     *
     * @param image The image file
     * @return The base64 encoded value
     */
    public static String toBase64(File image) {
        return toBase64(IO.inputStream(image), mimeType(image));
    }

    /**
     * Encode an image to base64 using a data: URI
     *
     * @param inputStream The image input stream
     * @param mimeType    The mime type, if not specified then default to {@link #DEF_MIME_TYPE}
     * @return The base64 encoded value
     */
    public static String toBase64(InputStream inputStream, String mimeType) {
        return toBase64(IO.readContent(inputStream), mimeType);
    }

    /**
     * Encode an image to base64 using a data: URI
     *
     * @param bytes    The image byte array
     * @param mimeType the mime type, if not specified then default to {@link #DEF_MIME_TYPE}
     * @return The base64 encoded value
     */
    public static String toBase64(byte[] bytes, String mimeType) {
        return "data:" + mimeType(mimeType) + ";base64," + Codec.encodeBase64(bytes);
    }

    private static String mimeType(File target) {
        return mimeType(target.getName());
    }

    private static String mimeType(String hint) {
        String mimeType = DEF_MIME_TYPE;
        if (S.blank(hint)) {
            return mimeType;
        }
        if (1 == S.count(hint, "/", false) && !hint.contains(".")) {
            // this is a mime type string
            return hint;
        }
        if (hint.endsWith("jpeg") || hint.endsWith("jpg")) {
            mimeType = JPG_MIME_TYPE;
        }
        if (hint.endsWith("gif")) {
            mimeType = GIF_MIME_TYPE;
        }
        return mimeType;
    }

    public static ImageOutputStream os(File file) {
        try {
            return new FileImageOutputStream(file);
        } catch (IOException e) {
            throw ioException(e);
        }
    }

    public static ImageOutputStream os(OutputStream os) {
        return new MemoryCacheImageOutputStream(os);
    }

    public static BufferedImage read(InputStream is) {
        try {
            return ImageIO.read(is);
        } catch (Exception e) {
            throw unexpected(e);
        }
    }

    public static BufferedImage read(File file) {
        try {
            return ImageIO.read(file);
        } catch (Exception e) {
            throw unexpected(e);
        }
    }

    public static BufferedImage read(URL url) {
        try {
            return ImageIO.read(url);
        } catch (Exception e) {
            throw unexpected(e);
        }
    }

    // -- Image operators

    /**
     * Resize an image
     */
    public static class Resizer extends Processor<Resizer, Resizer.Stage> {

        public static class Stage extends ProcessorStage<Stage, Resizer> {
            Stage(BufferedImage source, Resizer processor) {
                super(source, processor);
            }

            Stage(BufferedImage source) {
                super(source, new Resizer());
            }

            Stage dimension(int w, int h) {
                processor.w = requireNonNegative(w);
                processor.h = requireNonNegative(h);
                return this;
            }

            Stage dimension($.Tuple<Integer, Integer> dimension) {
                return to(dimension);
            }

            Stage dimension(Dimension dimension) {
                return dimension(dimension.width, dimension.height);
            }

            Stage to(int w, int h) {
                return dimension(w, h);
            }

            Stage to($.Tuple<Integer, Integer> dimension) {
                return to(dimension.left(), dimension.right());
            }

            Stage to(Dimension dimension) {
                return to(dimension.width, dimension.height);
            }

            Stage to(float scale) {
                return scale(scale);
            }

            Stage scale(float scale) {
                processor.scale = requirePositive(scale);
                return this;
            }

            Stage keepRatio() {
                processor.keepRatio = true;
                return this;
            }
        }

        int w;
        int h;
        float scale = Float.NaN;
        boolean keepRatio;

        Resizer() {
        }

        Resizer(int w, int h, boolean keepRatio) {
            this.w = requireNonNegative(w);
            this.h = requireNonNegative(h);
            this.keepRatio = keepRatio;
        }

        Resizer(float scale) {
            this.scale = requireNotNaN(scale);
            this.keepRatio = true;
        }

        @Override
        protected Stage createStage(BufferedImage source) {
            return new Stage(source, this);
        }

        @Override
        protected BufferedImage run() {
            int w = this.w;
            int h = this.h;
            final int maxWidth = w;
            final int maxHeight = h;

            if (Float.isNaN(scale)) {
                if (w < 0 && h < 0) {
                    w = sourceWidth;
                    h = sourceHeight;
                }

                final double ratio = this.sourceRatio;

                if (w < 0 && h > 0) {
                    w = (int) (h * ratio);
                }
                if (w > 0 && h < 0) {
                    h = (int) (w / ratio);
                }

                if (keepRatio) {
                    h = (int) (w / ratio);
                    if (h > maxHeight) {
                        h = maxHeight;
                        w = (int) (h * ratio);
                    }
                    if (w > maxWidth) {
                        w = maxWidth;
                        h = (int) (w / ratio);
                    }
                }
            } else {
                w = (int) (sourceWidth * scale);
                h = (int) (sourceHeight * scale);
            }

            // out
            setTargetSpec(w, h);
            Graphics g = g();
            if (!source.getColorModel().hasAlpha()) {
                // Create a white background if not transparency define
                g = target.getGraphics();
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, w, h);
            }
            Image srcResized = source.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            g.drawImage(srcResized, 0, 0, null);
            return target;
        }
    }

    public static class Cropper extends Processor<Cropper, Cropper.Stage> {

        public static class Stage extends ProcessorStage<Cropper.Stage, Cropper> {
            Stage(BufferedImage source, Cropper processor) {
                super(source, processor);
            }

            Stage(BufferedImage source) {
                super(source, new Cropper());
            }

            Stage from(int x, int y) {
                processor.x1 = x;
                processor.y1 = y;
                return this;
            }

            Stage to(int x, int y) {
                processor.x2 = x;
                processor.y2 = y;
                return this;
            }
        }

        private int x1;
        private int y1;
        private int x2;
        private int y2;

        Cropper() {
        }

        @Override
        protected Stage createStage(BufferedImage source) {
            return new Stage(source, this);
        }

        @Override
        protected BufferedImage run() {
            int x2 = this.x2;
            x2 = x2 < 0 ? sourceWidth + x2 : x2;
            int y2 = this.y2;
            y2 = y2 < 0 ? sourceHeight + y2 : y2;

            int w = x2 - x1;
            int h = y2 - y1;

            if (w < 0) {
                x1 = x2;
                w = -w;
            }
            if (h < 0) {
                y1 = y2;
                h = -h;
            }

            // out
            setTargetSpec(w, h);
            Image croppedImage = source.getSubimage(x1, y1, w, h);
            Graphics g = g();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, w, h);
            g.drawImage(croppedImage, 0, 0, null);
            return target;
        }
    }

    public static class Flip extends Processor<Flip, Flip.Stage> {

        public static class Stage extends ProcessorStage<Flip.Stage, Flip> {
            Stage(BufferedImage source, Flip processor) {
                super(source, processor);
            }

            Stage(BufferedImage source) {
                super(source, new Flip());
            }

            Flip.Stage vertically() {
                processor.dir = Direction.VERTICAL;
                return this;
            }

            Flip.Stage horizontally() {
                processor.dir = Direction.HORIZONTAL;
                return this;
            }

            Flip.Stage dir(Direction dir) {
                processor.dir = requireNotNull(dir);
                return this;
            }
        }

        Direction dir = Direction.HORIZONTAL;

        Flip() {
        }

        @Override
        protected Stage createStage(BufferedImage source) {
            return new Stage(source, this);
        }

        @Override
        protected BufferedImage run() {
            Graphics2D g = g();
            if (dir.isHorizontal()) {
                g.drawImage(source, sourceWidth, 0, -sourceWidth, sourceHeight, null);
            } else {
                g.drawImage(source, 0, sourceHeight, sourceWidth, -sourceHeight, null);
            }
            return target;
        }
    }


    public static class Blur extends Filter<Blur, Blur.Stage> {

        public static class Stage extends ProcessorStage<Stage, Blur> {
            public Stage(BufferedImage source) {
                super(source, new Blur());
            }

            public Stage(BufferedImage source, Blur processor) {
                super(source, processor);
            }

            Stage level(int level) {
                processor.setLevel(level);
                return this;
            }
        }

        static final int DEFAULT_LEVEL = 3;

        float[] matrix;
        int level;

        Blur() {
            setLevel(DEFAULT_LEVEL);
        }

        @Override
        protected Stage createStage(BufferedImage source) {
            return new Stage(source, this);
        }

        void setLevel(int level) {
            this.level = requirePositive(level);
            int max = level * level;
            matrix = new float[requirePositive(max)];
            for (int i = 0; i < max; ++i) {
                matrix[i] = (float) 1 / (float) max;
            }
        }

        @Override
        protected BufferedImage run() {
            BufferedImageOp op = new ConvolveOp(new Kernel(level, level, matrix), ConvolveOp.EDGE_NO_OP, null);
            target = op.filter(target, null);
            return target;
        }
    }

    public static class NoiseMaker extends Filter<NoiseMaker, NoiseMaker.Stage> {

        public static class Stage extends ProcessorStage<Stage, NoiseMaker> {
            public Stage(BufferedImage source) {
                super(source, new NoiseMaker());
            }
            public Stage(BufferedImage source, NoiseMaker processor) {
                super(source, processor);
            }

            public Stage setMinArcs(int n) {
                processor.minArcs = N.requireNonNegative(n);
                return this;
            }
            public Stage setMaxArcs(int n) {
                processor.maxArcs = N.requirePositive(n);
                return this;
            }
            public Stage setMaxArcSize(int size) {
                processor.maxArcSize = size;
                return this;
            }
            public Stage setMaxLines(int n) {
                processor.maxLines = N.requireNonNegative(n);
                return this;
            }
        }

        private int minArcs = 100;
        private int maxArcs = 200;
        private int maxArcSize = 5;
        private int minLines = 1;
        private int maxLines = 5;

        @Override
        protected NoiseMaker.Stage createStage(BufferedImage source) {
            return new NoiseMaker.Stage(source, this);
        }

        @Override
        protected BufferedImage run() {
            int w = sourceWidth;
            int h = sourceHeight;
            Random r = ThreadLocalRandom.current();

            Graphics2D g = g();

            // draw random arcs
            if (maxArcs < minArcs) {
                int tmp = maxArcs;
                maxArcs = minArcs;
                minArcs = tmp;
            }
            int dots = minArcs + r.nextInt(maxArcs);
            for (int i = 0; i < dots; i++) {
                // set a random color
                g.setColor(new Color(randomColorValue()));

                // pick up a random position
                int xInt = r.nextInt(w - 1);
                int yInt = r.nextInt(h - 1);

                // random angle
                int sAngleInt = r.nextInt(360);
                int eAngleInt = r.nextInt(360);

                // size of the arc
                int wInt = 1 + r.nextInt(maxArcSize);
                int hInt = 1 + r.nextInt(maxArcSize);

                g.fillArc(xInt, yInt, wInt, hInt, sAngleInt, eAngleInt);
            }

            // draw random lines;
            int lines = minLines + r.nextInt(maxLines - minLines);
            for (int i = 0; i < lines; ++i) {
                int xInt = r.nextInt(w - 1);
                int yInt = r.nextInt(h - 1);

                int xInt2 = r.nextInt(w - 1);
                int yInt2 = r.nextInt(h - 1);
                g.setColor(new Color(randomColorValue()));
                g.drawLine(xInt, yInt, xInt2, yInt2);
            }

            return target;
        }
    }

    public static class TextWriter extends Filter<TextWriter, TextWriter.Stage> {
        public static class Stage extends ProcessorStage<Stage, TextWriter> {
            public Stage(BufferedImage source) {
                super(source, new TextWriter());
            }

            public Stage(BufferedImage source, TextWriter processor) {
                super(source, processor);
            }

            public TextWriter.Stage text(String text) {
                processor.text = requireNotBlank(text);
                return this;
            }

            public TextWriter.Stage color(Color color) {
                processor.color = requireNotNull(color);
                return this;
            }

            public TextWriter.Stage font(Font font) {
                processor.font = requireNotNull(font);
                return this;
            }

            public TextWriter.Stage alpha(float alpha) {
                processor.alpha = requireAlpha(alpha);
                return this;
            }

            public TextWriter.Stage offset(int offsetX, int offsetY) {
                processor.offsetX = offsetX;
                processor.offsetY = offsetY;
                return this;
            }

            public TextWriter.Stage offsetY(int offsetY) {
                this.processor.offsetY = offsetY;
                return this;
            }

            public TextWriter.Stage offsetX(int offsetX) {
                this.processor.offsetX = offsetX;
                return this;
            }

            public TextWriter.Stage rotate(double theta) {
                this.processor.theta = theta;
                return this;
            }

        }
        Color color = Color.DARK_GRAY;
        Font font = new Font("Arial", Font.BOLD, 32);
        float alpha = 1.0f;
        String text;
        int offsetX;
        int offsetY;
        Double theta;
        TextWriter() {
        }

        TextWriter(String text) {
            this.text = text;
        }

        TextWriter(String text, int offsetX, int offsetY, Color color, Font font, float alpha) {
            this.text = text;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.color = color;
            this.font = font;
            this.alpha = alpha;
        }

        @Override
        protected Stage createStage(BufferedImage source) {
            return new Stage(source, this);
        }

        @Override
        protected BufferedImage run() {
            int w = sourceWidth;
            int h = sourceHeight;
            Graphics2D g = g();
            g.setColor(color);
            g.setFont(font);
            if (null != theta) {
                g.rotate(theta);
            }
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

            FontMetrics fontMetrics = g.getFontMetrics();
            Rectangle2D rect = fontMetrics.getStringBounds(text, g);
            int centerX = (w - (int) rect.getWidth() + offsetX) / 2;
            int centerY = (h - (int) rect.getHeight() + offsetY) / 2;
            g.drawString(text, centerX, centerY);
            return target;
        }

    }

    public static class Concatenater extends BinarySourceProcessor<Concatenater, Concatenater.Stage> {
        public static class Stage extends ProcessorStage<Stage, Concatenater> {

            protected Stage(BufferedImage source) {
                super(source);
                this.processor = new Concatenater();
            }

            public Stage dir(Direction dir) {
                this.processor.dir = requireNotNull(dir);
                return this;
            }

            public Stage horizontally() {
                this.processor.dir = Direction.HORIZONTAL;
                return this;
            }

            public Stage vertically() {
                this.processor.dir = Direction.VERTICAL;
                return this;
            }

            public Stage shinkToSmall() {
                this.processor.scaleFix = ScaleFix.SHRINK_TO_MIN;
                return this;
            }

            public Stage scaleToMax() {
                this.processor.scaleFix = ScaleFix.SCALE_TO_MAX;
                return this;
            }

            public Stage noScaleFix() {
                this.processor.scaleFix = ScaleFix.NO_FIX;
                return this;
            }

            public Stage scaleFix(ScaleFix scaleFix) {
                this.processor.scaleFix = requireNotNull(scaleFix);
                return this;
            }

            public Stage background(Color backgroundColor) {
                this.processor.background = requireNotNull(backgroundColor);
                return this;
            }

            public Stage reverse() {
                this.processor.reversed = !this.processor.reversed;
                return this;
            }

            public Stage with($.Func0<BufferedImage> secondImage) {
                this.processor.secondSource(secondImage.apply());
                return this;
            }

            public Stage appendWith($.Func0<BufferedImage> anotherOne) {
                return Img.concat(this, anotherOne);
            }

            public Stage appendTo($.Func0<BufferedImage> anotherOne) {
                return Img.concat(anotherOne, this);
            }

        }

        /**
         * Define the direction to concatenate two image sources
         *
         * Default value is {@link Direction#VERTICAL}
         */
        Direction dir = Direction.HORIZONTAL;

        /**
         * Define the stategy to handle scale mismatch of two image sources
         *
         * Default value is {@link ScaleFix#SCALE_TO_MAX}
         */
        ScaleFix scaleFix = ScaleFix.SCALE_TO_MAX;

        /**
         * The background color
         */
        Color background = COLOR_TRANSPARENT;

        boolean reversed = false;

        private Concatenater() {}

        Concatenater(BufferedImage secondImage) {
            this.secondSource(secondImage);
        }

        Concatenater(BufferedImage secondImage, Direction dir, ScaleFix scaleFix, Color background) {
            this.secondSource(secondImage);
            this.dir = requireNotNull(dir);
            this.scaleFix = requireNotNull(scaleFix);
            this.background = requireNotNull(background);
        }

        @Override
        protected BufferedImage run() {
            if (dir.isHorizontal()) {
                fixScale(sourceHeight, source2Height);
            } else {
                fixScale(sourceWidth, source2Width);
            }
            N.Dimension d = dir.concatenate(N.wh(sourceWidth, sourceHeight), N.wh(source2Width, source2Height));
            int w = d.w(), h = d.h();
            setTargetSpec(w, h);
            Graphics2D g = g();
            g.setColor(background);
            g.fillRect(0, 0, w, h);
            if (!reversed) {
                dir.drawImage(g, source, source2);
            } else {
                dir.drawImage(g, source2, source);
            }
            return target;
        }

        private void fixScale(int scale1, int scale2) {
            if (scale1 != scale2 && scaleFix.shouldFix()) {
                int targetScale = scaleFix.targetScale(scale1, scale2);
                float r1 = (float) targetScale / (float) scale1;
                float r2 = (float) targetScale / (float) scale2;
                if (N.neq(r1, 1.0f)) {
                    source(new Resizer(r1).source(source).run());
                }
                if (N.neq(r2, 1.0f)) {
                    secondSource(new Resizer(r2).source(source2).run());
                }
            }
        }
    }


    private static int randomColorValue() {
        return randomColorValue(true);
    }

    private static int randomColorValue(boolean withAlpha) {
        int a = N.randInt(256);
        int r = N.randInt(256);
        int g = N.randInt(256);
        return withAlpha ? a << 24 | r << 16 | g << 8 : a << 24 | r << 16 | g << 8;
    }

    /**
     * The namespace for functions
     */
    public enum F {
        ;

        public static $.Producer<Color> RANDOM_COLOR = new $.Producer<Color>() {
            @Override
            public Color produce() {
                Random r = ThreadLocalRandom.current();
                return new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255), r.nextInt(255));
            }
        };

        /**
         * A function that generates a transparent tracking pixel
         */
        public static $.Producer<BufferedImage> TRACKING_PIXEL = new $.Producer<java.awt.image.BufferedImage>() {
            @Override
            public BufferedImage produce() {
                BufferedImage trackPixel = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
                trackPixel.setRGB(0, 0, COLOR_TRANSPARENT.getRGB());
                return trackPixel;
            }
        };

        /**
         * A function that generates a transparent background in rectangular area
         *
         * @param w the width
         * @param h the height
         * @return a function as described above
         */
        public static $.Producer<BufferedImage> background(final int w, final int h) {
            return background(w, h, $.val(COLOR_TRANSPARENT));
        }

        /**
         * A function that generates a image with random picked pixels with random color. The
         * background is transparent. There are about 6 percent of pixels in the image selected
         * to be rendered with random color.
         *
         * @param w the width
         * @param h the height
         * @return a function as described above
         */
        public static $.Producer<BufferedImage> randomPixels(final int w, final int h) {
            return randomPixels(w, h, 6);
        }


        /**
         * A function that generates a image with random picked pixels with random color.
         * The image background color is specified as `background`. There are about 6 percent
         * of pixels in the image selected to be rendered with random color.
         *
         *
         * @param w the width
         * @param h the height
         * @return a function as described above
         */
        public static $.Producer<BufferedImage> randomPixels(final int w, final int h, Color background) {
            return randomPixels(w, h, 7, background);
        }
        /**
         * A function that generates a image with random picked pixels with random color. The
         * background is transparent.
         *
         * @param w the width
         * @param h the height
         * @param percent the percent of pixels selected from all pixels in the image
         * @return a function as described above
         */
        public static $.Producer<BufferedImage> randomPixels(final int w, final int h, final int percent) {
            return randomPixels(w, h, percent, COLOR_TRANSPARENT);
        }

        /**
         * A function that generates a image with random picked pixels with random color. The image
         * use `background` color as the background
         *
         * @param w the width
         * @param h the height
         * @param percent the percent of pixels selected from all pixels in the image
         * @param background the background color
         * @return a function as described above
         */
        public static $.Producer<BufferedImage> randomPixels(final int w, final int h, final int percent, final Color background) {
            return background(w, h, $.val(background)).andThen(new $.Function<BufferedImage, BufferedImage>() {
                @Override
                public BufferedImage apply(BufferedImage img) throws NotAppliedException, Lang.Break {
                    Random r = ThreadLocalRandom.current();
                    for (int i = 0; i < w; ++i) {
                        for (int j = 0; j < h; ++j) {
                            if (r.nextInt(100) < percent) {
                                int v = Img.randomColorValue(false);
                                img.setRGB(i, j, v);
                            }
                        }
                    }
                    return img;
                }
            });
        }

        /**
         * A function that generates a background in rectangular area with color specified
         *
         * @param w the width
         * @param h the height
         * @param color the background color
         * @return a function as described above
         */
        public static $.Producer<BufferedImage> background(final int w, final int h, final Color color) {
            return background(w, h, $.val(color));
        }

        /**
         * A function that generates a background in rectangular area with color specified
         *
         * @param w the width
         * @param h the height
         * @param colorValueProvider the background color provider
         * @return a function as described above
         */
        public static $.Producer<BufferedImage> background(final int w, final int h, final $.Func0<Color> colorValueProvider) {
            $.NPE(colorValueProvider);
            requirePositive(w);
            requirePositive(h);
            return new $.Producer<BufferedImage>() {
                @Override
                public BufferedImage produce() {
                    BufferedImage b = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = b.createGraphics();
                    g.setPaint(colorValueProvider.apply());
                    g.fillRect(0, 0, w, h);
                    return b;
                }
            };
        }

        public static $.Provider<BufferedImage> source(final InputStream is) {
            return new $.Provider<BufferedImage>() {
                @Override
                public BufferedImage get() {
                    return read(is);
                }
            };
        }

        public static $.Provider<BufferedImage> source(final File file) {
            return new $.Provider<BufferedImage>() {
                @Override
                public BufferedImage get() {
                    return read(file);
                }
            };
        }

        public static $.Val<BufferedImage> source(final BufferedImage image) {
            return $.F.provides(image);
        }
    }

}
