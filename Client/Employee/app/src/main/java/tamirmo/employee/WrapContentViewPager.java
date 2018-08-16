package tamirmo.employee;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Tamir on 13/06/2018.
 * View pager that handles a bug with height wrap content.
 * This pager sets it's height as it's highest child.
 */

public class WrapContentViewPager extends ViewPager {
    public WrapContentViewPager(@NonNull Context context) {
        super(context);
    }

    public WrapContentViewPager(@NonNull Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = 0;
        for(int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if(h > height) height = h;
        }

        if (height != 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
