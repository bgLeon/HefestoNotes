package com.hefesto.bg.hefestonotes.auxclases;

/**
 * Created by Borja on 11/08/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ObjectsDbAdaptor {

    private static final String DATABASE_TABLE_NOTA = "notas";
    public static final String COL_TITULO = "titulo";
    public static final String COL_CONTENIDO = "contenido";
    public static final String COL_CATEGORIA = "etiquetas";
    public static final String COL_CIFRADO = "cifrado";
    public static final String COL_ID = "_id";

    public static final String NULLPASS = "NullPass";

    private static final String DATABASE_TABLE_PASS = "password";
    public static final String COL_CONTRASEÑA = "contraseña";
    public static final String COL_PREGUNTA = "pregunta";
    public static final String COL_RESPUESTA = "respuesta";

    private static final String TAG = ObjectsDbAdaptor.class.getSimpleName();
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private static final String DATABASE_NAME = "Base_datos";


    private static final String DATABASE_CREATE_NOTAS = "create table if not exists  " + DATABASE_TABLE_NOTA
            + " (_id integer primary key autoincrement, "
            + COL_TITULO + " text not null, "
            + COL_CONTENIDO + " text, "
            + COL_CIFRADO + " integer,"
            + COL_CATEGORIA + " text"
            + ");";
    private static final String DATABASE_CREATE_PASS = "create table if not exists  " + DATABASE_TABLE_PASS
            + " (_id integer primary key autoincrement, "
            + COL_CONTRASEÑA + " text not null,"
            + COL_PREGUNTA + "text not null,"
            + COL_RESPUESTA + "text not null"
            + ");";

    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "Creando base de datos");
            db.execSQL(DATABASE_CREATE_PASS);
            db.execSQL(DATABASE_CREATE_NOTAS);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
            Log.w(TAG, "Actualizando base de datos de la versiÃ³n " + versionAnterior + " a "
                    + versionNueva + ", lo que destruirÃ¡ todos los datos existentes");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_NOTA);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_PASS);
            onCreate(db); // en desarrollo, vuelvo a crear BD en vez de cambiar de versiÃ³n
        }
    }

    /**
     * Constructor - recibe el contexto de la base de datos que va a ser
     * abierta o creada. Abre la base de datos. Si no se puede abrir, intenta crear una nueva
     * instancia de la base de datos. Si no se puede crear, lanza una excepciÃ³n
     * para alertar del fallo
     *
     * @param ctx contexto con el que trabajar
     * @throws SQLException si la base de datos no estuviera ni abierta ni creada
     */
    public ObjectsDbAdaptor(Context ctx) throws SQLException {
        this.mCtx = ctx;
        dbHelper = new DBHelper(mCtx);
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    private int boolean2int(boolean b) {
        return b ? 0 : 1;
    }

    public String recuperaPassword() {
        Log.w(TAG, "onRecuperaPassword");
        Cursor c = db.query(DATABASE_TABLE_PASS, new String[]{COL_CONTRASEÑA}, null, null, null, null, null);
        if (c.moveToFirst()) {
            Log.i(TAG, "saliendo de recuperaPassword1");
            int indiceContraseña = c.getColumnIndexOrThrow(ObjectsDbAdaptor.COL_CONTRASEÑA);
            return c.getString(indiceContraseña);
        }
        Log.i(TAG, "saliendo de recuperaPassword2");
        return NULLPASS;
    }


    public long creaSeguridad(Seguridad parametros) {
        ContentValues valoresIniciales = new ContentValues();
        valoresIniciales.put(COL_CONTRASEÑA, parametros.getContraseña());
        valoresIniciales.put(COL_PREGUNTA, parametros.getPregunta());
        valoresIniciales.put(COL_RESPUESTA, parametros.getRespuesta());
        return db.insert(DATABASE_TABLE_PASS, null, valoresIniciales);
    }

    public boolean actualizaPass(Seguridad pass) {
        Log.i(TAG, "Actualiza pass");
        ContentValues args = new ContentValues();
        args.put(COL_CONTRASEÑA, pass.getContraseña());
        Cursor c = db.query(DATABASE_TABLE_NOTA, new String[]{COL_CATEGORIA}, null, null, null, null, null);
        int indiceContraseña = c.getColumnIndexOrThrow(ObjectsDbAdaptor.COL_CATEGORIA);
        return db.update(DATABASE_TABLE_PASS, args, COL_ID + "=" + indiceContraseña, null) > 0;
    }
    public String[] recuperaSeguridad() {
        Cursor c = db.query(DATABASE_TABLE_PASS, new String[]{COL_PREGUNTA, COL_RESPUESTA}, null, null, null, null, null);
        int indicePregunta = c.getColumnIndexOrThrow(ObjectsDbAdaptor.COL_PREGUNTA);
        int indiceRespuesta = c.getColumnIndexOrThrow(ObjectsDbAdaptor.COL_RESPUESTA);
        Log.w(TAG, "indicePregunta: "+ indicePregunta);
        Log.w(TAG, "indiceRespuesta: " + indiceRespuesta);
        String[] PregResp = new String[2];
        PregResp[0]=c.getString(indicePregunta);
        PregResp[1]=c.getString(indiceRespuesta);
        Log.i(TAG,"Pregunta: "+ PregResp[0]+ " || Respuesta: " + PregResp[1]);
        return PregResp;
    }
    public void borraSeguridad() {
        db.delete(DATABASE_TABLE_PASS, null, null);
    }


    public long creaNota(Nota nota) {
        ContentValues valoresIniciales = new ContentValues();
        valoresIniciales.put(COL_TITULO, nota.getTitulo());
        valoresIniciales.put(COL_CONTENIDO, nota.getContenido());
        valoresIniciales.put(COL_CIFRADO, boolean2int(nota.isCifrado()));
        valoresIniciales.put(COL_CATEGORIA, nota.getCategoria());

        return db.insert(DATABASE_TABLE_NOTA, null, valoresIniciales);
    }


    public boolean borraNota(long id) {
        Log.i(TAG, "borra nota id " + id);
        return db.delete(DATABASE_TABLE_NOTA, COL_ID + "=" + id, null) > 0;
    }


    public String[] recuperaCategorias() {
        Cursor c = db.query(DATABASE_TABLE_NOTA, new String[]{COL_CATEGORIA}, null, null, null, null, null);
        if (c == null) {
            Log.i(TAG, "categorias vacias ");
            return new String[0];
        }
        int indiceCategoria = c.getColumnIndexOrThrow(ObjectsDbAdaptor.COL_CATEGORIA);
        c.moveToFirst();
        String[] categorias = new String[c.getCount()];
        for (int i = 0; i < c.getCount(); i++) {
            categorias[i] = c.getString(indiceCategoria);
            c.moveToNext();
        }
        return categorias;
    }

    public Cursor recuperaTodasLasNotas() {

        return db.query(DATABASE_TABLE_NOTA, new String[]{COL_ID, COL_TITULO,
                COL_CONTENIDO, COL_CIFRADO, COL_CATEGORIA}, null, null, null, null, null);
    }

    public Cursor recuperaNota(long id) throws SQLException {

        Cursor mCursor =

                db.query(true, DATABASE_TABLE_NOTA, new String[]{COL_ID,
                                COL_TITULO, COL_CONTENIDO, COL_CIFRADO, COL_CATEGORIA}, COL_ID + "=" + id, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }


    public void borraTodasLasNotas() {
        db.delete(DATABASE_TABLE_NOTA, null, null);
    }

    public boolean actualizaNota(long id, Nota nota) {
        Log.i(TAG, "Actualiza nota " + id + " " + nota);
        ContentValues args = new ContentValues();
        args.put(COL_TITULO, nota.getTitulo());
        args.put(COL_CONTENIDO, nota.getContenido());
        args.put(COL_CIFRADO, boolean2int(nota.isCifrado()));
        args.put(COL_CATEGORIA, nota.getCategoria());
        return db.update(DATABASE_TABLE_NOTA, args, COL_ID + "=" + id, null) > 0;
    }


}

