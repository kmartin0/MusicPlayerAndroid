package com.kevin.musicplayer.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur
import com.kevin.musicplayer.R

class BitmapHelper {

	companion object {

		fun blurBitMap(context: Context, bitmapOriginal: Bitmap): Bitmap {
			val rs: RenderScript = RenderScript.create(context)

			val input = Allocation.createFromBitmap(rs, bitmapOriginal); //use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
			val output = Allocation.createTyped(rs, input.type)
			val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
			script.setRadius(23f)
			script.setInput(input)
			script.forEach(output)
			output.copyTo(bitmapOriginal)

			return bitmapOriginal
		}

		/**
		 * @return a [Bitmap] from the [Drawable]
		 */
		fun drawableToBitmap(drawable: Drawable, inSampleSize: Int = 1): Bitmap {
			if (drawable is BitmapDrawable) {
				if (drawable.bitmap != null) {
					return drawable.bitmap
				}
			}

			var bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
				Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created
			} else {
				Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
			}

			val canvas = Canvas(bitmap)
			drawable.setBounds(0, 0, canvas.width, canvas.height)
			drawable.draw(canvas)
			if (inSampleSize > 1) bitmap = Bitmap.createScaledBitmap(bitmap, drawable.intrinsicWidth / inSampleSize, drawable.intrinsicHeight / inSampleSize, false)

			return bitmap
		}

		/**
		 * @return [GradientDrawable] containing a gradient from two selected pixels from the [bitmap]
		 */
		fun gradientFromBitmap(bitmap: Bitmap): GradientDrawable {
			val gradient1 = bitmap.getPixel(bitmap.width / 4, bitmap.height / 4)
			val gradient2 = bitmap.getPixel(bitmap.width - (bitmap.width / 4), bitmap.height - (bitmap.height / 4))
			return GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(gradient1, gradient2))
		}

		fun transparentOverlayBitmap(bitmap: Bitmap, context: Context): Bitmap {
			val canvas = Canvas(bitmap)
			canvas.drawColor(ContextCompat.getColor(context, R.color.blackTransparent50))
			return bitmap
		}

		fun blurAlbumArt(mediaItemUri: Uri?, context: Context): Bitmap {

			val bitmap = AlbumArtHelper.getAlbumArtBitmap(mediaItemUri, context, 8)
			val blurredBitmap = blurBitMap(context, bitmap)

			return transparentOverlayBitmap(blurredBitmap, context)
		}
	}
}