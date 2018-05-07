package tp_android.tp_android;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.content.Context;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.view.View;
import android.widget.ListView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import java.util.LinkedHashMap;


public class ListItemActivity extends AppCompatActivity {

    private Context mContext;
    private String response;
    private Button buttonZleRozpoznanie;
    private TextView ecv;
    private ImageView image;
    private Bitmap photo;
    private JSONObject jsonResponce;
    private String photoSend;
    private static final String MY_PREFS_NAME = "Setting";
    private SharedPreferences prefs;
    private JSONArray priestupky, kontroly;
    private String spz;
    private Boolean validita_poistenia = null;
    private String znacka, model = "", farba = "", poistovna = "",typ_poistenia = "",cislo_OP = "", celemeno = "", rok_poistenia = "";
    private Long recordID;
    static final int INCORECT_RECOGNIZED_PLATE = 1;
    private ArrayList<JSONObject> priestupky_list = new ArrayList<>();
    private ArrayList<JSONObject> prehliadky_list = new ArrayList<>();
    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> egovCollection;
    ExpandableListView expListView;
    private boolean help;
    private FloatingActionButton fb;

        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.item);

            Intent intent = getIntent();
            mContext = getApplicationContext();
            response = intent.getStringExtra("response");
            photoSend = (intent.getStringExtra("photoSend"));
            recordID = (intent.getLongExtra("recordID", -1));

            prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            help = prefs.getBoolean("help", true);

            byte[] decodedString = Base64.decode(photoSend, Base64.DEFAULT);
            photo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            try {
                jsonResponce = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                spz = (jsonResponce.getString("plate"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            buttonZleRozpoznanie = (Button) findViewById(R.id.buttonIncorectRecognized);
            ecv = (TextView) findViewById(R.id.editTextSpz);
            ecv.setText("Rozpoznaná ŠPZ: "+spz);
            image = (ImageView) findViewById(R.id.imageView);

            image.setImageBitmap(photo);
            buttonZleRozpoznanie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ListItemActivity.this, IncorrectRecognizedPlateActivity.class);
                    intent.putExtra("photoSend", photoSend);
                    intent.putExtra("recordID", recordID);
                    startActivityForResult(intent,INCORECT_RECOGNIZED_PLATE);
                }
            });

            try {
                JSONObject car_info = (JSONObject) jsonResponce.getJSONObject("vehicle");
                createCollection();
                expListView = (ExpandableListView) findViewById(R.id.egov_list);
                final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(this, groupList, egovCollection);
                expListView.setAdapter(expListAdapter);


                expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                    public boolean onChildClick(ExpandableListView parent, View view,int groupPosition, int childPosition, long id) {
                        final String selected = (String) expListAdapter.getChild(groupPosition, childPosition);

                        if(selected.contains("Priestupok: ")){
                            String child = String.valueOf(selected.replace("Priestupok: ",""));
                            String child2 = priestupky_list.get(Integer.parseInt(child)-1).toString();
                            alertDialogPriestupky(Integer.parseInt(child), child2);
                        }
                        if(selected.contains("Prehliadka: ")){
                            String child = String.valueOf(selected.replace("Prehliadka: ",""));
                            String child2 = prehliadky_list.get(Integer.parseInt(child)-1).toString();
                            alertDialogPrehliadky(Integer.parseInt(child), child2);
                        }
                        return true;
                    }
                });
                fb = findViewById(R.id.info);
                fb.show();
                if(help) {
                    fb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialogHelp();
                        }
                    });
                }
                else {
                    fb.hide();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), ("Žiadne e-gov data..."), Toast.LENGTH_SHORT).show();
                fb = findViewById(R.id.info);
                fb.hide();
                e.printStackTrace();
            }

        }


	private void createCollection() {

        groupList = new ArrayList<>();

        ArrayList<String> vozidlo = new ArrayList<>();
        vozidlo.clear();
        try {
            znacka = (((JSONObject) jsonResponce.getJSONObject("vehicle")).getJSONObject("parameters").getString("brand"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(znacka!= "null" && znacka!=null && JSONObject.NULL!=znacka && !znacka.equals("") && !znacka.equals(" ")){
            vozidlo.add("Značka: " + znacka);
        }

        try {
            model = (((JSONObject) jsonResponce.getJSONObject("vehicle")).getJSONObject("parameters").getString("model"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(model!= "null" && model!=null && JSONObject.NULL!=model && !model.equals("") && !model.equals(" ")){
            vozidlo.add("Model: " + model);
        }

        try {
            farba = (((JSONObject) jsonResponce.getJSONObject("vehicle")).getJSONObject("parameters").getString("color"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(farba!= "null" && farba!=null && JSONObject.NULL!=farba && !farba.equals("") && !farba.equals(" ")){
            vozidlo.add("Farba: " + farba);
        }
        if (!vozidlo.isEmpty()){
            try {
                ((JSONObject) jsonResponce.getJSONObject("vehicle")).getJSONObject("parameters").toString();
                groupList.add("Vozidlo");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ArrayList<String> poistenie = new ArrayList<>();
        poistenie.clear();

        try {
            poistovna = (((JSONObject) jsonResponce.getJSONObject("vehicle")).getJSONObject("insurance").getString("company"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(poistovna!= "null" && poistovna!=null && JSONObject.NULL!=poistovna && !poistovna.equals("") && !poistovna.equals(" ")) {
            poistenie.add("Poisťovňa: " + poistovna);
        }

        try {
            typ_poistenia = (((JSONObject) jsonResponce.getJSONObject("vehicle")).getJSONObject("insurance").getString("type"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(typ_poistenia!= "null" && typ_poistenia!=null && JSONObject.NULL!=typ_poistenia && !typ_poistenia.equals("") && !typ_poistenia.equals(" ")) {
            poistenie.add("Typ poistenia: " + typ_poistenia);
        }

        try {
            rok_poistenia = (((JSONObject) jsonResponce.getJSONObject("vehicle")).getJSONObject("insurance").getString("year"));

        }catch (JSONException e) {
            e.printStackTrace();
        }
        if (rok_poistenia!= "null" && rok_poistenia != null && JSONObject.NULL!=rok_poistenia && !rok_poistenia.equals("") && !rok_poistenia.equals(" "))  {
            poistenie.add("Rok poistenia: " + rok_poistenia);
        }
        try {
            validita_poistenia = (((JSONObject) jsonResponce.getJSONObject("vehicle")).getJSONObject("insurance").getBoolean("valid"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if ( validita_poistenia != null && JSONObject.NULL!=validita_poistenia && JSONObject.NULL!=validita_poistenia && !validita_poistenia.equals(" ")) {
            if (validita_poistenia) {
                poistenie.add("Poistenie zaplatené");
            } else {
                poistenie.add("Poistenie nezaplatené");
            }
        }

        if (!poistenie.isEmpty()) {
            try {
                ((JSONObject) jsonResponce.getJSONObject("vehicle")).getJSONObject("insurance").toString();
                groupList.add("Poistenie");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        ArrayList<String> majitel = new ArrayList<>();
        majitel.clear();

        try {
            celemeno = (((JSONObject) jsonResponce.getJSONObject("vehicle")).getJSONObject("owner").getString("fullname"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (celemeno != "null" && celemeno != null && JSONObject.NULL!=celemeno && !celemeno.equals("") && !celemeno.equals(" ")) {
            majitel.add("Celé meno: " + celemeno);
        }

        try {
            cislo_OP = (((JSONObject) jsonResponce.getJSONObject("vehicle")).getJSONObject("owner").getString("pid"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (cislo_OP!= "null" && cislo_OP != null && JSONObject.NULL!=cislo_OP &&  !cislo_OP.equals("") && !cislo_OP.equals(" ")) {
            majitel.add("Číslo OP: " + cislo_OP);
        }
        try {
             priestupky = (((JSONObject) jsonResponce.getJSONObject("vehicle")).getJSONObject("owner").getJSONArray("violations"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < priestupky.length(); i++) {
            majitel.add("Priestupok: "+(i+1));
            try {
                priestupky_list.add((JSONObject)priestupky.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (!majitel.isEmpty()) {
            try {
                ((JSONObject) jsonResponce.getJSONObject("vehicle")).getJSONObject("owner").toString();
                groupList.add("Majiteľ");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ArrayList<String> prehliadky  = new ArrayList<>();
        prehliadky.clear();
        Map<String,ArrayList<String>> prehliadky_map = new HashMap<>();
        prehliadky_map.clear();

        try {
             kontroly = (((JSONObject) jsonResponce.getJSONObject("vehicle")).getJSONArray("checks"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < kontroly.length(); i++) {
            String vysledok_servisnej_prehliadky = "";
            String typ_servisnej_prehliadky = "";
            String datum_servisnej_prehliadky = "";

            try {
                typ_servisnej_prehliadky = ((JSONObject)kontroly.get(i)).getString("service_check_type");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                vysledok_servisnej_prehliadky = ((JSONObject)kontroly.get(i)).getString("service_check_result");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                datum_servisnej_prehliadky = ((JSONObject)kontroly.get(i)).getString("service_check_date");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (datum_servisnej_prehliadky != "null" && datum_servisnej_prehliadky != null && JSONObject.NULL!=datum_servisnej_prehliadky && !datum_servisnej_prehliadky.equals("") && !datum_servisnej_prehliadky.equals(" ")) {
                if (vysledok_servisnej_prehliadky!= "null" && vysledok_servisnej_prehliadky != null && JSONObject.NULL!=vysledok_servisnej_prehliadky && !vysledok_servisnej_prehliadky.equals("")&& !vysledok_servisnej_prehliadky.equals(" ")) {
                    if (typ_servisnej_prehliadky!= "null" && typ_servisnej_prehliadky != null && JSONObject.NULL!=typ_servisnej_prehliadky && !typ_servisnej_prehliadky.equals("") && !typ_servisnej_prehliadky.equals(" ")) {
                        prehliadky.add("Prehliadka: " + (i + 1));
                        try {
                            prehliadky_list.add((JSONObject) kontroly.get(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        if (!prehliadky.isEmpty()) {
            try {
                ((JSONObject) jsonResponce.getJSONObject("vehicle")).getJSONObject("owner").toString();
                groupList.add("Prehliadky");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        egovCollection = new LinkedHashMap<>();

        for (String egov : groupList) {
            if (egov.equals("Vozidlo")) {
                loadChild(vozidlo);
            } else if (egov.equals("Poistenie")) {
                loadChild(poistenie);
            } else if (egov.equals("Majiteľ")){
                loadChild(majitel);
            }else if (egov.equals("Prehliadky")) {
                loadChild(prehliadky);
            }
			egovCollection.put(egov, childList);
		}
	}

	private void loadChild(ArrayList<String> egovModels) {
		childList = new ArrayList<String>();
		for (String model : egovModels)
			childList.add(model);
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item, menu);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == INCORECT_RECOGNIZED_PLATE) {
            if(resultCode == Activity.RESULT_OK){
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                this.finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    public void alertDialogPriestupky(int id,String priestupok) {

        JSONObject JSONpriestupok = null;
        String[] priestu = new String[3];
        String datum_priestupku = "" ;
        String pokutovatel= "";
        int pokuta =0;
        try {
            JSONpriestupok = new JSONObject(priestupok);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            datum_priestupku = (JSONpriestupok.getString("violation_date"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(datum_priestupku!= "null" && datum_priestupku!=null && JSONObject.NULL!=datum_priestupku && !datum_priestupku.equals("") && !datum_priestupku.equals(" ")) {
            priestu[0]= ("Deň priestupku: " + datum_priestupku);
        }

        try {
            pokutovatel = (JSONpriestupok.getString("issueOfficer"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(pokutovatel!= "null" && pokutovatel!=null && JSONObject.NULL!=pokutovatel && !pokutovatel.equals("") && !pokutovatel.equals(" ")) {
            priestu[2]=("Pokutovateľ: " + pokutovatel);
        }

        try {
            pokuta = (JSONpriestupok.getInt("cost"));

        }catch (JSONException e) {
            e.printStackTrace();
        }
        if (pokuta>=0)  {
            priestu[1] = ("Suma pokuty: " + pokuta +" €");
        }

        ListView listView = new ListView(this);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, R.layout.called_list_item, R.id.txtitem,priestu);

        listView.setAdapter(adapter);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(listView);
        builder.setTitle("Priestupok: "+id);
        builder.setCancelable(true);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void alertDialogPrehliadky(int id, String prehliadka) {
        String vysledok_servisnej_prehliadky = "";
        String typ_servisnej_prehliadky = "";
        String datum_servisnej_prehliadky = "";
        JSONObject JSONprehliadka = null;
        String[] kontro = new String[3];
        try {
            JSONprehliadka = new JSONObject(prehliadka);
        } catch (JSONException e) {
            e.printStackTrace();
        }
            try {
                typ_servisnej_prehliadky = JSONprehliadka.getString("service_check_type");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (typ_servisnej_prehliadky!= "null" && typ_servisnej_prehliadky != null && JSONObject.NULL!=typ_servisnej_prehliadky && !typ_servisnej_prehliadky.equals("") && !typ_servisnej_prehliadky.equals(" ")) {
                kontro[0]= ("Typ prehliadky: " + typ_servisnej_prehliadky);
            }
            try {
                vysledok_servisnej_prehliadky = JSONprehliadka.getString("service_check_result");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (vysledok_servisnej_prehliadky!= "null" && vysledok_servisnej_prehliadky != null && JSONObject.NULL!=vysledok_servisnej_prehliadky && !vysledok_servisnej_prehliadky.equals("")&& !vysledok_servisnej_prehliadky.equals(" ")) {
                kontro[1]= ("Vysledok prehliadky: " + vysledok_servisnej_prehliadky);
            }
            try {
                datum_servisnej_prehliadky = JSONprehliadka.getString("service_check_date");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (datum_servisnej_prehliadky != "null" && datum_servisnej_prehliadky != null && JSONObject.NULL!=datum_servisnej_prehliadky && !datum_servisnej_prehliadky.equals("") && !datum_servisnej_prehliadky.equals(" ")) {
                kontro[2]= ("Dátum prehliadky: " + datum_servisnej_prehliadky);
            }


        ListView listView = new ListView(this);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, R.layout.called_list_item, R.id.txtitem,kontro);

        listView.setAdapter(adapter);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(listView);
        builder.setTitle("Prehliadka: "+id);
        builder.setCancelable(true);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void alertDialogHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Info");
        builder.setMessage("Položky ako Kontrola a Priestupky je možné stlačením otvoriť");
        builder.setCancelable(true);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
}



