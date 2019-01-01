package com.kevin.musicplayer.util

import android.content.Context
import android.renderscript.Allocation
import android.renderscript.ScriptIntrinsicBlur
import android.renderscript.RenderScript
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.renderscript.Element
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable


private const val BITMAP_SCALE = .1f
private const val BLUR_RADIUS = 25f

class BitmapHelper {

	companion object {
		fun blurBitmap(context: Context, image: Bitmap): Bitmap {
			val width = Math.round(image.width * BITMAP_SCALE)
			val height = Math.round(image.height * BITMAP_SCALE)

			val inputBitmap = Bitmap.createScaledBitmap(image, width, height, false)
			val outputBitmap = Bitmap.createBitmap(inputBitmap)

			val rs = RenderScript.create(context)
			val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
			val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
			val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
			theIntrinsic.setRadius(BLUR_RADIUS)
			theIntrinsic.setInput(tmpIn)
			theIntrinsic.forEach(tmpOut)
			tmpOut.copyTo(outputBitmap)

			return outputBitmap
		}

		fun drawableToBitmap(drawable: Drawable): Bitmap {
			if (drawable is BitmapDrawable) {
				if (drawable.bitmap != null) {
					return drawable.bitmap
				}
			}

			val bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
				Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
			} else {
				Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
			}

			val canvas = Canvas(bitmap)
			drawable.setBounds(0, 0, canvas.width, canvas.height)
			drawable.draw(canvas)
			return bitmap
		}

		fun colorToBitmap(color: Int): Bitmap {
			return Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888).also {
				Canvas(it).apply { drawColor(color) }
			}
		}

		fun gradientFromBitmap(bitmap: Bitmap): GradientDrawable {
			val gradient1 = bitmap.getPixel(bitmap.width / 4, bitmap.height / 4)
			val gradient2 = bitmap.getPixel(bitmap.width - (bitmap.width / 4), bitmap.height - (bitmap.height / 4))
			return GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(gradient1, gradient2))
		}
	}
}