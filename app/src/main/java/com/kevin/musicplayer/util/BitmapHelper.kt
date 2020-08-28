package com.kevin.musicplayer.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.core.content.ContextCompat
import android.util.Log
import com.kevin.musicplayer.R


private const val BITMAP_SCALE = 1f
private const val BLUR_RADIUS = 10f

class BitmapHelper {

	companion object {

		/**
		 * @return a blurred [Bitmap] of the [image]
		 * Not used for now because the renderscript causes some warning
		 * E/RenderScript: CONTEXT CALLBACK: Warning:::A very large global_work_size was requested, this will a very long time to execute or result in an error due to lack of resources
		 * E/RenderScript: CONTEXT CALLBACK: Error:::Event terminated with internal error code -2147467262 ('EVENT_JOB_CANCELLED')
		 * E/RenderScript: GPU kernel(0) returned error code: -2147467262
		 * E/RenderScript: Attempting CPU fallback
		 */
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

		/**
		 * @return a [Bitmap] from the [Drawable]
		 */
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

		/**
		 * @return [GradientDrawable] containing a gradient from two selected pixels from the [bitmap]
		 */
		fun gradientFromBitmap(bitmap: Bitmap): GradientDrawable {
			val gradient1 = bitmap.getPixel(bitmap.width / 4, bitmap.height / 4)
			val gradient2 = bitmap.getPixel(bitmap.width - (bitmap.width / 4), bitmap.height - (bitmap.height / 4))
			return GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(gradient1, gradient2))
		}

