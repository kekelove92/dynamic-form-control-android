package jj.app.dynamicform;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jj.app.dynamicform.components.InputTextView;
import jj.app.dynamicform.components.RadioButtonView;
import jj.app.dynamicform.models.Constants;
import jj.app.dynamicform.models.MyControl;
import jj.app.dynamicform.models.MyOptions;
import jj.app.dynamicform.newmodel.FormData;
import jj.app.dynamicform.newmodel.MyForm;
import jj.app.dynamicform.newmodel.Schema;
import jj.app.dynamicform.newmodel.Value;

public class DemoActivity extends AppCompatActivity implements View.OnClickListener {
    List<Schema> myControlList;
    LinearLayout llMain;
    private InputTextView inputTextView;
    Button button;
    MyForm myForm = new MyForm();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        llMain = findViewById(R.id.layout_main);
        myControlList = new ArrayList<>();
        getControls();
        prepareView();

        button = findViewById(R.id.btn_next);
        button.setOnClickListener(this);
    }


    private void prepareView() {
        int position = 0;
        for (int i = 0; i < myControlList.size(); i++) {
            Schema data = myControlList.get(i);
            switch (data.getType()) {
                case Constants.header:
                    if (data.getNgModel() == null) {
                        addText(data);
                        position++;
                    }
                    break;
                case Constants.radio_group:
//                    final int index = position;
                    final RadioButtonView radioButtonView = new RadioButtonView(this);
                    radioButtonView.setTitle(data.getLabel());
                    radioButtonView.setTag(data.getName());
                    radioButtonView.setData(data.getValues());
                    radioButtonView.setConditions(data.getName(), myControlList);
//                    radioButtonView.setOnItemSelected(new RadioButtonView.OnItemSelected() {
//                        @Override
//                        public void onSelectedItem(Schema view, String value) {
//                            if (inputTextView != null)
//                                llMain.removeView(inputTextView);
//                            inputTextView = new InputTextView(DemoActivity.this);
//                            inputTextView.setData(view);
//                            llMain.addView(inputTextView, index);
//                        }
//                    });

                    llMain.addView(radioButtonView);
                    position++;
                    break;
                case Constants.textarea:
                    addTextArea(data);
                    position++;
                    break;

                case Constants.text:

                    break;

            }
        }
    }

    private void addText(Schema schema) {
        String title = schema.getLabel().toString();
        String[] split = title.split("nbsp;");
        String secondString = split[1];

        String[] split2 = secondString.split("<");
        String firstString = split2[0];
        addCaption(firstString);
    }

    private void addCaption(String title) {
        TextView textTitle = new TextView(this);
        textTitle.setText(title);
        textTitle.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        textTitle.setTextSize(18f);
        textTitle.setLayoutParams(getLayoutParam());
        llMain.addView(textTitle);
    }

    public void addRadioButtons(Schema schema) {
        addCaption(schema.getLabel());

        final RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(LinearLayout.VERTICAL);

        List<Value> valueList = schema.getValues();
        for (int i = 0; i < valueList.size(); i++) {
            Value myValue = valueList.get(i);
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(Integer.parseInt(myValue.getValue()));
            radioButton.setText(myValue.getLabel());
            if (schema.getValues().equals(myValue.getValue())) {
                radioButton.setChecked(true);
            }

            radioGroup.addView(radioButton);
        }


//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
//                boolean isChecked = checkedRadioButton.isChecked();
//                if (isChecked) {
//                    Toast.makeText(DemoActivity.this, "" + checkedRadioButton.getText(), Toast.LENGTH_SHORT).show();
//                }
//                switch (checkedId) {
//                    case 0:
//
//                        break;
//                    case 1:
//
//                        break;
//                    case 2:
//
//                        break;
//                }
//            }
//        });
        radioGroup.setLayoutParams(getLayoutParam());

        llMain.addView(radioGroup);
    }

    public void addTextArea(Schema schema) {
        addCaption(schema.getLabel());
        EditText editText = new EditText(this);
        editText.setHint("Enter Value");
        editText.setMaxLines(5);
        editText.setLayoutParams(getLayoutParam());
        llMain.addView(editText);
    }

    private LinearLayout.LayoutParams getLayoutParam() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 15);
        return params;
    }

    private void getControls() {
        String data = readFile();
//        data = data.replace("\n  ", "");
        Gson gson = new Gson();
        myForm = gson.fromJson(data, MyForm.class);
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(data);
            JSONObject jsonObject = jsonObj.getJSONObject("form_data");
            JSONArray jsonArray = jsonObject.getJSONArray("schemas");
            Type type = new TypeToken<List<Schema>>() {
            }.getType();
            myControlList = new Gson().fromJson(String.valueOf(jsonArray), type);
            if (myControlList == null) myControlList = new ArrayList<>();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private String readFile() {
        String json = null;
        try {
            InputStream is = getAssets().open("dynamic_form_2.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            int a = is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_next) {
            for (int i = 0; i < myControlList.size(); i++) {
                Schema schema = myControlList.get(i);
                switch (schema.getType()) {
                    case Constants.radio_group:
                        RadioButtonView radioButtonView = llMain.findViewWithTag(schema.getName());
                        if (radioButtonView.getCheckedId() != -1 && radioButtonView.getValueInput() == null) {
                            Toast.makeText(this, "Fill data", Toast.LENGTH_SHORT).show();
                        } else {
                            String dataValue = radioButtonView.getValueInput();
                            for (Schema item : myControlList) {
                                if (item.equals(radioButtonView.getSchemaData())) {
                                    int index = myControlList.indexOf(item);
                                    List<String> stringValueInput = new ArrayList<>();
                                    stringValueInput.add(dataValue);
//                                    item.setUserData(stringValueInput);
                                    myForm.getFormData().getSchemas().get(index).setUserData(stringValueInput);
                                    break;
                                }
                            }

                            // value checked radio group
                            List<String> stringValueChecked = new ArrayList<>();
                            stringValueChecked.add(radioButtonView.getCheckedId() + "");
                            myForm.getFormData().getSchemas().get(i).setUserData(stringValueChecked);
                        }
                        System.out.println("" +  myForm.toString());
                        break;
                }
            }
        }
    }
}