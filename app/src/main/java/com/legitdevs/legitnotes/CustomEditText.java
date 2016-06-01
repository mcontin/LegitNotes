package com.legitdevs.legitnotes;

/**
 * Created by Gabry on 26/05/2016.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.Html.ImageGetter;
import android.text.Html.TagHandler;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ToggleButton;
//import windyzboy.github.io.customeeditor.UnderlineSpan;
import android.text.style.UnderlineSpan;

public class CustomEditText extends EditText {
    public static final String TAG = "DroidWriter";
    private static final int STYLE_BOLD = 0;
    private static final int STYLE_ITALIC = 1;
    private static final int STYLE_UNDERLINED = 2;
    private ToggleButton boldToggle;
    private ToggleButton italicsToggle;
    private ToggleButton underlineToggle;
    private Html.ImageGetter imageGetter;
    private boolean isDeleteCharaters = false;
    private int currentColor = -1;
    private CustomEditText.EventBack eventBack;

    public CustomEditText.EventBack getEventBack() {
        return this.eventBack;
    }

    public void setEventBack(CustomEditText.EventBack eventBack) {
        this.eventBack = eventBack;
    }

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if(event.getKeyCode() == 4) {
            this.eventBack.close();
        } else {
            this.eventBack.show();
        }

        return super.dispatchKeyEvent(event);
    }

    public CustomEditText(Context context) {
        super(context);
        this.initialize();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initialize();
    }

    private void initialize() {
        this.imageGetter = new Html.ImageGetter() {
            public Drawable getDrawable(String source) {
                return null;
            }
        };
        this.addTextChangedListener(new CustomEditText.DWTextWatcher(null));
    }

    private void toggleStyle(int style) {
        int selectionStart = this.getSelectionStart();
        int selectionEnd = this.getSelectionEnd();
        if(selectionStart > selectionEnd) {
            int temp = selectionEnd;
            selectionEnd = selectionStart;
            selectionStart = temp;
        }

        if(selectionEnd > selectionStart) {
            switch(style) {
                case 0:
                    this.boldButtonClick(selectionStart, selectionEnd);
                    break;
                case 1:
                    this.italicButtonClick(selectionStart, selectionEnd);
                    break;
                case 2:
                    this.underlineButtonClick(selectionStart, selectionEnd);
            }
        }

    }

    private void underlineButtonClick(int selectionStart, int selectionEnd) {
        boolean exists = false;
        Editable str = this.getText();
        UnderlineSpan[] underSpan = (UnderlineSpan[])str.getSpans(selectionStart, selectionEnd, UnderlineSpan.class);
        int underlineStart = -1;
        int underlineEnd = -1;
        UnderlineSpan[] arr$ = underSpan;
        int len$ = underSpan.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            UnderlineSpan styleSpan = arr$[i$];
            if(str.getSpanStart(styleSpan) < selectionStart) {
                underlineStart = str.getSpanStart(styleSpan);
            }

            if(str.getSpanEnd(styleSpan) > selectionEnd) {
                underlineEnd = str.getSpanEnd(styleSpan);
            }

            str.removeSpan(styleSpan);
            exists = true;
        }

        if(underlineStart > -1) {
            str.setSpan(new UnderlineSpan(), underlineStart, selectionStart, 33);
        }

        if(underlineEnd > -1) {
            str.setSpan(new UnderlineSpan(), selectionEnd, underlineEnd, 33);
        }

        if(!exists) {
            str.setSpan(new UnderlineSpan(), selectionStart, selectionEnd, 18);
        } else {
            this.underlineToggle.setChecked(false);
        }

        this.setSelection(selectionStart, selectionEnd);
    }

    private void italicButtonClick(int selectionStart, int selectionEnd) {
        this.handleStyleSpannable(selectionStart, selectionEnd, 2);
    }

    private void boldButtonClick(int selectionStart, int selectionEnd) {
        this.handleStyleSpannable(selectionStart, selectionEnd, 1);
    }

    private void handleStyleSpannable(int selectionStart, int selectionEnd, int type) {
        boolean exists = false;
        Editable str = this.getText();
        StyleSpan[] styleSpans = (StyleSpan[])str.getSpans(selectionStart, selectionEnd, StyleSpan.class);
        int styleStart = -1;
        int styleEnd = -1;
        StyleSpan[] arr$ = styleSpans;
        int len$ = styleSpans.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            StyleSpan styleSpan = arr$[i$];
            if(styleSpan.getStyle() == type) {
                if(str.getSpanStart(styleSpan) < selectionStart) {
                    styleStart = str.getSpanStart(styleSpan);
                }

                if(str.getSpanEnd(styleSpan) > selectionEnd) {
                    styleEnd = str.getSpanEnd(styleSpan);
                }

                str.removeSpan(styleSpan);
                exists = true;
            }
        }

        if(styleStart > -1) {
            str.setSpan(new StyleSpan(type), styleStart, selectionStart, 33);
        }

        if(styleEnd > -1) {
            str.setSpan(new StyleSpan(type), selectionEnd, styleEnd, 33);
        }

        if(!exists) {
            str.setSpan(new StyleSpan(type), selectionStart, selectionEnd, 18);
        } else {
            switch(type) {
                case 1:
                    this.boldToggle.setChecked(false);
                    break;
                case 2:
                    this.italicsToggle.setChecked(false);
            }
        }

        this.setSelection(selectionStart, selectionEnd);
    }

    public void onSelectionChanged(int selStart, int selEnd) {
        boolean boldExists = false;
        boolean italicsExists = false;
        boolean underlinedExists = false;
        CharacterStyle[] styleSpans;
        int i;
        if(selStart > 0 && selStart == selEnd) {
            styleSpans = (CharacterStyle[])this.getText().getSpans(selStart - 1, selStart, CharacterStyle.class);

            for(i = 0; i < styleSpans.length; ++i) {
                if(styleSpans[i] instanceof StyleSpan) {
                    if(((StyleSpan)styleSpans[i]).getStyle() == 1) {
                        boldExists = true;
                    } else if(((StyleSpan)styleSpans[i]).getStyle() == 2) {
                        italicsExists = true;
                    } else if(((StyleSpan)styleSpans[i]).getStyle() == 3) {
                        italicsExists = true;
                        boldExists = true;
                    }
                } else if(styleSpans[i] instanceof UnderlineSpan) {
                    underlinedExists = true;
                }
            }
        } else if(!TextUtils.isEmpty(this.getText())) {
            styleSpans = (CharacterStyle[])this.getText().getSpans(selStart, selEnd, CharacterStyle.class);

            for(i = 0; i < styleSpans.length; ++i) {
                if(styleSpans[i] instanceof StyleSpan) {
                    if(((StyleSpan)styleSpans[i]).getStyle() == 1) {
                        if(this.getText().getSpanStart(styleSpans[i]) <= selStart && this.getText().getSpanEnd(styleSpans[i]) >= selEnd) {
                            boldExists = true;
                        }
                    } else if(((StyleSpan)styleSpans[i]).getStyle() == 2) {
                        if(this.getText().getSpanStart(styleSpans[i]) <= selStart && this.getText().getSpanEnd(styleSpans[i]) >= selEnd) {
                            italicsExists = true;
                        }
                    } else if(((StyleSpan)styleSpans[i]).getStyle() == 3 && this.getText().getSpanStart(styleSpans[i]) <= selStart && this.getText().getSpanEnd(styleSpans[i]) >= selEnd) {
                        italicsExists = true;
                        boldExists = true;
                    }
                } else if(styleSpans[i] instanceof UnderlineSpan && this.getText().getSpanStart(styleSpans[i]) <= selStart && this.getText().getSpanEnd(styleSpans[i]) >= selEnd) {
                    underlinedExists = true;
                }
            }
        }

        if(this.boldToggle != null) {
            if(boldExists) {
                this.boldToggle.setChecked(true);
            } else {
                this.boldToggle.setChecked(false);
            }
        }

        if(this.italicsToggle != null) {
            if(italicsExists) {
                this.italicsToggle.setChecked(true);
            } else {
                this.italicsToggle.setChecked(false);
            }
        }

        if(this.underlineToggle != null) {
            if(underlinedExists) {
                this.underlineToggle.setChecked(true);
            } else {
                this.underlineToggle.setChecked(false);
            }
        }

    }

    public Spanned getSpannedText() {
        return this.getText();
    }

    public void setSpannedText(Spanned text) {
        this.setText(text);
    }

    public String getStringText() {
        return this.getText().toString();
    }

    public void setStringText(String text) {
        this.setText(text);
    }

    public String getTextHTML() {
        return Html.toHtml(this.getText());
    }

    public void setTextHTML(String text) {
        this.setText(Html.fromHtml(text, this.imageGetter, (Html.TagHandler)null));
    }

    public void setImageGetter(Html.ImageGetter imageGetter) {
        this.imageGetter = imageGetter;
    }

    public void setBoldToggleButton(ToggleButton button) {
        this.boldToggle = button;
        this.boldToggle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CustomEditText.this.toggleStyle(0);
            }
        });
    }

    public void setItalicsToggleButton(ToggleButton button) {
        this.italicsToggle = button;
        this.italicsToggle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CustomEditText.this.toggleStyle(1);
            }
        });
    }

    public void setUnderlineToggleButton(ToggleButton button) {
        this.underlineToggle = button;
        this.underlineToggle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CustomEditText.this.toggleStyle(2);
            }
        });
    }

    public void setColor(int color, int selectionStart, int selectionEnd) {
        this.currentColor = color;
        if(selectionStart > selectionEnd) {
            int spannable = selectionEnd;
            selectionEnd = selectionStart;
            selectionStart = spannable;
        }

        if(selectionEnd > selectionStart) {
            Editable var14 = this.getText();
            ForegroundColorSpan[] appliedStyles = (ForegroundColorSpan[])var14.getSpans(selectionStart, selectionEnd, ForegroundColorSpan.class);
            if(appliedStyles != null && appliedStyles.length > 0) {
                int colorStart = -1;
                int colorEnd = -1;
                int beforeColor = 0;
                int afterColor = 0;
                ForegroundColorSpan[] arr$ = appliedStyles;
                int len$ = appliedStyles.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    ForegroundColorSpan foregroundColorSpan = arr$[i$];
                    if(var14.getSpanStart(foregroundColorSpan) < selectionStart) {
                        colorStart = var14.getSpanStart(foregroundColorSpan);
                        beforeColor = foregroundColorSpan.getForegroundColor();
                    }

                    if(var14.getSpanEnd(foregroundColorSpan) > selectionEnd) {
                        colorEnd = var14.getSpanEnd(foregroundColorSpan);
                        afterColor = foregroundColorSpan.getForegroundColor();
                    }

                    var14.removeSpan(foregroundColorSpan);
                }

                if(colorStart > -1) {
                    var14.setSpan(new ForegroundColorSpan(beforeColor), colorStart, selectionStart, 33);
                }

                if(colorEnd > -1) {
                    var14.setSpan(new ForegroundColorSpan(afterColor), selectionEnd, colorEnd, 33);
                }

                var14.setSpan(new ForegroundColorSpan(color), selectionStart, selectionEnd, 33);
            } else {
                var14.setSpan(new ForegroundColorSpan(color), selectionStart, selectionEnd, 33);
            }

            this.setSelection(selectionStart, selectionEnd);
        }

    }

    private class DWTextWatcher implements TextWatcher {
        private int beforeChangeTextLength;
        private int appendTextLength;

        private DWTextWatcher(Object o) {
            this.beforeChangeTextLength = 0;
            this.appendTextLength = 0;
        }

        public void afterTextChanged(Editable editable) {
            int position = Selection.getSelectionStart(CustomEditText.this.getText());
            this.appendTextLength = Math.abs(position - this.beforeChangeTextLength);
            if(this.appendTextLength != 0 && !CustomEditText.this.isDeleteCharaters) {
                if(position < 0) {
                    position = 0;
                }

                if(position > 0) {
                    CharacterStyle[] appliedStyles = (CharacterStyle[])editable.getSpans(position - 1, position, CharacterStyle.class);
                    StyleSpan currentBoldSpan = null;
                    StyleSpan currentItalicSpan = null;
                    UnderlineSpan currentAgsUnderlineSpan = null;
                    ForegroundColorSpan currentForegroundColorSpan = null;

                    for(int i = 0; i < appliedStyles.length; ++i) {
                        if(appliedStyles[i] instanceof StyleSpan) {
                            if(((StyleSpan)appliedStyles[i]).getStyle() == 1) {
                                currentBoldSpan = (StyleSpan)appliedStyles[i];
                            } else if(((StyleSpan)appliedStyles[i]).getStyle() == 2) {
                                currentItalicSpan = (StyleSpan)appliedStyles[i];
                            }
                        } else if(appliedStyles[i] instanceof UnderlineSpan) {
                            currentAgsUnderlineSpan = (UnderlineSpan)appliedStyles[i];
                        } else if(appliedStyles[i] instanceof ForegroundColorSpan && currentForegroundColorSpan == null) {
                            currentForegroundColorSpan = (ForegroundColorSpan)appliedStyles[i];
                        }
                    }

                    this.handleInsertBoldCharacter(editable, position, currentBoldSpan);
                    this.handleInsertItalicCharacter(editable, position, currentItalicSpan);
                    this.handleInsertUnderlineCharacter(editable, position, currentAgsUnderlineSpan);
                    this.handleInsertColorCharacter(editable, position, currentForegroundColorSpan);
                }

            }
        }

        private void handleInsertColorCharacter(Editable editable, int position, ForegroundColorSpan currentForegroundColorSpan) {
            int colorEndNextSpan;
            if(currentForegroundColorSpan != null) {
                if(currentForegroundColorSpan.getForegroundColor() != CustomEditText.this.currentColor) {
                    int nextSpan = editable.getSpanStart(currentForegroundColorSpan);
                    colorEndNextSpan = editable.getSpanEnd(currentForegroundColorSpan);
                    if(position == colorEndNextSpan) {
                        ForegroundColorSpan oldColor = this.getNextForegroundColorSpan(editable, position);
                        if(oldColor != null && CustomEditText.this.currentColor == oldColor.getForegroundColor()) {
                            int colorEndNextSpan1 = editable.getSpanEnd(oldColor);
                            editable.removeSpan(currentForegroundColorSpan);
                            editable.removeSpan(oldColor);
                            editable.setSpan(new ForegroundColorSpan(currentForegroundColorSpan.getForegroundColor()), nextSpan, colorEndNextSpan - this.appendTextLength, 34);
                            editable.setSpan(new ForegroundColorSpan(oldColor.getForegroundColor()), position - this.appendTextLength, colorEndNextSpan1, 34);
                            return;
                        }
                    }

                    editable.removeSpan(currentForegroundColorSpan);
                    if(position - this.appendTextLength < colorEndNextSpan && nextSpan != colorEndNextSpan) {
                        int oldColor1 = currentForegroundColorSpan.getForegroundColor();
                        if(nextSpan < position - this.appendTextLength) {
                            editable.setSpan(new ForegroundColorSpan(oldColor1), nextSpan, position - this.appendTextLength, 34);
                        }

                        editable.setSpan(new ForegroundColorSpan(CustomEditText.this.currentColor), position - this.appendTextLength, position, 34);
                        if(position < colorEndNextSpan) {
                            editable.setSpan(new ForegroundColorSpan(oldColor1), position, colorEndNextSpan, 34);
                        }
                    } else {
                        editable.setSpan(new ForegroundColorSpan(CustomEditText.this.currentColor), position - this.appendTextLength, colorEndNextSpan, 34);
                    }
                }
            } else if(CustomEditText.this.currentColor != -1) {
                ForegroundColorSpan nextSpan1 = this.getNextForegroundColorSpan(editable, position);
                if(nextSpan1 != null) {
                    colorEndNextSpan = editable.getSpanEnd(nextSpan1);
                    if(CustomEditText.this.currentColor == nextSpan1.getForegroundColor()) {
                        editable.removeSpan(nextSpan1);
                        editable.setSpan(new ForegroundColorSpan(nextSpan1.getForegroundColor()), position - this.appendTextLength, colorEndNextSpan, 34);
                        return;
                    }
                }

                editable.setSpan(new ForegroundColorSpan(CustomEditText.this.currentColor), position - this.appendTextLength, position, 34);
            }

        }

        private ForegroundColorSpan getNextForegroundColorSpan(Editable editable, int position) {
            ForegroundColorSpan[] nextSpans = (ForegroundColorSpan[])editable.getSpans(position, position + 1, ForegroundColorSpan.class);
            return nextSpans.length > 0?nextSpans[0]:null;
        }

        private void handleInsertUnderlineCharacter(Editable editable, int position, UnderlineSpan currentAgsUnderlineSpan) {
            if(CustomEditText.this.underlineToggle != null && CustomEditText.this.underlineToggle.isChecked() && currentAgsUnderlineSpan == null) {
                editable.setSpan(new UnderlineSpan(), position - this.appendTextLength, position, 18);
            } else if(CustomEditText.this.underlineToggle != null && !CustomEditText.this.underlineToggle.isChecked() && currentAgsUnderlineSpan != null) {
                int underLineStart = editable.getSpanStart(currentAgsUnderlineSpan);
                int underLineEnd = editable.getSpanEnd(currentAgsUnderlineSpan);
                editable.removeSpan(currentAgsUnderlineSpan);
                if(underLineStart <= position - this.appendTextLength) {
                    editable.setSpan(new UnderlineSpan(), underLineStart, position - this.appendTextLength, 33);
                }

                if(underLineEnd > position) {
                    editable.setSpan(new UnderlineSpan(), position, underLineEnd, 33);
                }
            }

        }

        private void handleInsertItalicCharacter(Editable editable, int position, StyleSpan currentItalicSpan) {
            if(CustomEditText.this.italicsToggle != null && CustomEditText.this.italicsToggle.isChecked() && currentItalicSpan == null) {
                editable.setSpan(new StyleSpan(2), position - this.appendTextLength, position, 18);
            } else if(CustomEditText.this.italicsToggle != null && !CustomEditText.this.italicsToggle.isChecked() && currentItalicSpan != null) {
                int italicStart = editable.getSpanStart(currentItalicSpan);
                int italicEnd = editable.getSpanEnd(currentItalicSpan);
                editable.removeSpan(currentItalicSpan);
                if(italicStart <= position - this.appendTextLength) {
                    editable.setSpan(new StyleSpan(2), italicStart, position - this.appendTextLength, 33);
                }

                if(italicEnd > position) {
                    editable.setSpan(new StyleSpan(2), position, italicEnd, 33);
                }
            }

        }

        private void handleInsertBoldCharacter(Editable editable, int position, StyleSpan currentBoldSpan) {
            if(CustomEditText.this.boldToggle != null) {
                if(CustomEditText.this.boldToggle.isChecked() && currentBoldSpan == null) {
                    editable.setSpan(new StyleSpan(1), position - this.appendTextLength, position, 18);
                } else if(!CustomEditText.this.boldToggle.isChecked() && currentBoldSpan != null) {
                    int boldStart = editable.getSpanStart(currentBoldSpan);
                    int boldEnd = editable.getSpanEnd(currentBoldSpan);
                    editable.removeSpan(currentBoldSpan);
                    if(boldStart <= position - this.appendTextLength) {
                        editable.setSpan(new StyleSpan(1), boldStart, position - this.appendTextLength, 33);
                    }

                    if(boldEnd > position) {
                        editable.setSpan(new StyleSpan(1), position, boldEnd, 33);
                    }
                }
            }

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            int position = Selection.getSelectionStart(CustomEditText.this.getText());
            if(position < 0) {
                position = 0;
            }

            this.beforeChangeTextLength = position;
            if(count - after == 1 || s.length() == 0 && position > 0) {
                Editable editable = CustomEditText.this.getText();
                this.removeForegroundColorSpan(position, editable);
                this.removeAgsUnderlineSpan(position, editable);
                this.removeStyleSpan(position, editable, 2);
                this.removeStyleSpan(position, editable, 1);
            }

        }

        private void removeForegroundColorSpan(int position, Editable editable) {
            ForegroundColorSpan previousColorSpan = (ForegroundColorSpan)this.getPreviousForegroundColorSpan(editable, position, ForegroundColorSpan.class);
            ForegroundColorSpan[] appliedStyles = (ForegroundColorSpan[])editable.getSpans(position - 1, position, ForegroundColorSpan.class);
            if(appliedStyles.length > 0 && appliedStyles[0] != null && previousColorSpan != null && previousColorSpan.getForegroundColor() != appliedStyles[0].getForegroundColor()) {
                ForegroundColorSpan colorSpan = appliedStyles[0];
                int colorStart = editable.getSpanStart(colorSpan);
                int colorEnd = editable.getSpanEnd(colorSpan);
                editable.removeSpan(colorSpan);
                if(colorStart < position - 1) {
                    editable.setSpan(new UnderlineSpan(), colorStart, position - 1, 33);
                }

                if(colorEnd > position) {
                    editable.setSpan(new UnderlineSpan(), position, colorEnd, 33);
                }
            }

        }

        private void removeAgsUnderlineSpan(int position, Editable editable) {
            UnderlineSpan previousColorSpan = (UnderlineSpan)this.getPreviousForegroundColorSpan(editable, position, UnderlineSpan.class);
            UnderlineSpan[] appliedStyles = (UnderlineSpan[])editable.getSpans(position - 1, position, UnderlineSpan.class);
            if(appliedStyles.length > 0 && previousColorSpan == null) {
                UnderlineSpan colorSpan = appliedStyles[0];
                int underLineStart = editable.getSpanStart(colorSpan);
                int underLineEnd = editable.getSpanEnd(colorSpan);
                editable.removeSpan(colorSpan);
                if(underLineStart < position - 1) {
                    editable.setSpan(new UnderlineSpan(), underLineStart, position - 1, 33);
                }

                if(underLineEnd > position) {
                    editable.setSpan(new UnderlineSpan(), position, underLineEnd, 33);
                }
            }

        }

        private void removeStyleSpan(int position, Editable editable, int type) {
            StyleSpan previousColorSpan = (StyleSpan)this.getPreviousForegroundColorSpan(editable, position, StyleSpan.class);
            StyleSpan[] appliedStyles = (StyleSpan[])editable.getSpans(position - 1, position, StyleSpan.class);
            StyleSpan styleSpan = null;
            StyleSpan[] styleStart = appliedStyles;
            int styleEnd = appliedStyles.length;

            for(int i$ = 0; i$ < styleEnd; ++i$) {
                StyleSpan span = styleStart[i$];
                if(span.getStyle() == type) {
                    styleSpan = span;
                }
            }

            if(styleSpan != null && previousColorSpan == null) {
                int var11 = editable.getSpanStart(styleSpan);
                styleEnd = editable.getSpanEnd(styleSpan);
                editable.removeSpan(styleSpan);
                if(var11 < position - 1) {
                    editable.setSpan(new StyleSpan(type), var11, position - 1, 33);
                }

                if(styleEnd > position) {
                    editable.setSpan(new StyleSpan(type), position, styleEnd, 33);
                }
            }

        }

        private Object getPreviousForegroundColorSpan(Editable editable, int position, Class<?> clss) {
            if(position - 2 >= 0) {
                Object[] nextSpans = editable.getSpans(position - 2, position - 1, clss);
                if(nextSpans.length > 0) {
                    return nextSpans[0];
                }
            }

            return null;
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            CustomEditText.this.isDeleteCharaters = count == 0;
            if(CustomEditText.this.getText().toString().isEmpty()) {
                CharacterStyle[] appliedStyles = (CharacterStyle[])CustomEditText.this.getText().getSpans(0, CustomEditText.this.getText().length(), CharacterStyle.class);
                CharacterStyle[] arr$ = appliedStyles;
                int len$ = appliedStyles.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    CharacterStyle characterStyle = arr$[i$];
                    CustomEditText.this.getText().removeSpan(characterStyle);
                }
            }

        }
    }

    public interface EventBack {
        void close();

        void show();
    }
}
