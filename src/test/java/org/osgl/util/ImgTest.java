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

import static org.osgl.util.Img.concat;
import static org.osgl.util.Img.source;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class ImgTest {

    private static InputStream img1() {
        URL url = ImgTest.class.getResource("/img/img1.png");
        return IO.is(url);
    }

    private static InputStream img2() {
        URL url = ImgTest.class.getResource("/img/img2.jpg");
        return IO.is(url);
    }

    private static InputStream img3() {
        return IO.is(ImgTest.class.getResource("/img/img3.png"));
    }

    static void testCrop() {
        Img.crop(source(img1()))
                .from(30, 30)
                .to(100, 100)
                .writeTo(new File("/tmp/img1_crop.gif"));
    }

    static void testResize() {
        Img.resize(source(img1()))
                .to(100, 200)
                .writeTo(new File("/tmp/img1_resize.png"));
        Img.resize(source(img1()))
                .to(2.0f)
                .writeTo(new File("/tmp/img1_resize_x2.png"));
    }

    static void testResizeKeepRatio() {
source(img1())
        .resize(100, 200)
        .keepRatio()
        .writeTo(new File("/tmp/img1_resize_keep_ratio.png"));
    }

    private static void testIllegalArguments() {
        try {
            source(img2()).resize(0.0f).writeTo("/tmp/img2_resize_zero.png");
            E.unexpected("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // pass
        }
    }

    static void testWatermark() {
        source(img1())
                .watermark("CONFIDENTIAL")
                .writeTo("/tmp/img1_watermark.png");
    }

    static void testTextWriter() {
        source(img1())
                .text("Hello World!")
                .writeTo("/tmp/img1_text.png");
    }

    private static void testCompress() {
        source(img1())
                .compress(0.01f)
                .writeTo("/tmp/img1_compress.jpeg");
    }

    private static void testCopy() {
        source(img1()).copy().writeTo("/tmp/img1_copy.jpeg");
    }

    private static void testPipeline() {
        source(img1())
                .resize(300, 400)
                .pipeline()
                .crop(50, 50, 250, 350)
                .pipeline()
                .watermark("HELLO OSGL")
                .pipeline(new Sunglass())
                .writeTo("/tmp/img1_pipeline.png");
    }

    private static void testPipelineMultiple() {
        ArrayList<Img.Processor> list = new ArrayList();
        list.add(new Img.Resizer(2.0f));
        list.add(new Img.TextWriter("OSGL"));
        source(img1()).pipeline(list).writeTo("/tmp/img1_mpipeline.png");
    }

    private static void testResizeByScale() {
        source(img2())
                .resize(0.5f)
                .writeTo("/tmp/img2_resize_scale.png");
    }

    private static void testProcessJPEGfile() {
        source(img2())
                .resize(640, 480)
                .pipeline()
                .crop(50, 50, -50, -50)
                .pipeline()
                .watermark("HELLO OSGL")
                .writeTo("/tmp/img2_pipeline.jpg");
    }

    private static void testGenerateTrackingPixel() {
        IO.write(Img.TRACKING_PIXEL_BYTES, new File("/tmp/tracking_pixel.gif"));
    }

    private static void testFlip() {
        source(img1())
                .flip()
                .writeTo("/tmp/img1_flip_h.png");
        source(img1())
                .flipVertial()
                .writeTo("/tmp/img1_flip_v.png");
    }

    private static class Sunglass extends Img.Processor {
        private float alpha = 0.3f;

        Sunglass() {}
        Sunglass(float alpha) {this.alpha = alpha;}

        @Override
        protected BufferedImage run() {
            int w = sourceWidth;
            int h = sourceHeight;
            Graphics2D g = g();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.drawImage(source, 0, 0, w, h, null);
            return target;
        }
    }
    private static void testCustomizedProcessor() {
        // style A
        new Sunglass(0.7f)
                .process(img2())
                .pipeline()
                .resize(0.5f)
                .writeTo("/tmp/img2_sunglass_style_a.jpg");
        // style B
        source(img2())
                .resize(0.3f)
                .pipeline(new Sunglass())
                .pipeline().blur(3)
                .writeTo("/tmp/img2_sunglass_style_b.png");
    }

    private static class FluentSunglass extends Img.Processor<FluentSunglass, FluentSunglass.Stage> {
        static class Stage extends Img.ProcessorStage<Stage, FluentSunglass> {
            public Stage(BufferedImage source, FluentSunglass processor) {
                super(source, processor);
            }
            public Stage alpha(float alpha) {
                processor.alpha = alpha;
                return this;
            }

            public Stage dark() {
                return alpha(0.3f);
            }

            public Stage light() {
                return alpha(0.7f);
            }

            public Stage darker() {
                return alpha(0.1f);
            }

            public Stage lighter() {
                return alpha(0.9f);
            }
        }

        float alpha;

        public FluentSunglass() {
            super();
        }

        @Override
        protected BufferedImage run() {
            return new Sunglass(alpha).source(source).run();
        }
    }

    private static void testCustomizedFluentProcessor() {
        source(img2())
                .resize(0.3f)
                .pipeline(FluentSunglass.class)
                .lighter()
                .pipeline()
                .makeNoise()
                .writeTo("/tmp/img2_f_sunglass_lighter.png");

        source(img2())
                .resize(0.3f)
                .pipeline(FluentSunglass.class)
                .dark()
                .writeTo("/tmp/img2_f_sunglass_darker.png");
    }


    private static void randomPixels() {
        source(Img.F.randomPixels(400, 200, Color.WHITE)).writeTo("/tmp/img_random_pixels.png");
    }

    private static void testWriteTextToSmallImage() {
        source(Img.F.randomPixels(200,  100, new Color(85, 85, 85)))
                .text("Hello World")
                .writeTo("/tmp/img_text.png");
    }

    private static void noises() {
        source(Img.F.randomPixels(400, 200, Color.WHITE))
                .makeNoise().writeTo("/tmp/img_noise.png");
    }

    private static void testBlur() {
        source(img1()).blur().writeTo("/tmp/img1_blur_default.png");
        source(img2()).blur(10).writeTo("/tmp/img2_blur_10.jpg");
        source(img2()).blur(2).writeTo("/tmp/img2_blur_2.jpg");
        source(img3()).blur(5).writeTo("/tmp/img3_blur_5.jpg");
    }

    private static void testConcatenate() {
        source(img2())
                .appendWith(source(img3()))
                .writeTo("/tmp/img_concat_2_3.png");
        source(img2())
                .appendTo(source(img3()))
                .writeTo("/tmp/img_concat_3_2.png");

        source(img2()).appendWith(source(img1()))
                .noScaleFix()
                .vertically()
                .writeTo("/tmp/img_concat_2_1.png");

        source(img3()).appendWith(source(img1()))
                .shinkToSmall()
                .writeTo("/tmp/img_concat_3_1.png");

        concat(source(img1()), source(img2()))
                .appendWith(source(img3()))
                .vertically()
                .writeTo("/tmp/img_concat_123.png");

        concat(source(img2()))
                .with(source(img3()))
                .vertically()
                .appendWith(source(img1()))
                .writeTo("/tmp/img_concat_231.png");
    }

    public static void main(String[] args) {
//        testConcatenate();
//        testResize();
//        testResizeByScale();
//        testResizeKeepRatio();
//        testCrop();
//        testWatermark();
//        testTextWriter();
//        testWatermark();
//        testCompress();
//        testCopy();
//        testPipeline();
//        testProcessJPEGfile();
//        testGenerateTrackingPixel();
//        testCustomizedProcessor();
//        testIllegalArguments();
//        testBlur();
//        testFlip();
//        randomPixels();
//        noises();
//        testPipelineMultiple();
        testWriteTextToSmallImage();
    }

}
