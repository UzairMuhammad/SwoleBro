import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview


@Preview
@Composable
private fun VectorPreview() {
	Image(SweatDroplets, null)
}

private var _SweatDroplets: ImageVector? = null

public val SweatDroplets: ImageVector
	get() {
		if (_SweatDroplets != null) {
			return _SweatDroplets!!
		}
		_SweatDroplets = ImageVector.Builder(
			name = "SweatDroplets",
			defaultWidth = 128.dp,
			defaultHeight = 128.dp,
			viewportWidth = 128f,
			viewportHeight = 128f
		).apply {
			group {
				path(
					fill = SolidColor(Color(0xFF4FC3F7)),
					fillAlpha = 1.0f,
					stroke = null,
					strokeAlpha = 1.0f,
					strokeLineWidth = 1.0f,
					strokeLineCap = StrokeCap.Butt,
					strokeLineJoin = StrokeJoin.Miter,
					strokeLineMiter = 1.0f,
					pathFillType = PathFillType.NonZero
				) {
					moveTo(59.2f, 94.33f)
					curveToRelative(-1.66f, 16.44f, -14.08f, 27.62f, -30.55f, 26.34f)
					reflectiveCurveToRelative(-23.29f, -15.18f, -22.45f, -28.34f)
					curveToRelative(0.89f, -14.08f, 7.68f, -20.35f, 12f, -29f)
					curveToRelative(6f, -12f, 3.92f, -21.06f, 5.16f, -25.9f)
					curveToRelative(0.54f, -2.11f, 3.09f, -2.93f, 4.74f, -1.51f)
					curveToRelative(9.74f, 8.35f, 33.58f, 33.86f, 31.1f, 58.41f)
					close()
				}
				path(
					fill = SolidColor(Color(0xFF4FC3F7)),
					fillAlpha = 1.0f,
					stroke = null,
					strokeAlpha = 1.0f,
					strokeLineWidth = 1.0f,
					strokeLineCap = StrokeCap.Butt,
					strokeLineJoin = StrokeJoin.Miter,
					strokeLineMiter = 1.0f,
					pathFillType = PathFillType.NonZero
				) {
					moveTo(118.97f, 92.21f)
					curveToRelative(5.69f, 10.66f, 3.03f, 25.35f, -8.09f, 29.66f)
					curveToRelative(-16.67f, 6.46f, -27.67f, -5.54f, -29.67f, -25.54f)
					curveToRelative(-0.76f, -7.64f, -6.11f, -19.46f, -7.34f, -22.9f)
					curveToRelative(-0.53f, -1.5f, 0.66f, -3.02f, 2.23f, -2.83f)
					curveToRelative(9.23f, 1.15f, 34.37f, 5.7f, 42.87f, 21.61f)
					close()
				}
				path(
					fill = SolidColor(Color(0xFF4FC3F7)),
					fillAlpha = 1.0f,
					stroke = null,
					strokeAlpha = 1.0f,
					strokeLineWidth = 1.0f,
					strokeLineCap = StrokeCap.Butt,
					strokeLineJoin = StrokeJoin.Miter,
					strokeLineMiter = 1.0f,
					pathFillType = PathFillType.NonZero
				) {
					moveTo(107.28f, 11.49f)
					curveToRelative(13.56f, 9.43f, 17.84f, 27.88f, 8.43f, 41.46f)
					curveToRelative(-6.51f, 9.38f, -24.41f, 11.5f, -35.51f, 4.38f)
					curveToRelative(-11.88f, -7.62f, -15.13f, -15.55f, -22f, -24f)
					curveToRelative(-6.64f, -8.16f, -14.2f, -12.74f, -17.83f, -16.18f)
					curveToRelative(-1.58f, -1.5f, -1.06f, -4.13f, 0.98f, -4.88f)
					curveToRelative(12.03f, -4.46f, 45.67f, -14.87f, 65.93f, -0.78f)
					close()
				}
			}
			path(
				fill = SolidColor(Color(0xFFB3E5FC)),
				fillAlpha = 1.0f,
				stroke = null,
				strokeAlpha = 1.0f,
				strokeLineWidth = 1.0f,
				strokeLineCap = StrokeCap.Butt,
				strokeLineJoin = StrokeJoin.Miter,
				strokeLineMiter = 1.0f,
				pathFillType = PathFillType.NonZero
			) {
				moveTo(53.42f, 94.82f)
				curveToRelative(-0.37f, 1.11f, -0.89f, 2.2f, -1.73f, 3f)
				curveToRelative(-0.85f, 0.81f, -2.07f, 1.29f, -3.2f, 1.02f)
				curveToRelative(-1.66f, -0.39f, -2.58f, -2.13f, -3.16f, -3.73f)
				curveToRelative(-1.59f, -4.38f, -2.18f, -9.11f, -1.72f, -13.74f)
				curveToRelative(0.26f, -2.64f, 1.23f, -6.09f, 4.22f, -6.81f)
				curveToRelative(2f, -0.48f, 3.27f, 1.08f, 4.09f, 2.7f)
				curveToRelative(2.52f, 4.97f, 3.26f, 12.31f, 1.5f, 17.56f)
				close()
			}
			path(
				fill = SolidColor(Color(0xFFB3E5FC)),
				fillAlpha = 1.0f,
				stroke = null,
				strokeAlpha = 1.0f,
				strokeLineWidth = 1.0f,
				strokeLineCap = StrokeCap.Butt,
				strokeLineJoin = StrokeJoin.Miter,
				strokeLineMiter = 1.0f,
				pathFillType = PathFillType.NonZero
			) {
				moveTo(114.92f, 99.24f)
				curveToRelative(-0.02f, 0.88f, -0.16f, 1.81f, -0.76f, 2.45f)
				curveToRelative(-1.06f, 1.15f, -2.97f, 0.78f, -4.36f, 0.07f)
				curveToRelative(-3.15f, -1.6f, -5.55f, -4.45f, -7.09f, -7.63f)
				curveToRelative(-1f, -2.09f, -2.52f, -5.92f, -0.28f, -7.7f)
				curveToRelative(1.56f, -1.24f, 3.97f, -1.28f, 5.57f, -0.21f)
				curveToRelative(3.99f, 2.68f, 7.05f, 8.25f, 6.92f, 13.02f)
				close()
			}
			path(
				fill = SolidColor(Color(0xFFB3E5FC)),
				fillAlpha = 1.0f,
				stroke = null,
				strokeAlpha = 1.0f,
				strokeLineWidth = 1.0f,
				strokeLineCap = StrokeCap.Butt,
				strokeLineJoin = StrokeJoin.Miter,
				strokeLineMiter = 1.0f,
				pathFillType = PathFillType.NonZero
			) {
				moveTo(106.97f, 24.04f)
				curveToRelative(-2.48f, 3.92f, -8.47f, 2.53f, -13.35f, -4.05f)
				curveToRelative(-2.46f, -3.3f, -6.35f, -4.8f, -8.52f, -5.77f)
				curveToRelative(-4.04f, -1.81f, -0.38f, -4.26f, 1.47f, -4.44f)
				curveToRelative(5.35f, -0.51f, 8.35f, -0.11f, 13.2f, 2.31f)
				curveToRelative(3.82f, 1.92f, 10.04f, 7.47f, 7.2f, 11.95f)
				close()
			}
		}.build()
		return _SweatDroplets!!
	}

