package jj.app.dynamicform.components;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jj.app.dynamicform.R;
import jj.app.dynamicform.models.Constants;
import jj.app.dynamicform.newmodel.Schema;
import jj.app.dynamicform.newmodel.Value;

public class RadioButtonView extends LinearLayout {

    private AppCompatTextView txtTitle;
    private RadioGroup rgOption;
    private  List<Pair<Schema, String>> listCondition = new ArrayList<>();

    private OnItemSelected onItemSelected;

    public void setOnItemSelected(OnItemSelected onItemSelected) {
        this.onItemSelected = onItemSelected;
    }

    public RadioButtonView(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public RadioButtonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public RadioButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        View view = inflate(context, R.layout.view_radio_button, this);
        rgOption = view.findViewById(R.id.rgOption);
        txtTitle = view.findViewById(R.id.txtTitle);

        rgOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for(Pair<Schema, String> item : listCondition) {
                    if(item.second.equals(String.valueOf(checkedId))) {
                        onItemSelected.onSelectedItem(item.first, item.second);
                    }
                }
            }
        });
    }

    public void setData(List<Value> values) {
        for (int i = 0; i < values.size(); i++) {
            Value myValue = values.get(i);
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setId(Integer.parseInt(myValue.getValue()));
            radioButton.setText(myValue.getLabel());
            if (values.equals(myValue.getValue())) {
                radioButton.setChecked(true);
            }

            rgOption.addView(radioButton);
        }
    }

    public void setTitle(String label) {
        txtTitle.setText(label);
    }


    public void setConditions(String name, List<Schema> myControlList) {
        for (Schema item : myControlList) {
            if (!TextUtils.isEmpty(item.getConditions()) && item.getType().equals(Constants.text)) {
                String condition = item.getConditions().replace("[", "").replace("]", "");
                try {
                    JSONObject jsonObject = new JSONObject(condition);

                    String nameCondition = (String) jsonObject.get("name");
                    String valueCondition = (String) jsonObject.get("value");
                    if (name.equals(nameCondition)){
                        listCondition.add(new Pair<>(item, valueCondition));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface OnItemSelected {
        void onSelectedItem(Schema view, String value);
    }
}
