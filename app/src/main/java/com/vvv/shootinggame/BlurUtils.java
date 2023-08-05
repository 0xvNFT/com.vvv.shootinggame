package com.vvv.shootinggame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.vvv.shootinggame.fragment.GamingFragment;

public class BlurUtils {

    public static Drawable applyBlur(Context context, Drawable drawable, int radius) {
        if (drawable == null) {
            return null;
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        if (width <= 0 || height <= 0) {
            return drawable;
        }

        Bitmap bitmap = drawableToBitmap(drawable);

        Bitmap blurredBitmap = fastBlur(context, bitmap, radius);

        Drawable blurredDrawable = new BitmapDrawable(context.getResources(), blurredBitmap);

        return blurredDrawable;
    }

    private static Bitmap fastBlur(Context context, Bitmap bitmap, int radius) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        return output;
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        if (width <= 0 || height <= 0) {
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    private static Bitmap applyBlur(Context context, Bitmap bitmap, int radius) {
        RenderScript rs = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setInput(input);
        script.setRadius(radius);
        script.forEach(output);
        output.copyTo(bitmap);
        rs.destroy();
        return bitmap;
    }

    public static Drawable applyBlur(GamingFragment gamingFragment, Drawable originalDrawable, int radius) {
        return gamingFragment.applyBlur(gamingFragment.getActivity().getApplicationContext(), originalDrawable, radius);
    }
}
