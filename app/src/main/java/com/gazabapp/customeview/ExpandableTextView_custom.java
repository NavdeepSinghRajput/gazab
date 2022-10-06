package com.gazabapp.customeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import com.gazabapp.R;


public class ExpandableTextView_custom extends TextView
{
    private static final int DEFAULT_TRIM_LENGTH = 200;
    private static final String ELLIPSIS = ".....";

    private CharSequence originalText;
    private CharSequence trimmedText;
    private BufferType bufferType;
    private boolean trim = true;
    private int trimLength;
    public ExpandableTextView_custom(Context context)
    {
        this(context, null);

        setFont(context,null);
    }
    public ExpandableTextView_custom(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setFont(context,attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView_custom);
        this.trimLength = typedArray.getInt(R.styleable.ExpandableTextView_custom_trimLength, DEFAULT_TRIM_LENGTH);
        typedArray.recycle();

        setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                trim = !trim;
                setText();
                requestFocusFromTouch();
            }
        });


    }

    private void setText() {
        super.setText(getDisplayableText(), bufferType);
    }

    private CharSequence getDisplayableText() {
        return trim ? trimmedText : originalText;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        originalText = text;
        trimmedText = getTrimmedText(text);
        bufferType = type;
        setText();
    }

    private CharSequence getTrimmedText(CharSequence text)
    {
        if (originalText != null && originalText.length() > trimLength)
        {
            return new SpannableStringBuilder(originalText, 0, trimLength + 1).append(ELLIPSIS);
        }
        else
        {
            return originalText;
        }
    }

    public CharSequence getOriginalText() {
        return originalText;
    }

    public void setTrimLength(int trimLength) {
        this.trimLength = trimLength;
        trimmedText = getTrimmedText(originalText);
        setText();
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
    public int getTrimLength()
    {
        return trimLength;
    }
}
