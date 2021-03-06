package com.hefesto.bg.hefestonotes.activities;

import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.hefesto.bg.hefestonotes.auxclases.Cifrador;
import com.hefesto.bg.hefestonotes.auxclases.Nota;
import com.hefesto.bg.hefestonotes.auxclases.ObjectsDbAdaptor;
import com.hefesto.bg.hefestonotes.R;

public class EdicionNotaActivity extends AppCompatActivity {
    private static final String TAG = EdicionNotaActivity.class.getSimpleName();

    private EditText editTitulo;
    private EditText editContenido;
    private AutoCompleteTextView editCategoria;
    private ArrayAdapter<String> adaptadorAutocomplete;
    private Button buttonGuardar;
    private Button buttonCancelar;
    private ImageButton buttonCifrar;
    private ObjectsDbAdaptor objectsDbAdaptor;
    private long id;
    private String clave;
    private boolean isCifrado;
    private boolean delete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_edicion_nota);
        editCategoria = (AutoCompleteTextView) findViewById(R.id.editCategoria);
        objectsDbAdaptor = new ObjectsDbAdaptor(this);
        adaptadorAutocomplete = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,
                eliminaRepetidos(objectsDbAdaptor.recuperaCategorias()));
        editCategoria.setAdapter(adaptadorAutocomplete);
        editTitulo = (EditText) findViewById(R.id.editTitulo);
        editContenido = (EditText) findViewById(R.id.editContenido);
        delete=false;

        buttonGuardar = (Button) findViewById(R.id.saveButton);
        buttonGuardar.setOnClickListener(new MiButtonGuardarOnClickListener());
        buttonCifrar = (ImageButton) findViewById(R.id.lockButton);
        buttonCifrar.setOnClickListener(new MiButtonCifrarOnClickListener());
        buttonCancelar = (Button) findViewById(R.id.cancelButton);
        buttonCancelar
                .setOnClickListener(new MiButtonCancelarOnClickListener());
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Toast.makeText(this, getString(R.string.error_detalle),
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error sin extras ");
            finish();
        }
        Bundle bundle = getIntent().getExtras();
        this.clave = bundle.getString("key");
        isCifrado = false;
        int requestCode = extras.getInt(ListActivity.REQUEST_CODE);
        if (requestCode == ListActivity.MODIFICA_NOTA) {
            Nota nota = (Nota) extras.getSerializable(Nota.NOTA);
            id = extras.getLong(ObjectsDbAdaptor.COL_ID);
            String titulo = nota.getTitulo();
            String contenido = nota.getContenido();
            String categoria = nota.getCategoria();
            isCifrado = nota.isCifrado();
            if (isCifrado) editContenido.setEnabled(false);
            cambiaInterfaz();

            if (titulo != null) {
                editTitulo.setText(titulo);
            }

            if (contenido != null) {
                editContenido.setText(contenido);
            }
            if (categoria != null) {
                editCategoria.setText(categoria);
            }
            Log.d(TAG, "Edito " + nota);
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edicion_nota, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            confirmarSalida();
        }

        return super.onKeyDown(keyCode, event);
    }

    private void confirmarSalida(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog
                .setMessage(R.string.confirm_exit)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                finish();
                            }
                        })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                            }
                        });

        alertDialog.show();
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.BorrarNota: {
                confirmarBorrado();
                return true;
            }
            default: {
                Log.w(TAG, "Opción desconocida " + item.getItemId());
                return false;
            }
        }
    }
    private class MiButtonGuardarOnClickListener implements
            View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick SaveButton");
            String titulo = editTitulo.getText().toString();
            if (titulo.isEmpty()) {
                decirRellenar();
            }
            else {
                edicionCompletada();
            }

        }
    }
    private void edicionCompletada(){
        Intent miIntent = new Intent();
        Bundle extras = new Bundle();
        extras.putLong(ObjectsDbAdaptor.COL_ID, id);
        String titulo = editTitulo.getText().toString();
        String contenido = editContenido.getText().toString();
        String categoria = editCategoria.getText().toString();
        Nota nt = new Nota(titulo, contenido, categoria, isCifrado);
        extras.putSerializable(Nota.NOTA, nt);
        extras.putBoolean("Delete",delete);
        miIntent.putExtras(extras);
        setResult(RESULT_OK, miIntent);
        if (delete) decirBorrado();
        else decirGuardado();
        finish();

    }
    private void decirRellenar(){
        Toast.makeText(this, R.string.titulo_vacio, Toast.LENGTH_LONG).show();
    }
    private void decirBorrado(){
        Toast.makeText(this, R.string.nota_eliminada, Toast.LENGTH_LONG).show();

    }
    private void confirmarBorrado(){
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle(R.string.atencion);
        dialogo.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert));
        dialogo.setMessage(R.string.conf_borrar_nota);
        dialogo.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete=true;
                        edicionCompletada();

                    }
                });
        dialogo.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog confirma = dialogo.create();
        confirma.show();
    }


    private void decirGuardado() {
        Toast.makeText(this, R.string.nota_guardada, Toast.LENGTH_LONG).show();
    }

    private class MiButtonCancelarOnClickListener implements
            View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick CancelButton");
            confirmarSalida();
        }
    }

    private class MiButtonCifrarOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick CifrarButton");
            cifra();

        }
    }

    private void decirIncorrecta() {
        Toast.makeText(this, R.string.inc_key, Toast.LENGTH_LONG).show();
    }

    private void decirCifrado() {
        Toast.makeText(this, R.string.decirCifrado, Toast.LENGTH_LONG).show();
    }

    private void decirDesCifrado() {
        Toast.makeText(this, R.string.decirDescifrado, Toast.LENGTH_LONG).show();
    }

    private void cifra() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setMessage(R.string.intr_key);

        final EditText input = new EditText(this);
        dialogo.setView(input);

        dialogo.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String claveintr = input.getText().toString();
                        if (claveintr.equals(clave)) {
                            String cif = "0";
                            if (isCifrado) {
                                cif = "1";
                            }
                            Log.d(TAG, "antes del asinctask");
                            new Cifrar().execute(claveintr, editContenido
                                    .getText().toString(), cif);

                        } else {
                            decirIncorrecta();
                            Log.w(TAG,"contraseña correcta es: " + clave);
                        }

                    }
                });
        dialogo.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.w(TAG, "Cancelado introducir clave");

                    }
                });
        AlertDialog confirma = dialogo.create();
        confirma.show();
    }

    private class Cifrar extends AsyncTask<String, Integer, String[]> {
        private final ProgressDialog barra = new ProgressDialog(
                EdicionNotaActivity.this);

        protected void onPreExecute() {
            Log.d(TAG, "empieza OnPre");
            editContenido.setEnabled(false);
            barra.show();
            Log.d(TAG, "acaba OnPre");
        }

        // se crea una hebra automaticamente
        @Override
        protected String[] doInBackground(final String... params) {
            Log.d(TAG, "empieza Doin");

            Cifrador cifrador = new Cifrador(params[0]);

            if (params[2].equals("0")) {
                Log.d(TAG, "continua Doin cifrando");

                int n = 0;
                publishProgress(n, 0);
                StringBuilder buffer = new StringBuilder(params[1].length());
                for (int i = 0; i < params[1].length(); i++) {
                    char c2 = cifrador.cifraCaracter(params[1].charAt(i));
                    buffer.append(c2);
                   /* try {
                        for (Long j = 0L; j < 3L; j++) {
                            Thread.sleep(1);

                        }

                    } catch (InterruptedException e) {
                        Log.v("Codificacion int", e.getMessage());
                    }
                    publishProgress(++n, 0);*/
                }

                isCifrado = true;
                String[] txt = new String[2];
                txt[0] = buffer.toString();
                txt[1] = "1";
                return txt;
            }
            Log.d(TAG, "Continua Doin descifrando");
            StringBuilder buffer = new StringBuilder(params[0].length());
            int n = 0;
            publishProgress(n, 1);
            for (int i = 0; i < params[1].length(); i++) {
                char c1 = params[1].charAt(i);
                char c2 = cifrador.descifraCaracter(c1);
                buffer.append(c2);
               /* try {
                    for (Long j = 0L; j < 3L; j++) {
                        Thread.sleep(1);

                    }
                } catch (InterruptedException e) {
                    Log.v("Codificacion int", e.getMessage());
                }*/
                publishProgress(++n, 1);
            }
            isCifrado = false;

            String[] txt = new String[2];
            txt[0] = buffer.toString();
            txt[1] = "0";
            return txt;
        }

        protected void onProgressUpdate(Integer... value) {
            super.onProgressUpdate(value);
            if (value[1] == 0) {
                barra.setMessage("\nLetras codificadas " + value[0]);
            } else {
                barra.setMessage("\nLetras decodificadas " + value[0]);
            }
        }

        protected void onPostExecute(final String... txt) {
            if (txt[1].equals("0")) editContenido.setEnabled(true);
            editContenido.setText(txt[0]);
            cambiaInterfaz();
            if (barra.isShowing()) {
                barra.dismiss();

            }
            if (isCifrado) decirCifrado();
            else {
                decirDesCifrado();
            }

        }
    }

    /**
     * cambia la imagen del ImageButton
     */
    public void cambiaInterfaz() {
        ImageButton candado = (ImageButton) findViewById(R.id.lockButton);
        if (isCifrado) {
            Log.i(TAG, "Candado cerrado");
            candado.setImageResource(R.drawable.ic_menu_cifrar);
        } else {
            Log.i(TAG, "Candado abierto");
            candado.setImageResource(R.drawable.ic_menu_descifrar);
        }

    }

    /**
     * Elimina las categorias repetidas y las vacias para el autocomplete
     *
     * @param parametro Las categorias
     * @return Las categorias sin repetir
     */
    private String[] eliminaRepetidos(String[] parametro) {
        int n = parametro.length;
        if (n == 0 || n == 1)
            return parametro;
        String empty = "";
        Log.d(TAG, "el primer for" + n);
        for (int i = 0; i < parametro.length; i++) {
            if (parametro[i].equals(empty)) {
                Log.i(TAG, "resta");
                n--;
            }
            for (int j = i + 1; j < parametro.length; j++) {
                if (parametro[i] != null && parametro[j] != null
                        && !parametro[i].equals(empty)
                        && !parametro[j].equals(empty)) {
                    if (parametro[i].equals(parametro[j])) {
                        Log.d(TAG, "Parametro i " + parametro[i]);
                        Log.d(TAG, "Parametro j " + parametro[j]);
                        n--;
                        for (int k = i - 1; k >= 0; k--) {
                            if (parametro[k].equals(parametro[j])) {
                                Log.i(TAG, "suma");
                                n++;
                            }
                        }
                    }
                }
            }
        }
        Log.d(TAG, "Pasado el primer for " + n);
        String[] param = new String[n];
        param[0] = parametro[0];
        int l = 1;
        for (int i = 0; i < parametro.length; i++) {
            int z = 0;
            if (parametro[i] != null && !parametro[i].equals(empty)) {
                for (int j = 0; j < param.length; j++) {
                    if (param[j] != null) {

                        if (parametro[i].equals(param[j])) {
                            z++;
                        }
                    }
                    if (j == param.length - 1) {
                        if (z == 0) {

                            Log.d(TAG, "Rellenando " + l);
                            Log.d(TAG, "Rellenando " + i);
                            Log.d(TAG, "Rellenando " + parametro[i]);
                            Log.i(TAG, "cambio");
                            param[l] = parametro[i];
                            l++;
                        }
                    }
                }
            }
        }

        Log.d(TAG, "Terminando " + param.length);
        return param;
    }
}


