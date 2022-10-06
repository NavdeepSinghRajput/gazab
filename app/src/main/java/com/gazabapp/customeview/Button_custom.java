package com.gazabapp.customeview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import androidx.core.content.res.ResourcesCompat;

import com.gazabapp.R;

public class Button_custom extends Button
{
    public Button_custom(Context context) {
        super(context);
        setFont(context,null);
    }

    public Button_custom(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont(context,attrs);
    }

    public Button_custom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont(context,attrs);
    }

    public Button_custom(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setFont(context,attrs);
    }
    private void setFont(Context context,AttributeSet attrs)
    {

        Typeface typeface = null;
        if(getTypeface().getStyle() == Typeface.NORMAL)
        {
            typeface = ResourcesCompat.getFont(context, R.font.roboto_en);
        }
        else if(getTypeface().getStyle() == Typeface.BOLD)
        {
            typeface = ResourcesCompat.getFont(context, R.font.roboto_bold);
        }
        else if(getTypeface().getStyle() == Typeface.ITALIC)
        {
            typeface = ResourcesCompat.getFont(context, R.font.roboto_italic);
        }
        else
        {
            typeface = ResourcesCompat.getFont(context, R.font.roboto_en);
        }
        this.setTypeface(typeface);
    }
}
