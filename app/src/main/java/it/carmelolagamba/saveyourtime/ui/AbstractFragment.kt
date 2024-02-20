package it.carmelolagamba.saveyourtime.ui

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import it.carmelolagamba.saveyourtime.R
import kotlin.math.abs

abstract class AbstractFragment() : Fragment(), GestureDetector.OnGestureListener {


    protected var gestureDetector: GestureDetector = GestureDetector(context, this)


    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {
        //TODO("Not yet implemented")
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        //TODO("Not yet implemented")
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        innerOnScroll(distanceX, distanceY)
        return false
    }

    abstract fun innerOnScroll(x: Float, y: Float)

    override fun onLongPress(e: MotionEvent) {
        //TODO("Not yet implemented")
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val swipeThreshold = 100
        val swipeVelocityThreshold = 100
        try {
            val diffY = e2.y - e1!!.y
            val diffX = e2.x - e1!!.x
            if (abs(diffX) > abs(diffY)) {
                if (abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
                    if (diffX > 0) {
                        findNavController().navigate(R.id.navigation_home)
                    } else {
                        findNavController().navigate(R.id.navigation_info)
                    }
                }
            }
        } catch (exception: Exception) {
            Log.d("SYT", "error on swipe")
        }finally {
            return true
        }
    }

}