		/**
		 * Added this function due to warning from the Renderscript
		 */
		fun fastBlur(sentBitmap: Bitmap, scale: Float, radius: Int): Bitmap? {
			var tmpBitmap = sentBitmap

			val width = Math.round(tmpBitmap.width * scale)
			val height = Math.round(tmpBitmap.height * scale)
			tmpBitmap = Bitmap.createScaledBitmap(tmpBitmap, width, height, false)

			val bitmap = tmpBitmap.copy(tmpBitmap.config, true)

			if (radius < 1) {
				return null
			}

			val w = bitmap.width
			val h = bitmap.height

			val pix = IntArray(w * h)
			Log.e("pix", w.toString() + " " + h + " " + pix.size)
			bitmap.getPixels(pix, 0, w, 0, 0, w, h)

			val wm = w - 1
			val hm = h - 1
			val wh = w * h
			val div = radius + radius + 1

			val r = IntArray(wh)
			val g = IntArray(wh)
			val b = IntArray(wh)
			var rsum: Int
			var gsum: Int
			var bsum: Int
			var x: Int
			var y: Int
			var i: Int
			var p: Int
			var yp: Int
			var yi: Int
			var yw: Int
			val vmin = IntArray(Math.max(w, h))

			var divsum = div + 1 shr 1
			divsum *= divsum
			val dv = IntArray(256 * divsum)
			i = 0
			while (i < 256 * divsum) {
				dv[i] = i / divsum
				i++
			}

			yi = 0
			yw = yi

			val stack = Array(div) { IntArray(3) }
			var stackpointer: Int
			var stackstart: Int
			var sir: IntArray
			var rbs: Int
			val r1 = radius + 1
			var routsum: Int
			var goutsum: Int
			var boutsum: Int
			var rinsum: Int
			var ginsum: Int
			var binsum: Int

			y = 0
			while (y < h) {
				bsum = 0
				gsum = bsum
				rsum = gsum
				boutsum = rsum
				goutsum = boutsum
				routsum = goutsum
				binsum = routsum
				ginsum = binsum
				rinsum = ginsum
				i = -radius
				while (i <= radius) {
					p = pix[yi + Math.min(wm, Math.max(i, 0))]
					sir = stack[i + radius]
					sir[0] = p and 0xff0000 shr 16
					sir[1] = p and 0x00ff00 shr 8
					sir[2] = p and 0x0000ff
					rbs = r1 - Math.abs(i)
					rsum += sir[0] * rbs
					gsum += sir[1] * rbs
					bsum += sir[2] * rbs
					if (i > 0) {
						rinsum += sir[0]
						ginsum += sir[1]
						binsum += sir[2]
					} else {
						routsum += sir[0]
						goutsum += sir[1]
						boutsum += sir[2]
					}
					i++
				}
				stackpointer = radius

				x = 0
				while (x < w) {

					r[yi] = dv[rsum]
					g[yi] = dv[gsum]
					b[yi] = dv[bsum]

					rsum -= routsum
					gsum -= goutsum
					bsum -= boutsum

					stackstart = stackpointer - radius + div
					sir = stack[stackstart % div]

					routsum -= sir[0]
					goutsum -= sir[1]
					boutsum -= sir[2]

					if (y == 0) {
						vmin[x] = Math.min(x + radius + 1, wm)
					}
					p = pix[yw + vmin[x]]

					sir[0] = p and 0xff0000 shr 16
					sir[1] = p and 0x00ff00 shr 8
					sir[2] = p and 0x0000ff

					rinsum += sir[0]
					ginsum += sir[1]
					binsum += sir[2]

					rsum += rinsum
					gsum += ginsum
					bsum += binsum

					stackpointer = (stackpointer + 1) % div
					sir = stack[stackpointer % div]

					routsum += sir[0]
					goutsum += sir[1]
					boutsum += sir[2]

					rinsum -= sir[0]
					ginsum -= sir[1]
					binsum -= sir[2]

					yi++
					x++
				}
				yw += w
				y++
			}
			x = 0
			while (x < w) {
				bsum = 0
				gsum = bsum
				rsum = gsum
				boutsum = rsum
				goutsum = boutsum
				routsum = goutsum
				binsum = routsum
				ginsum = binsum
				rinsum = ginsum
				yp = -radius * w
				i = -radius
				while (i <= radius) {
					yi = Math.max(0, yp) + x

					sir = stack[i + radius]

					sir[0] = r[yi]
					sir[1] = g[yi]
					sir[2] = b[yi]

					rbs = r1 - Math.abs(i)

					rsum += r[yi] * rbs
					gsum += g[yi] * rbs
					bsum += b[yi] * rbs

					if (i > 0) {
						rinsum += sir[0]
						ginsum += sir[1]
						binsum += sir[2]
					} else {
						routsum += sir[0]
						goutsum += sir[1]
						boutsum += sir[2]
					}

					if (i < hm) {
						yp += w
					}
					i++
				}
				yi = x
				stackpointer = radius
				y = 0
				while (y < h) {
					// Preserve alpha channel: ( 0xff000000 & pix[yi] )
					pix[yi] = -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]

					rsum -= routsum
					gsum -= goutsum
					bsum -= boutsum

					stackstart = stackpointer - radius + div
					sir = stack[stackstart % div]

					routsum -= sir[0]
					goutsum -= sir[1]
					boutsum -= sir[2]

					if (x == 0) {
						vmin[y] = Math.min(y + r1, hm) * w
					}
					p = x + vmin[y]

					sir[0] = r[p]
					sir[1] = g[p]
					sir[2] = b[p]

					rinsum += sir[0]
					ginsum += sir[1]
					binsum += sir[2]

					rsum += rinsum
					gsum += ginsum
					bsum += binsum

					stackpointer = (stackpointer + 1) % div
					sir = stack[stackpointer]

					routsum += sir[0]
					goutsum += sir[1]
					boutsum += sir[2]

					rinsum -= sir[0]
					ginsum -= sir[1]
					binsum -= sir[2]

					yi += w
					y++
				}
				x++
			}

			Log.e("pix", w.toString() + " " + h + " " + pix.size)
			bitmap.setPixels(pix, 0, w, 0, 0, w, h)

			return bitmap
		}

		fun transparentOverlayBitmap(bitmap: Bitmap, context: Context): Bitmap {
			val canvas = Canvas(bitmap)
			canvas.drawColor(ContextCompat.getColor(context, R.color.blackTransparent50))
			return bitmap
		}

		fun blurAlbumArt(albumArtUri: String, context: Context): Bitmap {
			val options = BitmapFactory.Options().apply { inSampleSize = 18 }
			val bitmap = BitmapFactory.decodeFile(albumArtUri, options)
			val blurredBitmap = fastBlur(bitmap, 1f, 12)
			return transparentOverlayBitmap(blurredBitmap!!, context)
		}
	}
}