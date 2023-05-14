import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

open class OnSwipeTouchListener : View.OnTouchListener {
    private val MIN_DISTANCE = 100
    private var downX = 0f
    private var downY = 0f
    private var upX = 0f
    private var upY = 0f

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                return true
            }
            MotionEvent.ACTION_UP -> {
                upX = event.x
                upY = event.y

                val deltaX = downX - upX
                val deltaY = downY - upY

                if (abs(deltaX) > MIN_DISTANCE) {
                    if (deltaX < 0) {
                        onSwipeRight()
                    } else {
                        onSwipeLeft()
                    }
                } else if (abs(deltaY) > MIN_DISTANCE) {
                    if (deltaY < 0) {
                        onSwipeDown()
                    } else {
                        onSwipeUp()
                    }
                }
                return true
            }
        }
        return false
    }

    open fun onSwipeRight() {}

    open fun onSwipeLeft() {}

    open fun onSwipeUp() {}

    open fun onSwipeDown() {}
}
