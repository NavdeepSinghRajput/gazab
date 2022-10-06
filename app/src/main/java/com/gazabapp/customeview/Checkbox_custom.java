package com.gazabapp.customeview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.res.ResourcesCompat;

import com.gazabapp.R;

public class Checkbox_custom extends AppCompatCheckBox
{
    public Checkbox_custom(Context context)
    {
        super(context);
        setFont(context);
    }

    public Checkbox_custom(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont(context);
    }

    public Checkbox_custom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont(context);
    }


    private void setFont(Context context)
    {
        Typeface typeface = ResourcesCompat.getFont(context, R.font.roboto_en);
        this.setTypeface(typeface);
    }
}
