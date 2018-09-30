package worhavah.regloginlib.tools

import android.graphics.Rect
import android.text.method.TransformationMethod
import android.view.View

class DoNothingTransformationMethod : TransformationMethod {
    override fun onFocusChanged(
            view: View?,
            sourceText: CharSequence?,
            focused: Boolean,
            direction: Int,
            previouslyFocusedRect: Rect?
    ) {

    }

    override fun getTransformation(source: CharSequence, view: View?): CharSequence {
        return source
    }
}