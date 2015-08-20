package com.hefesto.bg.hefestonotes.activities;

import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hefesto.bg.hefestonotes.auxclases.Nota;
import com.hefesto.bg.hefestonotes.auxclases.ObjectsDbAdaptor;
import com.hefesto.bg.hefestonotes.R;
import com.hefesto.bg.hefestonotes.auxclases.Seguridad;


public class ListActivity extends AppCompatActivity implements OnItemClickListener {
    private static final String TAG = ListActivity.class.getSimpleName();
    public static final String REQUEST_CODE = "requestCode";
    public static final int CREA_NOTA = 0;
    public static final int MODIFICA_NOTA = 1;


    ListView lista;
    private ObjectsDbAdaptor objectsDbAdaptor;
    private Cursor notasCursor;
    private SimpleCursorAdapter cursorAdapter;

    private int indiceContenido;
    private int indiceTitulo;
    private int indiceCategoria;
    private int indiceCifrado;
    private String clave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        objectsDbAdaptor = new ObjectsDbAdaptor(this);
        notasCursor = objectsDbAdaptor.recuperaTodasLasNotas();
        clave=objectsDbAdaptor.recuperaPassword();
        indiceTitulo = notasCursor
                .getColumnIndexOrThrow(ObjectsDbAdaptor.COL_TITULO);
        indiceContenido = notasCursor
                .getColumnIndexOrThrow(ObjectsDbAdaptor.COL_CONTENIDO);
        indiceCategoria = notasCursor
                .getColumnIndexOrThrow(ObjectsDbAdaptor.COL_CATEGORIA);
        indiceCifrado = notasCursor
                .getColumnIndexOrThrow(ObjectsDbAdaptor.COL_CIFRADO);


        String[] from = new String[]{ObjectsDbAdaptor.COL_TITULO,
                ObjectsDbAdaptor.COL_CATEGORIA,};
        int[] to = new int[]{android.R.id.text1, android.R.id.text2};
        cursorAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, // PodrÃ­a usarse
                // android.R.layout.simple_list_item_2
                notasCursor, from, to);

        lista = (ListView) findViewById(R.id.miLista);
        lista.setAdapter(cursorAdapter);
        lista.setOnItemClickListener(this);
        TextView emptyText = (TextView) findViewById(android.R.id.empty);
        lista.setEmptyView(emptyText);

        if (clave.equals(ObjectsDbAdaptor.NULLPASS))checkClave();

    }

    private void actualizaLista() {
        notasCursor.requery();
        cursorAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.info: {
                Toast.makeText(this, getString(R.string.msg_info),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.Annadir: {
                Intent miIntent = new Intent(this, EdicionNotaActivity.class);
                Bundle extras = new Bundle();
                extras.putInt(REQUEST_CODE, CREA_NOTA);
                extras.putString("key", clave);
                miIntent.putExtras(extras);
                startActivityForResult(miIntent, CREA_NOTA);
                return true;
            }
            case R.id.BorrarLista: {
                borrarLista();
                actualizaLista();
                return true;

            }

            default: {
                Log.w(TAG, "Opción desconocida " + item.getItemId());
                return false;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
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

    /**
     * Te devuelve el equivalente en boolean de si esta cifrado o no.
     *
     * @param cifrado estado del cifrado
     * @return si esta cifrado
     */

    private boolean conversorCifrado(int cifrado) {
        if (cifrado == 0)
            return true;
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> cursorAdapter, View view, int position,
                            long id) {
        Log.d(TAG, "onItemClick");
        Cursor c = notasCursor;
        c.moveToPosition(position);
        Nota nota = new Nota(c.getString(indiceTitulo),
                c.getString(indiceContenido), c.getString(indiceCategoria),
                conversorCifrado(c.getInt(indiceCifrado)));

        Intent intent = new Intent(this, EdicionNotaActivity.class);
        Bundle extras = new Bundle();
        extras.putLong(ObjectsDbAdaptor.COL_ID, id);
        extras.putSerializable(Nota.NOTA, nota);
        extras.putInt(REQUEST_CODE, MODIFICA_NOTA);
        extras.putString("key",clave);
        intent.putExtras(extras);
        startActivityForResult(intent, MODIFICA_NOTA);
    }

    private void borrarLista() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setMessage(R.string.dialogo_borrar_lista);
        dialogo.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        objectsDbAdaptor.borraTodasLasNotas();
                        actualizaLista();
                    }
                });
        dialogo.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.w(TAG, "Cancelado borrar lista");

                    }
                });
        AlertDialog confirma = dialogo.create();
        confirma.show();
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_CANCELED) {
            Log.i(TAG, "Cancelado");
            return;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            Toast.makeText(this, getString(R.string.error_detalle),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        for (String s : extras.keySet()) {
            Log.i(TAG, "extra " + s);
        }
        Nota nota = (Nota) extras.getSerializable(Nota.NOTA);

        switch (requestCode) {
            case CREA_NOTA: {
                Log.i(TAG, "Crea producto " + nota);
                objectsDbAdaptor.creaNota(nota);

                cursorAdapter.notifyDataSetChanged();
                actualizaLista();
                break;
            }
            case MODIFICA_NOTA: {
                Long id = extras.getLong(ObjectsDbAdaptor.COL_ID);
                Log.i(TAG, "Modifica producto " + id);
                if (id == null) {
                    Log.e(TAG, getString(R.string.error_detalle));
                    finish();
                }

                objectsDbAdaptor.actualizaNota(id, nota);
                actualizaLista();
                break;
            }
            default: {
                Log.e(TAG, "Opción no conocida " + requestCode);
            }
        }

    }

    public void checkClave() {
        if (clave.equals(ObjectsDbAdaptor.NULLPASS) ) {
                AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
                dialogo.setMessage(R.string.empty_key);

                final EditText input = new EditText(this);
                dialogo.setView(input);

                dialogo.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clave= input.getText().toString();

                                objectsDbAdaptor.creaPass(new Seguridad(clave));
                                Log.i(TAG, "Nueva passw " + objectsDbAdaptor.recuperaPassword());
                            }

                        });
                AlertDialog confirma = dialogo.create();
                confirma.show();
            }

        }
    }