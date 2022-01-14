package jj.app.dynamicform.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import jj.app.dynamicform.R;
import jj.app.dynamicform.newmodel.Schema;

public class InputTextView extends LinearLayout {

    private TextView txtTitle;
    private EditText edtInput;

    public InputTextView(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public InputTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public InputTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        View view = inflate(context, R.layout.view_input_text, this);
        txtTitle = view.findViewById(R.id.txtTitle);
        edtInput = view.findViewById(R.id.edtInput);
    }


    public void setTitle(Schema view) {
        txtTitle.setText(view.getLabel());
    }

    public String getInputValue(){
        return edtInput.getText().toString();
    }
}
