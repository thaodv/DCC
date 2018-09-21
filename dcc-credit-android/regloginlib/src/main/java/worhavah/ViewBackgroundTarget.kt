package worhavah

import android.annotation.SuppressLint
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition

class ViewBackgroundTarget(view: View) : ViewTarget<View, Drawable>(view),
            Transition.ViewAdapter {
        override fun getCurrentDrawable(): Drawable? {
            return view.background
        }

        override fun setDrawable(drawable: Drawable?) {
            @SuppressLint("ObsoleteSdkInt")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.background = drawable
            } else {
                view.setBackgroundDrawable(drawable)
            }
        }

        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            if (transition == null || !transition.transition(resource, this)) {
                setResourceInternal(resource)
            } else {
                maybeUpdateAnimatable(resource)
            }
        }

        override fun onLoadStarted(placeholder: Drawable?) {
            super.onLoadStarted(placeholder)
            setResourceInternal(null)
            setDrawable(placeholder)
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            super.onLoadFailed(errorDrawable)
            setResourceInternal(null)
            setDrawable(errorDrawable)
        }

        override fun onLoadCleared(placeholder: Drawable?) {
            super.onLoadCleared(placeholder)
            setResourceInternal(null)
            setDrawable(placeholder)
        }

        private fun setResourceInternal(resource: Drawable?) {
            maybeUpdateAnimatable(resource)
            setResource(resource)
        }

        private fun setResource(resource: Drawable?) {
            setDrawable(resource)
        }

        private var animatable: Animatable? = null

        override fun onStart() {
            if (animatable != null) {
                animatable!!.start()
            }
        }

        override fun onStop() {
            if (animatable != null) {
                animatable!!.stop()
            }
        }

        private fun maybeUpdateAnimatable(resource: Drawable?) {
            if (resource is Animatable) {
                animatable = resource as Animatable?
                animatable!!.start()
            } else {
                animatable = null
            }
        }
    }