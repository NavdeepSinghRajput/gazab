package com.gazabapp.customeview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.gazabapp.R;

public class TextView_custom extends TextView
{
    public TextView_custom(Context context)
    {
        super(context);
        setFont(context,null);
    }

    public TextView_custom(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        setFont(context,attrs);
    }

    public TextView_custom(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont(context,attrs);
    }

    public TextView_custom(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
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
