package com.zeng.chuan.sample;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zeng.chuan.marquee.MarqueeTextView;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private MarqueeTextView mMarqueeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMarqueeTextView = findViewById(R.id.mt_content);
        mMarqueeTextView.setText(Html.fromHtml("<font color='#ff00ff'>github</font>" +
                        "<img src=\"https://github.com/fluidicon.png\">最好的文字走马灯",
                new URLImageParser(mMarqueeTextView), null));

        initbar();
    }

    private void initbar() {
        SeekBar seekbar = findViewById(R.id.seekbar);
        seekbar.setMax(20);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMarqueeTextView.setSpeed(progress);
                TextView speed_pt = findViewById(R.id.speed_pt);
                speed_pt.setText(progress + "px");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public class URLImageParser implements Html.ImageGetter {
        MarqueeTextView mTextView;

        public URLImageParser(MarqueeTextView textView) {
            this.mTextView = textView;
        }

        @Override
        public Drawable getDrawable(final String source) {
            final URLDrawable urlDrawable = new URLDrawable();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Bitmap bmp = Glide.with(MainActivity.this)
                                .asBitmap()
                                .load(source)
                                .submit(55, 40)
                                .get();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                urlDrawable.bitmap = bmp;
                                urlDrawable.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
                                mTextView.invalidate();
                                mTextView.setText(mTextView.getText());
                            }
                        });

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();


//            ImageLoader.getInstance().loadImage(source,
//                    new SimpleImageLoadingListener() {
//                        @Override
//                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                            urlDrawable.bitmap = loadedImage;
//                            urlDrawable.setBounds(0, 0, loadedImage.getWidth(), loadedImage.getHeight());
//                            mTextView.invalidate();
//                            mTextView.setText(mTextView.getText());
//                        }
//                    });
            return urlDrawable;
        }
    }

    public class URLDrawable extends BitmapDrawable {
        protected Bitmap bitmap;

        @Override
        public void draw(Canvas canvas) {
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 0, 0, getPaint());
            }
        }
    }


}


